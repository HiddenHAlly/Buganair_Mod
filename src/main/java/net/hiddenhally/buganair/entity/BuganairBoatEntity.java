package net.hiddenhally.buganair.entity;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Depending on your exact mappings, this might also be called VehicleInventory
public class BuganairBoatEntity extends BoatEntity implements Inventory, NamedScreenHandlerFactory, RideableInventory {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 100;
    private static final int DEFAULT_SPEED = 7;

    private static final TrackedData<Integer> HORIZONTAL_SPEED = DataTracker.registerData(BuganairBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VERTICAL_SPEED = DataTracker.registerData(BuganairBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Creates the 27-slot list to track inventory items
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);

    private int forwardInput;
    private int sidewaysInput;
    private int verticalInput;

    // 1. Register a custom data tracker string for the variant
    private static final TrackedData<String> BOAT_VARIANT = DataTracker.registerData(BuganairBoatEntity.class, TrackedDataHandlerRegistry.STRING);

    public BuganairBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        // This super call is absolutely required so Java knows how to build the base BoatEntity!
        super(entityType, world, () -> getFallbackItemForType(entityType));
    }

    public void setVariant(String variant) {
        this.dataTracker.set(BOAT_VARIANT, variant);
    }

    public String getVariant() {
        return this.dataTracker.get(BOAT_VARIANT);
    }

    private static net.minecraft.item.Item getFallbackItemForType(EntityType<?> entityType) {
        Identifier id = Registries.ENTITY_TYPE.getId(entityType);
        String path = id.getPath();

        String woodType = "oak"; // Default fallback

        // Fix: Explicitly check for the base boat so we don't do negative string math
        if (path.equals("buganair_boat")) {
            woodType = "oak";
        } else if (path.contains("_") && path.startsWith("buganair_") && path.endsWith("_boat")) {
            woodType = path.substring("buganair_".length(), path.length() - "_boat".length());
            if (woodType.isEmpty()) {
                woodType = "oak";
            }
        }

        Identifier itemId = Identifier.of(id.getNamespace(), "buganair_" + woodType + "_boat");
        return Registries.ITEM.get(itemId);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HORIZONTAL_SPEED, DEFAULT_SPEED);
        builder.add(VERTICAL_SPEED, DEFAULT_SPEED);
        builder.add(BOAT_VARIANT, "oak"); // Default fallback
    }

    public int getHorizontalSpeed() {
        return this.dataTracker.get(HORIZONTAL_SPEED);
    }

    public int getVerticalSpeed() {
        return this.dataTracker.get(VERTICAL_SPEED);
    }

    public void setSpeedSettings(int horizontalSpeed, int verticalSpeed) {
        setHorizontalSpeed(horizontalSpeed);
        setVerticalSpeed(verticalSpeed);
    }

    public void setHorizontalSpeed(int speed) {
        this.dataTracker.set(HORIZONTAL_SPEED, clampSpeed(speed));
    }

    public void setVerticalSpeed(int speed) {
        this.dataTracker.set(VERTICAL_SPEED, clampSpeed(speed));
    }

    public void adjustHorizontalSpeed(int delta) {
        setHorizontalSpeed(getHorizontalSpeed() + delta);
    }

    public void adjustVerticalSpeed(int delta) {
        setVerticalSpeed(getVerticalSpeed() + delta);
    }

    public void setMovementInput(int forwardInput, int sidewaysInput, int verticalInput) {
        this.forwardInput = MathHelper.clamp(forwardInput, -1, 1);
        this.sidewaysInput = MathHelper.clamp(sidewaysInput, -1, 1);
        this.verticalInput = MathHelper.clamp(verticalInput, -1, 1);
    }

    @Override
    public void setInputs(boolean left, boolean right, boolean forward, boolean back) {
        // Ignore vanilla boat paddle input so A/D/W/S do not trigger boat steering.
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);

        PlayerEntity rider = getControllingPassenger() instanceof PlayerEntity player ? player : null;
        if (rider != null) {
            setYaw(rider.getYaw());
            Vec3d movement = getMovementVector(rider.getYaw());
            setVelocity(movement);
            move(MovementType.SELF, getVelocity());
            setVelocity(Vec3d.ZERO);
        } else {
            forwardInput = 0;
            sidewaysInput = 0;
            verticalInput = 0;
            setVelocity(Vec3d.ZERO);
        }
    }

    private Vec3d getMovementVector(float yaw) {
        double x = sidewaysInput;
        double z = forwardInput;
        double horizontalLength = Math.sqrt((x * x) + (z * z));
        if (horizontalLength > 1.0D) {
            x /= horizontalLength;
            z /= horizontalLength;
        }

        double yawRadians = Math.toRadians(yaw);
        Vec3d forward = new Vec3d(-Math.sin(yawRadians), 0.0D, Math.cos(yawRadians));
        Vec3d right = new Vec3d(-Math.cos(yawRadians), 0.0D, -Math.sin(yawRadians));

        double horizontalScale = getHorizontalSpeed() / 20.0D;
        double verticalScale = getVerticalSpeed() / 20.0D;
        Vec3d horizontal = forward.multiply(z).add(right.multiply(x)).multiply(horizontalScale);
        double vertical = verticalInput * verticalScale;

        return new Vec3d(horizontal.x, vertical, horizontal.z);
    }

    @Override
    public void onPassengerLookAround(net.minecraft.entity.Entity passenger) {
        if (passenger instanceof PlayerEntity player) {
            setYaw(player.getYaw());
        }
    }

    // New: Handle sneak right-click to view storage container UI
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (!this.getEntityWorld().isClient()) {
                player.openHandledScreen(this);
            }
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("BuganairHorizontalSpeed", getHorizontalSpeed());
        view.putInt("BuganairVerticalSpeed", getVerticalSpeed());
        view.putString("BuganairWoodType", this.getVariant());
        // Leverages 1.21.11 Inventories utility to save internal item list via WriteView
        Inventories.writeData(view, this.inventory);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        setHorizontalSpeed(view.getInt("BuganairHorizontalSpeed", DEFAULT_SPEED));
        setVerticalSpeed(view.getInt("BuganairVerticalSpeed", DEFAULT_SPEED));
        if (view.contains("BuganairWoodType")) {
            this.setVariant(view.getString("BuganairWoodType",this.getVariant()));
        }
        // Leverages 1.21.11 Inventories utility to read internal item list via ReadView
        Inventories.readData(view, this.inventory);
    }

    private static int clampSpeed(int speed) {
        return MathHelper.clamp(speed, MIN_SPEED, MAX_SPEED);
    }

    // --- NamedScreenHandlerFactory Implementation ---

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.chestBoat");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
        // This MUST point to BuganairBoatScreenHandler now!
        return new net.hiddenhally.buganair.screen.BuganairBoatScreenHandler(syncId, playerInventory, this);
    }

    // --- Inventory Interface Delegations ---

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public void markDirty() {
        // Persistence auto-saves elements correctly via chunk-unloads
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !this.isRemoved() && player.squaredDistanceTo(this) <= 64.0;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    // ==========================================
    // FIX 1: Open inventory when pressing 'E'
    // ==========================================
    @Override
    public void openInventory(PlayerEntity player) {
        if (!this.getEntityWorld().isClient()) {
            player.openHandledScreen(this);
        }
    }

    // ==========================================
    // FIX 2: Scatter items when the boat breaks
    // ==========================================
    @Override
    public void remove(Entity.RemovalReason reason) {
        // Check if the entity is being destroyed on the server side
        if (!this.getEntityWorld().isClient() && reason.shouldDestroy()) {
            // Drops the contents of this inventory at the boat's location
            net.minecraft.util.ItemScatterer.spawn(this.getEntityWorld(), this, this);
        }

        // Continue with the normal removal process (which drops the boat item)
        super.remove(reason);
    }
}