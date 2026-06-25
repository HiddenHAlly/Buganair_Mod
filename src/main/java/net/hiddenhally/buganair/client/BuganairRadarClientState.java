package net.hiddenhally.buganair.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
import java.util.concurrent.CompletableFuture;

public class BuganairRadarClientState {
    private static final TagKey<net.minecraft.block.Block> ORE_TAG = TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "ores"));

    private static List<BlockPos> foundOres = new ArrayList<>();
    private static BlockPos radarCenter = null;
    private static long radarStartTime = 0;
    private static boolean isActive = false;

    public static void startRadar(BlockPos center) {
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

                if (state.isIn(ORE_TAG)) {
//                    if (state.equals(Blocks.COAL_ORE.getDefaultState())) {
//                        Buganair.LOGGER.info("Coal Ore");
//                    }
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
            int outlineColor = BuganairConfig.INSTANCE.outlineColor;
            float oA = ((outlineColor >> 24) & 0xFF) / 255f;
            float oR = ((outlineColor >> 16) & 0xFF) / 255f;
            float oG = ((outlineColor >> 8) & 0xFF) / 255f;
            float oB = (outlineColor & 0xFF) / 255f;

            int bubbleColor = BuganairConfig.INSTANCE.bubbleColor;
            float bA = ((bubbleColor >> 24) & 0xFF) / 255f;
            float bR = ((bubbleColor >> 16) & 0xFF) / 255f;
            float bG = ((bubbleColor >> 8) & 0xFF) / 255f;
            float bB = (bubbleColor & 0xFF) / 255f;

            VertexConsumer lineBuffer = vertexConsumers.getBuffer(RenderLayers.lines());
            Matrix4f matrix = context.matrices().peek().getPositionMatrix();
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.world != null;

            // 1. Draw Ore Outlines
            for (BlockPos pos : foundOres) {
                outlineColor = BuganairConfig.INSTANCE.outlineColor;

                BlockState state = client.world.getBlockState(pos);
                outlineColor = getBlockColor(state,outlineColor);


                double x = pos.getX() - camPos.x;
                double y = pos.getY() - camPos.y;
                double z = pos.getZ() - camPos.z;

                //drawBoxLines(matrix, lineBuffer, x, y, z, x + 1.0, y + 1.0, z + 1.0, oR, oG, oB, oA);
                VertexConsumer outlineBuffer = vertexConsumers.getBuffer(RenderLayer.of(
                        "lines_always",
                        RenderSetup.builder(RenderPipelines.LINES)
                                .layeringTransform(LayeringTransform.NO_LAYERING)
                                .outputTarget(OutputTarget.OUTLINE_TARGET)//.useOverlay()
                                //.depthTest(DepthTest.ALWAYS) // Tells the pipeline to draw regardless of blocks
                                .build()
                ));
                VertexRendering.drawOutline(
                        context.matrices(),
                        outlineBuffer,
                        VoxelShapes.fullCube(),
                        x,
                        y,
                        z,
                        outlineColor-BuganairConfig.INSTANCE.outlineColorAlphaRemoval,
                        1.0f
                );
                //RenderLayers.debugFilledBox().
            }

            // 2. Draw Expanding Cube Bubble Effect
            float expandTime = 2000f;
            float progress = Math.min(1.0f, elapsed / expandTime);
            double currentRadius = BuganairConfig.INSTANCE.radarRadius * Math.pow(progress, 0.5);

            if (progress < 1.0f) {
                double cx = radarCenter.getX() + 0.5 - camPos.x;
                double cy = radarCenter.getY() + 0.5 - camPos.y;
                double cz = radarCenter.getZ() + 0.5 - camPos.z;

                double r = currentRadius;
                //double r = 2;
                //float alpha = 1.0f;

                VertexConsumer fillBuffer =
                        vertexConsumers.getBuffer(RenderLayers.debugFilledBox());

                drawFilledCube(
                        context.matrices().peek().getPositionMatrix(),
                        fillBuffer,
                        (float)(cx - r),
                        (float)(cy - r),
                        (float)(cz - r),
                        (float)(cx + r),
                        (float)(cy + r),
                        (float)(cz + r),
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

                VertexConsumer outlineBuffer =
                        vertexConsumers.getBuffer(RenderLayers.lines());

                VertexRendering.drawOutline(
                        context.matrices(),
                        outlineBuffer,
                        VoxelShapes.cuboid(
                                -r, -r, -r,
                                r,  r,  r
                        ),
                        cx,
                        cy,
                        cz,
                        bubbleColor,
                        2.0f
                );
                //drawBoxLines(matrix, lineBuffer, cx - r, cy - r, cz - r, cx + r, cy + r, cz + r, bR, bG, bB, bA * (1.0f - progress));
            }

            vertexConsumers.draw();
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

    private static void drawFilledCube(
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

    // Helper method to draw a simple bounding box using line primitives
    private static void drawBoxLines(Matrix4f matrix, VertexConsumer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        float x1 = (float) minX, y1 = (float) minY, z1 = (float) minZ;
        float x2 = (float) maxX, y2 = (float) maxY, z2 = (float) maxZ;

        // Bottom face lines
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(1, 0, 0); buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(1, 0, 0);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(0, 0, 1); buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(0, 0, 1);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(-1, 0, 0); buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(-1, 0, 0);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(0, 0, -1); buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 0, -1);

        // Top face lines
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(1, 0, 0); buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(1, 0, 0);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(0, 0, 1); buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 0, 1);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(-1, 0, 0); buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(-1, 0, 0);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(0, 0, -1); buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(0, 0, -1);

        // Vertical corner lines
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 1, 0); buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(0, 1, 0);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(0, 1, 0); buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(0, 1, 0);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(0, 1, 0); buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 1, 0);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(0, 1, 0); buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(0, 1, 0);
    }
}