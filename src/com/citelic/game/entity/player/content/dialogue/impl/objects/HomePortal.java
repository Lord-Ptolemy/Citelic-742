package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class HomePortal extends Dialogue {

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			end();
			break;
		}
	}

	@Override
	public void start() {
		stage = 0;
		// sendOptionsDialogue("Where do you want to teleport?", "Clanwars",
		// "Wilderness Ditch");
		player.setNextTile(new Tile(3100, 3519, 0));
		sendDialogue("GET WILDERNESS PKING ACTIVE", "//JESPER");
	}
}