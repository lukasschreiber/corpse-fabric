package org.lukasschreiber.corpsefabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import org.lukasschreiber.corpsefabric.commands.DeathHistoryCommand;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.entities.CorpseEntity;
import org.lukasschreiber.corpsefabric.entities.DummyPlayerEntity;
import org.lukasschreiber.corpsefabric.entities.EntityTypes;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;

import java.util.List;


public class Corpse implements ModInitializer {
    public static final String NAMESPACE = "corpse";
    @Override
    public void onInitialize() {
        DeathHistoryCommand.register();
        FabricDefaultAttributeRegistry.register(EntityTypes.DUMMY_PLAYER_ENTITY_TYPE, DummyPlayerEntity.createPlayerLikeAttributes());
        FabricDefaultAttributeRegistry.register(EntityTypes.CORPSE_ENTITY_TYPE, CorpseEntity.createPlayerLikeAttributes());

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.REQUEST_ALL_DEATHS, ((server, player, handler, buf, responseSender) -> {
            List<Death> deaths = DeathManager.getDeaths(player);
            NbtList deathsNbt = new NbtList();
            deaths.stream().map(Death::toNbt).forEach(deathsNbt::add);
            NbtCompound compound = new NbtCompound();
            compound.put("Deaths", deathsNbt);
            PacketByteBuf data = PacketByteBufs.create().writeNbt(compound);

            responseSender.sendPacket(NetworkingConstants.ALL_DEATHS, data);
        }));
    }
}
