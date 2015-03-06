package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.GameConstants;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class HomeTeleport extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				Magic.sendNormalTeleportSpell(player, 0, 0,
						GameConstants.RESPAWN_PLAYER_LOCATION);
				end();
			} else if (componentId == OPTION_2) {
				player.getInterfaceManager().sendInterface(1092);
				end();
			} else {
				end();
			}
			break;
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Home Teleport",
				"Lodestone network", "Nevermind");
	}
}