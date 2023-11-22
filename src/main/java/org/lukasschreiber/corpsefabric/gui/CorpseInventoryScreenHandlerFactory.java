package org.lukasschreiber.corpsefabric.gui;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.lukasschreiber.corpsefabric.death.Death;

public class CorpseInventoryScreenHandlerFactory extends AbstractExtendedScreenHandlerFactory {
    private final Death death;

    public CorpseInventoryScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text name, Death death) {
        super(baseFactory, name);
        this.death = death;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeNbt(this.death.toNbt());
    }
}
