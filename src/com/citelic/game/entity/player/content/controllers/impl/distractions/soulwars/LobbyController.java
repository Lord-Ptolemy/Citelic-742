package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.containers.Inventory;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.PlayerType;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.Teams;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class LobbyController extends Controller {

	private boolean bool;

	@Override
	public void start() {
		bool = player.getInterfaceManager().hasRezizableScreen();
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 27 : 11,
				837);
		((LobbyTask) Engine.soulWars.getTasks().get(PlayerType.INSIDE_LOBBY))
				.getPlayers().add(player);
	}

	@Override
	public void sendInterfaces() {
		if (bool != player.getInterfaceManager().hasRezizableScreen()) {
			bool = player.getInterfaceManager().hasRezizableScreen();
			player.getInterfaceManager()
					.sendTab(
							player.getInterfaceManager().hasRezizableScreen() ? 27
									: 11, 837);
		}
		final int minutes = SoulWarsManager.MINUTES_BEFORE_NEXT_GAME.get();
		boolean noGame = minutes < 4;
		int blue, red;
		if (noGame) {
			LobbyTask task = (LobbyTask) Engine.soulWars.getTasks().get(
					PlayerType.INSIDE_LOBBY);
			blue = task.getPlayers(Teams.BLUE).size();
			red = task.getPlayers(Teams.RED).size();
		} else {
			GameTask task = (GameTask) Engine.soulWars.getTasks().get(
					PlayerType.IN_GAME);
			blue = task.getPlayers(Teams.BLUE).size();
			red = task.getPlayers(Teams.RED).size();
		}
		player.getPackets().sendGlobalConfig(632, noGame ? 0 : 1);
		player.getPackets().sendIComponentText(837, 9,
				"New game: " + minutes + " " + (minutes == 1 ? "min" : "mins"));
		if (noGame) {
			player.getPackets().sendGlobalConfig(633,
					blue + (10 - SoulWarsManager.REQUIRED_TEAM_MEMBERS));
			player.getPackets().sendGlobalConfig(634,
					red + (10 - SoulWarsManager.REQUIRED_TEAM_MEMBERS));
		} else {
			player.getPackets().sendIComponentText(837, 3, "" + blue);
			player.getPackets().sendIComponentText(837, 5, "" + red);
		}
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == Equipment.SLOT_CAPE) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	@Override
	public void forceClose() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 27 : 11);
		((LobbyTask) Engine.soulWars.getTasks().get(PlayerType.INSIDE_LOBBY))
				.getPlayers().remove(player);
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getPackets().sendGameMessage("You can't just leave like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getPackets().sendGameMessage("You can't just leave like that!");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		forceClose();
		removeController();
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (interfaceId == 590 & componentId == 8) {
			player.getPackets().sendGameMessage(
					"This is a battleground, not a circus.");
			return false;
		}
		if (interfaceId == 387 && componentId == 9) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
			return false;
		}
		if (interfaceId == 667 && componentId == 9) {
			player.getPackets().sendGameMessage(
					"You can't remove your team's colours.");
			return false;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE || interfaceId == 670) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null) { // shouldnt happen - cheat client
				if (item.getId() == SoulWarsManager.TEAM_CAPE_INDEX
						|| item.getId() == SoulWarsManager.TEAM_CAPE_INDEX + 1) {
					player.getPackets().sendGameMessage(
							"You can't remove your team's colours.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 42029:
		case 42030:
		case 42031:
			Engine.soulWars
					.passBarrier(PlayerType.INSIDE_LOBBY, player, object);
			return true;
		}
		return false;
	}

	@Override
	public boolean login() {
		player.getControllerManager().startController("AreaController");
		return false;
	}

	@Override
	public boolean logout() {
		Engine.soulWars.resetPlayer(player, PlayerType.INSIDE_LOBBY, true);
		return false;
	}
}