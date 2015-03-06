package com.citelic.game.entity.player.content.controllers.impl.distractions.bosses;

import com.citelic.GameConstants;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class CorpBeastController extends Controller {

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 37929 || object.getId() == 38811) {
			removeController();
			player.stopAll();
			player.setNextTile(new Tile(2970, 4384, player.getZ()));
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
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
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						killer.removeDamage(player);
					}
					player.sendItemsOnDeath(player);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					player.setNextTile(new Tile(
							GameConstants.RESPAWN_PLAYER_LOCATION));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeController();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void start() {

	}
}