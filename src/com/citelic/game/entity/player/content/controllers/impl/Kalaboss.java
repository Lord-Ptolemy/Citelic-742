package com.citelic.game.entity.player.content.controllers.impl;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;

public class Kalaboss extends Controller {

	public static boolean isAtKalaboss(Tile tile) {
		return tile.getX() >= 3385 && tile.getX() <= 3513
				&& tile.getY() >= 3605 && tile.getY() <= 3794;
	}

	private boolean showingOption;

	@SuppressWarnings("unused")
	@Override
	public boolean canPlayerOption1(Player target) {
		if (true) {
			return true;
		}
		player.setNextFaceTile(target);
		player.getPackets().sendGameMessage("You can't do that right now.");
		return false;
	}

	@Override
	public void forceClose() {
		setInviteOption(false);
	}

	@Override
	public boolean login() {
		moved();
		return false;
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void moved() {
		if (player.getX() == 3385 && player.getY() == 3615) {
			setInviteOption(false);
			removeController();
			player.getControllerManager().startController("Wilderness");
		} else {
			if (!isAtKalaboss(player)) {
				setInviteOption(false);
				removeController();
			} else
				setInviteOption(true);
		}
	}

	@Override
	public boolean sendDeath() {
		setInviteOption(false);
		removeController();
		return true;
	}

	public void setInviteOption(boolean show) {
		if (show == showingOption)
			return;
		showingOption = show;
		player.getPackets()
				.sendPlayerOption(show ? "Invite" : "null", 1, false);
	}

	@Override
	public void start() {
		setInviteOption(true);
	}
}
