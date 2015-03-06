package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class Nastroth extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 30);
				end();
			}
			if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, 27);
				end();
			}
			if (componentId == OPTION_3) {
				end();
			}
		} else if (stage == 3) {
			end();
		}
	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("What shop would you like to see?", "Zerker Gear",
				"Pure Gear", "Nevermind");
	}
}