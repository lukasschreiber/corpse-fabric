package org.lukasschreiber.corpsefabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import org.lukasschreiber.corpsefabric.commands.DeathHistoryCommand;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;

import java.util.List;
import java.util.stream.Collectors;

public class Corpse implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        DeathHistoryCommand.register();

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.REQUEST_ALL_DEATHS, ((server, player, handler, buf, responseSender) -> {
            List<Death> deaths = DeathManager.getDeaths(player);
            NbtList deathsNbt = new NbtList();
            deaths.stream().map(Death::toNbt).forEach(deathsNbt::add);
            NbtCompound compound = new NbtCompound();
            compound.put("deaths", deathsNbt);
            PacketByteBuf data = PacketByteBufs.create().writeNbt(compound);

            responseSender.sendPacket(NetworkingConstants.ALL_DEATHS, data);
        }));
    }
}
