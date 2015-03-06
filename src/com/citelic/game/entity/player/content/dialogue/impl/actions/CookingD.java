package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.skills.cooking.Cooking;
import com.citelic.game.entity.player.content.actions.skills.cooking.Cooking.Cookables;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;
import com.citelic.game.map.objects.GameObject;

public class CookingD extends Dialogue {

	private Cookables cooking;
	private GameObject object;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new Cooking(object, cooking.getRawItem(), SkillsDialogue
						.getQuantity(player)));
		end();
	}

	@Override
	public void start() {
		this.cooking = (Cookables) parameters[0];
		this.object = (GameObject) parameters[1];

		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.COOK,
						"Choose how many you wish to cook,<br>then click on the item to begin.",
						player.getInventory().getItems()
								.getNumberOf(cooking.getRawItem()),
						new int[] { cooking.getProduct().getId() }, null);
	}

}
