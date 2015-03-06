package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class AncientAltar extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (player.getCombatDefinitions().getSpellBook() != 193) {
				sendDialogue("Your mind clears and you switch",
						"back to the ancient spellbook.");
				player.getCombatDefinitions().setSpellBook(1);
			} else {
				sendDialogue("Your mind clears and you switch",
						"back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
			}
		} else
			end();
	}

	@Override
	public void start() {
		sendOptionsDialogue("Change spellbooks?", "Yes, replace my spellbook.",
				"Never mind.");
	}

}
