package com.citelic.game.entity.player.content.controllers.impl.distractions.puropuro;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class PuroPuroController extends Controller {

	@Override
	public boolean login() {
		player.getPackets().sendMiniMapStatus(2);
		player.setNextTile(new Tile(2591, 4319, 0));
		return true;
	}

	@Override
	public boolean logout() {
		return true; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		player.getPackets().sendMiniMapStatus(0);
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getId() == 25014) {
			player.lock();
			player.setNextGraphics(new Graphics(1118));
			player.setNextAnimation(new Animation(6601));
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.setNextTile(new Tile(2427, 4446, 0));
					player.getPackets().sendMiniMapStatus(0);
					player.unlock();
					removeController();
					stop();
				}
			}, 9);
		}
		return false;
	}

	@Override
	public boolean sendDeath() {
		removeController();
		player.getPackets().sendMiniMapStatus(0);
		return true;
	}

	@Override
	public void start() {
		player.lock();
		player.setNextGraphics(new Graphics(1118));
		player.setNextAnimation(new Animation(6601));
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(new Tile(2591, 4319, 0));
				player.getPackets().sendMiniMapStatus(2);
				player.unlock();
				stop();
			}
		}, 9);
	}
}