package com.creativemd.playerrevive.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class PlayerReviveGuiHandler implements IGuiHandler {

    public static final int GUI_DOWNED_INVENTORY = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_DOWNED_INVENTORY:
                 
                EntityPlayer downedPlayer = null;
                for (EntityPlayer entityPlayer : world.playerEntities) {
                    if (entityPlayer.getEntityId() == x) {
                        downedPlayer = entityPlayer;
                        break;
                    }
                }

                if (downedPlayer != null) {
                    return new ContainerDownedInventory(player, downedPlayer);
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_DOWNED_INVENTORY:
                 
                EntityPlayer downedPlayer = null;
                for (EntityPlayer entityPlayer : world.playerEntities) {
                    if (entityPlayer.getEntityId() == x) {
                        downedPlayer = entityPlayer;
                        break;
                    }
                }

                if (downedPlayer != null) {
                    return new GuiDownedInventory(player, downedPlayer);
                }
                return null;
            default:
                return null;
        }
    }
}