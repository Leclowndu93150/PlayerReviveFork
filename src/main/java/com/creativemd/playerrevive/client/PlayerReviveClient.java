package com.creativemd.playerrevive.client;

import com.creativemd.playerrevive.server.PlayerReviveServer;

import net.minecraftforge.common.MinecraftForge;

public class PlayerReviveClient extends PlayerReviveServer {
	
	@Override
	public void loadSide() {
		MinecraftForge.EVENT_BUS.register(new ReviveEventClient());
	}
	
}
