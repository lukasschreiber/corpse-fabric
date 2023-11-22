package org.lukasschreiber.corpsefabric.gui;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.death.Death;

import java.util.ArrayList;
import java.util.List;

public class HistoryScreenHandler extends ScreenHandler {
    public static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;
    public static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;

    private final List<Death> deaths;
    private int selectedDeathIndex = 0;
    private final PlayerEntity player;

    static {
        EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }

    public HistoryScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new ArrayList<>());
        NbtCompound compound = buf.readNbt();
        if(compound == null) return;

        NbtList deathList = compound.getList("Deaths", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < deathList.size(); i++) {
            this.deaths.add(Death.fromNbt(deathList.getCompound(i)));
        }

        this.init();
    }

    public HistoryScreenHandler(int syncId, PlayerInventory inventory, List<Death> deaths) {
        super(Corpse.HISTORY_SCREEN_HANDLER_TYPE, syncId);
        this.deaths = deaths;
        this.player = inventory.player;
        this.init();
    }

    private void init() {
        this.selectedDeathIndex = deaths.stream().filter(death -> death.getPos().distanceTo(this.player.getPos()) < 2).findFirst().map(this.deaths::indexOf).orElse(0);
        Inventory inventory = this.deaths.isEmpty() ? new SimpleInventory() : this.deaths.get(this.selectedDeathIndex).getInventory();
        this.initSlots(inventory);
    }

    private void initSlots(Inventory inventory) {
        this.slots.clear();
        for (int i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(inventory, 3 - i, 8, 8 + i * 18) {
                public void setStack(ItemStack stack) {
                    super.setStack(stack);
                }

                public int getMaxItemCount() {
                    return 1;
                }

                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }

                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
                }

                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, HistoryScreenHandler.EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i + 4, 8 + i * 18, 142));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9 + 4, 8 + j * 18, 84 + i * 18));
            }
        }

        this.addSlot(new Slot(inventory, 40, 77, 62) {
            public void setStack(ItemStack stack) {
                super.setStack(stack);
            }

            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }

    public void setInventory(Inventory inventory) {
        // Math.min(this.inventorySize, inventory.size())
        int revision = this.nextRevision();
        //TODO: needs to account for armor slots and offhand

        for (int i = 0; i < 40; i++) {
            this.setStackInSlot(i, revision, inventory.getStack(i < 4 ? 3 - i : i));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public List<Death> getDeaths() {
        return deaths;
    }

    public int getSelectedDeathIndex() {
        return selectedDeathIndex;
    }

    public void setSelectedDeathIndex(int selectedDeathIndex) {
        this.selectedDeathIndex = selectedDeathIndex;
    }
}
