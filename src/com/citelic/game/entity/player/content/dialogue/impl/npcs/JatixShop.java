package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class JatixShop extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 20);
				end();
			}
			if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, 61);
				end();
			}
			if (componentId == OPTION_3) {
				end();
			}
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Herblore weeds",
				"Herbore ingredients", "Nevermind");
	}
}