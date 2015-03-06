package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

/**
 * RuneRetro
 * 
 * @author Joris
 *
 */

public class Tanner extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Greetings friend. I am a manufacturer of leather." },
				IS_NPC, npcId, 9850);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (player.getInventory().containsItem(1739, 1)) {
				stage = 5;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
						NPCDefinitions.getNPCDefinitions(npcId).name,
						"I see you have brought me a hide.",
						"Would you like me to tan it for you?" }, IS_NPC,
						npcId, 9803);
			} else {
				stage = 15;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
						"Can I buy some leather then?",
						"Leather is rather weak stuff.");
			}
			break;
		case 5:
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes please.",
					"No thanks.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				stage = 9;
				sendPlayerDialogue(9850, "Yes please.");
				break;
			case OPTION_2:
				stage = 20;
				sendPlayerDialogue(9781, "No thanks.");
				break;
			}
			break;
		case 9:
			end();
			player.getDialogueManager().startDialogue("TanningD", npcId);
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(9827, "How?");
			break;
		case 11:
			stage = 50;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Sorry kiddo, I'm a fur trader not a damsel in distress." },
					IS_NPC, npcId, 9803);
			break;
		case 12:
			stage = 13;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Let's have a look at it." }, IS_NPC, npcId, 9803);
			break;
		case 13:
			stage = 14;
			player.getInterfaceManager().sendChatBoxInterface(1189);
			player.getPackets().sendItemOnIComponent(1189, 1, 948, 1);
			player.getPackets().sendIComponentText(1189, 4,
					"You hand Baraek your fur to look at.");
			break;
		case 14:
			pause();
			stage = 15;
			player.lock(4);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					sendEntityDialogue(
							SEND_2_TEXT_CHAT,
							new String[] {
									NPCDefinitions.getNPCDefinitions(npcId).name,
									"It's not in the best condition. I guess I could give you 12",
									"coins for it." }, IS_NPC, npcId, 9803);
					stop();
				}
			}, 4, 0);
			break;
		case 15:
			switch (componentId) {
			case OPTION_1:
				stage = 20;
				sendPlayerDialogue(9827, "Can I buy some leather then?");
				break;
			case OPTION_2:
				stage = 46;
				sendPlayerDialogue(9781, "Leather is rather weak stuff.");
				break;
			}
			break;
		case 20:
			stage = 50;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"I make leather from animal hides. Bring me some cowhides",
							"and one gold coin per hide, and i'll tan them into soft",
							"leather for you." }, IS_NPC, npcId, 9803);
			break;
		case 46:
			stage = 47;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Normal leather may be quite weak, but it's very cheap - I",
							"make it from cowhides for only 1 gp per hide - and it's so",
							"easy to craft that anyone can work with it." },
					IS_NPC, npcId, 9850);
			break;
		case 47:
			stage = 48;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Alternatively you could try hard leather. It's not so easy",
							"to craft, but I only charge 3 gp per cowhide to prepare it,",
							"and it makes much sturdier armour." }, IS_NPC,
					npcId, 9850);
			break;
		case 48:
			stage = 59;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"I can also tan snake hides and dragonhides, suitable for",
							"crafting into the highest quality armour for rangers." },
					IS_NPC, npcId, 9850);
			break;
		case 49:
			stage = 50;
			sendPlayerDialogue(9831, "Thanks, I'll bear it in mind.");
			break;
		case 50:
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}