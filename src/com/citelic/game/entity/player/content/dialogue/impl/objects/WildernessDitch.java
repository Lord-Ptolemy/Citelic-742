package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class WildernessDitch extends Dialogue {

	private GameObject ditch;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {

	}

	@Override
	public void start() {
		ditch = (GameObject) parameters[0];
		player.stopAll();
		player.lock(4);
		player.setNextAnimation(new Animation(6132));
		final Tile toTile = new Tile(ditch.getRotation() == 3
				|| ditch.getRotation() == 1 ? ditch.getX() - 1 : player.getX(),
				ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ditch
						.getY() + 2 : player.getY(), ditch.getZ());
		player.setNextForceMovement(new ForceMovement(
				new Tile(player),
				1,
				toTile,
				2,
				ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ForceMovement.NORTH
						: ForceMovement.WEST));
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(toTile);
				player.faceObject(ditch);
				player.getControllerManager().startController("Wilderness");
				player.resetReceivedDamage();
			}
		}, 2);
		end();
	}
}