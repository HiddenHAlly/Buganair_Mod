package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

public class BuganairBoatEntityRenderer extends AbstractBoatEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(Buganair.MOD_ID, "textures/entity/buganair_boat.png");

    private final Model.SinglePartModel waterMaskModel;
    private final EntityModel<BoatEntityRenderState> model;

    public BuganairBoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.waterMaskModel = new Model.SinglePartModel(
            context.getPart(EntityModelLayers.BOAT),
            texture -> RenderLayers.waterMask()
        );
        this.model = new BoatEntityModel(context.getPart(EntityModelLayers.OAK_BOAT));
    }

    @Override
    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(TEXTURE);
    }

    @Override
    protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        if (!state.submergedInWater) {
            queue.submitModel(
                this.waterMaskModel,
                Unit.INSTANCE,
                matrices,
                this.waterMaskModel.getLayer(TEXTURE),
                light,
                OverlayTexture.DEFAULT_UV,
                state.outlineColor,
                null
            );
        }
    }
}
