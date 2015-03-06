package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SorceressGardenNPCs extends Dialogue {

	public NPC npc;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			if (npc.getId() == 5563) {
				sendNPCDialogue(npc.getId(), 9827, "Hiss!");
				finish();
				return;
			} else if (npc.getId() == 5532) {
				sendNPCDialogue(npc.getId(), 9827,
						"Okay, here goes - and remember, to return just drink from the fountain.");
			}
		} else if (stage == 0) {
			stage = 1;
			if (npc.getId() == 5532) {
				player.getControllerManager()
						.startController("SorceressGarden");
				npc.setNextForceTalk(new ForceTalk(
						"Senventior Disthinte Molesko!"));
			}
			end();
		}
	}

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendPlayerDialogue(
				9827,
				((npc.getId() != 5532 && npc.getId() == 5563) ? "Hey kitty!"
						: "Hey apprentice, do you want to try out your teleport skills again?"));
	}

}
