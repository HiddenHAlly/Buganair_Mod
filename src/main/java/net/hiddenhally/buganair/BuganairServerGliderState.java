package net.hiddenhally.buganair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BuganairServerGliderState {
    private BuganairServerGliderState() {}

    private static final Map<UUID, Boolean> GLIDING = new HashMap<>();
    private static final Map<UUID, Float> PITCH = new HashMap<>();
    private static final Map<UUID, Float> YAW = new HashMap<>();

    public static boolean isGliding(UUID uuid) {
        return GLIDING.getOrDefault(uuid, false);
    }

    public static void setGliding(UUID uuid, boolean value) {
        if (value) {
            GLIDING.put(uuid, true);
        } else {
            GLIDING.remove(uuid);
            PITCH.remove(uuid);
            YAW.remove(uuid);
            //ROLL.remove(uuid);
        }
    }

    public static float getPitch(UUID uuid) { return PITCH.getOrDefault(uuid, 0f); }
    public static float getYaw(UUID uuid)   { return YAW.getOrDefault(uuid, 0f); }

    public static void updateOrientation(UUID uuid, float pitch, float yaw) {
        PITCH.put(uuid, pitch);
        YAW.put(uuid, yaw);
        //ROLL.put(uuid, roll);
    }

}