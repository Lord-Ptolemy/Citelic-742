package com.citelic.game.entity.player.content.dialogue.impl.simplemessages;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SimpleNPCMessageX extends Dialogue {

	private int npcId;
	private int dId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		dId = (Integer) parameters[1];
		String[] messages = new String[parameters.length - 2];
		for (int i = 0; i < messages.length; i++)
			messages[i] = (String) parameters[i + 2];
		sendNPCDialogue(npcId, dId, messages);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
