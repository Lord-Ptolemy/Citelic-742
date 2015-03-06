package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class StrangeFace extends Dialogue {

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendDialogue("Hello.");
		} else if (stage == 0) {
			stage = 1;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(), "Woah!" },
					IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 1) {
			stage = 2;
			sendDialogue(

			"It is intrigring that you took so before comming to me. Fearful,",
					"traveller?");
		} else if (stage == 2) {
			stage = 3;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(), "Should I be?" },
					IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 3) {
			stage = 4;
			sendDialogue(

					"It is my duty inform you that many warriors fight here, and they",
					"all succumb to defeat eventually. If that instills terror in you, walk",
					"away now.");
			player.getPackets().sendVoice(7881);
		} else if (stage == 4) {
			stage = 5;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(),
							"There are monsters in the tower?" }, IS_PLAYER,
					player.getIndex(), 9827);
		} else if (stage == 5) {
			stage = 6;
			sendDialogue(

					"If that is the terminolgy you would use, yes. Through the powers",
					"bestowed upon me by my creator, I can generate opponents for you",
					"based on your memories of them. Men and women have fought here",
					"for generations.");
		} else if (stage == 6) {
			stage = 7;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(),
							"Impressive. So you control the tower?" },
					IS_PLAYER, player.getIndex(), 9827);

		} else if (stage == 7) {
			stage = 8;
			sendDialogue(

					"The Tower is I, and I have control of the tower. I see what happens,",
					"in any corner of any floor. I am always watching.");
		} else if (stage == 8) {
			stage = 9;
			sendDialogue("So you believe yourself a mighty warrior?");
		} else if (stage == 9) {
			stage = 10;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					"Only the greatest warrior that ever lived!",
					"I'm pretty handy with a weapon.");
		} else if (stage == 10) {
			stage = (byte) (componentId == OPTION_1 ? 100 : 101);
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							player.getDisplayName(),
							componentId == 2 ? "Only the greatest warrior that ever lived!"
									: "I'm pretty handy with a weapon." },
					IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 100 || stage == 101) {
			sendDialogue("Intriguing. "
					+ (stage == 100 ? "Such belief in your own abilities..."
							: "I sence humility in you."));
			stage = 12;
		} else if (stage == 12) {
			stage = 13;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(), "What?" },
					IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 13) {
			stage = 14;
			sendDialogue(

			"Your confidence may have a foundation, but judgment will come in",
					"battle.");
			player.getPackets().sendVoice(7896);
		} else if (stage == 14) {
			stage = 15;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							player.getDisplayName(),
							"You mentioned that you were created by someone, but",
							"why?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 15) {
			stage = 16;
			sendDialogue("My purpose...must never stop...");
		} else if (stage == 16) {
			stage = 17;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] { player.getDisplayName(),
							"Sorry? Are you alright?" }, IS_PLAYER,
					player.getIndex(), 9827);
		} else if (stage == 17) {
			stage = 18;
			sendDialogue(

					"You must fight in the tower, warrior. Demonstrate your ability to",
					"others and learn.");
		} else if (stage == 18) {
			stage = 19;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							player.getDisplayName(),
							"I'd thought that, as a guide, you'd be a little more",
							"welcoming." }, IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 19) {
			stage = 20;
			sendDialogue("You will find I am welcoming enough.");
		} else if (stage == 20) {
			stage = 21;
			sendDialogue(

					"Now, I can offer you more guidance; or, if you overflow with",
					"confidence, you can figure out yourself. I am the tower, I am",
					"ever-present, so come to me if you change your mind.");
		} else if (stage == 21) {
			stage = 22;
			sendOptionsDialogue("Receive further instruction?", "Yes.", "No.");
			player.getDominionTower().setTalkedWithFace(true);
		} else if (stage == 22) {
			stage = 23;
			if (componentId == OPTION_2) {
				sendDialogue("Your choice. Come back if you change your mind.");
			} else {
				player.getDominionTower().talkToFace(true);
				end();
			}
		} else {
			end();
		}

	}

	@Override
	public void start() {
		sendEntityDialogue(SEND_1_TEXT_CHAT,
				new String[] { player.getDisplayName(), "Hello?" }, IS_PLAYER,
				player.getIndex(), 9827);

	}

}
