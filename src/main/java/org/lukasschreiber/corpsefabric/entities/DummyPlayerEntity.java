package org.lukasschreiber.corpsefabric.entities;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.List;

public class DummyPlayerEntity extends PlayerLikeEntity {

    public DummyPlayerEntity(EntityType<DummyPlayerEntity> type, World world) {
        super(type, world);
    }

    public static DummyPlayerEntity fromDeath(Death death, World world) {
        DummyPlayerEntity dummy = new DummyPlayerEntity(EntityTypes.DUMMY_PLAYER_ENTITY_TYPE, world);
        dummy.setSkinTexture(death.getSkinTexture());

        Vec3d pos = death.getPos();
        dummy.setPos(pos.x, pos.y + 1, pos.z);
        List<ItemStack> armorItems = death.getArmor();

        for (int i = 0; i < Math.min(armorItems.size(), 4); i++) {
            dummy.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i), armorItems.get(i));
        }

        dummy.equipStack(EquipmentSlot.MAINHAND, death.getMainHandStack());
        dummy.equipStack(EquipmentSlot.OFFHAND, death.getOffHandStack());

        return dummy;
    }

}
