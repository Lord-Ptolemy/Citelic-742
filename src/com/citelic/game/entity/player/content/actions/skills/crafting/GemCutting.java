package com.citelic.game.entity.player.content.actions.skills.crafting;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;
import com.citelic.networking.codec.decode.impl.InventoryOptionsHandler;

public class GemCutting extends Action {

	private Gem gem;
	private int quantity;

	public GemCutting(Gem gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	private static void cut(Player player, Gem gem) {
		if (player.getInventory().getItems()
				.getNumberOf(new Item(gem.getUncut(), 1)) <= 1) {
			// just
			// 1 lets start
			player.getActionManager().setAction(new GemCutting(gem, 1));
		} else {
			player.getDialogueManager().startDialogue("GemCuttingD", gem);
		}
	}

	public static boolean isCutting(Player player, int gemId) {
		for (Gem gem : Gem.values()) {
			if (gem.uncut == gemId) {
				GemCutting.cut(player, gem);
				return true;
			}
		}
		return false;
	}

	public static boolean isCutting(Player player, Item item1, Item item2) {
		Item gem = InventoryOptionsHandler.contains(1755, item1, item2);
		if (gem == null)
			return false;
		return GemCutting.isCutting(player, gem.getId());
	}

	public boolean checkAll(Player player) {
		if (!player.getInventory().containsItemToolBelt(1755)) {
			player.getPackets().sendGameMessage(
					"You do not have the required items to cut this.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < gem
				.getLevelRequired()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a crafting level of " + gem.getLevelRequired()
							+ " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getUncut())) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You don't have any "
							+ ItemDefinitions
									.getItemDefinitions(gem.getUncut())
									.getName().toLowerCase() + " to cut.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(gem.getUncut(), 1);
		player.getInventory().addItem(gem.getCut(), 1);
		player.getSkills().addXp(Skills.CRAFTING, gem.getExperience());
		player.getPackets().sendGameMessage(
				"You cut the "
						+ ItemDefinitions.getItemDefinitions(gem.getUncut())
								.getName().toLowerCase() + ".", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote())); // start
		// the
		// emote and add
		// 2 delay
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 1);
			player.setNextAnimation(new Animation(gem.getEmote()));
			return true;
		}
		return false;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public enum Gem {
		OPAL(1625, 1609, 15.0, 1, 886),

		JADE(1627, 1611, 20, 13, 886),

		RED_TOPAZ(1629, 1613, 25, 16, 887),

		SAPPHIRE(1623, 1607, 50, 20, 888),

		EMERALD(1621, 1605, 67, 27, 889),

		RUBY(1619, 1603, 85, 34, 887),

		DIAMOND(1617, 1601, 107.5, 43, 890),

		DRAGONSTONE(1631, 1615, 137.5, 55, 885),

		ONYX(6571, 6573, 167.5, 67, 2717);

		private double experience;
		private int levelRequired;
		private int uncut, cut;

		private int emote;

		private Gem(int uncut, int cut, double experience, int levelRequired,
				int emote) {
			this.uncut = uncut;
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
		}

		public int getCut() {
			return cut;
		}

		public int getEmote() {
			return emote;
		}

		public double getExperience() {
			return experience;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getUncut() {
			return uncut;
		}

	}
}