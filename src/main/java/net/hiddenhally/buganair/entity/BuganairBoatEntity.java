package net.hiddenhally.buganair.entity;

import net.hiddenhally.buganair.BuganairMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BuganairBoatEntity extends BoatEntity {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 100;
    private static final int DEFAULT_SPEED = 7;

    private static final TrackedData<Integer> HORIZONTAL_SPEED = DataTracker.registerData(BuganairBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VERTICAL_SPEED = DataTracker.registerData(BuganairBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int forwardInput;
    private int sidewaysInput;
    private int verticalInput;

    public BuganairBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HORIZONTAL_SPEED, DEFAULT_SPEED);
        builder.add(VERTICAL_SPEED, DEFAULT_SPEED);
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

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BuganairHorizontalSpeed", getHorizontalSpeed());
        nbt.putInt("BuganairVerticalSpeed", getVerticalSpeed());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BuganairHorizontalSpeed")) {
            setHorizontalSpeed(nbt.getInt("BuganairHorizontalSpeed"));
        }
        if (nbt.contains("BuganairVerticalSpeed")) {
            setVerticalSpeed(nbt.getInt("BuganairVerticalSpeed"));
        }
    }

    @Override
    public Type getVariant() {
        return Type.OAK;
    }

    @Override
    public Item asItem() {
        return BuganairMod.BUGANAIR_BOAT_ITEM;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BuganairMod.BUGANAIR_BOAT_ITEM);
    }

    private static int clampSpeed(int speed) {
        return MathHelper.clamp(speed, MIN_SPEED, MAX_SPEED);
    }
}
