package com.citelic.game.entity.player.content.controllers.impl.distractions.battleterrace;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;

public class battleTerraceWaiting extends Controller {

	public static void leaveGame(Player player) {
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getEquipment().refresh();
		player.lock(2);
		player.setCanPvp(false);
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getEquipment().init();
		player.reset();
		player.getInterfaceManager().closeOverlay(false);
		player.setNextTile(new Tile(1718, 5599, 0));
		BattleTerrace.waiting.remove(player);
		player.getPackets().sendGameMessage("You have left the waiting room.");
	}

	@Override
	public boolean logout() {
		leaveGame(player);
		removeController();
		return true;
	}

	@Override
	public void start() {
		player.setNextTile(new Tile(1653, 5600, 0));
	}

}