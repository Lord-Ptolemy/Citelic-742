package com.citelic.game.entity.player.content.actions.skills.magic;

import java.util.HashMap;
import java.util.Map;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;

public class Superheating {

	public static SmeltingBar bar;

	public static void processSuperHeat(Player player, final int barId) {
		if (!Magic.checkRunes(player, true, 554, 4, 561, 1))
			return;
		player.lock(2);
		bar = SmeltingBar.forId(barId);
		player.getActionManager().setAction(new Action() {

			@Override
			public boolean process(Player player) {
				if (bar == null || player == null) {
					player.getDialogueManager().startDialogue(
							"SimpleItemMessage", barId,
							"This item cannot be superheated.");
					return false;
				}
				if (!player.getInventory().containsItem(
						bar.getItemsRequired()[0].getId(),
						bar.getItemsRequired()[0].getAmount())) {
					player.getDialogueManager().startDialogue(
							"SimpleMessage",
							"You need "
									+ bar.getItemsRequired()[0]
											.getDefinitions().getName()
									+ " to create a "
									+ bar.getProducedBar().getDefinitions()
											.getName() + ".");
					return false;
				}
				if (bar.getItemsRequired().length > 1) {
					if (!player.getInventory().containsItem(
							bar.getItemsRequired()[1].getId(),
							bar.getItemsRequired()[1].getAmount())) {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You need "
										+ bar.getItemsRequired()[1]
												.getDefinitions().getName()
										+ " to create a "
										+ bar.getProducedBar().getDefinitions()
												.getName() + ".");
						return false;
					}
				}
				if (player.getSkills().getLevel(Skills.SMITHING) < bar
						.getLevelRequired()) {
					player.getDialogueManager().startDialogue(
							"SimpleMessage",
							"You need a Smithing level of at least "
									+ bar.getLevelRequired()
									+ " to superheat "
									+ bar.getProducedBar().getDefinitions()
											.getName());
					return false;
				}
				return true;
			}

			@Override
			public int processWithDelay(Player player) {
				for (Item required : bar.getItemsRequired()) {
					player.getInventory().deleteItem(required.getId(),
							required.getAmount());
				}
				player.setNextAnimation(new Animation(722));
				player.setNextGraphics(new Graphics(148));
				player.getInventory().addItem(bar.getProducedBar());
				player.getSkills().addXp(Skills.SMITHING, bar.getExperience());
				player.getSkills().addXp(Skills.MAGIC, 45);
				stop(player);
				return -1;
			}

			@Override
			public boolean start(Player player) {
				player.closeInterfaces();
				return true;
			}

			@Override
			public void stop(Player player) {
				player.getInterfaceManager().openGameTab(7);
				setActionDelay(player, 3);
			}

		});
	}

	public enum SmeltingBar {

		BRONZE(1, 6.2, new Item[] { new Item(436), new Item(438) }, new Item(
				2349), 438),

		BLURITE(8, 8.0, new Item[] { new Item(668) }, new Item(9467), 668),

		IRON(15, 12.5, new Item[] { new Item(440) }, new Item(2351), 440),

		SILVER(20, 13.7, new Item[] { new Item(442) }, new Item(2355), 442),

		GOLD(40, 22.5, new Item[] { new Item(444) }, new Item(2357), 444),

		MITHRIL(50, 30, new Item[] { new Item(447), new Item(453, 4) },
				new Item(2359), 447),

		ADAMANT(70, 37.5, new Item[] { new Item(449), new Item(453, 6) },
				new Item(2361), 449),

		RUNE(85, 50, new Item[] { new Item(451), new Item(453, 8) }, new Item(
				2363), 451),

		DRAGONBANE(80, 50, new Item[] { new Item(21779) }, new Item(21783, 1),
				21779),

		WALLASALKIBANE(80, 50, new Item[] { new Item(21780) }, new Item(21784,
				1), 21780),

		BASILISKBANE(80, 50, new Item[] { new Item(21781) },
				new Item(21785, 1), 21781), ABYSSSALBANE(80, 50,
				new Item[] { new Item(21782) }, new Item(21786, 1), 21782);
		private static Map<Integer, SmeltingBar> bars = new HashMap<Integer, SmeltingBar>();

		static {
			for (SmeltingBar bar : SmeltingBar.values()) {
				SmeltingBar.bars.put(bar.getButtonId(), bar);
			}
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item producedBar;

		private SmeltingBar(int levelRequired, double experience,
				Item[] itemsRequired, Item producedBar, int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.producedBar = producedBar;
			this.buttonId = buttonId;
		}

		public static SmeltingBar forId(int buttonId) {
			return SmeltingBar.bars.get(buttonId);
		}

		public int getButtonId() {
			return buttonId;
		}

		public double getExperience() {
			return experience;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProducedBar() {
			return producedBar;
		}
	}

}
