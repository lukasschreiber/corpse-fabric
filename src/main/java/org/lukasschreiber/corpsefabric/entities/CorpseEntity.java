package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.List;

public class CorpseEntity extends PlayerLikeEntity {
    public CorpseEntity(EntityType<CorpseEntity> type, World world) {
        super(type, world);
    }

    public static CorpseEntity fromDeath(Death death, World world) {
        CorpseEntity corpse = new CorpseEntity(EntityTypes.CORPSE_ENTITY_TYPE, world);
        corpse.setSkinTexture(death.getSkinTexture());
        Vec3d pos = death.getPos();
        corpse.setPos(pos.x, pos.y + 1, pos.z);
        List<ItemStack> armorItems = death.getArmor();

        for (int i = 0; i < Math.min(armorItems.size(), 4); i++) {
            corpse.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i), armorItems.get(i));
        }

        corpse.equipStack(EquipmentSlot.MAINHAND, death.getMainHandStack());
        corpse.equipStack(EquipmentSlot.OFFHAND, death.getOffHandStack());

        return corpse;
    }

    private void recalculateBoundingBox() {
        Direction facing = Direction.fromRotation(this.getRotationVector().y);
        this.setBoundingBox(new Box(
            this.getX() - (facing.getOffsetX() != 0 ? 1D : 0.5D),
            this.getY(),
            this.getZ() - (facing.getOffsetZ() != 0 ? 1D : 0.5D),
            this.getX() + (facing.getOffsetX() != 0 ? 1D : 0.5D),
            this.getY() + 0.5D,
            this.getZ() + (facing.getOffsetZ() != 0 ? 1D : 0.5D)
        ));
    }

    @Override
    public void tick() {
        super.tick();
        this.recalculateBoundingBox();
    }


}
