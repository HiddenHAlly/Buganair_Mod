package net.hiddenhally.buganair.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

public class BuganairBoatRenderState extends BoatEntityRenderState {
    // Unique data stored per boat instance per frame
    public Identifier texture;
    public EntityModel<BoatEntityRenderState> activeModel;
    public Model.SinglePartModel activeWaterMask;
}