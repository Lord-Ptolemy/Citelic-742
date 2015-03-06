package com.citelic.game.entity.player.content.controllers.impl;

import com.citelic.GameConstants;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class JailController extends Controller {

	public static void stopControler(Player p) {
		p.getControllerManager().getController().removeController();
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void process() {
		if (player.getJailed() <= Utilities.currentTimeMillis()) {
			player.getControllerManager().getController().removeController();
			player.getPackets().sendGameMessage(
					"Your account has been unjailed.", true);
			player.setNextTile(GameConstants.RESPAWN_PLAYER_LOCATION);
		}
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		player.getPackets().sendGameMessage(
				"You can't do that while you're jailed!");
		return false;
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
	public boolean sendDeath() {
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					player.reset();
					player.setCanPvp(false);
					player.sendRandomJail(player);
					player.unlock();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void start() {
		if (player.getJailed() > Utilities.currentTimeMillis()) {
			player.sendRandomJail(player);
			player.getPackets().sendGameMessage(
					"Apply on forums to get unjailed!");
		}
	}

}