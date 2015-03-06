package com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;

public class DuelControler extends Controller {

	@Override
	public void start() {
		sendInterfaces();
		player.getPackets().sendPlayerOption("Challenge", 1, false);
		player.getGlobalPlayerUpdate().generateAppearenceData();
		moved();
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		remove();
	}

	@Override
	public void moved() {
		if (player.getRegionId() != 13363) {
			removeController();
			remove();
		}
	}

	@Override
	public boolean canPlayerOption1(final Player target) {
		player.stopAll();
		if (target.getInterfaceManager().containsScreenInter()) {
			player.getPackets().sendGameMessage("The other player is busy.");
			return false;
		}
		if (target.getTemporaryAttributtes().get("DuelChallenged") == player) {
			player.getControllerManager().removeControllerWithoutCheck();
			target.getControllerManager().removeControllerWithoutCheck();
			target.getTemporaryAttributtes().remove("DuelChallenged");
			player.setLastDuelRules(new DuelRules(player, target));
			target.setLastDuelRules(new DuelRules(target, player));
			player.getControllerManager().startController("DuelArena", target, target.getTemporaryAttributtes().get("DuelFriendly"));
			target.getControllerManager().startController("DuelArena", player, target.getTemporaryAttributtes().remove("DuelFriendly"));
			return false;
		}
		player.getTemporaryAttributtes().put("DuelTarget", target);
		player.getInterfaceManager().sendInterface(1369);
		player.getPackets().sendPlayerOnIComponent(1369, 8);
		player.getPackets().sendIComponentText(1369, 9, player.getDisplayName());
		player.getPackets().sendIComponentText(1369, 16, target.getDisplayName());
		player.getTemporaryAttributtes().put("WillDuelFriendly", true);
		for(int i = 0; i < 160; i++)
		player.getPackets().sendUnlockIComponentOptionSlots(1369, i, 0, 300, 0);
		return false;
	}

	public static void challenge(Player player) {
		player.closeInterfaces();
		Boolean friendly = (Boolean) player.getTemporaryAttributtes().remove(
				"WillDuelFriendly");
		if (friendly == null)
			return;
		Player target = (Player) player.getTemporaryAttributtes().remove(
				"DuelTarget");
		if (target == null
				|| target.hasFinished()
				|| !target.withinDistance(player, 14)
				|| !(target.getControllerManager().getController() instanceof DuelControler)) {
			player.getPackets().sendGameMessage(
					"Unable to find "
							+ (target == null ? "your target" : target
									.getDisplayName()));
			return;
		}
		player.getTemporaryAttributtes().put("DuelChallenged", target);
		player.getTemporaryAttributtes().put("DuelFriendly", friendly);
		player.getPackets().sendGameMessage(
				"Sending " + target.getDisplayName() + " a request...");
		target.getPackets().sendDuelChallengeRequestMessage(player, friendly);
	}

	public void remove() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 11 : 27);
		player.getAppearence().generateAppearenceData();
		player.getPackets().sendPlayerOption("null", 1, false);
	}

	@Override
	public void sendInterfaces() {
		if (player.getRegionId() == 13363) {
			player.getInterfaceManager().sendOverlay(1362,
					player.getInterfaceManager().hasRezizableScreen());
		}
	}

	public static boolean isAtDuelArena(Tile player) {
		return (player.getX() >= 3355 && player.getX() <= 3360
				&& player.getY() >= 3267 && player.getY() <= 3279)
				|| (player.getX() >= 3355 && player.getX() <= 3379
				&& player.getY() >= 3272 && player.getY() <= 3279)
				|| (player.getX() >= 3374 && player.getX() <= 3379
				&& player.getY() >= 3267 && player.getY() <= 3271);
	}
}
