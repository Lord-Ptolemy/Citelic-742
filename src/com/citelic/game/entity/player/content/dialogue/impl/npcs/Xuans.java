package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class Xuans extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Xuan", "Open Loyalty Shop",
					"How many points do I currently have?",
					"How do I earn Loyalty Points?");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 28);
				end();
			}
			if (componentId == OPTION_2) {
				sendNPCDialogue(npcId, 9827,
						"You currently have " + player.getLoyaltyPoints()
								+ " Loyalty Points.");
				stage = 3;
			}
			if (componentId == OPTION_3) {
				sendNPCDialogue(npcId, 9827,
						"The only way to get Loyalty Points is by playing for 30 minutes.");
				stage = 3;
			}
		} else if (stage == 3) {
			end();
		}
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(
				npcId,
				9827,
				"Hello "
						+ player.getDisplayName()
						+ ", I am Xuan, the loyalty master of the server, How can I help you?");
	}
}