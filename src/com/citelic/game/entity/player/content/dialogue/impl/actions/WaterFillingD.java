package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.WaterFilling;
import com.citelic.game.entity.player.content.actions.WaterFilling.Fill;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;

public class WaterFillingD extends Dialogue {

	private Fill fill;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new WaterFilling(fill, SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void start() {
		fill = (Fill) parameters[0];
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.MAKE,
						"Choose how many you wish to fill,<br>then click on the item to begin.",
						player.getInventory().getItems()
								.getNumberOf(fill.getEmpty()),
						new int[] { fill.getEmpty() }, null);
	}

}
