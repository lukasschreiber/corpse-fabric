package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lukasschreiber.corpsefabric.Corpse;

public class PlayerLikeEntity extends MobEntity {
    protected Identifier skinTexture;

    public PlayerLikeEntity(EntityType<? extends PlayerLikeEntity> type, World world) {
        super(type, world);
        this.skinTexture = new Identifier(Corpse.NAMESPACE, "textures/entity/dummy/steve.png");
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
        return skinTexture;
    }
    public void setSkinTexture(Identifier skinTexture) {
        this.skinTexture = skinTexture;
    }

}
