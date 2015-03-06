package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.skills.fletching.BoltTipFletching;
import com.citelic.game.entity.player.content.actions.skills.fletching.BoltTipFletching.Tips;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;

public class BoltTipFletchingD extends Dialogue {

	private Tips tip;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new BoltTipFletching(tip, SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void start() {
		tip = (Tips) parameters[0];
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.CUT,
						"Choose how many you wish to cut,<br>then click on the item to begin.",
						player.getInventory().getItems()
								.getNumberOf(tip.getGem()),
						new int[] { tip.getTip() }, null);

	}

}
