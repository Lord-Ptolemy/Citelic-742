package com.citelic.game.entity.player.content.dialogue.impl.simplemessages;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SimplePlayerMessageX extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		String message = (String) parameters[1];
		sendPlayerDialogue(npcId, message);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
