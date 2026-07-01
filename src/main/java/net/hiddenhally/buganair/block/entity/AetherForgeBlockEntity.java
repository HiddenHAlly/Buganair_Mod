package net.hiddenhally.buganair.block.entity;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.hiddenhally.buganair.screen.AetherForgeScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class AetherForgeBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
    // Slots: 0=Input, 1=Fuel, 2=Output, 3=Breeze Rod
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    public int progress = 0;
    public int maxProgress = 200;
    public int fuelTime = 0;
    public int maxFuelTime = 0;
    public int breezeCharges = 0;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AetherForgeBlockEntity.this.progress;
                case 1 -> AetherForgeBlockEntity.this.maxProgress;
                case 2 -> AetherForgeBlockEntity.this.fuelTime;
                case 3 -> AetherForgeBlockEntity.this.maxFuelTime;
                case 4 -> AetherForgeBlockEntity.this.breezeCharges;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AetherForgeBlockEntity.this.progress = value;
                case 1 -> AetherForgeBlockEntity.this.maxProgress = value;
                case 2 -> AetherForgeBlockEntity.this.fuelTime = value;
                case 3 -> AetherForgeBlockEntity.this.maxFuelTime = value;
                case 4 -> AetherForgeBlockEntity.this.breezeCharges = value;
            }
        }
        @Override public int size() { return 5; }
    };

    public AetherForgeBlockEntity(BlockPos pos, BlockState state) {
        super(BuganairMod.AETHER_FORGE_BE, pos, state);
    }

    // ── DATA LOADING & SAVING ──
    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.progress = view.getInt("Progress", 0);
        this.fuelTime = view.getInt("FuelTime", 0);
        this.maxFuelTime = view.getInt("MaxFuelTime", 0);
        this.breezeCharges = view.getInt("BreezeCharges", 0);
        Inventories.readData(view, this.inventory);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("Progress", this.progress);
        view.putInt("FuelTime", this.fuelTime);
        view.putInt("MaxFuelTime", this.maxFuelTime);
        view.putInt("BreezeCharges", this.breezeCharges);
        Inventories.writeData(view, this.inventory);
    }

    // ── TICK & SMELTING LOGIC ──
    public static void tick(World world, BlockPos pos, BlockState state, AetherForgeBlockEntity entity) {
        if (world.isClient()) return;

        boolean isDirty = false;
        boolean wasBurning = entity.fuelTime > 0;

        // 1. Consume Fuel
        if (entity.fuelTime > 0) {
            entity.fuelTime--;
            isDirty = true;
        }

        // 2. Consume Breeze Rods (Max 80 charges)
        ItemStack breezeStack = entity.inventory.get(3);
        if (entity.breezeCharges <= 80 && breezeStack.isOf(Items.BREEZE_ROD)) {
            entity.breezeCharges += 20;
            breezeStack.decrement(1);
            isDirty = true;
        }

        // 3. Process Crafting using the updated 1.21+ canCraft check
        if (canCraft(entity)) {
            // Burn new fuel if empty
            if (entity.fuelTime <= 0) {
                int burnValue = getBurnTime(entity.inventory.get(1));
                if (burnValue > 0) {
                    entity.fuelTime = burnValue;
                    entity.maxFuelTime = burnValue;
                    entity.inventory.get(1).decrement(1);
                    isDirty = true;
                }
            }

            // Smelt if we have active fuel running
            if (entity.fuelTime > 0) {
                int speedMultiplier = (entity.breezeCharges > 0) ? 3 : 1; // 3x speed boost
                entity.progress += speedMultiplier;

                if (entity.progress >= entity.maxProgress) {
                    craftItem(entity); // Modern 1.21 craft logic
                    entity.progress = 0;
                    if (entity.breezeCharges > 0) entity.breezeCharges--;
                }
                isDirty = true;
            }
        } else {
            // Cooldown if invalid or empty
            if (entity.progress > 0) {
                entity.progress = Math.max(0, entity.progress - 2);
                isDirty = true;
            }
        }

        // 4. Update LIT Blockstate visually
        boolean isBurning = entity.fuelTime > 0;
        if (wasBurning != isBurning) {
            world.setBlockState(pos, state.with(Properties.LIT, isBurning), 3);
            isDirty = true;
        }

        if (isDirty) markDirty(world, pos, state);
    }

    // ── 1.21+ DYNAMIC RECIPE RESOLUTION ──
    private static Optional<RecipeEntry<SmeltingRecipe>> getRecipe(World world, ItemStack inputStack) {
        if (inputStack.isEmpty() || world == null) return Optional.empty();

        // Wrap the ItemStack into a SingleStackRecipeInput required by 1.21+ RecipeManager
        SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(inputStack);

        // Query the manager directly for any vanilla smelting recipe
        return world.getRecipeManager().getSynchronizedRecipes().getFirstMatch(RecipeType.SMELTING, recipeInput, world);
    }

    private static boolean canCraft(AetherForgeBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null) return false;

        ItemStack inputStack = entity.inventory.get(0); // Input Slot
        if (inputStack.isEmpty()) return false;

        Optional<RecipeEntry<SmeltingRecipe>> recipeOpt = getRecipe(world, inputStack);
        if (recipeOpt.isEmpty()) return false;

        // Use .craft() with the wrapped input and RegistryManager to resolve output dynamically
        SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(inputStack);
        ItemStack resultStack = recipeOpt.get().value().craft(recipeInput, world.getRegistryManager());

        ItemStack outputSlot = entity.inventory.get(2); // Output Slot
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.areItemsAndComponentsEqual(outputSlot, resultStack)) return false;

        return outputSlot.getCount() + resultStack.getCount() <= outputSlot.getMaxCount();
    }

    private static void craftItem(AetherForgeBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null) return;

        ItemStack inputStack = entity.inventory.get(0);
        if (inputStack.isEmpty()) return;

        Optional<RecipeEntry<SmeltingRecipe>> recipeOpt = getRecipe(world, inputStack);
        if (recipeOpt.isPresent() && canCraft(entity)) {
            SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(inputStack);
            ItemStack resultStack = recipeOpt.get().value().craft(recipeInput, world.getRegistryManager());

            ItemStack outputSlot = entity.inventory.get(2);
            if (outputSlot.isEmpty()) {
                entity.inventory.set(2, resultStack.copy());
            } else if (ItemStack.areItemsAndComponentsEqual(outputSlot, resultStack)) {
                outputSlot.increment(resultStack.getCount());
            }

            inputStack.decrement(1);
        }
    }

    private static int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        if (fuel.isOf(Items.COAL) || fuel.isOf(Items.CHARCOAL)) return 1600;
        if (fuel.isOf(BuganairBlocks.SKYWOOD_WOOD.asItem())) return 1500;
        if (fuel.isOf(BuganairBlocks.SKYWOOD_LOG.asItem()) || fuel.isOf(BuganairBlocks.SKYWOOD_PLANKS.asItem())) return 300;
        if (fuel.isOf(Items.STICK)) return 100;
        return 0;
    }

    // ── INVENTORY OVERRIDES ──
    @Override public int size() { return this.inventory.size(); }
    @Override public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getStack(int slot) { return this.inventory.get(slot); }
    @Override public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, amount);
        if (!result.isEmpty()) this.markDirty();
        return result;
    }
    @Override public ItemStack removeStack(int slot) { return Inventories.removeStack(this.inventory, slot); }
    @Override public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
        this.markDirty();
    }
    @Override public boolean canPlayerUse(PlayerEntity player) { return Inventory.canPlayerUse(this, player); }
    @Override public void clear() { this.inventory.clear(); this.markDirty(); }

    // ── UI FACTORY ──
    @Override public Text getDisplayName() { return Text.translatable("container.buganair.aether_forge"); }
    @Override public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AetherForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}