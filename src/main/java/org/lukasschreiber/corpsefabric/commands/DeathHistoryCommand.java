package org.lukasschreiber.corpsefabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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

                            if (death.isPresent()) {
                                Long timestamp = death.get().getTimestamp();
                                try {
                                    String time = new SimpleDateFormat(Text.translatable("time.corpse.format.date_time").getString()).format(timestamp);
                                    source.sendFeedback(() -> Text.translatable("messages.corpse.player_died", player.getName().getString(), time), true);
                                } catch (IllegalArgumentException exc) {
                                    Logger.INSTANCE.log(Level.ERROR, "Could not format the timestamp, the issue is most likely an incorrect translation file, where time.corpse.format.date_time is not correct. Please check this reference for creating correct formats: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html");
                                }
                            } else {
                                source.sendFeedback(() -> Text.translatable("messages.corpse.no_death_found", player.getName().getString()), true);
                            }
                            return 1;
                        })
                    )
                )
            )
        );
    }
}
