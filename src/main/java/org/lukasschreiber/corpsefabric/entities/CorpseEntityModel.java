package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class CorpseEntityModel extends PlayerEntityModel<CorpseEntity> {

    public CorpseEntityModel(ModelPart modelPart) {
        super(modelPart, false);
    }

    @Override
    public void setAngles(CorpseEntity livingEntity, float f, float g, float h, float i, float j) {
        // we do not want to change any angles
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64);
    }
}
