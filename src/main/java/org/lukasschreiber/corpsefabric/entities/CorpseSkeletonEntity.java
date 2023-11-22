package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.List;

public class CorpseSkeletonEntity extends SkeletonEntity {
    public CorpseSkeletonEntity(World world, Death death) {
        super(EntityType.SKELETON, world);

        List<ItemStack> armorItems = death.getArmor();

        for (int i = 0; i < Math.min(armorItems.size(), 4); i++) {
            this.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i), armorItems.get(i));
        }

        this.equipStack(EquipmentSlot.MAINHAND, death.getMainHandStack());
        this.equipStack(EquipmentSlot.OFFHAND, death.getOffHandStack());
    }


}
