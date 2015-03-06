package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Bob extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "Can you repair my items for me?");
				break;
			case OPTION_2:
				stage = 2;
				sendPlayerDialogue(9827, "Nevermind");
				break;
			}
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(
					npcId,
					9827,
					"Of course I can, though the materials may cost you. Just hand me the item and I'll have a look.");
			break;
		case 2:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		stage = 0;
		sendOptionsDialogue("Select an Option",
				"Can you repair my items for me?", "Nevermind");
	}

}