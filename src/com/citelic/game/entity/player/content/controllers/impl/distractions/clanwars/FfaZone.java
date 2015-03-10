package com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

/**
 * Handles the FFA Clan Wars zone.
 * 
 * @author Emperor
 * 
 */
public final class FfaZone extends Controller {

	/**
	 * If the FFA zone is the risk zone.
	 */
	private boolean risk;

	/**
	 * If the player was in the ffa pvp area.
	 */
	private transient boolean wasInArea;

	@Override
	public void start() {
		if (getArguments() == null || getArguments().length < 1) {
			this.risk = player.getX() >= 2948 && player.getY() >= 5508
					&& player.getX() <= 3071 && player.getY() <= 5631;
		} else {
			this.risk = (Boolean) getArguments()[0];
		}
		moved();
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().closeOverlay(false);
		player.getInterfaceManager().sendOverlay(789,
				player.getInterfaceManager().hasRezizableScreen());
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					if (risk) {
						Player killer = player
								.getMostDamageReceivedSourcePlayer();
						if (killer != null) {
							killer.removeDamage(player);
							if (risk)
								player.sendItemsOnDeath(killer);
						}
					}
					player.setNextTile(new Tile(2993, 9679, 0));
					player.getControllerManager().startController(
							"clan_wars_request");
					player.reset();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 38700:
			player.setNextTile(new Tile(2993, 9679, 0));
			player.getControllerManager().forceStop();
			player.getControllerManager().startController("clan_wars_request");
			return false;
		}
		return true;
	}

	@Override
	public void moved() {
		boolean inArea = inPvpArea(player);
		if (inArea && !wasInArea) {
			player.setCanPvp(true);
			wasInArea = true;
			Wilderness.checkBoosts(player);
		} else if (!inArea && wasInArea) {
			player.setCanPvp(false);
			wasInArea = false;
		}
	}

	@Override
	public boolean keepCombating(Entity victim) {
		if (!(victim instanceof Player))
			return true;
		return player.isCanPvp() && ((Player) victim).isCanPvp();
	}

	@Override
	public void forceClose() {
		player.setCanPvp(false);
		player.getInterfaceManager().closeOverlay(true);
	}

	@Override
	public boolean logout() {
		setArguments(new Object[] { risk });
		return false;
	}

	@Override
	public boolean login() {
		moved();
		sendInterfaces();
		return false;
	}

	/**
	 * Checks if the location is in a ffa pvp zone.
	 * 
	 * @param t
	 *            The world tile.
	 * @return {@code True} if so.
	 */
	public static boolean inPvpArea(Tile t) {
		return (t.getX() >= 2948 && t.getY() >= 5512 && t.getX() <= 3071 && t
				.getY() <= 5631) // Risk
				// area.
				|| (t.getX() >= 2756 && t.getY() >= 5512 && t.getX() <= 2879 && t
						.getY() <= 5631); // Safe
		// area.
	}

	/**
	 * Checks if the location is in a ffa zone.
	 * 
	 * @param t
	 *            The world tile.
	 * @return {@code True} if so.
	 */
	public static boolean inArea(Tile t) {
		return (t.getX() >= 2948 && t.getY() >= 5508 && t.getX() <= 3071 && t
				.getY() <= 5631) // Risk
				// area.
				|| (t.getX() >= 2756 && t.getY() >= 5508 && t.getX() <= 2879 && t
						.getY() <= 5631); // Safe
		// area.
	}

	/**
	 * Checks if a player's overload effect is changed (due to being in the risk
	 * ffa zone, in pvp)
	 * 
	 * @param player
	 *            The player.
	 * @return {@code True} if so.
	 */
	public static boolean isOverloadChanged(Player player) {
		if (!(player.getControllerManager().getController() instanceof FfaZone)) {
			return false;
		}
		return player.isCanPvp()
				&& ((FfaZone) player.getControllerManager().getController()).risk;
	}
}