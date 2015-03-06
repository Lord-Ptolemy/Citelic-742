package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class KBDArtifact extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 13) {
			Magic.pushLeverTeleport(player, new Tile(2273, 4681, 0), 827,
					"You activate the artefact...",
					"and teleport into the lair of the King Black Dragon!");
		}
		player.stopAll();
	}

	@Override
	public void start() {
		player.getInterfaceManager().sendInterface(1361);
	}

}
