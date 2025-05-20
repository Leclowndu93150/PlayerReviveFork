package com.creativemd.playerrevive.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpenDownedInventoryPacket extends CreativeCorePacket {

    private String uuid;

    public OpenDownedInventoryPacket(EntityPlayer target) {
        this.uuid = target.getUniqueID().toString();
    }

    public OpenDownedInventoryPacket() {
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        writeString(buf, uuid);
    }

    @Override
    public void readBytes(ByteBuf buf) {
        uuid = readString(buf);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void executeClient(EntityPlayer player) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("uuid", uuid);
        com.creativemd.creativecore.common.gui.opener.GuiHandler.openGui("pldownedinv", nbt, player);
    }

    @Override
    public void executeServer(EntityPlayer player) {
    }
}