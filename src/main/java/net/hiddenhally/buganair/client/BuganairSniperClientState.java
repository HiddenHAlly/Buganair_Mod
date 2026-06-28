package net.hiddenhally.buganair.client;

import net.minecraft.util.math.MathHelper;

public final class BuganairSniperClientState {
    private BuganairSniperClientState() {}

    public static final int MIN_ZOOM_LEVEL = 1;
    public static final int MAX_ZOOM_LEVEL = 10;
    private static final int DEFAULT_ZOOM_LEVEL = 4;

    private static boolean aiming   = false;
    private static boolean crawling = false;   // ← NUOVO: stato crawl
    private static int zoomLevel    = DEFAULT_ZOOM_LEVEL;
    private static long lastFireTick = -1000;

    public static float yaw          = 0f;
    public static float roll         = 0f;
    public static float maxRoll      = 45.0f;
    public static float targetRoll   = 0f;
    public static float xOffset      = 0.0f;
    public static float maxXOffset   = 0.5f;
    public static float targetXOffset = 0.0f;

    // ── Aiming ───────────────────────────────────────────────────────────────
    public static boolean isAiming()             { return aiming; }
    public static void    setAiming(boolean v)   { aiming = v; }
    public static void    toggleAiming()         { aiming = !aiming; }

    // ── Crawl ─────────────────────────────────────────────────────────────────
    public static boolean isCrawling()           { return crawling; }
    public static void    setCrawling(boolean v) { crawling = v; }

    // ── Zoom ──────────────────────────────────────────────────────────────────
    public static int  getZoomLevel()            { return zoomLevel; }
    public static void adjustZoom(int delta) {
        zoomLevel = Math.clamp(zoomLevel + delta, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
    }

    /** Usato dal Mixin del FOV: 1.0 = nessuno zoom, valori più alti = più ingrandimento. */
    public static float getZoomDivisor() {
        return aiming ? (float) zoomLevel : 1.0f;
    }

    // ── Cooldown fuoco ────────────────────────────────────────────────────────
    public static boolean canFire(long currentTick, int cooldownTicks) {
        return currentTick - lastFireTick >= cooldownTicks;
    }
    public static void markFired(long currentTick) { lastFireTick = currentTick; }

    // ── Getters camera ────────────────────────────────────────────────────────
    public static float getYaw()     { return yaw; }
    public static float getRoll()    { return roll; }
    public static float getXOffset() { return xOffset; }

    /**
     * Gestisce il lean/roll della camera tramite le frecce ← →.
     * Non richiede più Shift: le frecce sono input dedicati allo sniper.
     * <p>
     * Chiamato ogni tick client in BuganairModClient quando si tiene lo sniper.
     *
     * @param leftArrow  freccia sinistra premuta (lean a sinistra)
     * @param rightArrow freccia destra  premuta (lean a destra)
     */
    public static void handleKeyboardInput(boolean leftArrow, boolean rightArrow) {
        if (leftArrow) {
            targetRoll    =  maxRoll;
            targetXOffset = -maxXOffset;
        } else if (rightArrow) {
            targetRoll    = -maxRoll;
            targetXOffset =  maxXOffset;
        } else {
            targetXOffset = 0.0f;
            targetRoll    = 0.0f;
        }

        // Interpolazione fluida verso il target
        xOffset = MathHelper.lerp(0.25f, xOffset, targetXOffset);
        roll    = MathHelper.lerp(0.25f, roll,    targetRoll);

        // Snap a 0 per evitare jitter floating-point
        if (Math.abs(xOffset) < 0.1f) xOffset = 0.0f;
        if (Math.abs(xOffset) > Math.abs(maxXOffset) - 0.1f)
            xOffset = Math.signum(xOffset) * Math.abs(maxXOffset);

        if (Math.abs(roll) < 0.1f) roll = 0f;
        if (Math.abs(roll) > Math.abs(maxRoll) - 0.1f)
            roll = Math.signum(roll) * Math.abs(maxRoll);
    }
}