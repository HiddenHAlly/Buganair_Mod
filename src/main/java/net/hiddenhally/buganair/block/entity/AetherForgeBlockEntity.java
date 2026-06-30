package net.hiddenhally.buganair.block.entity;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.screen.AetherForgeScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AetherForgeBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
    // 4 Slots: 0=Input, 1=Fuel, 2=Output, 3=Breeze Rod
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

        @Override
        public int size() {
            return 5;
        }
    };

    public AetherForgeBlockEntity(BlockPos pos, BlockState state) {
        super(BuganairMod.AETHER_FORGE_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AetherForgeBlockEntity entity) {
        if (world.isClient()) return;

        boolean isDirty = false;
        boolean wasBurning = entity.fuelTime > 0;

        // 1. Decrease active fuel burn time
        if (entity.fuelTime > 0) {
            entity.fuelTime--;
            isDirty = true;
        }

        // 2. Replenish Breeze Booster Charges from Slot 3 (Max capacity: 100)
        ItemStack breezeStack = entity.inventory.get(3);
        if (entity.breezeCharges <= 80 && breezeStack.isOf(Items.BREEZE_ROD)) {
            entity.breezeCharges += 20; // 1 rod grants 20 process cycles of boost
            breezeStack.decrement(1);
            isDirty = true;
        }

        // 3. Process Crafting Logic
        if (canCraft(entity)) {
            // If out of fuel but has valid ingredients, try to burn a new item from Slot 1
            if (entity.fuelTime <= 0) {
                int burnValue = getBurnTime(entity.inventory.get(1));
                if (burnValue > 0) {
                    entity.fuelTime = burnValue;
                    entity.maxFuelTime = burnValue;
                    entity.inventory.get(1).decrement(1); // Consume fuel item
                    isDirty = true;
                }
            }

            // If actively burning, advance progress
            if (entity.fuelTime > 0) {
                // Multiplier: Speed is 3x faster if boosted by breeze charges, otherwise normal (1x)
                int speedMultiplier = (entity.breezeCharges > 0) ? 3 : 1;
                entity.progress += speedMultiplier;

                if (entity.progress >= entity.maxProgress) {
                    craftItem(entity);
                    entity.progress = 0;

                    // Consume one boost charge upon successful forge completion
                    if (entity.breezeCharges > 0) {
                        entity.breezeCharges--;
                    }
                }
                isDirty = true;
            }
        } else {
            // Cool down progress slowly if input is cleared or full
            if (entity.progress > 0) {
                entity.progress = Math.max(0, entity.progress - 2);
                isDirty = true;
            }
        }

        // 4. Update blockstate appearance if lit status changed
        boolean isBurning = entity.fuelTime > 0;
        if (wasBurning != isBurning) {
            // Assuming your block property handles `Properties.LIT`
            // world.setBlockState(pos, state.with(Properties.LIT, isBurning), 3);
            isDirty = true;
        }

        if (isDirty) {
            markDirty(world, pos, state);
        }
    }

    private static boolean canCraft(AetherForgeBlockEntity entity) {
        ItemStack input = entity.inventory.get(0);
        if (input.isEmpty()) return false;

        // Placeholder matching check: e.g., Iron Ore -> Iron Ingot
        ItemStack outputResult = getRecipeResult(input);
        if (outputResult.isEmpty()) return false;

        ItemStack outputSlot = entity.inventory.get(2);
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.areItemsEqual(outputSlot, outputResult)) return false;

        return outputSlot.getCount() + outputResult.getCount() <= outputSlot.getMaxCount();
    }

    private static void craftItem(AetherForgeBlockEntity entity) {
        ItemStack input = entity.inventory.get(0);
        ItemStack outputResult = getRecipeResult(input);
        ItemStack outputSlot = entity.inventory.get(2);

        if (outputSlot.isEmpty()) {
            entity.inventory.set(2, outputResult.copy());
        } else if (ItemStack.areItemsEqual(outputSlot, outputResult)) {
            outputSlot.increment(outputResult.getCount());
        }

        input.decrement(1);
    }

    private static ItemStack getRecipeResult(ItemStack input) {
        // Replace this template mapping with your true custom item mechanics / datapack loaders!
        if (input.isOf(Items.RAW_IRON) || input.isOf(Items.IRON_ORE)) {
            return new ItemStack(Items.IRON_INGOT);
        }
        return ItemStack.EMPTY;
    }

    private static int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        // Simple furnace mapping replacements
        if (fuel.isOf(Items.COAL) || fuel.isOf(Items.CHARCOAL)) return 1600;
        if (fuel.isOf(Items.STICK)) return 100;
        return 0;
    }

    // ── INVENTORY INTERFACE METHODS ──

    @Override
    public int size() { return this.inventory.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) { return this.inventory.get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, amount);
        if (!result.isEmpty()) this.markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) { return Inventories.removeStack(this.inventory, slot); }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return Inventory.canPlayerUse(this, player); }

    @Override
    public void clear() { this.inventory.clear(); this.markDirty(); }

    @Override
    public Text getDisplayName() { return Text.translatable("container.buganair.aether_forge"); }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AetherForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    // ── 1.21.11 DATA LOADING ──────────────────────────────────────────────
    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        // Load the functional state variables from the data view
        this.progress = view.getInt("Progress", 0);
        this.fuelTime = view.getInt("FuelTime", 0);
        this.maxFuelTime = view.getInt("MaxFuelTime", 0);
        this.breezeCharges = view.getInt("BreezeCharges", 0);

        // If your environment's Inventories class hasn't been updated to accept views yet,
        // use view.getCompound("Inventory") or pass the underlying compound delegate if exposed:
        // Inventories.readNbt(view.getCompound("Inventory"), this.inventory, registries);
    }

    // ── 1.21.11 DATA SAVING ───────────────────────────────────────────────
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        // Save the functional state variables into the data view
        view.putInt("Progress", this.progress);
        view.putInt("FuelTime", this.fuelTime);
        view.putInt("MaxFuelTime", this.maxFuelTime);
        view.putInt("BreezeCharges", this.breezeCharges);

        // Save inventory into a clean sub-view or sub-compound
        // NbtCompound invNbt = new NbtCompound();
        // Inventories.writeNbt(invNbt, this.inventory, registries);
        // view.putCompound("Inventory", invNbt);
    }
}