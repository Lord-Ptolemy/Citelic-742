package com.citelic.game.entity.player.content.dialogue.impl.distractions.playersafety;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

/**
 * RuneRetro
 * 
 * @author Joris
 *
 */

public class SafetyGuard extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.playerSafety[0] == false) {
			sendNPCDialogue(npcId, 9803, "Can I help you?");
		} else {
			sendNPCDialogue(
					npcId,
					9850,
					"Hello again. Did you want me to tell you about the Report",
					"button?");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (player.playerSafety[0] == false) {
				stage = 3;
				sendPlayerDialogue(9803, "I hope so. What is this place?");
			} else {
				stage = 10;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.",
						"No thanks.");
			}
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(
					npcId,
					9803,
					"Above us is the Misthalin Training Centre of Excellence,",
					"where young adventurers are thaught things that will help",
					"keep themselves safe.");
			break;
		case 4:
			stage = 5;
			player.playerSafety[0] = true;
			player.sendByFiles();
			sendNPCDialogue(npcId, 9836,
					"They say that hidden away somewhere here is the",
					"entrance to the old jail, which no doubt has fabulous",
					"treasures for those willing to search for them.");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(npcId, 9803,
					"Together they're called the Stronghold of Player Safety,",
					"for historical reasons.");
			break;
		case 6:
			stage = 7;
			sendPlayerDialogue(9827, "So what do you do?");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(npcId, 9850,
					"I guard this stairway to make sure that prospective",
					"students are ready, and to explain the Report function.");
			break;
		case 8:
			stage = 9;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					"What is this Report thing?",
					"That's interesting. Goodbye.");
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827, "What is this Report thing?");
				stage = 12;
				break;
			case OPTION_2:
				stage = 50;
				sendPlayerDialogue(9803, "That's interesting. Goodbye.");
				break;
			}
			break;
		case 10:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9850, "Yes, please.");
				stage = 12;
				break;
			case OPTION_2:
				stage = 50;
				sendPlayerDialogue(9803, "No thanks, Goodbye.");
				break;
			}
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(npcId, 9803,
					"If you find a player who acts in a way that breaks",
					"one of our rules, you should report them.");
			break;
		case 13:
			stage = 14;
			sendNPCDialogue(
					npcId,
					9803,
					"Reporting is very simple and easy to do.",
					"If you enable right-click reporting in the game options,",
					"you can right click a player and choose to 'report' them.",
					"Or you can simply click the 'Report' button at the bottom");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(npcId, 9803,
					"of the screen and you will be shown the following screens:");
			break;
		case 15:
			stage = 16;
			pause();
			player.lock(7);
			player.getInterfaceManager().sendInterface(594);
			EngineTaskManager.schedule(new EngineTask() {
				int count = 6;

				@Override
				public void run() {
					if (count == 3) {
						player.getPackets().sendHideIComponent(594, 104, false);
					} else if (count == 1) {
						player.closeInterfaces();
					} else if (count == 0) {
						sendNPCDialogue(
								npcId,
								9803,
								"If you used the 'Report' button you'll see the first page,",
								"simply click a line of chat the player has said. Then click",
								"'Next'. For offences other than botting or bug abuse you",
								"can only report players who have spoken recently.");
						this.stop();
					}
					count--;
				}
			}, 0, 1);
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(npcId, 9803,
					"On the following page, click on the offence that the",
					"player has committed.");
			break;
		case 17:
			stage = 18;
			sendNPCDialogue(
					npcId,
					9803,
					"Finally, you'll be given the option to temporarily ignore the",
					"player you've reported. That will last until you next log",
					"out.");
			break;
		case 18:
			stage = 50;
			sendPlayerDialogue(9803, "Thank you. I'll bear that in mind.");
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