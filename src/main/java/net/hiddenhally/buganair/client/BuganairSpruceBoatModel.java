package net.hiddenhally.buganair.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class BuganairSpruceBoatModel extends EntityModel<BoatEntityRenderState> {
    private final ModelPart root;

    public BuganairSpruceBoatModel(ModelPart root) {
        super(root);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // --- CONVERTED BASE BLOCKS & ORGANIZED HIERARCHY ---
        ModelPartData base = root.addChild("base", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));
        ModelPartData group7 = base.addChild("group7", ModelPartBuilder.create(), ModelTransform.origin(7.0F, 7.0F, 14.0F));
        ModelPartData group = group7.addChild("group", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        // Main geometric detailed blocks
        ModelPartData group12 = group.addChild("group12", ModelPartBuilder.create()
                        .uv(48, 77).cuboid(-8.0F, -23.0F, -12.0F, 2.0F, 23.0F, 2.0F)
                        .uv(70, 80).cuboid(-6.0F, -13.0F, -11.0F, 8.0F, 1.0F, 1.0F)
                        .uv(78, 91).cuboid(-1.0F, -14.0F, -12.0F, 2.0F, 1.0F, 1.0F)
                        .uv(56, 77).cuboid(-14.0F, -16.0F, -13.0F, 14.0F, 2.0F, 1.0F)
                        .uv(48, 74).cuboid(-14.0F, -18.0F, -14.0F, 14.0F, 2.0F, 1.0F)
                        .uv(78, 74).cuboid(-14.0F, -20.0F, -13.0F, 14.0F, 2.0F, 1.0F)
                        .uv(78, 93).cuboid(-1.0F, -21.0F, -12.0F, 2.0F, 1.0F, 1.0F)
                        .uv(70, 82).cuboid(-6.0F, -22.0F, -11.0F, 8.0F, 1.0F, 1.0F)
                        .uv(70, 84).cuboid(-16.0F, -13.0F, -11.0F, 8.0F, 1.0F, 1.0F)
                        .uv(32, 104).cuboid(-15.0F, -14.0F, -12.0F, 2.0F, 1.0F, 1.0F)
                        .uv(104, 32).cuboid(-15.0F, -21.0F, -12.0F, 2.0F, 1.0F, 1.0F)
                        .uv(70, 86).cuboid(-16.0F, -22.0F, -11.0F, 8.0F, 1.0F, 1.0F)
                        .uv(16, 100).cuboid(-8.0F, -2.0F, -32.0F, 2.0F, 1.0F, 2.0F)
                        .uv(86, 77).cuboid(-10.0F, -2.0F, -30.0F, 6.0F, 1.0F, 2.0F)
                        .uv(24, 100).cuboid(-8.0F, -1.0F, -30.0F, 2.0F, 1.0F, 2.0F)
                        .uv(16, 97).cuboid(-12.0F, -2.0F, -28.0F, 3.0F, 1.0F, 2.0F)
                        .uv(96, 70).cuboid(-9.0F, -1.0F, -28.0F, 4.0F, 1.0F, 2.0F)
                        .uv(96, 100).cuboid(-8.0F, 0.0F, -28.0F, 2.0F, 1.0F, 2.0F)
                        .uv(26, 97).cuboid(-5.0F, -2.0F, -28.0F, 3.0F, 1.0F, 2.0F)
                        .uv(66, 101).cuboid(-5.0F, -1.0F, -26.0F, 2.0F, 1.0F, 2.0F)
                        .uv(86, 101).cuboid(-3.0F, -2.0F, -26.0F, 2.0F, 1.0F, 2.0F)
                        .uv(86, 96).cuboid(-9.0F, 0.0F, -26.0F, 4.0F, 1.0F, 2.0F)
                        .uv(102, 77).cuboid(-11.0F, -1.0F, -26.0F, 2.0F, 1.0F, 2.0F)
                        .uv(94, 103).cuboid(-13.0F, -2.0F, -26.0F, 2.0F, 1.0F, 2.0F)
                        .uv(102, 103).cuboid(-14.0F, -2.0F, 0.0F, 3.0F, 1.0F, 1.0F)
                        .uv(98, 96).cuboid(-12.0F, -2.0F, 1.0F, 4.0F, 1.0F, 1.0F)
                        .uv(98, 98).cuboid(-9.0F, -2.0F, 2.0F, 4.0F, 1.0F, 1.0F)
                        .uv(66, 99).cuboid(-9.0F, -1.0F, 1.0F, 4.0F, 1.0F, 1.0F)
                        .uv(104, 34).cuboid(-8.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F)
                        .uv(0, 104).cuboid(-11.0F, -1.0F, 0.0F, 3.0F, 1.0F, 1.0F)
                        .uv(8, 104).cuboid(-6.0F, -1.0F, 0.0F, 3.0F, 1.0F, 1.0F)
                        .uv(86, 99).cuboid(-6.0F, -2.0F, 1.0F, 4.0F, 1.0F, 1.0F)
                        .uv(104, 30).cuboid(-3.0F, -2.0F, 0.0F, 3.0F, 1.0F, 1.0F),
                ModelTransform.origin(0.0F, 0.0F, 0.0F));

        // Rotated Subcomponents
        group12.addChild("cube_r1", ModelPartBuilder.create().uv(0, 74).cuboid(6.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-5.0F, 2.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r2", ModelPartBuilder.create().uv(60, 0).cuboid(6.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F).uv(0, 51).cuboid(-8.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-7.0F, 1.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r3", ModelPartBuilder.create().uv(24, 103).cuboid(5.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F).uv(48, 102).cuboid(-7.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-7.0F, -1.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r4", ModelPartBuilder.create().uv(16, 103).cuboid(5.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-5.0F, 0.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r5", ModelPartBuilder.create().uv(68, 88).cuboid(-7.0F, -1.0F, -2.0F, 6.0F, 1.0F, 2.0F), ModelTransform.of(-11.0F, 1.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -3.0F, -14.0F, 8.0F, 1.0F, 22.0F), ModelTransform.of(-11.0F, 3.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r7", ModelPartBuilder.create().uv(48, 51).cuboid(-8.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-9.0F, 2.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
        group12.addChild("cube_r8", ModelPartBuilder.create().uv(56, 102).cuboid(-7.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-9.0F, 0.0F, -24.0F, 0.0F, 3.1416F, 0.0F));

        base.addChild("group8", ModelPartBuilder.create(), ModelTransform.origin(7.0F, 7.0F, 14.0F));
        base.addChild("group9", ModelPartBuilder.create(), ModelTransform.origin(7.0F, 7.0F, 14.0F));

        root.addChild("group2", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));
        root.addChild("group3", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));
        root.addChild("group4", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));

        ModelPartData group6 = root.addChild("group6", ModelPartBuilder.create(), ModelTransform.origin(2.0F, 24.0F, 14.0F));
        ModelPartData group11 = group6.addChild("group11", ModelPartBuilder.create()
                        .uv(56, 87).cuboid(-4.0F, -9.0F, -37.0F, 4.0F, 6.0F, 2.0F)
                        .uv(36, 97).cuboid(-3.0F, -9.0F, -38.0F, 2.0F, 6.0F, 1.0F)
                        .uv(68, 91).cuboid(-1.0F, -9.0F, -35.0F, 3.0F, 6.0F, 2.0F)
                        .uv(94, 88).cuboid(1.0F, -9.0F, -33.0F, 3.0F, 6.0F, 2.0F)
                        .uv(96, 54).cuboid(3.0F, -9.0F, -31.0F, 2.0F, 6.0F, 2.0F)
                        .uv(96, 62).cuboid(4.0F, -9.0F, -29.0F, 2.0F, 6.0F, 2.0F)
                        .uv(78, 96).cuboid(5.0F, -9.0F, -27.0F, 2.0F, 6.0F, 2.0F)
                        .uv(42, 97).cuboid(5.0F, -9.0F, -3.0F, 2.0F, 6.0F, 1.0F)
                        .uv(0, 97).cuboid(3.0F, -9.0F, -2.0F, 3.0F, 6.0F, 1.0F)
                        .uv(56, 95).cuboid(0.0F, -9.0F, -1.0F, 4.0F, 6.0F, 1.0F),
                ModelTransform.origin(0.0F, 0.0F, 3.0F));

        group11.addChild("cube_r9", ModelPartBuilder.create().uv(96, 23).cuboid(4.0F, -9.0F, 0.0F, 4.0F, 6.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r10", ModelPartBuilder.create().uv(8, 97).cuboid(5.0F, -9.0F, 0.0F, 3.0F, 6.0F, 1.0F), ModelTransform.of(-2.0F, 0.0F, -1.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r11", ModelPartBuilder.create().uv(98, 80).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 1.0F), ModelTransform.of(-2.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r12", ModelPartBuilder.create().uv(56, 80).cuboid(2.0F, -9.0F, 0.0F, 6.0F, 6.0F, 1.0F), ModelTransform.of(3.0F, 0.0F, 1.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r13", ModelPartBuilder.create().uv(48, 23).cuboid(-10.0F, -9.0F, -14.0F, 2.0F, 6.0F, 22.0F).uv(0, 23).cuboid(8.0F, -9.0F, -14.0F, 2.0F, 6.0F, 22.0F), ModelTransform.of(-2.0F, 0.0F, -17.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r14", ModelPartBuilder.create().uv(84, 88).cuboid(6.0F, -9.0F, -1.0F, 3.0F, 6.0F, 2.0F), ModelTransform.of(3.0F, 0.0F, -34.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r15", ModelPartBuilder.create().uv(88, 80).cuboid(6.0F, -9.0F, -1.0F, 3.0F, 6.0F, 2.0F), ModelTransform.of(1.0F, 0.0F, -32.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r16", ModelPartBuilder.create().uv(96, 46).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(-1.0F, 0.0F, -28.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r17", ModelPartBuilder.create().uv(96, 38).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, -30.0F, 0.0F, 3.1416F, 0.0F));
        group11.addChild("cube_r18", ModelPartBuilder.create().uv(96, 30).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(-2.0F, 0.0F, -26.0F, 0.0F, 3.1416F, 0.0F));

        root.addChild("group5", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, -9.0F));
        root.addChild("group10", ModelPartBuilder.create(), ModelTransform.origin(8.0F, 24.0F, -8.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }
}