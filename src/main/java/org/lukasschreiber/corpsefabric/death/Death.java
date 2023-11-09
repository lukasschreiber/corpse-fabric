package org.lukasschreiber.corpsefabric.death;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Death {
    private final DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> additionalItems = DefaultedList.of();
    private UUID playerUuid;
    private String playerName;
    private Long timestamp;
    private UUID uuid;
    private Vec3d pos;
    private String causeOfDeath;
    private Identifier skinTexture;
    private DefaultedList<ItemStack> mainInventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    private DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private ItemStack mainHandStack = ItemStack.EMPTY;
    private ItemStack offHandStack = ItemStack.EMPTY;

    public Death() {
        super();
    }

    public Death(ServerPlayerEntity player, DamageSource source) {
        super();
        this.playerUuid = player.getUuid();
        this.uuid = UUID.randomUUID();
        this.playerName = player.getName().getString();
        this.mainInventory = player.getInventory().main;
        this.armor = player.getInventory().armor;
        this.timestamp = System.currentTimeMillis();
        this.pos = player.getPos();
        this.causeOfDeath = source.getDeathMessage(player).getString();
        this.skinTexture = DefaultSkinHelper.getTexture(this.playerUuid);
        this.mainHandStack = player.getMainHandStack();
        this.offHandStack = player.getOffHandStack();
        // use combined Inventory to get everything
    }

    public static Death fromNbt(NbtCompound compound) {
        Death death = new Death();
        if (compound.containsUuid("Uuid")) {
            death.uuid = compound.getUuid("Uuid");
        }
        if (compound.containsUuid("PlayerUuid")) {
            death.playerUuid = compound.getUuid("PlayerUuid");
        }
        if (compound.contains("PlayerName")) {
            death.playerName = compound.getString("PlayerName");
        }
        if (compound.contains("Timestamp")) {
            death.timestamp = compound.getLong("Timestamp");
        }
        if (compound.contains("PosX") && compound.contains("PosY") && compound.contains("PosZ")) {
            death.pos = new Vec3d(compound.getDouble("PosX"), compound.getDouble("PosY"), compound.getDouble("PosZ"));
        }
        if (compound.contains("CauseOfDeath")) {
            death.causeOfDeath = compound.getString("CauseOfDeath");
        }
        if (compound.contains("ArmorInventory")) {
            nbtListToInventory(compound.getList("ArmorInventory", NbtElement.COMPOUND_TYPE), death.armor);
        }
        if (compound.contains("MainInventory")) {
            nbtListToInventory(compound.getList("MainInventory", NbtElement.COMPOUND_TYPE), death.mainInventory);
        }
        if (compound.contains("SkinIdentifier")) {
            death.skinTexture = new Identifier(compound.getString("SkinIdentifier"));
        }
        if(compound.contains("MainHandStack")) {
            death.mainHandStack = ItemStack.fromNbt(compound.getCompound("MainHandStack"));
        }
        if(compound.contains("OffHandStack")) {
            death.offHandStack = ItemStack.fromNbt(compound.getCompound("OffHandStack"));
        }
        return death;
    }

    private static NbtList inventoryToNbtList(DefaultedList<ItemStack> inventory) {
        NbtList list = new NbtList();

        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty()) {
                NbtCompound slot = new NbtCompound();
                slot.putInt("Slot", i);
                inventory.get(i).writeNbt(slot);
                list.add(slot);
            }
        }

        return list;
    }

    private static void nbtListToInventory(NbtList list, DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound slot = list.getCompound(i);
            int j = slot.getInt("Slot");
            if (j >= 0 && j < inventory.size()) {
                inventory.set(j, ItemStack.fromNbt(slot));
            }
        }

    }

    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putUuid("PlayerUuid", this.playerUuid);
        compound.putString("PlayerName", this.playerName);
        compound.putLong("Timestamp", this.timestamp);
        compound.putUuid("Uuid", this.uuid);
        compound.putDouble("PosX", this.pos.getX());
        compound.putDouble("PosY", this.pos.getY());
        compound.putDouble("PosZ", this.pos.getZ());
        compound.putString("CauseOfDeath", this.causeOfDeath);
        compound.put("ArmorInventory", inventoryToNbtList(this.armor));
        compound.putString("SkinIdentifier", this.skinTexture.toString());
        compound.put("MainInventory", inventoryToNbtList(this.mainInventory));
        compound.put("MainHandStack", this.mainHandStack.writeNbt(new NbtCompound()));
        compound.put("OffHandStack", this.offHandStack.writeNbt(new NbtCompound()));
        return compound;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Vec3d getPos() {
        return pos;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public DefaultedList<ItemStack> getArmor() {
        return armor;
    }

    public SimpleInventory getInventory() {
        ItemStack[] combinedInventory = Stream.of(this.armor, this.mainInventory, List.of(this.offHandStack))
            .flatMap(List::stream)
            .toArray(ItemStack[]::new);
        return new SimpleInventory(combinedInventory);
    }

    public Identifier getSkinTexture() {
        return skinTexture;
    }

    public void setSkinTexture(Identifier skinTexture) {
        this.skinTexture = skinTexture;
    }

    public ItemStack getMainHandStack() {
        return mainHandStack;
    }

    public ItemStack getOffHandStack() {
        return offHandStack;
    }
}
