package com.creativemd.playerrevive.server;

import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.playerrevive.CapaReviveProvider;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.api.DamageBledToDeath;
import com.creativemd.playerrevive.api.IRevival;

import com.creativemd.playerrevive.gui.PlayerReviveGuiHandler;
import com.creativemd.playerrevive.packet.PlayerReviveNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ReviveEventServer {

	private static Boolean isClient = null;

	public static boolean isClient() {
		if (isClient == null) {
			try {
				isClient = Class.forName("net.minecraft.client.Minecraft") != null;
			} catch (ClassNotFoundException e) {
				isClient = false;
			}
		}
		return isClient;
	}

	public static boolean isReviveActive() {
		if (isClient())
			return !isSinglePlayer();
		return true;
	}

	@SideOnly(Side.CLIENT)
	private static boolean isSinglePlayer() {
		return Minecraft.getMinecraft().isSingleplayer() && !Minecraft.getMinecraft().getIntegratedServer().getPublic();
	}

	public static MinecraftServer getMinecraftServer() {
		if (isClient())
			return getMinecraftServerClient();
		return FMLServerHandler.instance().getServer();
	}

	@SideOnly(Side.CLIENT)
	private static MinecraftServer getMinecraftServerClient() {
		return Minecraft.getMinecraft().getIntegratedServer();
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.START && event.side == Side.SERVER && isReviveActive()) {
			EntityPlayer player = event.player;
			if (player.isDead)
				return;
			IRevival revive = PlayerReviveServer.getRevival(player);

			if (!revive.isHealty()) {
				revive.tick();

				if (revive.getTimeLeft() % 20 == 0)
					PlayerReviveServer.sendUpdatePacket(player);

				if (PlayerRevive.CONFIG.usePercentageHealth) {
					float maxHealth = player.getMaxHealth();
					float newHealth = maxHealth * (PlayerRevive.CONFIG.playerDownedHealthPercentage / 100.0f);
					if (player.getHealth() > newHealth) {
						player.setHealth(newHealth);
					}
					player.getFoodStats().setFoodLevel(PlayerRevive.CONFIG.playerFoodAfter);
				} else if (player.getHealth() > PlayerRevive.CONFIG.playerDownedHealth) {
					player.setHealth(PlayerRevive.CONFIG.playerDownedHealth);
					player.getFoodStats().setFoodLevel(PlayerRevive.CONFIG.playerFoodAfter);
				}

				if (!PlayerRevive.CONFIG.allowDamageWhileBleeding) {
					player.capabilities.disableDamage = true;
					player.setEntityInvulnerable(true);
				}

				if (revive.isRevived())
					PlayerReviveServer.revive(player);
				else if (revive.isDead())
					PlayerReviveServer.kill(player);
			}
		}
	}

	@SubscribeEvent
	public void playerLeave(PlayerLoggedOutEvent event) {
		IRevival revive = PlayerReviveServer.getRevival(event.player);
		if (!revive.isHealty())
			PlayerReviveServer.kill(event.player);
		if (!event.player.world.isRemote)
			PlayerReviveServer.removePlayerAsHelper(event.player);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void playerInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof EntityPlayer && !event.getWorld().isRemote) {
			EntityPlayer target = (EntityPlayer) event.getTarget();
			EntityPlayer player = event.getEntityPlayer();
			IRevival revive = PlayerReviveServer.getRevival(target);

			if (!revive.isHealty()) {
				if (player.isSneaking()) {
					player.openGui(PlayerRevive.instance,
							PlayerReviveGuiHandler.GUI_DOWNED_INVENTORY,
							player.world,
							target.getEntityId(), 0, 0);
					event.setCanceled(true);
				} else if (!PlayerReviveServer.isPlayerBleeding(player)) {
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("uuid", EntityPlayer.getUUID(target.getGameProfile()).toString());
					revive.getRevivingPlayers().add(player);
					GuiHandler.openGui("plreviver", nbt, player);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event) {
		if (event.getTarget() instanceof EntityPlayer && !event.getEntityPlayer().world.isRemote) {
			EntityPlayer target = (EntityPlayer) event.getTarget();
			EntityPlayer attacker = event.getEntityPlayer();
			IRevival revive = PlayerReviveServer.getRevival(target);

			if (!revive.isHealty()) {
				float damage = (float) attacker.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

				/*
				if (attacker.getHeldItemMainhand() != null) {
					damage += net.minecraftforge.common.ForgeHooks.getEnchantPower(attacker.getInventory().armorInventory, attacker.getLastDamageSource());
				}*/

				if (target.getHealth() <= damage) {
					PlayerReviveServer.kill(target);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void playerDamage(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			IRevival revive = PlayerReviveServer.getRevival(player);

			if (!revive.isHealty()) {
				if (player.getHealth() <= event.getAmount()) {
					PlayerReviveServer.kill(player);
					event.setCanceled(true);
					return;
				}

				if (!PlayerRevive.CONFIG.allowDamageWhileBleeding ||
						event.getSource() == DamageBledToDeath.bledToDeath ||
						PlayerRevive.CONFIG.bypassDamageSources.contains(event.getSource().damageType)) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerDied(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer && isReviveActive() && !event.getEntityLiving().world.isRemote && event.getSource() != DamageBledToDeath.bledToDeath && !PlayerRevive.CONFIG.bypassDamageSources.contains(event.getSource().damageType)) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			IRevival revive = PlayerReviveServer.getRevival(player);

			if (revive.isDead()) {
				revive.stopBleeding();
				return;
			}

			PlayerReviveServer.startBleeding(player, event.getSource());

			if (PlayerRevive.CONFIG.resistanceTime > 0 && PlayerRevive.CONFIG.resistanceStrength >= 0) {
				player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("resistance"),
						PlayerRevive.CONFIG.resistanceTime,
						PlayerRevive.CONFIG.resistanceStrength));
			}

			if (PlayerRevive.CONFIG.usePercentageHealth) {
				float maxHealth = player.getMaxHealth();
				float newHealth = maxHealth * (PlayerRevive.CONFIG.playerDownedHealthPercentage / 100.0f);
				player.setHealth(Math.max(0.5F, newHealth));
			} else {
				player.setHealth(Math.max(0.5F, PlayerRevive.CONFIG.playerDownedHealth));
			}

			if (PlayerRevive.CONFIG.allowDamageWhileBleeding) {
				player.capabilities.disableDamage = false;
				player.setEntityInvulnerable(false);
			} else {
				player.capabilities.disableDamage = true;
				player.setEntityInvulnerable(true);
			}

			if (player.isRiding())
				player.dismountRidingEntity();

			event.setCanceled(true);

			if (!PlayerRevive.CONFIG.disableBleedingMessage)
				player.getServer().getPlayerList().sendMessage(new TextComponentString(player.getDisplayNameString() + " is bleeding..."));
		}
	}

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(PlayerRevive.modid, "revive"), new CapaReviveProvider());
		}
	}
}