package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class BuganairBoatEntityRenderer extends AbstractBoatEntityRenderer {

    private final Map<String, EntityModel<BoatEntityRenderState>> variantModels = new HashMap<>();

    // 1. A map that ties a specific entity's render state to its variant string safely
    private final Map<BoatEntityRenderState, String> stateVariants = new WeakHashMap<>();

    // 2. The ThreadLocal is now ONLY active for the exact millisecond the pixels draw
    private final ThreadLocal<String> activeVariant = ThreadLocal.withInitial(() -> "oak");

    public BuganairBoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        // --- OAK VARIANT SETUP ---
        this.variantModels.put("oak", new BoatEntityModel(context.getPart(EntityModelLayers.OAK_BOAT)));


        // --- ACACIA VARIANT SETUP ---
        this.variantModels.put("acacia", new BoatEntityModel(context.getPart(EntityModelLayers.ACACIA_BOAT)));


        // --- BAMBOO VARIANT SETUP ---
        this.variantModels.put("bamboo", new BoatEntityModel(context.getPart(EntityModelLayers.BAMBOO_BOAT)));


        // --- BIRCH VARIANT SETUP ---
        this.variantModels.put("birch", new BoatEntityModel(context.getPart(EntityModelLayers.BIRCH_BOAT)));
        // Note: Standard wood boats share the same water mask hull geometry layer


        // --- CHERRY VARIANT SETUP ---
        this.variantModels.put("cherry", new BoatEntityModel(context.getPart(EntityModelLayers.CHERRY_BOAT)));


        // --- DARK OAK VARIANT SETUP ---
        this.variantModels.put("dark_oak", new BoatEntityModel(context.getPart(EntityModelLayers.DARK_OAK_BOAT)));


        // --- JUNGLE VARIANT SETUP ---
        this.variantModels.put("jungle", new BoatEntityModel(context.getPart(EntityModelLayers.JUNGLE_BOAT)));


        // --- MANGROVE VARIANT SETUP ---
        this.variantModels.put("mangrove", new BoatEntityModel(context.getPart(EntityModelLayers.MANGROVE_BOAT)));


        // --- PALE OAK VARIANT SETUP ---
        this.variantModels.put("pale_oak", new BoatEntityModel(context.getPart(EntityModelLayers.PALE_OAK_BOAT)));


        // --- SPRUCE VARIANT SETUP ---
        this.variantModels.put("spruce", new BoatEntityModel(context.getPart(EntityModelLayers.SPRUCE_BOAT)));


        // Add other variants models here as needed...
    }

    @Override
    public void updateRenderState(AbstractBoatEntity entity, BoatEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        // Save the variant into the map, tied specifically to this frame's state object
        if (entity instanceof BuganairBoatEntity buganairBoat) {
            stateVariants.put(state, buganairBoat.getVariant());
        }
    }

    // 3. We intercept the MAIN render method to set our ThreadLocal exactly when needed
    @Override
    public void render(BoatEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        // Grab the specific variant for this boat and lock it in
        activeVariant.set(stateVariants.getOrDefault(state, "oak"));

        // Let vanilla draw the boat using the newly updated parameters
        super.render(state, matrices, queue, cameraRenderState);

        // Clean up the lock
        activeVariant.remove();
    }

    @Override
    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.variantModels.getOrDefault(this.activeVariant.get(), this.variantModels.get("oak"));
    }

    @Override
    protected RenderLayer getRenderLayer() {
        String variant = this.activeVariant.get();
        Identifier textureId = Identifier.of(Buganair.MOD_ID, "textures/entity/boat/buganair_" + variant + ".png");
        return this.getModel().getLayer(textureId);
    }

    // Note: You can optionally add your renderWaterMask method back here if your boats
    // require custom water mask manipulation, but vanilla handles default shapes natively!
}