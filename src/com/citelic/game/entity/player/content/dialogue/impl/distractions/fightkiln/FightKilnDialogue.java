package com.citelic.game.entity.player.content.dialogue.impl.distractions.fightkiln;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class FightKilnDialogue extends Dialogue {

	@Override
	public void finish() {
		player.getControllerManager().startController("FightKilnControler", 0);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void start() {
		player.lock();
		sendDialogue("You journey directly to the Kiln.");
	}

}
