package org.lukasschreiber.corpsefabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.commands.DeathHistoryCommand;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.entities.CorpseEntity;
import org.lukasschreiber.corpsefabric.entities.DummyPlayerEntity;
import org.lukasschreiber.corpsefabric.entities.EntityTypes;
import org.lukasschreiber.corpsefabric.gui.CorpseInventoryScreenHandler;
import org.lukasschreiber.corpsefabric.gui.HistoryScreenHandler;
import org.lukasschreiber.corpsefabric.gui.HistoryScreenHandlerFactory;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;

import java.util.List;


public class Corpse implements ModInitializer {
    public static final String NAMESPACE = "corpse";
    public static final ExtendedScreenHandlerType<HistoryScreenHandler> HISTORY_SCREEN_HANDLER_TYPE;
    public static final ExtendedScreenHandlerType<CorpseInventoryScreenHandler> CORPSE_INVENTORY_SCREEN_HANDLER_TYPE;
    static {
        HISTORY_SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, new Identifier(NAMESPACE, "history"), new ExtendedScreenHandlerType<>(HistoryScreenHandler::new));
        CORPSE_INVENTORY_SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, new Identifier(NAMESPACE, "corpse_inventory"), new ExtendedScreenHandlerType<>(CorpseInventoryScreenHandler::new));
    }

    @Override
    public void onInitialize() {
        DeathHistoryCommand.register();
        FabricDefaultAttributeRegistry.register(EntityTypes.DUMMY_PLAYER_ENTITY_TYPE, DummyPlayerEntity.createPlayerLikeAttributes());
        FabricDefaultAttributeRegistry.register(EntityTypes.CORPSE_ENTITY_TYPE, CorpseEntity.createPlayerLikeAttributes());

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.OPEN_DEATH_HISTORY, ((server, player, handler, buf, responseSender) -> {
            List<Death> deaths = DeathManager.getDeaths(player);
            if (deaths.isEmpty()) {
                player.sendMessageToClient(Text.translatable("messages.corpse.no_death_found", player.getName()), true);
            }else{
                player.openHandledScreen(new HistoryScreenHandlerFactory((syncId, playerInventory, player1) -> new HistoryScreenHandler(syncId, playerInventory, deaths), Text.translatable("screen.corpse.history_title"), deaths));
            }
        }));
    }
}
