package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Cyrisus extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 1;
			sendPlayerDialogue(9827, "Not much, im bored at the moment.");
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
			sendNPCDialogue(npcId, 9827,
					"Oh i see, would you like to follow me and kill a bitch?");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue("Would you like to kill a bitch?",
					"Yeah nothing to lose anyway!", "No, im to scared!");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				sendPlayerDialogue(9827,
						"Yeah nothing to lose anyway, i hate bitches!");
				break;
			case OPTION_2:
				stage = 8;
				end();
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, 9827, "Thank you mate! follow me!");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue("Would you like to follow Cyrisus?", "Yes",
					"No");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				stage = 7;
				sendNPCDialogue(npcId, 9827,
						"Here we go! Remember this is a dream!");

				break;
			case OPTION_2:
				stage = 8;
				end();
				break;
			}
			break;
		case 7:
			sendPlayerDialogue(9827, "Wait! What??!");
			player.getControllerManager()
					.startController("DreamMentorFight", 1);
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

	/*
	 * public void run(int interfaceId, int componentId) { if (stage == -1) {
	 * int option; sendPlayerDialogue(9827,
	 * "Oh so you're a legend of SiriusX, oh well you dont have to explain anything, what items can you exchange?"
	 * ); stage = 2; } else if (stage == 2) { sendOptionsDialogue("Merchant",
	 * "Polypore Staff", "Ganodermic Armour", "Fungal Armour"); if(componentId
	 * == OPTION_1) { ShopsHandler.openShop(player, 28); end(); } if(componentId
	 * == OPTION_2) { sendNPCDialogue(npcId, 9827, "You currently have " +
	 * player.getLoyaltyPoints() + " Loyalty Points." ); stage = 3; }
	 * if(componentId == OPTION_3) { sendNPCDialogue(npcId, 9827,
	 * "The only way to get Loyalty Points is by playing Artisticy for 30 minutes."
	 * ); stage = 3; } } else if (stage == 3) { end(); } }
	 */

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		stage = 8; // -1
		sendNPCDialogue(npcId, 9827,
				"Leave me alone!!!! I dont feel like talking right now!!");
	}

}