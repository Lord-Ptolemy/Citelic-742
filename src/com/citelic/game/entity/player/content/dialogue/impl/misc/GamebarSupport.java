package com.citelic.game.entity.player.content.dialogue.impl.misc;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.TicketSystem;

public class GamebarSupport extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			end(); // end before sends ticket cuz othewise the ticketsystem
			// thinks your busy :p
			TicketSystem.requestTicket(player);
		} else {
			end();
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Send a ticket or visit forum help section?",
				"Send an in-game ticket.", "Visit forum help section.");
	}

}
