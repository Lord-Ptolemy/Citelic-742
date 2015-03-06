package com.citelic.game.entity.player.content.dialogue.impl.simplemessages;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SimpleNPCMessageNoContinue extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		String[] messages = new String[parameters.length - 1];
		for (int i = 0; i < messages.length; i++)
			messages[i] = (String) parameters[i + 1];
		sendNPCDialogueNoContinue(player, npcId, 9827, messages);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				closeNoContinueDialogue(player);
				end();
			}
		}, 15);
	}

}
