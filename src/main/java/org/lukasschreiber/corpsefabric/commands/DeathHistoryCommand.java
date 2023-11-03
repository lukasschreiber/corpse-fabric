package org.lukasschreiber.corpsefabric.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.*;

public class DeathHistoryCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            literal("deaths")
                .then(literal("latest")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                            Optional<Death> death = DeathManager.getLatestDeath(player);

                            if(death.isPresent()) {
                                Long timestamp = death.get().getTimestamp();
                                String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(timestamp);
                                source.sendFeedback(() -> Text.literal("The player " + player.getName().getString() + " has died at " + time), true);
                            } else {
                                source.sendFeedback(() -> Text.literal("The player " + player.getName().getString() + " has not died recently."), true);
                            }
                            return 1;
                        })
                    )
                )
            )
        );
    }
}
