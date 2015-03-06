package com.citelic.game.entity.player.content.controllers.impl.distractions.sc;

import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;

/**
 * @author Richard
 * @author Khaled
 * 
 */
public class StealingCreationLobby extends Controller {

	@Override
	public void forceClose() {
		if ((boolean) getArguments()[0])
			StealingCreation.getRedTeam().remove(player);
		else
			StealingCreation.getRedTeam().remove(player);
		StealingCreation.updateInterfaces();
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	// TODO object click for exit

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(804,
				player.getInterfaceManager().hasRezizableScreen() ? 11 : 27);// TODO
		// find
		// correct
		// one
		StealingCreation.updateInterfaces();
	}

	@Override
	public void start() {
		if ((boolean) getArguments()[0])
			StealingCreation.getRedTeam().add(player);
		else
			StealingCreation.getRedTeam().add(player);
		sendInterfaces();
	}
}
