package com.creativemd.playerrevive;

import java.util.Arrays;
import java.util.List;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

public class PlayerReviveConfig {
	
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	@CreativeConfig.DecimalRange(min = 0, max = 2)
	public float volumeModifier = 1;
	
	@CreativeConfig
	public int playerReviveTime = 100;
	@CreativeConfig
	public int playerReviveSurviveTime = 1200;
	
	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 20)
	public int playerHealthAfter = 2;
	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 20)
	public int playerFoodAfter = 6;
	
	@CreativeConfig
	public boolean banPlayerAfterDeath = false;
	
	@CreativeConfig
	public float exhaustion = 0.5F;
	
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public boolean disableMusic = false;
	@CreativeConfig
	public boolean disableSounds = false;
	
	@CreativeConfig
	public boolean disableBleedingMessage = false;
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public boolean particleBeacon = false;
	
	@CreativeConfig
	public boolean disableGiveUp;
	@CreativeConfig
	public boolean disableDisconnect;
	
	@CreativeConfig
	public boolean allowCommandsWhileBleeding = false;
	
	@CreativeConfig
	public List<String> bypassDamageSources = Arrays.asList("gorgon", "death.attack.sgcraft:transient", "death.attack.sgcraft:iris");

	@CreativeConfig
	public boolean usePercentageHealth = false;

	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 20)
	public int playerDownedHealth = 4;

	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 100)
	public int playerDownedHealthPercentage = 50;

	@CreativeConfig
	public boolean allowDamageWhileBleeding = true;

	@CreativeConfig
	public List<String> blacklistedItems = Arrays.asList("minecraft:bedrock");

	@CreativeConfig
	@CreativeConfig.IntRange(min = 0, max = 6000)
	public int resistanceTime = 100;

	@CreativeConfig
	@CreativeConfig.IntRange(min = 0, max = 4)
	public int resistanceStrength = 4;

}
