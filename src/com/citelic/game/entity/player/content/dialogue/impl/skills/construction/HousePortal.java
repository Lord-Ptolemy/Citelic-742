package com.citelic.game.entity.player.content.dialogue.impl.skills.construction;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class HousePortal extends Dialogue {

	public HousePortal() {
	}

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == Dialogue.OPTION_1) {
				player.getHouse().enterMyHouse();
				end();
			} else if (componentId == Dialogue.OPTION_2) {
				end();
			}
		}

	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("What would you like to do?", "Enter Your House",
				"None");
	}

}