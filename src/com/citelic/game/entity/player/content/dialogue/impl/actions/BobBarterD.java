package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.BobBarter;

/**
 * 
 * @author Tyler
 * 
 */
public class BobBarterD extends Dialogue {
	int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("What would you like to say?",
					"Decant my potions...", "Nevermind.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendNPCDialogueNoContinue(player, npcId, 9827, "Decanting...");
				player.lock(5);
				end();
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						BobBarter decanting = new BobBarter(player);
						decanting.decant();
						sendNPCDialogueNoContinue(player, npcId, 9827,
								"There yer go, chum!");
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								closeNoContinueDialogue(player);
								end();
							}
						}, 2);
					}
				}, 5);
			} else {
				end();
			}

		}

	}

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Good day, How can I help you?" }, IS_NPC, npcId, 9827);

	}

}