package com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol;

import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public final class PestControlLobby extends Controller {

	private int landerId;

	@Override
	public boolean canSummonFamiliar() {
		player.getPackets()
				.sendGameMessage(
						"You feel it's best to keep your Familiar away during this game.");
		return false;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().closeOverlay(false);
		Lander.getLanders()[landerId].exitLander(player);
	}

	@Override
	public boolean logout() {
		Lander.getLanders()[landerId].remove(player);// to stop the timer in the
		// lander and prevent
		// future errors
		return false;
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 14314:
		case 25629:
		case 25630:
			player.getDialogueManager().startDialogue("LanderD");
			return true;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getPackets().sendIComponentText(
				407,
				3,
				Utilities.fixChatMessage(Lander.getLanders()[landerId]
						.toString()));
		int minutesLeft = (Lander.getLanders()[landerId].getTimer()
				.getMinutes());
		player.getPackets().sendIComponentText(
				407,
				13,
				"Next Departure: " + minutesLeft + " minutes "
						+ (!(minutesLeft % 2 == 0) ? " 30 seconds" : ""));
		player.getPackets().sendIComponentText(
				407,
				14,
				"Player's Ready: "
						+ Lander.getLanders()[landerId].getPlayers().size());
		player.getPackets().sendIComponentText(407, 16,
				"Commendations: " + player.getPestPoints());
		player.getInterfaceManager().sendOverlay(407, false);
	}

	@Override
	public void start() {
		this.landerId = (Integer) getArguments()[0];
	}
}