package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.skills.crafting.GemCutting;
import com.citelic.game.entity.player.content.actions.skills.crafting.GemCutting.Gem;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;

public class GemCuttingD extends Dialogue {

	private Gem gem;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new GemCutting(gem, SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void start() {
		this.gem = (Gem) parameters[0];
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.CUT,
						"Choose how many you wish to cut,<br>then click on the item to begin.",
						player.getInventory().getItems()
								.getNumberOf(gem.getUncut()),
						new int[] { gem.getUncut() }, null);

	}

}
