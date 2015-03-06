package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class WiseOldMan extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 18);
				end();
			} else if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, 29);
				end();
			} else {
				end();
			}
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Skillcapes",
				"Skillcape hoods", "Nevermind");
	}
}