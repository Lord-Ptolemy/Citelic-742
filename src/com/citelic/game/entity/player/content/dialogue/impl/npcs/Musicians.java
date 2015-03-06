package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.actions.resting.Listen;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Musicians extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"Yes please!", "No thank you.");
			break;
		case 0:
			if (componentId == Dialogue.OPTION_2) {
				stage = 1;
				sendPlayerDialogue(9827, "No thank you.");
			} else {
				stage = 2;
				sendPlayerDialogue(9827, "Yes please!");
			}
			break;
		case 1:
			end();
			break;
		case 2:
			player.getActionManager().setAction(new Listen());
			end();
			break;
		}
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello, " + player.getDisplayName() + "!",
				"Would you like to hear some of the finest music",
				"that is being played around the lands of Nevellion?");
	}
}