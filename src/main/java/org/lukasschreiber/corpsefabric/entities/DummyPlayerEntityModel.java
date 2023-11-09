package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class DummyPlayerEntityModel extends PlayerEntityModel<DummyPlayerEntity> {

    public DummyPlayerEntityModel(ModelPart modelPart) {
        super(modelPart, false);
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64);
    }
}
