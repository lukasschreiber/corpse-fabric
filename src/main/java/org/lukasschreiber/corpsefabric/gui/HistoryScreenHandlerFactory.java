package org.lukasschreiber.corpsefabric.gui;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.List;

public class HistoryScreenHandlerFactory extends AbstractExtendedScreenHandlerFactory {

    private final List<Death> deaths;

    public HistoryScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text name, List<Death> deaths) {
        super(baseFactory, name);
        this.deaths = deaths;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        NbtList list = new NbtList();
        for (Death death : this.deaths) {
            list.add(death.toNbt());
        }
        NbtCompound data = new NbtCompound();
        data.put("Deaths", list);
        buf.writeNbt(data);
    }
}
