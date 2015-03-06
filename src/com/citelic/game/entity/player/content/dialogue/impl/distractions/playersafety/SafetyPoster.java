package com.citelic.game.entity.player.content.dialogue.impl.distractions.playersafety;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class SafetyPoster extends Dialogue {

	@Override
	public void start() {
		sendDialogue("There appears to be a tunnel behind the poster.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			player.setNextTile(new Tile(3140, 4230, 2));
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
