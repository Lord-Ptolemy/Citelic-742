package com.citelic.game.entity.player.content.dialogue.impl.distractions.duelarena;

import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelArena;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class ForfeitDialouge extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			if (!player.getLastDuelRules().getRule(7)) {
				if (!((DuelArena) player.getControllerManager().getController())
						.getTarget().isDead()) {
					((DuelArena) player.getControllerManager().getController())
							.endDuel(player.getLastDuelRules().getTarget(),
									player);
				}
			} else {
				sendDialogue("You can't forfeit during this duel.");
			}
			break;
		}
		end();
	}

	@Override
	public void start() {
		sendOptionsDialogue("Forfeit Duel?", "Yes.", "No.");
	}

}
