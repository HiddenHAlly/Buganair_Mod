package net.hiddenhally.buganair.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.network.BuganairOreRadarPayload;
import net.hiddenhally.buganair.network.BuganairScoutingFlarePayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Matrix4f;

import static net.hiddenhally.buganair.client.BuganairRadarClientState.drawFilledCube;
import static net.minecraft.client.gl.RenderPipelines.*;

public class BuganairScoutingFlareClientState {

    private static BlockPos radarCenter = null;
    private static long entityRadarStartTimer;
    private static boolean isActive = false;
    private static boolean enemy = false;
    private static int outlineColor;
    private static int bubbleColor;

    // 1. Grab the pipeline from the ShaderManager using your Identifier
    private static final RenderPipeline boxShaderManager = RenderPipelines.register(
            RenderPipeline.builder(RenderPipeline.builder(TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthWrite(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .buildSnippet()).withLocation("pipeline/debug_filled_box").build());

    // 1. Grab the pipeline from the ShaderManager using your Identifier
    private static final RenderPipeline outlineBoxShaderManager = RenderPipelines.register(RenderPipeline.builder(RenderPipeline.builder(TRANSFORMS_PROJECTION_FOG_SNIPPET, GLOBALS_SNIPPET)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.DEBUG_LINES)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .buildSnippet()).withLocation("pipeline/lines_always").build());

    public static void startRadar(BlockPos center, boolean enemy) {
        // THE FIX: Send the new state to the server to update the scoreboard
        ClientPlayNetworking.send(new BuganairScoutingFlarePayload(true));
        radarCenter = center;
        entityRadarStartTimer = System.currentTimeMillis();
        isActive = true;
        radarCenter = center;
        outlineColor = enemy ? BuganairConfig.INSTANCE.entityEnemyOutlineColor : BuganairConfig.INSTANCE.entityOutlineColor;
        bubbleColor = enemy ? BuganairConfig.INSTANCE.entityEnemyBubbleColor : BuganairConfig.INSTANCE.entityBubbleColor;
    }

    public static void registerRenderer() {
        WorldRenderEvents.END_MAIN.register(context -> {
            if (!isActive || radarCenter == null) return;

            long elapsed = System.currentTimeMillis() - entityRadarStartTimer;
            long durationMs = BuganairConfig.INSTANCE.entityRadarDurationSeconds * 1000L;

            if (elapsed > durationMs) {
                isActive = false;
                return;
            }

            Camera camera = context.gameRenderer().getCamera();
            Vec3d camPos = camera.getCameraPos();
            VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            // Extract Colors

            float oA = ((outlineColor >> 24) & 0xFF) / 255f;
            float oR = ((outlineColor >> 16) & 0xFF) / 255f;
            float oG = ((outlineColor >> 8) & 0xFF) / 255f;
            float oB = (outlineColor & 0xFF) / 255f;


            float bA = ((bubbleColor >> 24) & 0xFF) / 255f;
            float bR = ((bubbleColor >> 16) & 0xFF) / 255f;
            float bG = ((bubbleColor >> 8) & 0xFF) / 255f;
            float bB = (bubbleColor & 0xFF) / 255f;

            VertexConsumer lineBuffer = vertexConsumers.getBuffer(RenderLayers.lines());
            Matrix4f matrix = context.matrices().peek().getPositionMatrix();
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.world != null;

            // 2. Draw Expanding Cube Bubble Effect
            float expandTime = BuganairConfig.INSTANCE.entityRadarExpandTime;
            float progress = Math.min(1.0f, elapsed / expandTime);
            double currentRadius = (BuganairConfig.INSTANCE.entityRadarRadius*5.0/4.0)* Math.pow(progress, 0.5)-(BuganairConfig.INSTANCE.entityRadarRadius*1.0/4.0);

            if (progress < 1.0f && currentRadius >= 0.0) {
                //Buganair.LOGGER.info("{}",currentRadius);
                double cx = radarCenter.getX() + 0.5 - camPos.x;
                double cy = radarCenter.getY() + 0.5 - camPos.y;
                double cz = radarCenter.getZ() + 0.5 - camPos.z;

                //double r = currentRadius;
                //double r = 2;
                //float alpha = 1.0f;

                VertexConsumer fillBuffer =
                        vertexConsumers.getBuffer(RenderLayer.of(
                                "debug_filled_box", RenderSetup.builder(boxShaderManager)
                                        .translucent()
                                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                                        .outputTarget(OutputTarget.MAIN_TARGET)
                                        .build()
                        ));

                drawFilledCube(
                        context.matrices().peek().getPositionMatrix(),
                        fillBuffer,
                        (float)(cx - currentRadius),
                        (float)(cy - currentRadius),
                        (float)(cz - currentRadius),
                        (float)(cx + currentRadius),
                        (float)(cy + currentRadius),
                        (float)(cz + currentRadius),
                        bR,
                        bG,
                        bB,
                        bA * (1.0f - progress)
                );

//                VertexRendering.drawOutline(
//                        context.matrices(),
//                        lineBuffer,
//                        VoxelShapes.cuboid(
//                                -r, -r, -r,
//                                r,  r,  r
//                        ),
//                        cx,
//                        cy,
//                        cz,
//                        bubbleColor,
//                        2.0f
//                );

                // 1. Flush any deferred rendering up to this point so your lines don't get batched incorrectly
                vertexConsumers.draw();

//                // 2. Manually override the rendering states
//                RenderSystem.disableDepthTest();
//                RenderSystem.enableBlend();
//                RenderSystem.defaultBlendFunc();
//                RenderSystem.setShader(GameRenderer::getPositionColorNormalProgram);

                // 3. Set up the Tessellator for immediate drawing
                // (VertexRendering.drawOutline requires POSITION_COLOR_NORMAL)
                // Tessellator tessellator = Tessellator.getInstance();
                // BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);

//                VertexConsumer outlineBuffer =
//                        vertexConsumers.getBuffer(RenderLayer.of(
//                                "lines_always",
//                                RenderSetup.builder(RenderPipelines.LINES_TRANSLUCENT)
//                                        //.layeringTransform(LayeringTransform.NO_LAYERING)
//                                        .outputTarget(OutputTarget.OUTLINE_TARGET)//.useOverlay()
//                                        //.depthTest(DepthTest.ALWAYS) // Tells the pipeline to draw regardless of blocks
//                                        .build()
//                        ));

                // 1. Grab the pipeline from the ShaderManager using your Identifier
//                var shaderManager = RenderPipelines.register(RenderPipeline.builder(RenderPipeline.builder(TRANSFORMS_PROJECTION_FOG_SNIPPET, GLOBALS_SNIPPET)
//                        .withVertexShader("core/rendertype_lines")
//                        .withFragmentShader("core/rendertype_lines")
//                        .withBlend(BlendFunction.TRANSLUCENT)
//                        .withCull(false)
//                        .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.DEBUG_LINES)
//                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
//                        .buildSnippet()).withLocation("pipeline/lines_always").build());
                //var customPipeline = shaderManager;//Identifier.of("buganair", "lines_always"));

                // 2. Feed the compiled pipeline into the RenderSetup builder
                VertexConsumer outlineBuffer = vertexConsumers.getBuffer(RenderLayer.of(
                        "lines_always",
                        RenderSetup.builder(outlineBoxShaderManager)
                                .outputTarget(OutputTarget.MAIN_TARGET) // Back to main target, no glowing artifacts!
                                .layeringTransform(LayeringTransform.NO_LAYERING)
                                .build()
                ));

                // Convert back to an integer scale (0 - 255)
                int alphaInt = (int) (255-progress * 255);

                // Strip the original alpha from outlineColor and apply the new one
                int baseColorRGB = outlineColor & 0x00FFFFFF;
                int dynamicColor = (alphaInt << 24) | baseColorRGB;

                VertexRendering.drawOutline(
                        context.matrices(),
                        outlineBuffer,
                        VoxelShapes.cuboid(
                                -currentRadius, -currentRadius, -currentRadius,
                                currentRadius,  currentRadius,  currentRadius
                        ),
                        cx,
                        cy,
                        cz,
                        dynamicColor,
                        BuganairConfig.INSTANCE.entityRadarOutlineSize
                );
                //drawBoxLines(matrix, lineBuffer, cx - r, cy - r, cz - r, cx + r, cy + r, cz + r, bR, bG, bB, bA * (1.0f - progress));
            }

            // 4. Draw immediately to the screen using the global program.drawWithGlobalProgram(bufferBuilder.end());

// 5.       CRITICAL: Re-enable depth testing immediately so you don't break the rest of Minecraft's rendering!
            //RenderSystem.enableDepthTest();

            vertexConsumers.draw();

            if (progress>=1) {
                ClientPlayNetworking.send(new BuganairScoutingFlarePayload(false));
            }
        });
    }
}
