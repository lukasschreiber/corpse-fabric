package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.client.CorpseClient;

public class CorpseEntityRenderer extends AbstractPlayerLikeRender<CorpseEntity, CorpseEntityModel> {
    private static final Identifier SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

    public CorpseEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CorpseEntityModel(ctx.getPart(CorpseClient.MODEL_CORPSE_ENTITY_LAYER)));
    }

    @Override
    public void render(CorpseEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), -90));
        matrixStack.translate(0, -1, 0.2);
        if(entity.isSkeleton()) {
            CorpseSkeletonEntity skeleton = entity.getSkeletonEntity();
            MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(skeleton).render(skeleton, f, 0, matrixStack, vertexConsumerProvider, i);
        }else{
            super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
        }
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(CorpseEntity entity) {
        return entity.isSkeleton() ? SKELETON_TEXTURE : entity.getSkinTexture();
    }
}
