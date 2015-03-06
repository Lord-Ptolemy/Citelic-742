package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Fiara extends Dialogue {

	private int npcId = 7600;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (!player.hasTut) {
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
						"Sure, Tell me about it.", "No I'm fine thanks.");
				stage = 1;
			} else {
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
						"Please tell me my Warrior Level",
						"Please tell me my Archer Level",
						"Please tell me my Magician Level",
						"I would like to select my class");
				stage = 5;
			}
			break;

		case 0:
			end();
			break;

		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827, "Sure, Tell me about this minigame.");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(9827, "No, I'm fine thanks.");
				stage = 0;
				break;
			}
			break;

		case 2:
			sendNPCDialogue(npcId, 9827,
					"You can enter this minigame by going through the passageway over there.");
			stage++;
			break;

		case 3:
			sendNPCDialogue(
					npcId,
					9827,
					"You are not able to take familiers, equipment or items into the waiting room. All items will be provided depending on the class you assign yourself to inside the waiting room.");
			stage++;
			break;

		case 4:
			sendNPCDialogue(
					npcId,
					9827,
					"This is a PvP based minigame so you will fight other players inside the arena.");
			player.hasTut = true;
			player.warriorLevel = 1;
			player.archerLevel = 1;
			player.mageLevel = 1;
			stage = 0;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827, "Please tell me my Warrior Level");
				sendNPCDialogue(npcId, 9827, "Your warrior level is currently "
						+ player.warriorLevel + ".");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(9827, "Please tell me my Archer Level");
				sendNPCDialogue(npcId, 9827, "Your archer level is currently "
						+ player.archerLevel + ".");
				stage = 0;
				break;
			case OPTION_3:
				sendPlayerDialogue(9827, "Please tell me my Magician Level");
				sendNPCDialogue(npcId, 9827,
						"Your magician level is currently " + player.mageLevel
								+ ".");
				stage = 0;
				break;
			case OPTION_4:
				sendPlayerDialogue(9827,
						"I would like to change my fighting class.");
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Warrior",
						"Archer", "Magician");
				stage = 6;
				break;
			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827,
						"I would like to choose the Warrior class.");
				sendNPCDialogue(npcId, 9827,
						"You have selected the warrior class, warrior level is currently "
								+ player.warriorLevel + ".");
				player.selectedClass = "Warrior";
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(9827,
						"I would like to choose the Archer class.");
				sendNPCDialogue(npcId, 9827,
						"You have selected the Archer class, Archer level is currently "
								+ player.archerLevel + ".");
				player.selectedClass = "Archer";
				stage = 0;
				break;
			case OPTION_3:
				sendPlayerDialogue(9827,
						"I would like to choose the Magician class.");
				sendNPCDialogue(npcId, 9827,
						"You have selected the Magician class, Magician level is currently "
								+ player.mageLevel + ".");
				player.selectedClass = "Magician";
				stage = 0;
				break;
			}
			break;

		}
	}

	@Override
	public void start() {
		if (!player.hasTut)
			sendEntityDialogue(
					IS_NPC,
					"Fiara",
					npcId,
					9827,
					"Welcome to the BattleTerrace human. Before you can play allow me to explain the basics of this minigame.");
		else
			sendEntityDialogue(IS_NPC, "Fiara", npcId, 9827,
					"Welcome to the BattleTerrace human.");
	}

}