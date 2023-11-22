package org.lukasschreiber.corpsefabric.entities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.gui.CorpseInventoryScreenHandler;
import org.lukasschreiber.corpsefabric.gui.CorpseInventoryScreenHandlerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CorpseEntity extends PlayerLikeEntity {
    private static final TrackedData<NbtCompound> DEATH = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Boolean> IS_SKELETON = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int age;
    private CorpseSkeletonEntity skeleton = null;

    public CorpseEntity(EntityType<CorpseEntity> type, World world) {
        super(type, world);
        String serverString = world.isClient ? "client" : "server";
        Logger.INSTANCE.log(Level.INFO, "Create new instance of CorpseEntity on the " + serverString);
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
        corpse.setDeath(death);

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
    protected void initDataTracker() {
        super.initDataTracker();
        getDataTracker().startTracking(DEATH, new NbtCompound());
        getDataTracker().startTracking(IS_SKELETON, this.age >= 5000000);
    }

    @Override
    public void tick() {
        super.tick();
        this.recalculateBoundingBox();

        this.age++;
        if(age >= 5000000) {
            this.getDataTracker().set(IS_SKELETON, true);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        // what if uuid is null?
        // save death data in corpse?
        Death death = this.getDeath();
        nbt.putUuid("Death", death.getUuid());
        nbt.putUuid("Owner", death.getPlayerUuid());
        nbt.putInt("Age", this.age);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID deathUuid = nbt.getUuid("Death");
        UUID playerUuid = nbt.getUuid("Owner");
        this.age = nbt.getInt("Age");
        Logger.INSTANCE.log(Level.INFO, "Reading custom nbt");

        Death death = DeathManager.getDeathFromFile(this.getServer(), playerUuid, deathUuid);
        if(death != null) this.setDeath(death);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        DeathManager.removeDeath(this.getServer(), this.getDeath());
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (DEATH.equals(data)) {
            this.setSkinTexture(this.getDeath().getSkinTexture());
        }
    }

    public Death getDeath() {
        return Death.fromNbt(this.getDataTracker().get(DEATH));
    }

    public void setDeath(Death death) {
        this.getDataTracker().set(DEATH, death.toNbt());
    }

    public boolean isSkeleton() {
        return this.getDataTracker().get(IS_SKELETON);
    }

    public CorpseSkeletonEntity getSkeletonEntity() {
        if(this.skeleton == null) {
            this.skeleton = new CorpseSkeletonEntity(this.getWorld(), this.getDeath());
        }
        return this.skeleton;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Death death = this.getDeath();
        if (!death.getInventory().isEmpty()) {
            player.openHandledScreen(new CorpseInventoryScreenHandlerFactory((syncId, playerInventory, playerEntity) -> new CorpseInventoryScreenHandler(syncId, playerInventory, death), Text.translatable("screen.corpse.corpse_of", death.getPlayerName()), death));
            return ActionResult.success(this.getWorld().isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3f;
    }
}
