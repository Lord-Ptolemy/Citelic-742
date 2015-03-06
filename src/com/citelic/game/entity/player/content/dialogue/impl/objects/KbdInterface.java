package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class KbdInterface extends Dialogue {

	private GameObject artefact;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1361 && componentId == 13) {
			Magic.sendNormalTeleportSpell(player, 0, 0, new Tile(2273, 4681, 0));
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.faceObject(artefact);
				}
			}, 2);
		} else
			player.getPackets().sendGameMessage(
					"Good choice you did not click go.");
		player.closeInterfaces();
		end();
	}

	@Override
	public void start() {
		artefact = (GameObject) parameters[0];
		player.getInterfaceManager().sendInterface(1361);
	}
}