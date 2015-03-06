package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class EasterBunny extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					"How do I get Easter Eggs?", "Can I exchange my eggs?",
					"Reward Shop", "Happy Easter!");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 5;
				sendNPCDialogue(
						npcId,
						9827,
						"Easter Eggs will be randomly dropped by my friends around SiriusX every 20th minutes!");
				break;
			case OPTION_2:
				stage = 1;
				sendPlayerDialogue(9827, "Can I exchange my eggs?");
				break;
			case OPTION_3:
				ShopsHandler.openShop(player, 53);
				end();
				break;
			case OPTION_4:
				stage = 5;
				sendPlayerDialogue(9827, "Happy Easter!");
				break;
			}
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(
					npcId,
					9827,
					"Yes I can give you Easter reward points to you as exchange, with Easter reward points you can buy rewards in my reward shop.");
			break;
		case 2:
			stage = 3;
			sendOptionsDialogue(
					"Do you want to exchange all your eggs in your inventory?",
					"Yes.", "No.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				sendPlayerDialogue(9827, "Yes");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 4:
			int numberOfEggs;
			numberOfEggs = player.getInventory().getNumberOf(7928)
					+ player.getInventory().getNumberOf(7929)
					+ player.getInventory().getNumberOf(7930)
					+ player.getInventory().getNumberOf(7931)
					+ player.getInventory().getNumberOf(7932)
					+ player.getInventory().getNumberOf(7933);
			if (numberOfEggs == 0) {
				sendNPCDialogue(npcId, 9827,
						"You don't have any eggs in your inventory.");
			} else {
				for (int item = 7928; item < 7934; item++) {
					player.getInventory().deleteItem(item, Integer.MAX_VALUE);
				}
				sendNPCDialogue(npcId, 9827, "Thanks! I've exchanged all your "
						+ numberOfEggs + " eggs into " + numberOfEggs
						+ " Easter reward points.");
				player.setEasterPoints(player.getEasterPoints() + numberOfEggs);
			}
			stage = 5;
			break;
		case 5:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Happy Easter!");
	}
}