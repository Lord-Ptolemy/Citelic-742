package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.crafting.Spinning;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;

public class SpinningD extends Dialogue {

	private int itemId[];

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		itemId = Spinning.getBeforeFromAfter(SkillsDialogue.getItem(option));
		final int[] quantity = { SkillsDialogue.getQuantity(player) };
		int invQuantity = player.getInventory().getItems()
				.getNumberOf(itemId[0]);
		if (quantity[0] > invQuantity) {
			quantity[0] = invQuantity;
		}
		Action action = new Action() {
			@Override
			public boolean process(Player player) {
				if (quantity[0] <= 0)
					return false;
				return true;
			}

			@Override
			public int processWithDelay(Player player) {
				quantity[0]--;
				Spinning.canSpin(player, itemId[0]);
				return 5;
			}

			@Override
			public boolean start(Player player) {
				if (quantity[0] <= 0)
					return false;
				return true;
			}

			@Override
			public void stop(Player player) {
			}
		};
		player.getActionManager().setAction(action);
		end();
	}

	@Override
	public void start() {
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.MAKE,
						"Choose how many you wish to make, then click on the chosen item to begin.",
						28, new int[] { 1759, 1777, 9438, 9438, 6038, 954 },
						new SkillsDialogue.ItemNameFilter() {
							@Override
							public String rename(String name) {
								if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(1759).getName()))
									return "Ball of wool (Wool)";
								else if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(1777).getName()))
									return "Bow string (Flax)";
								else if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(9438).getName())
										&& SkillsDialogue.getItemSlot(16) == 2)
									return "C'bow string (Sinew)";
								else if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(9438).getName())
										&& SkillsDialogue.getItemSlot(17) == 3)
									return "C'bow string (Tree roots)";
								else if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(6038).getName()))
									return "Magic string (Magic roots)";
								else if (name.equalsIgnoreCase(ItemDefinitions
										.getItemDefinitions(954).getName()))
									return "Rope (Yak hide)";
								return name;
							}
						});
	}
}