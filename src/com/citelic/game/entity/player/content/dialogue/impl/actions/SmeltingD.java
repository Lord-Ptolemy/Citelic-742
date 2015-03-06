package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.smithing.Smelting;
import com.citelic.game.entity.player.content.actions.skills.smithing.Smelting.SmeltingBar;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue.ItemNameFilter;
import com.citelic.game.map.objects.GameObject;

public class SmeltingD extends Dialogue {

	private GameObject object;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new Smelting(SkillsDialogue.getItemSlot(componentId), object,
						SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void start() {
		object = (GameObject) parameters[0];
		int[] ids = new int[SmeltingBar.values().length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = SmeltingBar.values()[i].getProducedBar().getId();
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.MAKE,
						"How many bars you would like to smelt?<br>Choose a number, then click the bar to begin.",
						28, ids, new ItemNameFilter() {
							int count = 0;

							@Override
							public String rename(String name) {
								SmeltingBar bar = SmeltingBar.values()[count++];
								if (player.getSkills()
										.getLevel(Skills.SMITHING) < bar
										.getLevelRequired())
									name = "<col=ff0000>" + name
											+ "<br><col=ff0000>Level "
											+ bar.getLevelRequired();
								return name;

							}
						});
	}
}
