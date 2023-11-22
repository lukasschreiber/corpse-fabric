package org.lukasschreiber.corpsefabric.death;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class Death {
    //    private final DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
//    private final DefaultedList<ItemStack> additionalItems = DefaultedList.of();
    private UUID playerUuid;
    private String playerName;
    private Long timestamp;
    private UUID uuid;
    private Vec3d pos;
    private String causeOfDeath;
    private Identifier skinTexture;
    private String skinTextureString;
    private DefaultedList<ItemStack> mainInventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    private DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private ItemStack mainHandStack = ItemStack.EMPTY;
    private ItemStack offHandStack = ItemStack.EMPTY;
    private Identifier dimension = DimensionTypes.OVERWORLD_ID;

    public Death() {
        super();
    }

    /**
     * Creates a new death object that
     *
     * @param player the ServerPlayerEntity to whom the death belongs
     * @param source the source of death
     */
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
        this.mainHandStack = player.getMainHandStack();
        this.offHandStack = player.getOffHandStack();
        this.dimension = player.getServerWorld().getDimensionKey().getValue();
        this.skinTextureString = Iterables.getFirst(player.getGameProfile().getProperties().get("textures"), null).getValue();
        this.skinTexture = MinecraftClient.getInstance().getSkinProvider().loadSkin(player.getGameProfile());

    }

    /**
     * <p>loads a death object from storage</p>
     * <p>If the death has been saved by an older version and does not contain all properties that could lead to NullPointerExceptions</p>
     *
     * @param compound an NbtCompound containing death data
     * @return the saved death
     */
    public static Death fromNbt(@Nullable NbtCompound compound) {
        Death death = new Death();

        if (compound == null) return death;

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
        if (compound.contains("MainHandStack")) {
            death.mainHandStack = ItemStack.fromNbt(compound.getCompound("MainHandStack"));
        }
        if (compound.contains("OffHandStack")) {
            death.offHandStack = ItemStack.fromNbt(compound.getCompound("OffHandStack"));
        }
        if (compound.contains("Dimension")) {
            death.dimension = new Identifier(compound.getString("Dimension"));
        }
        if (compound.contains("SkinTexture")) {
            death.skinTextureString = compound.getString("SkinTexture");
            death.skinTexture = MinecraftClient.getInstance().getSkinProvider().loadSkin(death.getGameProfile());
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

    @SuppressWarnings("deprecation")
    private String loadSkinTexture(GameProfile profile, MinecraftServer server) {
        HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
        try {
            map.putAll(server.getSessionService().getTextures(profile, false));
        } catch (InsecurePublicKeyException insecurePublicKeyException) {
            // empty catch block
        }
        if (map.isEmpty()) {
            profile.getProperties().clear();
            if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
                map.putAll(server.getSessionService().getTextures(profile, false));
            } else {
                server.getSessionService().fillProfileProperties(profile, false);
                try {
                    map.putAll(server.getSessionService().getTextures(profile, false));
                } catch (InsecurePublicKeyException insecurePublicKeyException) {
                    // empty catch block
                }
            }
        }

        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
            return Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
        }

        return "";
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
        compound.put("MainInventory", inventoryToNbtList(this.mainInventory));
        compound.put("MainHandStack", this.mainHandStack.writeNbt(new NbtCompound()));
        compound.put("OffHandStack", this.offHandStack.writeNbt(new NbtCompound()));
        compound.putString("Dimension", this.dimension.toString());
        compound.putString("SkinTexture", this.skinTextureString);
        return compound;
    }

    public void teleport() {
        // will not work with an actual server
        MinecraftServer server = MinecraftClient.getInstance().getServer();
        if(server == null) return;
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(this.getPlayerUuid());
        Optional<RegistryKey<World>> destinationWorldKey = server.getWorldRegistryKeys().stream().filter(key -> key.getValue().toString().equals(this.getDimension().toString())).findFirst();
        if(player == null || destinationWorldKey.isEmpty()) return;
        ServerWorld destinationWorld = server.getWorld(destinationWorldKey.get());
        player.teleport(destinationWorld, this.getPos().x, this.getPos().y, this.getPos().z, player.getYaw(), player.getPitch());
    }

    public boolean canTeleport() {
        MinecraftServer server = MinecraftClient.getInstance().getServer();
        if(server == null) return false;
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(this.getPlayerUuid());
        Optional<RegistryKey<World>> destinationWorldKey = server.getWorldRegistryKeys().stream().filter(key -> key.getValue().toString().equals(this.getDimension().toString())).findFirst();
        return player != null && destinationWorldKey.isPresent();
    }

    public SimpleInventory transferInventory(PlayerEntity player) {
        MinecraftServer server = MinecraftClient.getInstance().getServer();
        if(player instanceof ClientPlayerEntity && server != null) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(player.getUuid());
            if(serverPlayer != null) player = serverPlayer;
        }

        for(int i = 0; i < this.armor.size(); i++) {
            ItemStack stack = this.armor.get(i);
            if(stack.isEmpty()) continue;
            player.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i), stack);
        }

        this.armor.clear();
        DeathManager.updateDeath(MinecraftClient.getInstance().getServer(), this);
        return this.getInventory();
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
        // only on client
        return this.skinTexture;
    }

    public ItemStack getMainHandStack() {
        return mainHandStack;
    }

    public ItemStack getOffHandStack() {
        return offHandStack;
    }

    public Identifier getDimension() {
        return dimension;
    }

    public String getSkinTextureString() {
        return this.skinTextureString;
    }

    public GameProfile getGameProfile() {
        GameProfile profile = new GameProfile(this.getPlayerUuid(), this.getPlayerName());
        profile.getProperties().put(PlayerSkinProvider.TEXTURES, new Property(PlayerSkinProvider.TEXTURES, this.skinTextureString));
        return profile;
    }
}
