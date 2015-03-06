package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class ClimbNoEmoteStairs extends Dialogue {

	private Tile upTile;
	private Tile downTile;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			player.useStairs(-1, upTile, 0, 1);
		} else if (componentId == OPTION_2)
			player.useStairs(-1, downTile, 0, 1);
		end();
	}

	// uptile, downtile, climbup message, climbdown message, emoteid
	@Override
	public void start() {
		upTile = (Tile) parameters[0];
		downTile = (Tile) parameters[1];
		sendOptionsDialogue("What would you like to do?",
				(String) parameters[2], (String) parameters[3], "Never mind.");
	}

}
