package org.lukasschreiber.corpsefabric.death;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class Death  {
    private UUID playerUuid;
    private String playerName;
    private Long timestamp;
    private UUID uuid;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    private DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
    private DefaultedList<ItemStack> additionalItems = DefaultedList.of();
    public Death(){
        super();
    }

    public Death(ServerPlayerEntity player) {
        super();
        this.playerUuid = player.getUuid();
        this.uuid = UUID.randomUUID();
        this.playerName = player.getName().getString();
        this.inventory = player.getInventory().main;
        this.armor = player.getInventory().armor;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {return uuid;}

    public Long getTimestamp() {
        return timestamp;
    }

    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putUuid("player", this.playerUuid);
        compound.putString("playerName", this.playerName);
        compound.putLong("timestamp", this.timestamp);
        compound.putUuid("uuid", this.uuid);
        return compound;
    }

    public static Death fromNbt(NbtCompound compound) {
        Death death = new Death();
        if(compound.containsUuid("uuid")) {
            death.uuid = compound.getUuid("uuid");
        }
        if(compound.containsUuid("player")) {
            death.playerUuid = compound.getUuid("player");
        }
        if(compound.contains("playerName")) {
            death.playerName = compound.getString("playerName");
        }
        if(compound.contains("timestamp")) {
            death.timestamp = compound.getLong("timestamp");
        }
        return death;
    }


}
