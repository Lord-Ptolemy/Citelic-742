package com.citelic.game.entity.player.content.dialogue.impl.items;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.Utilities;

public class EnchantedGemD extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"How many monsters do I have left?", "Give me a tip.",
					"Nothing, Nevermind.");
		} else if (stage == 0) {
			if (componentId == Dialogue.OPTION_1) {
				player.getSlayerManager().checkKillsLeft();
				end();
			} else if (componentId == Dialogue.OPTION_2) {
				stage = 1;
				if (player.getSlayerManager().getCurrentTask() == null) {
					sendNPCDialogue(npcId, 9827,
							"You currently don't have a task.");
					return;
				}
				String[] tipDialouges = player.getSlayerManager()
						.getCurrentTask().getTips();
				if (tipDialouges != null && tipDialouges.length != 0) {
					String chosenDialouge = tipDialouges[Utilities
							.random(tipDialouges.length)];
					if (chosenDialouge == null || chosenDialouge.equals("")) {
						sendNPCDialogue(npcId, 9827,
								"I don't have any tips for you currently.");
					} else {
						sendNPCDialogue(npcId, 9827, chosenDialouge);
					}
				} else {
					sendNPCDialogue(npcId, 9827,
							"I don't have any tips for you currently.");
				}
			} else {
				end();
			}
		} else if (stage == 1) {
			end();
		}
	}

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendNPCDialogue(npcId, 9827, "'Ello and what are you after then?");
	}
}