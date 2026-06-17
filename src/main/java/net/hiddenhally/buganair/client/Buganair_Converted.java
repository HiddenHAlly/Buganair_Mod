package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

public class Buganair_Converted extends EntityModel<BoatEntityRenderState> {

	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
			Identifier.of(Buganair.MOD_ID, "buganair_converted"), "main"
	);

	private final ModelPart base;
	private final ModelPart group7;
	private final ModelPart group;
	private final ModelPart group12;
	private final ModelPart group8;
	private final ModelPart group9;
	private final ModelPart group2;
	private final ModelPart group3;
	private final ModelPart group4;
	private final ModelPart group6;
	private final ModelPart group11;
	private final ModelPart group5;
	private final ModelPart group10;

	public Buganair_Converted(ModelPart root) {
		super(root); // Automatically renders the entire hierarchy starting from root!
		this.base = root.getChild("base");
		this.group7 = this.base.getChild("group7");
		this.group = this.group7.getChild("group");
		this.group12 = this.group.getChild("group12");
		this.group8 = this.base.getChild("group8");
		this.group9 = this.base.getChild("group9");
		this.group2 = root.getChild("group2");
		this.group3 = root.getChild("group3");
		this.group4 = root.getChild("group4");
		this.group6 = root.getChild("group6");
		this.group11 = this.group6.getChild("group11");
		this.group5 = root.getChild("group5");
		this.group10 = root.getChild("group10");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		// Base & its nested hierarchy
		ModelPartData baseData = modelPartData.addChild("base",
				ModelPartBuilder.create(),
				ModelTransform.origin(0.0F, 16.0F, 0.0F)
		);

		ModelPartData group7Data = baseData.addChild("group7",
				ModelPartBuilder.create(),
				ModelTransform.origin(7.0F, 7.0F, 14.0F)
		);

		ModelPartData groupData = group7Data.addChild("group",
				ModelPartBuilder.create(),
				ModelTransform.origin(0.0F, 0.0F, 0.0F)
		);

		// Group 12 with all its cubes mapped accurately to UV positions
		ModelPartData group12Data = groupData.addChild("group12", ModelPartBuilder.create()
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
				ModelTransform.origin(0.0F, 0.0F, 0.0F)
		);

		// Rotated child parts of group12 (using ModelTransform.of for angles)
		group12Data.addChild("cube_r1", ModelPartBuilder.create().uv(0, 74).cuboid(6.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-5.0F, 2.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r2", ModelPartBuilder.create().uv(60, 0).cuboid(6.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F).uv(0, 51).cuboid(-8.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-7.0F, 1.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r3", ModelPartBuilder.create().uv(24, 103).cuboid(5.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F).uv(48, 102).cuboid(-7.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-7.0F, -1.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r4", ModelPartBuilder.create().uv(16, 103).cuboid(5.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-5.0F, 0.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r5", ModelPartBuilder.create().uv(68, 88).cuboid(-7.0F, -1.0F, -2.0F, 6.0F, 1.0F, 2.0F), ModelTransform.of(-11.0F, 1.0F, -24.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -3.0F, -14.0F, 8.0F, 1.0F, 22.0F), ModelTransform.of(-11.0F, 3.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r7", ModelPartBuilder.create().uv(48, 51).cuboid(-8.0F, -3.0F, -14.0F, 2.0F, 1.0F, 22.0F), ModelTransform.of(-9.0F, 2.0F, -14.0F, 0.0F, 3.1416F, 0.0F));
		group12Data.addChild("cube_r8", ModelPartBuilder.create().uv(56, 102).cuboid(-7.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-9.0F, 0.0F, -24.0F, 0.0F, 3.1416F, 0.0F));

		baseData.addChild("group8", ModelPartBuilder.create(), ModelTransform.origin(7.0F, 7.0F, 14.0F));
		baseData.addChild("group9", ModelPartBuilder.create(), ModelTransform.origin(7.0F, 7.0F, 14.0F));

		// Root Level Elements
		modelPartData.addChild("group2", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));
		modelPartData.addChild("group3", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));
		modelPartData.addChild("group4", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 16.0F, 0.0F));

		ModelPartData group6Data = modelPartData.addChild("group6",
				ModelPartBuilder.create(),
				ModelTransform.origin(2.0F, 24.0F, 14.0F)
		);

		// Group 11 nested within Group 6
		ModelPartData group11Data = group6Data.addChild("group11", ModelPartBuilder.create()
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
				ModelTransform.origin(0.0F, 0.0F, 3.0F)
		);

		// Rotated child parts of group11
		group11Data.addChild("cube_r9", ModelPartBuilder.create().uv(96, 23).cuboid(4.0F, -9.0F, 0.0F, 4.0F, 6.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r10", ModelPartBuilder.create().uv(8, 97).cuboid(5.0F, -9.0F, 0.0F, 3.0F, 6.0F, 1.0F), ModelTransform.of(-2.0F, 0.0F, -1.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r11", ModelPartBuilder.create().uv(98, 80).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 1.0F), ModelTransform.of(-2.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r12", ModelPartBuilder.create().uv(56, 80).cuboid(2.0F, -9.0F, 0.0F, 6.0F, 6.0F, 1.0F), ModelTransform.of(3.0F, 0.0F, 1.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r13", ModelPartBuilder.create().uv(48, 23).cuboid(-10.0F, -9.0F, -14.0F, 2.0F, 6.0F, 22.0F).uv(0, 23).cuboid(8.0F, -9.0F, -14.0F, 2.0F, 6.0F, 22.0F), ModelTransform.of(-2.0F, 0.0F, -17.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r14", ModelPartBuilder.create().uv(84, 88).cuboid(6.0F, -9.0F, -1.0F, 3.0F, 6.0F, 2.0F), ModelTransform.of(3.0F, 0.0F, -34.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r15", ModelPartBuilder.create().uv(88, 80).cuboid(6.0F, -9.0F, -1.0F, 3.0F, 6.0F, 2.0F), ModelTransform.of(1.0F, 0.0F, -32.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r16", ModelPartBuilder.create().uv(96, 46).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(-1.0F, 0.0F, -28.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r17", ModelPartBuilder.create().uv(96, 38).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, -30.0F, 0.0F, 3.1416F, 0.0F));
		group11Data.addChild("cube_r18", ModelPartBuilder.create().uv(96, 30).cuboid(7.0F, -9.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(-2.0F, 0.0F, -26.0F, 0.0F, 3.1416F, 0.0F));

		modelPartData.addChild("group5", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, -9.0F));
		modelPartData.addChild("group10", ModelPartBuilder.create(), ModelTransform.origin(8.0F, 24.0F, -8.0F));

		// Returns your actual 128x128 texture space layout mapped 1:1
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(BoatEntityRenderState state) {
		// Leave empty or add custom tilting animations here if desired

		// Direct offsets targeting the root block structure:

		// 1. Shift upwards by 18 pixels (Positive Y moves downwards in MC models)
		this.root.originY = -20.5F*2;
		//this.root.originX = -18.0F;

		// 2. Rotate 90 degrees counter-clockwise to counter the right-shift alignment
		// (If it ends up facing backwards instead, flip this value to positive: 1.5708F)
		this.root.yaw = -1.5708F;

		this.root.xScale=2f;
		this.root.yScale=2f;
		this.root.zScale=2f;
	}
}