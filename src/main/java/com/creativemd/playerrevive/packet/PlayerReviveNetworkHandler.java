package com.creativemd.playerrevive.packet;

import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.gui.PlayerReviveGuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerReviveNetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(PlayerRevive.modid);
    private static int packetId = 0;

    public static void init() {
        INSTANCE.registerMessage(OpenDownedInventoryPacket.Handler.class, OpenDownedInventoryPacket.class, packetId++, Side.CLIENT);
    }

    public static class OpenDownedInventoryPacket implements IMessage {
        private int downedPlayerId;

        public OpenDownedInventoryPacket() {}

        public OpenDownedInventoryPacket(int downedPlayerId) {
            this.downedPlayerId = downedPlayerId;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            downedPlayerId = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(downedPlayerId);
        }

        public static class Handler implements IMessageHandler<OpenDownedInventoryPacket, IMessage> {
            @Override
            @SideOnly(Side.CLIENT)
            public IMessage onMessage(OpenDownedInventoryPacket message, MessageContext ctx) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    player.openGui(PlayerRevive.instance,
                            PlayerReviveGuiHandler.GUI_DOWNED_INVENTORY,
                            player.world,
                            message.downedPlayerId, 0, 0);
                });
                return null;
            }
        }
    }

    public static void openDownedInventory(EntityPlayerMP player, EntityPlayer downedPlayer) {
        player.openGui(PlayerRevive.instance,
                PlayerReviveGuiHandler.GUI_DOWNED_INVENTORY,
                player.world,
                downedPlayer.getEntityId(), 0, 0);
    }
}