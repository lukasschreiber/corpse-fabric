package org.lukasschreiber.corpsefabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.entities.*;
import org.lukasschreiber.corpsefabric.gui.HistoryScreen;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CorpseClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_DUMMY_PLAYER_LAYER = new EntityModelLayer(new Identifier(Corpse.NAMESPACE, "dummy"), "main");
    public static final EntityModelLayer MODEL_CORPSE_ENTITY_LAYER = new EntityModelLayer(new Identifier(Corpse.NAMESPACE, "corpse"), "main");

    private static KeyBinding keyBinding;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(EntityTypes.DUMMY_PLAYER_ENTITY_TYPE, (DummyPlayerRenderer::new));
        EntityModelLayerRegistry.registerModelLayer(MODEL_DUMMY_PLAYER_LAYER, DummyPlayerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CORPSE_ENTITY_LAYER, CorpseEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(EntityTypes.CORPSE_ENTITY_TYPE, CorpseEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DIED, (client, handler, buf, responseSender) -> {
            NbtCompound compound = buf.readNbt();
            if (client.player == null || compound == null) return;
            Death death = Death.fromNbt(compound);

            // store death
            DeathManager.addDeath(client.getServer(), death);

            client.execute(() -> {
                client.player.sendMessage(Text.translatable("messages.corpse.player_died_short", death.getPlayerName()));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ALL_DEATHS, (client, handler, buf, responseSender) -> {
            NbtCompound response = buf.readNbt();
            if (response == null || !response.contains("Deaths")) return;
            NbtList deathsCompounds = response.getList("Deaths", NbtElement.COMPOUND_TYPE);
            List<Death> deaths = deathsCompounds.stream().map(c -> Death.fromNbt((NbtCompound) c)).toList();

            client.execute(() -> {
                client.setScreen(new HistoryScreen(client.player, deaths));
            });
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.corpse.death_overview", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "category.corpse.main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed() && client.player != null) {
                ClientPlayNetworking.send(NetworkingConstants.REQUEST_ALL_DEATHS, PacketByteBufs.empty());
            }
        });
    }
}
