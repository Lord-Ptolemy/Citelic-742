package com.citelic.game.entity.player.content.controllers.impl.distractions.castlewars;

import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.CastleWars;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class CastleWarsWaiting extends Controller {

	private int team;

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == Equipment.SLOT_CAPE || slotId == Equipment.SLOT_HAT) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	// You can't leave just like that!

	@Override
	public void forceClose() {
		leave();
	}

	public void leave() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 34 : 0);
		CastleWars.removeWaitingPlayer(player, team);
	}

	@Override
	public boolean logout() {
		player.setLocation(new Tile(CastleWars.LOBBY, 2));
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		leave();
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (interfaceId == 387) {
			if (componentId == 37)
				return false;
			if (componentId == 9 || componentId == 6) {
				player.getPackets().sendGameMessage(
						"You can't remove your team's colours.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		int id = object.getId();
		if (id == 4389 || id == 4390) {
			removeController();
			leave();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean sendDeath() {
		removeController();
		leave();
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 34 : 0, 57);
	}

	@Override
	public void start() {
		team = (int) getArguments()[0];
		sendInterfaces();
	}
}