package org.lukasschreiber.corpsefabric.gui;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.UUID;

public class PlayerCorpseItem {
    public static ItemStack fromDeath(Death death) {
        ItemStack stack;

        if (System.currentTimeMillis() - death.getTimestamp() < 60 * 1000 * 60 * 48) {
            stack = new ItemStack(Registries.ITEM.get(new Identifier("minecraft:player_head")));
            NbtCompound skullCompound = getSkullCompound(death);
            stack.setNbt(skullCompound);

            stack.setCustomName(Text.literal(death.getPlayerName()));
        }else {
            stack = new ItemStack(Registries.ITEM.get(new Identifier("minecraft:skeleton_skull")));
        }

        return stack;
    }

    @NotNull
    private static NbtCompound getSkullCompound(Death death) {
        NbtCompound skullOwner = new NbtCompound();
        skullOwner.putUuid("Id", death.getPlayerUuid());

//        use this to load custom textures --> used if texture has been changed

        NbtCompound properties = new NbtCompound();
        NbtList textures = new NbtList();
        NbtCompound texture = new NbtCompound();
        texture.putString("Value", death.getSkinTextureString());
        textures.add(texture);
        properties.put("textures", textures);
        skullOwner.put("Properties", properties);

        NbtCompound skullCompound = new NbtCompound();
        skullCompound.put("SkullOwner", skullOwner);
        return skullCompound;
    }
}
