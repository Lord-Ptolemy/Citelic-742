package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.PartyRoom;

public class PartyRoomLever extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 2) {
			PartyRoom.purchase(player, true);
		} else if (componentId == 3) {
			PartyRoom.purchase(player, false);
		}
		end();
	}

	@Override
	public void start() {
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
				"Balloon Bonanza (1000 coins).", "Nightly Dance (500 coins).",
				"No action.");
	}
}
