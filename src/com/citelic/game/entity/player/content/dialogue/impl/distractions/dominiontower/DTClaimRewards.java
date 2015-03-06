package com.citelic.game.entity.player.content.dialogue.impl.distractions.dominiontower;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class DTClaimRewards extends Dialogue {

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(
					"If you claim your rewards your progress will be reset.",
					"Claim Rewards", "Cancel");
		} else if (stage == 0) {
			if (componentId == 1)
				player.getDominionTower().openRewardsChest();
			end();
		}

	}

	@Override
	public void start() {
		sendDialogue("You have a Dominion Factor of "
				+ player.getDominionFactor() + ".");

	}

}
