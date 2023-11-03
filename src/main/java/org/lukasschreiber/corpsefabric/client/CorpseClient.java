package org.lukasschreiber.corpsefabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.gui.HistoryScreen;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;
import org.lwjgl.glfw.GLFW;

public class CorpseClient implements ClientModInitializer {
    private static KeyBinding keyBinding;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DIED, (client, handler, buf, responseSender) -> {
            NbtCompound compound = buf.readNbt();
            if (client.player == null || compound == null) return;
            Death death = Death.fromNbt(compound);

            // store death
            DeathManager.addDeath(client.getServer(), death);

            client.player.sendMessage(Text.literal("Player Died "+death.getPlayerName()));
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.corpse.death_overview", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "category.corpse.main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed() && client.player != null) {
                client.setScreen(new HistoryScreen(client.player));
            }
        });
    }
}
