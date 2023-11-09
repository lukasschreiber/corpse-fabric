package org.lukasschreiber.corpsefabric.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.entities.CorpseEntity;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void onDeath(DamageSource source, CallbackInfo info) {
        MinecraftServer server = getServerWorld().getServer();
        ServerPlayerEntity player = (ServerPlayerEntity) (Object)this;
        Logger.INSTANCE.log(Level.INFO, "Player has the armor items "+new ArrayList<>(player.getInventory().armor).stream().map(stack -> stack.getName().getString()).collect(Collectors.joining(" ")));
        Death death = new Death(player, source);
        PacketByteBuf data = PacketByteBufs.create().writeNbt(death.toNbt());

        server.execute(() -> {
            getServerWorld().addEntities(Stream.of(CorpseEntity.fromDeath(death, getServerWorld())));
            ServerPlayNetworking.send(player, NetworkingConstants.PLAYER_DIED, data);
        });
    }
}
