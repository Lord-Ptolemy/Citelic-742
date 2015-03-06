package com.citelic.game.entity.player.content.controllers.impl;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class TutorialControler extends Controller {

	public int questStage = 0;

	@Override
	public void start() {
		
		 player.getInterfaceManager().replaceRealChatBoxInterface(372);
		 player.getPackets().sendIComponentText(372, 0, "Tutorial");
		 player.getPackets().sendIComponentText(372, 2,
		 "Welcome to Citelic!"); player.getPackets().sendIComponentText(372,
		 3, "This is a Tutorial which will configure your account");
		 player.getPackets().sendIComponentText(372, 4,
		 "to your preferences. To start, talk to Major Nigel Corothers");
		 player.getPackets().sendIComponentText(372, 5,
		 "You can locate him by the Hint Icon.");
		 player.getPackets().sendIComponentText(372, 1, "");
		 player.getPackets().sendIComponentText(372, 6, "");
		 player.getHintIconsManager().addHintIcon(2886, 3530, 0, 10, 1, 1, -1, false);
	}
	
	@Override
	public boolean processNPCClick1(NPC npc) {
		if(npc.getId() == 14850)
			player.print("Hi");
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't use teleports right now.");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't use teleports right now.");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		player.getPackets().sendGameMessage(
				"You can't do that while you're jailed!");
		return false;
	}
	
	@Override
	public void sendInterfaces() {

	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}
