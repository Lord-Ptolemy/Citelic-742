package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class ClimbEmoteStairs extends Dialogue {

	private Tile upTile;
	private Tile downTile;
	private int emoteId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			player.useStairs(emoteId, upTile, 2, 3);
		else if (componentId == OPTION_2)
			player.useStairs(emoteId, downTile, 2, 2);
		end();
	}

	// uptile, downtile, climbup message, climbdown message, emoteid
	@Override
	public void start() {
		upTile = (Tile) parameters[0];
		downTile = (Tile) parameters[1];
		emoteId = (Integer) parameters[4];
		sendOptionsDialogue("What would you like to do?",
				(String) parameters[2], (String) parameters[3], "Never mind.");
	}

}
