package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class RfdPortal extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			end();
		}
	}

	@Override
	public void start() {
		stage = 1;
		sendDialogue("I think we should keep that door closed.");
	}

}