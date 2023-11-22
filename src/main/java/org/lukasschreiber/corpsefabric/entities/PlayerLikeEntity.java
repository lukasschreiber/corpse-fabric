package org.lukasschreiber.corpsefabric.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;

public class PlayerLikeEntity extends MobEntity {
    private Identifier skinTexture;

    public PlayerLikeEntity(EntityType<? extends PlayerLikeEntity> type, World world) {
        super(type, world);
        this.skinTexture = DefaultSkinHelper.getTexture();
    }

    // TODO: is that needed???
    public static DefaultAttributeContainer.Builder createPlayerLikeAttributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

//    @Override
//    public boolean damage(DamageSource source, float amount) {
//        return false;
//    }

    public Identifier getSkinTexture() {
        return this.skinTexture;
    }

    public void setSkinTexture(Identifier skinTexture) {
        this.skinTexture = skinTexture;
    }

}
