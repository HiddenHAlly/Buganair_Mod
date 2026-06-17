package net.hiddenhally.buganair.client;

public final class BuganairSniperClientState {
    private BuganairSniperClientState() {}

    public static final int MIN_ZOOM_LEVEL = 1;
    public static final int MAX_ZOOM_LEVEL = 10;
    private static final int DEFAULT_ZOOM_LEVEL = 4;

    private static boolean aiming = false;
    private static int zoomLevel = DEFAULT_ZOOM_LEVEL;
    private static long lastFireTick = -1000;

    public static boolean isAiming() {
        return aiming;
    }

    public static void setAiming(boolean value) {
        aiming = value;
    }

    public static void toggleAiming() {
        aiming = !aiming;
    }

    public static int getZoomLevel() {
        return zoomLevel;
    }

    public static void adjustZoom(int delta) {
        zoomLevel = Math.max(MIN_ZOOM_LEVEL, Math.min(MAX_ZOOM_LEVEL, zoomLevel + delta));
    }

    /** Usato dal Mixin del FOV: 1.0 = nessuno zoom, valori più alti = più ingrandimento. */
    public static float getZoomDivisor() {
        return aiming ? (float) zoomLevel : 1.0f;
    }

    /** Throttle locale lato client, solo per evitare di spammare pacchetti inutili. */
    public static boolean canFire(long currentTick, int cooldownTicks) {
        return currentTick - lastFireTick >= cooldownTicks;
    }

    public static void markFired(long currentTick) {
        lastFireTick = currentTick;
    }
}