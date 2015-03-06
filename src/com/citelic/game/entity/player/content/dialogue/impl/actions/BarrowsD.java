package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class BarrowsD extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					"Yes, I'm fearless.", "No way, that looks scary!");
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				player.setNextTile(new Tile(3534, 9677, 0));
			end();
		}
	}

	@Override
	public void start() {
		sendDialogue("You've found a hidden tunnel, do you want to enter?");
	}

}
