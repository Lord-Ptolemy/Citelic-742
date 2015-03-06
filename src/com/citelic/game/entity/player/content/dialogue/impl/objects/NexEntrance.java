package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.ZarosGodwars;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public final class NexEntrance extends Dialogue {

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(
					"There are currently " + ZarosGodwars.getPlayersCount()
							+ " people fighting.<br>Do you wish to join them?",
					"Climb down.", "Stay here.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.setNextTile(new Tile(2911, 5204, 0));
				player.getControllerManager().startController("ZGDController");
			}
			end();
		}

	}

	@Override
	public void start() {
		sendDialogue("The room beyond this point is a prison!",
				"There is no way out other than death or teleport.",
				"Only those who endure dangerous encounters should proceed.");
	}

}
