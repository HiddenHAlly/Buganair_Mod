package net.hiddenhally.buganair.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BuganairBoatEntity extends BoatEntity {
    private static final double MOVE_SPEED = 0.35D;

    private int forwardInput;
    private int sidewaysInput;
    private int verticalInput;

    public BuganairBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
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
        int x = sidewaysInput;
        int y = verticalInput;
        int z = forwardInput;
        if (x == 0 && y == 0 && z == 0) {
            return Vec3d.ZERO;
        }

        Vec3d input = new Vec3d(x, y, z);
        if (input.lengthSquared() > 1.0D) {
            input = input.normalize();
        }

        double yawRadians = Math.toRadians(yaw);
        Vec3d forward = new Vec3d(-Math.sin(yawRadians), 0.0D, Math.cos(yawRadians));
        Vec3d right = new Vec3d(-Math.cos(yawRadians), 0.0D, -Math.sin(yawRadians));

        Vec3d horizontal = forward.multiply(input.z).add(right.multiply(input.x));

        return new Vec3d(horizontal.x, input.y, horizontal.z).multiply(MOVE_SPEED);
    }

    @Override
    public void onPassengerLookAround(net.minecraft.entity.Entity passenger) {
        if (passenger instanceof PlayerEntity player) {
            setYaw(player.getYaw());
        }
    }

    @Override
    public Type getVariant() {
        return Type.OAK;
    }
}
