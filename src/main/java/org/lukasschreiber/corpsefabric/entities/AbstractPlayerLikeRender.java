package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

abstract public class AbstractPlayerLikeRender<E extends MobEntity, M extends BipedEntityModel<E>> extends BipedEntityRenderer<E, M> {

    public AbstractPlayerLikeRender(EntityRendererFactory.Context ctx, M modelPart) {
        super(ctx, modelPart, 0.5f);

        // render Armor here
        // does not support Slim model and does not support anything but armor currently
        this.addFeature(new ArmorFeatureRenderer<>(this, new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
    }
}
