package com.creativemd.playerrevive.gui;

import com.creativemd.playerrevive.PlayerRevive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDownedInventory extends Container {

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {
            EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
    };

    private final EntityPlayer downedPlayer;
    private final EntityPlayer interactingPlayer;
    private final int numRows = 6;

    public ContainerDownedInventory(EntityPlayer interactingPlayer, EntityPlayer downedPlayer) {
        this.downedPlayer = downedPlayer;
        this.interactingPlayer = interactingPlayer;

        int lvt_4_1_ = (this.numRows - 4) * 18;

        for (int i = 0; i < 4; i++) {
            final EntityEquipmentSlot equipmentSlot = VALID_EQUIPMENT_SLOTS[i];
            this.addSlotToContainer(new Slot(downedPlayer.inventory, 36 + (3 - i), 8, 18 + i * 18) {
                @Override
                public int getSlotStackLimit() { return 1; }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack.isEmpty() || isBlacklisted(stack)) return false;
                    return stack.getItem().isValidArmor(stack, equipmentSlot, downedPlayer);
                }

                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return !isBlacklisted(this.getStack());
                }

                @Override
                @SideOnly(Side.CLIENT)
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[equipmentSlot.getIndex()];
                }
            });
        }

        this.addSlotToContainer(new Slot(downedPlayer.inventory, 40, 8, 18 + 4 * 18) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return !isBlacklisted(stack);
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return !isBlacklisted(this.getStack());
            }

            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 1; col < 9; col++) {
                this.addSlotToContainer(new Slot(downedPlayer.inventory,
                        9 + (row * 9) + (col - 1),
                        8 + col * 18,
                        18 + row * 18) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return !isBlacklisted(stack);
                    }

                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn) {
                        return !isBlacklisted(this.getStack());
                    }
                });
            }
        }

        for (int col = 1; col < 9; col++) {
            this.addSlotToContainer(new Slot(downedPlayer.inventory,
                    col - 1,
                    8 + col * 18,
                    18 + 3 * 18) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return !isBlacklisted(stack);
                }

                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return !isBlacklisted(this.getStack());
                }
            });
        }

        this.addSlotToContainer(new SlotGlassPane(8, 18 + 5 * 18));

        for (int row = 4; row < numRows; row++) {
            for (int col = 1; col < 9; col++) {
                if (row == 4 && col == 0) continue;
                this.addSlotToContainer(new SlotGlassPane(8 + col * 18, 18 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(interactingPlayer.inventory, col + row * 9 + 9,
                        8 + col * 18, 103 + row * 18 + lvt_4_1_) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return !isBlacklisted(stack);
                    }

                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn) {
                        return !isBlacklisted(this.getStack());
                    }
                });
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(interactingPlayer.inventory, col,
                    8 + col * 18, 161 + lvt_4_1_) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return !isBlacklisted(stack);
                }

                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return !isBlacklisted(this.getStack());
                }
            });
        }
    }

    private boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation itemName = stack.getItem().getRegistryName();
        if (itemName == null) return false;
        return PlayerRevive.CONFIG.blacklistedItems.contains(itemName.toString());
    }

    private class SlotGlassPane extends Slot {
        private final ItemStack BLACK_GLASS_PANE = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15);

        public SlotGlassPane(int xPosition, int yPosition) {
            super(null, -1, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return false;
        }

        @Override
        public ItemStack getStack() {
            return BLACK_GLASS_PANE;
        }

        @Override
        public void putStack(ItemStack stack) {
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean getHasStack() {
            return true;
        }

        @Override
        public void onSlotChanged() {
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack() && !(slot instanceof SlotGlassPane)) {
            ItemStack slotStack = slot.getStack();

            if (isBlacklisted(slotStack)) {
                return ItemStack.EMPTY;
            }

            itemstack = slotStack.copy();

            if (index < 41) {
                if (!this.mergeItemStack(slotStack, 41 + numRows, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) slotStack.getItem();
                    int armorIndex = 3 - armor.armorType.getIndex();

                    if (!this.getSlot(armorIndex).getHasStack() &&
                            this.mergeItemStack(slotStack, armorIndex, armorIndex + 1, false)) {
                        return itemstack;
                    }
                }

                int startSlot = 5;
                int endSlot = 37;

                if (!this.mergeItemStack(slotStack, startSlot, endSlot, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        if (isBlacklisted(stack)) {
            return false;
        }
        return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
    }
}