package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class DungChest extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				if (player.getRegionId() == 13626) {
					ShopsHandler.openShop(player, 48);
				}
				end();
			}
			if (componentId == OPTION_2) {
				sendDialogue("You currently have "
						+ player.getDungeoneeringTokens()
						+ " Dungeoneering Tokens.");
				stage = 1;
			}
		} else {
			end();
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Open tokens reward shop",
				"How many Dungeoneering tokens do I have?");
	}

}