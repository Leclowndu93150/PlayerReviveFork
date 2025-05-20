package com.creativemd.playerrevive.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDownedInventory extends GuiContainer {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final EntityPlayer downedPlayer;
    private final int inventoryRows = 6;

    public GuiDownedInventory(EntityPlayer interactingPlayer, EntityPlayer downedPlayer) {
        super(new ContainerDownedInventory(interactingPlayer, downedPlayer));
        this.downedPlayer = downedPlayer;
        this.allowUserInput = false;

        int lvt_3_1_ = 222;
        int lvt_4_1_ = 114;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = TextFormatting.RED + this.downedPlayer.getName() + "'s Inventory";
        this.fontRenderer.drawString(title, 8, 6, 4210752);
        this.fontRenderer.drawString("Your Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);

        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_ + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}