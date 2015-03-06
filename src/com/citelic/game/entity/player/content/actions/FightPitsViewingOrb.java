package com.citelic.game.entity.player.content.actions;

import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

public class FightPitsViewingOrb extends Action {

	public static final Tile[] ORB_TELEPORTS = { new Tile(4571, 5092, 0),
			new Tile(4571, 5107, 0), new Tile(4590, 5092, 0),
			new Tile(4571, 5077, 0), new Tile(4557, 5092, 0) };

	private Tile tile;

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.getPackets().sendGameMessage(
					"You can't use orb while you're poisoned.");
			return false;
		}
		if (player.getFamiliar() != null) {
			player.getPackets().sendGameMessage(
					"You can't use orb with a familiar.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		tile = new Tile(player);
		player.getGlobalPlayerUpdate().switchHidden();
		player.getPackets().sendMiniMapStatus(5);
		player.setNextTile(ORB_TELEPORTS[0]);
		player.getInterfaceManager().sendInventoryInterface(374);
		return true;
	}

	@Override
	public void stop(final Player player) {
		player.lock(2);
		player.getInterfaceManager().closeInventoryInterface();
		player.getGlobalPlayerUpdate().switchHidden();
		player.getPackets().sendMiniMapStatus(0);
		player.setNextTile(tile);
	}

}
