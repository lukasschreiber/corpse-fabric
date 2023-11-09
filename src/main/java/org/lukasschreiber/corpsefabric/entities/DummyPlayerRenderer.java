package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.client.CorpseClient;

public class DummyPlayerRenderer extends AbstractPlayerLikeRender<DummyPlayerEntity, DummyPlayerEntityModel> {
    public DummyPlayerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DummyPlayerEntityModel(ctx.getPart(CorpseClient.MODEL_DUMMY_PLAYER_LAYER)));
    }

    @Override
    public Identifier getTexture(DummyPlayerEntity entity) {
        return entity.getSkinTexture();
    }
}
