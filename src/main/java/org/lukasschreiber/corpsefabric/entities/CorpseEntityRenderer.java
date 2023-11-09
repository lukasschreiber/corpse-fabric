package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lukasschreiber.corpsefabric.client.CorpseClient;

public class CorpseEntityRenderer extends AbstractPlayerLikeRender<CorpseEntity, CorpseEntityModel> {
    public CorpseEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CorpseEntityModel(ctx.getPart(CorpseClient.MODEL_CORPSE_ENTITY_LAYER)));
    }



    @Override
    public void render(CorpseEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), -90));
        matrixStack.translate(0, -1, 0.2);
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(CorpseEntity entity) {
        return entity.getSkinTexture();
    }
}
