package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Merchant extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(
					9827,
					"Oh so you're a legend of SiriusX, oh well you dont have to explain anything, what are you willing to exchange?");
			break;
		case 0:
			stage = 1;
			sendOptionsDialogue("Merchant", "Polypore Staff",
					"Ganodermic Armour", "Fungal Armour");
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendPlayerDialogue(9827,
						"I would like to exchange my Polypore sticks for a Polypore Staff.");
				break;
			case OPTION_2:
				stage = 3;
				sendOptionsDialogue(
						"Choose what item you would like to purchase",
						"Ganodermic visor", "Ganodermic poncho",
						"Ganodermic leggings");
				break;
			case OPTION_3:
				sendOptionsDialogue(
						"Choose what item you would like to purchase",
						"Fungal visor", "Fungal poncho", "Fungal leggings");
				stage = 5;
				break;
			}
			break;
		case 2:
			if (player.getInventory().containsItem(22499, 50)) {
				player.getInventory().deleteItem(22499, 50);
				player.getInventory().addItem(22494, 1);
				sendNPCDialogue(npcId, 9827,
						"Thank you my strange friend, heres your Polypore Staff.");
				stage = 4;
			} else {
				sendNPCDialogue(npcId, 9827,
						"You dont have enough Polypore Sticks to exchange for a Polypore Staff.");
				stage = 0;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (player.getInventory().containsItem(22451, 300000)) {
					player.getInventory().deleteItem(22451, 300000);
					player.getInventory().addItem(22482, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Ganodermic Flakes to exchange for a Ganodermic visor.");
					stage = 0;
				}
				break;
			case OPTION_2:
				if (player.getInventory().containsItem(22451, 400000)) {
					player.getInventory().deleteItem(22451, 400000);
					player.getInventory().addItem(22490, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Ganodermic Flakes to exchange for a Ganodermic phonco.");
					stage = 0;
				}
				break;
			case OPTION_3:
				if (player.getInventory().containsItem(22451, 300000)) {
					player.getInventory().deleteItem(22451, 300000);
					player.getInventory().addItem(22486, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Ganodermic Flakes to exchange for a Ganodermic leggings.");
					stage = 0;
				}
				break;
			}
			break;
		case 4:
			sendPlayerDialogue(
					9827,
					"Thanks Merchant, i'll come back when I have collected more items from the Polypore Dungeon.");
			stage = 10;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (player.getInventory().containsItem(22449, 45000)) {
					player.getInventory().deleteItem(22449, 45000);
					player.getInventory().addItem(22458, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Fungal Flakes to exchange for a Fungal visor.");
					stage = 0;
				}
				break;
			case OPTION_2:
				if (player.getInventory().containsItem(22449, 135000)) {
					player.getInventory().deleteItem(22449, 135000);
					player.getInventory().addItem(22466, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Fungal Flakes to exchange for a Fungal phonco.");
					stage = 0;
				}
				break;
			case OPTION_3:
				if (player.getInventory().containsItem(22449, 100000)) {
					player.getInventory().deleteItem(22449, 100000);
					player.getInventory().addItem(22462, 1);
					sendNPCDialogue(
							npcId,
							9827,
							"Great trade "
									+ player.getDisplayName()
									+ ", Hope you enjoy what i collected from the Dungeon.");
					stage = 4;
				} else {
					sendNPCDialogue(npcId, 9827,
							"You dont have enough Fungal Flakes to exchange for a Fungal leggings.");
					stage = 0;
				}
				break;
			}
			break;
		case 10:
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
		stage = -1;
		sendNPCDialogue(
				npcId,
				9827,
				"Greetings "
						+ player.getDisplayName()
						+ ". I have been here for a longtime collecting various items, I will exchange some of my items for the items you have collected here.");
	}

}