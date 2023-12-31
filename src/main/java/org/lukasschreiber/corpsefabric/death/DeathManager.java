package org.lukasschreiber.corpsefabric.death;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DeathManager {
    private static final String DEATHS = "deaths";

    public static void addDeath(MinecraftServer server, Death death) {
        File file = getDeathFile(server, death.getPlayerUuid(), death.getUuid());
        file.getParentFile().mkdirs();
        try {
            NbtIo.writeCompressed(death.toNbt(), file);
            Logger.INSTANCE.log(Level.INFO, "Saved death to file " + file.getAbsolutePath());
        } catch (IOException exception) {
            Logger.INSTANCE.log(Level.ERROR, "Failed to store player death! Could not create the death file.");
        }
    }

    public static List<Death> getDeaths(ServerPlayerEntity player) {
        return getDeaths(player.getServer(), player.getUuid());
    }

    public static List<Death> getDeaths(MinecraftServer server, UUID playerUuid) {
        List<Death> deaths = new ArrayList<>();

        Path dir = getPlayerDeathFolder(server, playerUuid).toPath();
        if (!dir.toFile().exists()) return deaths;

        try (Stream<Path> files = Files.list(dir)) {
            files.forEach(path -> {
                UUID deathUuid = UUID.fromString(path.getFileName().toString().replaceFirst("[.][^.]+$", ""));
                deaths.add(getDeathFromFile(server, playerUuid, deathUuid));

            });
        } catch (IOException exception) {
            Logger.INSTANCE.log(Level.ERROR, "Failed to enumerate files in the directory " + dir);
            exception.printStackTrace();
        }
        deaths.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return deaths;
    }

    public static Optional<Death> getLatestDeath(ServerPlayerEntity player) {
        return getLatestDeath(player.getServer(), player.getUuid());
    }

    public static Optional<Death> getLatestDeath(MinecraftServer server, UUID playerUuid) {
        Path dir = getPlayerDeathFolder(server, playerUuid).toPath();
        if (!dir.toFile().exists()) return Optional.empty();

        try (Stream<Path> files = Files.list(dir)) {
            Optional<Path> lastModified = files
                .filter(f -> !Files.isDirectory(f))
                .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

            if (lastModified.isPresent()) {
                UUID deathUuid = UUID.fromString(lastModified.map(f -> f.getFileName().toString().replaceFirst("[.][^.]+$", "")).get());
                return Optional.of(getDeathFromFile(server, playerUuid, deathUuid));
            }
        } catch (IOException exception) {
            Logger.INSTANCE.log(Level.ERROR, "Failed to enumerate files in the directory " + dir);
            exception.printStackTrace();
        }

        return Optional.empty();
    }

    public static void updateDeath(MinecraftServer server, Death death) {
        removeDeath(server, death);
        addDeath(server, death);
    }

    public static boolean removeDeath(MinecraftServer server, Death death) {
        File file = getDeathFile(server, death.getPlayerUuid(), death.getUuid());
        return !file.exists() || file.delete();
    }

    public static Death getDeathFromFile(MinecraftServer server, UUID playerUuid, UUID deathUuid) {
        try {
            File deathFile = getDeathFile(server, playerUuid, deathUuid);
            return Death.fromNbt(NbtIo.readCompressed(deathFile));
        } catch (IOException e) {
            Logger.INSTANCE.log(Level.ERROR, "Could not load death file for corpse entity!");
        }
        return null;
    }

    public static File getDeathFile(MinecraftServer server, UUID playerUuid, UUID deathUuid) {
        return new File(getPlayerDeathFolder(server, playerUuid), deathUuid.toString() + ".dat");
    }

    public static File getPlayerDeathFolder(MinecraftServer server, UUID uuid) {
        return new File(getDeathFolder(server), uuid.toString());
    }

    public static File getDeathFolder(MinecraftServer server) {
        return new File(server.getSavePath(WorldSavePath.ROOT).toFile(), DEATHS);
    }
}
