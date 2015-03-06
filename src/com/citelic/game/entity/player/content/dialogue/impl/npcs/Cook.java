package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Cook extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Would you like to do a favour for the Cook?",
					"Yes", "No");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "Sure, What do you need help with?");
				break;
			case OPTION_2:
				stage = 1;
				end();
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					9827,
					"An evil old chef from lumbridge has gone crazy! He's hiding behind the large door over there.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(9827,
					"Omg, that sounds crazy! Do you want me to help you?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, 9827,
					"Yes please! If you help me, get rid of him, i will reward you.");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(9827, "Okay, how prepared should i be?");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					9827,
					"Im not sure, but i think he got his friends with him, so be prepared to fight many of them.");
			stage = 6;
			break;
		case 6:
			player.setTalkedToCook();
			sendPlayerDialogue(9827, "Okay i'll do it, wish me goodluck Chief!");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, 9827,
					"Goodluck! Please kill them all, im counting on you "
							+ player.getDisplayName() + "!");
			stage = 8;
			break;
		case 8:
			end();
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.isKilledCulinaromancer()) {
			stage = 8;
			sendNPCDialogue(npcId, 9827,
					"Godbless you " + player.getDisplayName()
							+ "! Use my chest as reward!!");
			return;
		} else if (!player.hasTalkedtoCook()) {
			stage = -1;
			sendNPCDialogue(npcId, 9827, "Hello " + player.getDisplayName()
					+ ". Before we talk can you do me a favour?");
			return;
		} else if (player.hasTalkedtoCook()) {
			stage = 8;
			sendNPCDialogue(npcId, 9827,
					"Keep going " + player.getDisplayName()
							+ "! Kill them all!");
			return;
		}
	}

}