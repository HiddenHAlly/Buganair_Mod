package net.hiddenhally.buganair.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.network.BuganairOreRadarPayload;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gl.RenderPipelines.*;

public class BuganairRadarClientState {
    private static final TagKey<net.minecraft.block.Block> ORE_TAG = TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "ores"));

    private static List<BlockPos> foundOres = new ArrayList<>();
    private static BlockPos radarCenter = null;
    private static long radarStartTime = 0;
    private static boolean isActive = false;

    // 1. Grab the pipeline from the ShaderManager using your Identifier
    private static final RenderPipeline outlineShaderManager = RenderPipelines.register(RenderPipeline.builder(RenderPipeline.builder(TRANSFORMS_PROJECTION_FOG_SNIPPET, GLOBALS_SNIPPET)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(true)
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.DEBUG_LINES)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .buildSnippet()).withLocation("pipeline/lines_always").build());

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

    public static void startRadar(BlockPos center) {
        // THE FIX: Send the new state to the server to update the scoreboard
        ClientPlayNetworking.send(new BuganairOreRadarPayload(true));
        radarCenter = center;
        radarStartTime = System.currentTimeMillis();
        isActive = true;
        foundOres.clear();

        int radius = BuganairConfig.INSTANCE.radarRadius;
        MinecraftClient client = MinecraftClient.getInstance();

        List<BlockPos> tempOres = new ArrayList<>();

        if (client.world == null) return;

        Iterable<BlockPos> blocks = BlockPos.iterate(
                center.add(-radius, -radius, -radius),
                center.add(radius, radius, radius)
        );

        for (BlockPos pos : blocks) {
            if (pos.getSquaredDistance(center) <= radius * radius) {
                BlockState state = client.world.getBlockState(pos);

                if (state.isIn(ORE_TAG) || state.toString().contains("ore")) {
                    tempOres.add(pos.toImmutable());
                }
            }
        }

        foundOres = tempOres;
    }

    public static void registerRenderer() {
        WorldRenderEvents.END_MAIN.register(context -> {
            if (!isActive || radarCenter == null) return;

            long elapsed = System.currentTimeMillis() - radarStartTime;
            long durationMs = BuganairConfig.INSTANCE.radarDurationSeconds * 1000L;

            if (elapsed > durationMs) {
                isActive = false;
                foundOres.clear();
                return;
            }

            Camera camera = context.gameRenderer().getCamera();
            Vec3d camPos = camera.getCameraPos();
            VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            // Extract Colors

            int bubbleColor = BuganairConfig.INSTANCE.bubbleColor;
            float bA = ((bubbleColor >> 24) & 0xFF) / 255f;
            float bR = ((bubbleColor >> 16) & 0xFF) / 255f;
            float bG = ((bubbleColor >> 8) & 0xFF) / 255f;
            float bB = (bubbleColor & 0xFF) / 255f;

            MinecraftClient client = MinecraftClient.getInstance();
            assert client.world != null;

            // 1. Draw Ore Outlines
            for (BlockPos pos : foundOres) {
                //outlineColor = BuganairConfig.INSTANCE.outlineColor;

                BlockState state = client.world.getBlockState(pos);
                //outlineColor = getBlockColor(state,BuganairConfig.INSTANCE.outlineColor);


                double x = pos.getX() - camPos.x;
                double y = pos.getY() - camPos.y;
                double z = pos.getZ() - camPos.z;

                //drawBoxLines(matrix, lineBuffer, x, y, z, x + 1.0, y + 1.0, z + 1.0, oR, oG, oB, oA);
//                VertexConsumer outlineBuffer = vertexConsumers.getBuffer(RenderLayer.of(
//                        "lines_always",
//                        RenderSetup.builder(RenderPipelines.LINES)
//                                .layeringTransform(LayeringTransform.NO_LAYERING)
//                                .outputTarget(OutputTarget.OUTLINE_TARGET)//.useOverlay()
//                                //.depthTest(DepthTest.ALWAYS) // Tells the pipeline to draw regardless of blocks
//                                .build()
//                ));


                //var customPipeline = shaderManager;//Identifier.of("buganair", "lines_always"));

                // 2. Feed the compiled pipeline into the RenderSetup builder
                VertexConsumer outlineBuffer = vertexConsumers.getBuffer(RenderLayer.of(
                        "lines_always",
                        RenderSetup.builder(outlineShaderManager)
                                .outputTarget(OutputTarget.MAIN_TARGET) // Back to main target, no glowing artifacts!
                                .layeringTransform(LayeringTransform.NO_LAYERING)
                                .build()
                ));

                VertexRendering.drawOutline(
                        context.matrices(),
                        outlineBuffer,
                        VoxelShapes.fullCube(),
                        x,
                        y,
                        z,
                        getBlockColor(state,BuganairConfig.INSTANCE.outlineColor)-BuganairConfig.INSTANCE.outlineColorAlphaRemoval,
                        BuganairConfig.INSTANCE.outlineSize
                );
                //RenderLayers.debugFilledBox().
            }

            // 2. Draw Expanding Cube Bubble Effect
            float expandTime = BuganairConfig.INSTANCE.radarExpandTime;
            float progress = Math.min(1.0f, elapsed / expandTime);
            double currentRadius = (BuganairConfig.INSTANCE.radarRadius*5.0/4.0)* Math.pow(progress, 0.5)-(BuganairConfig.INSTANCE.radarRadius*1.0/4.0);

            if (progress < 1.0f && currentRadius >= 0.0) {
                //Buganair.LOGGER.info("{}",currentRadius);
                double cx = radarCenter.getX() + 0.5 - camPos.x;
                double cy = radarCenter.getY() + 0.5 - camPos.y;
                double cz = radarCenter.getZ() + 0.5 - camPos.z;

                //double r = currentRadius;
                //double r = 2;
                //float alpha = 1.0f;

//                VertexConsumer fillBuffer =
//                        vertexConsumers.getBuffer(RenderLayer.of(
//                                "debug_filled_box", RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX)
//                                        .translucent()
//                                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
//                                        .outputTarget(OutputTarget.OUTLINE_TARGET)
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
//                        .buildSnippet()).withLocation("pipeline/lines_always").build());;
                //var customPipeline = shaderManager;//Identifier.of("buganair", "lines_always"));

                // 2. Feed the compiled pipeline into the RenderSetup builder
                VertexConsumer fillBuffer = vertexConsumers.getBuffer(RenderLayer.of(
                        "lines_always",
                        RenderSetup.builder(boxShaderManager)
                                .outputTarget(OutputTarget.MAIN_TARGET) // Back to main target, no glowing artifacts!
                                .layeringTransform(LayeringTransform.NO_LAYERING)
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

//                VertexConsumer outlineBuffer =
//                        vertexConsumers.getBuffer(RenderLayer.of(
//                                "lines_always",
//                                RenderSetup.builder(RenderPipelines.LINES_TRANSLUCENT)
//                                        .layeringTransform(LayeringTransform.NO_LAYERING)
//                                        .outputTarget(OutputTarget.OUTLINE_TARGET)//.useOverlay()
//                                        //.depthTest(DepthTest.ALWAYS) // Tells the pipeline to draw regardless of blocks
//                                        .build()
//                        ));


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
                int baseColorRGB = bubbleColor & 0x00FFFFFF;
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
                        BuganairConfig.INSTANCE.radarOutlineSize
                );
                //drawBoxLines(matrix, lineBuffer, cx - r, cy - r, cz - r, cx + r, cy + r, cz + r, bR, bG, bB, bA * (1.0f - progress));
            }

            vertexConsumers.draw();

            if (progress>=1) {
                ClientPlayNetworking.send(new BuganairOreRadarPayload(false));
            }
        });
    }

    public static int getBlockColor(
            BlockState state,
            int outlineColor
    ) {
        if (state.isIn(ORE_TAG)) {
            if (state.equals(Blocks.COAL_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_COAL_ORE.getDefaultState())) {
                //Buganair.LOGGER.info("Coal Ore");
                outlineColor = BuganairConfig.INSTANCE.outlineColorCoal;
            } else if (state.equals(Blocks.COPPER_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_COPPER_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorCopper;
            } else if (state.equals(Blocks.IRON_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_IRON_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorIron;
            } else if (state.equals(Blocks.GOLD_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_GOLD_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorGold;
            } else if (state.equals(Blocks.REDSTONE_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorRedstone;
            } else if (state.equals(Blocks.LAPIS_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorLapis;
            } else if (state.equals(Blocks.EMERALD_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorEmerald;
            } else if (state.equals(Blocks.DIAMOND_ORE.getDefaultState()) || state.equals(Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorDiamond;
            } else if (state.equals(Blocks.NETHER_GOLD_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorNetherGold;
            } else if (state.equals(Blocks.NETHER_QUARTZ_ORE.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorNetherQuartz;
            } else if (state.equals(Blocks.ANCIENT_DEBRIS.getDefaultState())) {
                outlineColor = BuganairConfig.INSTANCE.outlineColorNetherAncientDebris;
            }
            //tempOres.add(pos.toImmutable());
        }
        return outlineColor;
    }

    public static void drawFilledCube(
            Matrix4f matrix,
            VertexConsumer buffer,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            float r,
            float g,
            float b,
            float a
    ) {
        // ==========================================
        // OUTSIDE FACES (Counter-Clockwise)
        // ==========================================

        // Front
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);

        // Back
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);

        // Left
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);

        // Right
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);

        // Top
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);

        // Bottom
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);

        // ==========================================
        // INSIDE FACES (Clockwise / Reversed Winding)
        // ==========================================

        // Front Inside
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);

        // Back Inside
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);

        // Left Inside
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);

        // Right Inside
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);

        // Top Inside
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b,a);

        // Bottom Inside
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b,a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b,a);
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b,a);
    }
}