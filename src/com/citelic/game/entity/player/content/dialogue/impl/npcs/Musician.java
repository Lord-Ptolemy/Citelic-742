package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Musician extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Who are you?",
				"Can I ask you some questions about resting?",
				"Can I ask you some questions about running?",
				"That's all for now.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "Who are you?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827,
						"Can I ask you some questions about resting?");
				stage = 11;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827,
						"Can I ask you some questions about running?");
				stage = 18;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827, "That's all for now.");
				stage = 40;
			}
			break;
		case 1:
			stage = 2;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Me? I'm a musician! Let me help you relax: sit down, rest",
							"your weary limbs and allow me to wash away the troubles",
							"of the day. After a long trek, What could be better than",
							"some music to give you the energy to continue? Did you" },
					IS_NPC, npcId, 9850);
			break;
		case 2:
			stage = 3;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"know music has curative properties? Music stimulates the",
							"healing humours in your body, so they say." },
					IS_NPC, npcId, 9850);
			break;
		case 3:
			sendPlayerDialogue(9827, "Who says that, then?");
			stage = 4;
			break;
		case 4:
			stage = 5;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"I was told by a travelling medical practitioner, selling oil",
							"extracted from snakes. It's a commonly known fact, so he",
							"said. After resting to some music, you will be able to run",
							"longer and your life points will increase noticeably. A" },
					IS_NPC, npcId, 9836);
			break;
		case 5:
			stage = 6;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"panacea, if you will. Ah, the power of music." },
					IS_NPC, npcId, 9836);
			break;
		case 6:
			sendPlayerDialogue(9827,
					"So, just listening to some music will cure me of all my ills?");
			stage = 7;
			break;
		case 7:
			stage = 8;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Well. not quite. Poison, lack of faith and dismembered",
							"limbs are all a bit beyond even the most rousing of",
							"harmonies, but I guarantee you will feel refreshed, and",
							"better equipped to take on the challenges of the day." },
					IS_NPC, npcId, 9850);
			break;
		case 8:
			sendPlayerDialogue(9827, "Does this cost me anything?");
			stage = 9;
			break;
		case 9:
			stage = 10;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Oh, no! My reward is the pleasure I bring to the masses.",
							"Just remember me and tell your friends, and that is",
							"payment enough. So sit down and enjoy!" }, IS_NPC,
					npcId, 9850);
			break;
		case 10:
			stage = -1;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Who are you?",
					"Can I ask you some questions about resting?",
					"Can I ask you some questions about running?",
					"That's all for now.");
			break;
		case 11:
			stage = 12;
			sendOptionsDialogue("Can I ask you some questions about resting?",
					"How does resting work?",
					"What's special about resting by a musician?",
					"Can you summarise the effects for me?",
					"That's all for now.");
			break;
		case 12:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "How does resting work?");
				stage = 13;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827,
						"What's special about resting by a musician?");
				stage = 11;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827,
						"Can you summarise the effects for me?");
				stage = 22;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827, "That's all for now.");
				stage = 40;
			}
			break;
		case 13:
			stage = 14;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Have you ever been on a long journey, and simply wanted",
							"to have a rest? When you're running from city to city, it's",
							"so easy to run out of breath, don't you find?" },
					IS_NPC, npcId, 9850);
			break;
		case 14:
			stage = 15;
			sendPlayerDialogue(9827, "Yes, I can never run as far as I'd like.");
			break;
		case 15:
			stage = 16;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Well, you may rest anywhere, simply choose the Rest",
							"option on the run buttons." }, IS_NPC, npcId, 9850);
			break;
		case 16:
			stage = 17;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"When you rest, you will sit on the floor. When you are nice",
							"and relaxed, you will recharge your run energy more",
							"quickly and your life points twice as fast as you would do",
							"normally." }, IS_NPC, npcId, 9850);
			break;
		case 17:
			stage = 18;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Of course, you can't do anything else while you're resting,",
							"other than talk." }, IS_NPC, npcId, 9850);
			break;
		case 18:
			stage = 19;
			sendPlayerDialogue(9827, "Why not?");
			break;
		case 19:
			stage = 20;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Well, you wouldn't be resting, now would you?" },
					IS_NPC, npcId, 9827);
			break;
		case 20:
			stage = 21;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Also, you should know that resting by a musician, has a",
							"similar effect but the benefits are greater." },
					IS_NPC, npcId, 9850);
			break;
		case 21:
			stage = 12;
			sendOptionsDialogue("Can I ask you some questions about resting?",
					"How does resting work?",
					"What's special about resting by a musician?",
					"Can you summarise the effects for me?",
					"That's all for now.");
			break;
		case 22:
			stage = 23;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Certainly. You can rest anywhere, simply choose the Rest",
							"option on the run buttons." }, IS_NPC, npcId, 9850);
			break;
		case 23:
			stage = 24;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Resting anywhere will replenish your run energy more",
							"quickly than normal, your life points will replenish twice as",
							"fast as well!" }, IS_NPC, npcId, 9850);
			break;
		case 24:
			stage = 21;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Resting by a musician will replenish your run energy many",
							"times faster than normal, and your life points will also replenish three times as fast." },
					IS_NPC, npcId, 9850);
			break;
		case 25:
			stage = 26;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Running? Of course! Not that I do much running. I prefer",
							"to saunter. But you adventuring types always seem to be",
							"in a rush, zipping hither and thither." }, IS_NPC,
					npcId, 9850);
			break;
		case 26:
			stage = 27;
			sendPlayerDialogue(9827, "Why do I need to run anyway?");
			break;
		case 27:
			stage = 28;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Running is the simplest way to get somewhere quickly.",
							"When you run you move twice as fast as you normally",
							"would. Also you don't look like the cowardly type, but",
							"most creatures can't run very fast, so if you don't want" },
					IS_NPC, npcId, 9850);
			break;
		case 28:
			stage = 29;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"to fight, you can always run away." }, IS_NPC,
					npcId, 9850);
			break;
		case 29:
			stage = 30;
			sendPlayerDialogue(9827, "Can I keep running forever?");
			break;
		case 30:
			stage = 31;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"No, eventually you'll get tired. When that happens you will",
							"stop running, and start walking. It takes a while to get",
							"your breath back, but once you've recovered a little, you",
							"can start running again. You recover quickly whilst" },
					IS_NPC, npcId, 9850);
			break;
		case 31:
			stage = 32;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"resting, or more slowly whilst walking." },
					IS_NPC, npcId, 9850);
			break;
		case 32:
			stage = 10;
			sendDialogue(
					"You may start running by clicking once on the Run button, which is the",
					"boot icon at the bottom-right of the minimap. Clicking the Run button",
					"a second time will switch you back to walking. It tells you how much run",
					"energy you currently have.");
			break;
		case 40:
			stage = 41;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Well, don't forget to have a rest every now and again." },
					IS_NPC, npcId, 9827);
			break;
		case 41:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}