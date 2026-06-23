package net.hiddenhally.buganair.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public final class BuganairGliderClientState {
    private BuganairGliderClientState() {}

    private static boolean gliding = false;
    private static float pitch = 0f;
    private static float yaw = 0f;
    private static float roll = 0f;

    public static boolean isGliding() { return gliding; }

    public static void setGliding(boolean v) {
        gliding = v;
        if (v) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                pitch = client.player.getPitch();
                yaw = client.player.getYaw();
                roll = 0f;
            }
        }
    }

    public static void toggleGliding() { setGliding(!gliding); }

    public static float getPitch() { return pitch; }
    public static float getYaw()   { return yaw; }
    public static float getRoll()  { return roll; }

    public static void setFlightAngles(float flightPitch, float flightYaw) {
        pitch = flightPitch;
        yaw = flightYaw;
    }

    public static void accumulateLook(double deltaX, double deltaY, double sensitivity) {
        yaw += (float) (deltaX * sensitivity);
        pitch += (float) (deltaY * sensitivity);

        // Keep yaw clean, wrap pitch across full loops
        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.wrapDegrees(pitch);
    }

    public static void handleMouseInput(double deltaX, double deltaY, double sensitivity) {
        // Channels mouse into true unconstrained flight simulator coordinate vectors
        pitch += (float) (deltaY * sensitivity * 0.15f);
        roll  += (float) (deltaX * sensitivity * 0.15f);

        // Normalize roll angles within -180 to 180 boundary
        if (roll > 180f) roll -= 360f;
        if (roll < -180f) roll += 360f;

        if (pitch > 360f) pitch -= 360f;
        if (pitch < -360f) pitch += 360f;
    }

    public static void handleKeyboardInput(boolean strafeLeft, boolean strafeRight) {
        float keyboardYawSpeed = 2.0f;
        if (strafeLeft)  yaw -= keyboardYawSpeed;
        if (strafeRight) yaw += keyboardYawSpeed;

        // Aerodynamic self-stabilization: slowly pulls the wings flat if no mouse input occurs
        if (Math.abs(roll) > 0.01f) {
            roll *= 0.94f;
        }
    }
}