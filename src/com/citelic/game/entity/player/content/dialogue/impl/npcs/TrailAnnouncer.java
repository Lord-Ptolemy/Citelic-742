package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class TrailAnnouncer extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "I am currently busy",
				"please go away citizen!");
	}

}