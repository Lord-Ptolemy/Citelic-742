package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue.ItemNameFilter;

public class AmuletAttachingD extends Dialogue {

	private static final double[] EXPERIENCE = { 30, 65, 70, 85, 100, 150, 165 };
	private static final int[] LEVELS = { 8, 24, 31, 31, 50, 70, 80, 90 };
	private static final int[] BASE = { 1673, 1675, 1677, 1679, 1681, 6579 };
	private static final int[] PRODUCTS = { 1692, 1694, 1696, 1698, 1700, 6581 };
	private static final int STRING = 1759;

	public static boolean isAttaching(int used, int usedWith) {
		for (int item : AmuletAttachingD.BASE) {
			if (item == used && usedWith == AmuletAttachingD.STRING
					|| item == usedWith && used == AmuletAttachingD.STRING)
				return true;
		}
		return false;
	}

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		final int index = SkillsDialogue.getItemSlot(componentId);
		if (index > AmuletAttachingD.PRODUCTS.length) {
			end();
			return;
		}
		player.getActionManager().setAction(new Action() {

			int ticks;

			public boolean checkAll(Player player) {
				if (player.getSkills().getLevel(Skills.CRAFTING) < AmuletAttachingD.LEVELS[index]) {
					player.getPackets().sendGameMessage(
							"You need a Crafting level of "
									+ AmuletAttachingD.LEVELS[index] + ".");
					return false;
				} else if (!player.getInventory().containsItem(
						AmuletAttachingD.STRING, 1)) {
					player.getPackets().sendGameMessage(
							"You have run out of balls of whool.");
					return false;
				} else if (!player.getInventory().containsItem(
						AmuletAttachingD.BASE[index], 1)) {
					player.getPackets().sendGameMessage(
							"You have run out amulets.");
					return false;
				}
				return true;
			}

			@Override
			public boolean process(Player player) {
				return checkAll(player) && ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.getSkills().addXp(Skills.CRAFTING,
						AmuletAttachingD.EXPERIENCE[index]);
				player.getInventory().deleteItem(AmuletAttachingD.STRING, 1);
				player.getInventory().deleteItem(AmuletAttachingD.BASE[index],
						1);
				player.getInventory().addItem(AmuletAttachingD.PRODUCTS[index],
						1);
				player.getPackets().sendGameMessage(
						"You put some string on your amulet.");
				return 2;
			}

			@Override
			public boolean start(final Player player) {
				if (!checkAll(player))
					return false;
				int amuletAmount = player.getInventory().getNumberOf(
						AmuletAttachingD.BASE[index]);
				int requestedAmount = SkillsDialogue.getQuantity(player);
				if (requestedAmount > amuletAmount) {
					requestedAmount = amuletAmount;
				}
				int stringAmount = player.getInventory().getNumberOf(
						AmuletAttachingD.STRING);
				if (requestedAmount > stringAmount) {
					requestedAmount = stringAmount;
				}
				ticks = requestedAmount;
				return true;
			}

			@Override
			public void stop(Player player) {
				setActionDelay(player, 3);
			}
		});
		end();
	}

	@Override
	public void start() {
		SkillsDialogue
				.sendSkillsDialogue(
						player,
						SkillsDialogue.MAKE,
						"Choose how many you wish to make,<br>then click on the item to begin.",
						28, AmuletAttachingD.PRODUCTS, new ItemNameFilter() {

							int count = 0;

							@Override
							public String rename(String name) {
								int levelRequired = AmuletAttachingD.LEVELS[count++];
								if (AmuletAttachingD.this.player.getSkills()
										.getLevel(Skills.CRAFTING) < levelRequired) {
									name = "<col=ff0000>" + name
											+ "<br><col=ff0000>Level "
											+ levelRequired;
								}
								return name;
							}
						});
	}
}
