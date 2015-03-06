package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.GravestoneSelection;

public class FatherAereck extends Dialogue {

	private int npcId;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"Can you change my gravestone?",
					"Can you change my prayers?",
					"Can you change my spellbook?");
			stage = 1;
			break;
		case 1:
			if (componentId == Dialogue.OPTION_1) {
				sendPlayerDialogue(9827, "Can you change my gravestone?");
				stage = 2;
			} else if (componentId == Dialogue.OPTION_2) {
				sendPlayerDialogue(9827, "Can you change my prayers?");
				stage = 4;
			} else if (componentId == Dialogue.OPTION_3) {
				sendPlayerDialogue(9827, "Can you change my spellbook?");
				stage = 7;
			}
			break;
		case 2:
			sendNPCDialogue(
					npcId,
					9827,
					"Certainly. All proceeds are donated to the Varrockian Guards' Widows & Orphans Fund.");
			stage = 3;
			break;
		case 3:
			GravestoneSelection.openSelectionInterface(player);
			end();
			break;
		case 4:
			sendNPCDialogue(npcId, 9827,
					"Of course I can change your prayers, select the book you would like to have.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"Normal Curses", "Ancient Curses", "Nevermind");
			stage = 6;
			break;
		case 6:
			if (componentId == Dialogue.OPTION_1) {
				player.getPrayer().setPrayerBook(false);
				sendPlayerDialogue(9827, "I would like the normal curses.");
				stage = 10;
			} else if (componentId == Dialogue.OPTION_2) {
				player.getPrayer().setPrayerBook(true);
				sendPlayerDialogue(9827, "I would like the ancient curses.");
				stage = 10;
			} else if (componentId == Dialogue.OPTION_3) {
				stage = 11;
			}
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					9827,
					"Indeed, I can change your spellbook! Select the spellbook you would like to use.");
			stage = 8;
			break;
		case 8:
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"Standard Spellbook", "Ancient Spellbook",
					"Lunar Spellbook", "Nevermind");
			stage = 9;
			break;
		case 9:
			if (componentId == Dialogue.OPTION_1) {
				player.getCombatDefinitions().setSpellBook(0);
				sendPlayerDialogue(9827, "I would like the normal spellbook.");
				stage = 10;
			} else if (componentId == Dialogue.OPTION_2) {
				player.getCombatDefinitions().setSpellBook(1);
				sendPlayerDialogue(9827, "I would like the ancient spellbook.");
				stage = 10;
			} else if (componentId == Dialogue.OPTION_3) {
				player.getCombatDefinitions().setSpellBook(2);
				sendPlayerDialogue(9827, "I would like the lunar spellbook.");
				stage = 10;
			} else if (componentId == Dialogue.OPTION_4) {
				stage = 11;
			}
			break;
		case 10:
			sendNPCDialogue(npcId, 9827, "There you go.");
			stage = 11;
			break;
		case 11:
			end();
			break;
		}
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827,
				"Welcome, my friend! What can I do for you today?");
		stage = 0;
	}
}