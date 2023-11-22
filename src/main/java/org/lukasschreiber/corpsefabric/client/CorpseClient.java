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
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.entities.*;
import org.lukasschreiber.corpsefabric.gui.CorpseInventoryScreen;
import org.lukasschreiber.corpsefabric.gui.HistoryScreen;
import org.lukasschreiber.corpsefabric.gui.HistoryScreenHandler;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;
import org.lwjgl.glfw.GLFW;

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
        EntityRendererRegistry.register(EntityTypes.CORPSE_ENTITY_TYPE, CorpseEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_DUMMY_PLAYER_LAYER, DummyPlayerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CORPSE_ENTITY_LAYER, CorpseEntityModel::getTexturedModelData);

        // somehow the compiler needs the type annotations here
        HandledScreens.<HistoryScreenHandler, HistoryScreen>register(Corpse.HISTORY_SCREEN_HANDLER_TYPE, HistoryScreen::new);
        HandledScreens.register(Corpse.CORPSE_INVENTORY_SCREEN_HANDLER_TYPE, CorpseInventoryScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DIED, (client, handler, buf, responseSender) -> {
            NbtCompound compound = buf.readNbt();
            if (client.player == null || compound == null) return;
            Death death = Death.fromNbt(compound);

            client.execute(() -> {
                client.player.sendMessage(Text.translatable("messages.corpse.player_died_short", death.getPlayerName()));
            });
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.corpse.death_overview", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "category.corpse.main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed() && client.player != null) {
                ClientPlayNetworking.send(NetworkingConstants.OPEN_DEATH_HISTORY, PacketByteBufs.empty());
            }
        });
    }
}
