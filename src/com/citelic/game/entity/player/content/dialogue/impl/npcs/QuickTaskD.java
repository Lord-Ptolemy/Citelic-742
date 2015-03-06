package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer.SlayerMaster;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class QuickTaskD extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void start() {
		SlayerMaster master = (SlayerMaster) parameters[0];
		int npcId = master.getNPCId();
		if (player.getSlayerManager().getCurrentMaster() != master) {
			sendNPCDialogue(
					npcId,
					9827,
					"I don't think that "
							+ player.getSlayerManager().getCurrentMaster()
									.toString().toLowerCase()
							+ " would be happy if I took one of his students just like that. Complete your task then return to me.");
			return;
		}
		if (player.getSlayerManager().getCurrentTask() != null) {
			sendNPCDialogue(
					npcId,
					9827,
					"You already have an assignment, come back when you have completed it for a new one.");
		} else {
			player.getSlayerManager().setCurrentTask(true);
			sendNPCDialogue(npcId, 9827, "Your new assignment is: "
					+ player.getSlayerManager().getCurrentTask().getName()
					+ "; only " + player.getSlayerManager().getCount()
					+ " more to go.");
		}
	}
}
