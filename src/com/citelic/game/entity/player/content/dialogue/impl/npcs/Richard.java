package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class Richard extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 56);
				end();
			}
			if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, 57);
				end();
			}
			if (componentId == OPTION_3) {
				end();
			}
		}
	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Select an option", "Cape 1", "Cape 2", "Nevermind");
	}
}