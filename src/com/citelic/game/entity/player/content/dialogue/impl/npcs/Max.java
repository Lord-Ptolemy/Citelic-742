package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.Utilities;

public class Max extends Dialogue {

	public static int checkAllStats(Player player) {
		int reqs = 0;
		for (int i = 0; i < 25; i++) {
			if (player.getSkills().getLevelForXp(i) >= 99) {
				reqs++;
				continue;
			}
			reqs--;
		}
		return reqs;
	}

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Who are you?",
					"Do you have any capes for sell?",
					"Toggle my Experience please");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 8;
				sendPlayerDialogue(9827, "Who are you?");
				break;
			case OPTION_2:
				stage = 1;
				sendPlayerDialogue(9827, "Do you have any capes for sell?");
				break;
			case OPTION_3:
				stage = 10;
				sendPlayerDialogue(9827, "Toggle my Experience please");
				break;
			}
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(npcId, 9827,
					"Yes that's right. I can only sell my cape to people who got all stats maxed.");
			break;
		case 2:
			stage = 3;
			sendPlayerDialogue(9827,
					"Okay. Do I have all my stats maxed for a cape?");
			break;
		case 3:
			sendNPCDialogue(npcId, 9827,
					"Give me a second to check if you're the right man for the cape.");
			stage = 9;
			break;
		case 4:
			sendNPCDialogue(npcId, 9827,
					"But I can't let you have it for free! You have to hand over 50m of coins.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					"Yes pay 50M for a Max cape.", "No thanks.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				stage = 7;
				sendPlayerDialogue(9827,
						"Sure, take " + Utilities.getFormattedNumber(50000000)
								+ " coins.");
				break;
			case OPTION_2:
				stage = 16;
				sendPlayerDialogue(9827, "No thanks.");
				break;
			}
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					9827,
					"You can't afford "
							+ Utilities.getFormattedNumber(50000000)
							+ " coins.");
			stage = 16;
			break;
		case 8:
			sendNPCDialogue(npcId, 9827,
					"I'm an old man waiting for the right man to buy a cape of mine.");
			stage = 16;
			break;
		case 9:
			if (checkAllStats(player) >= 24) {
				sendNPCDialogue(npcId, 9827,
						"Yes, you seem to have all stats maxed, therefor I can sell you the cape.");
				stage = 4;
			} else {
				sendNPCDialogue(npcId, 9827,
						"Ahh. You still need to train your character. Keep strong!");
				stage = 16;
			}
			break;
		case 10:
			if (player.isXpLocked()) {
				sendNPCDialogue(npcId, 9827,
						"Your Experience has been unlocked.");
				player.setXpLocked(false);
			} else {
				sendNPCDialogue(npcId, 9827, "Your Experience has been locked.");
				player.setXpLocked(true);
			}
			stage = 16;
			break;
		case 16:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hey " + player.getDisplayName()
				+ ". What can I do for you?");
	}
}