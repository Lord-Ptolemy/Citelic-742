package com.citelic.game.entity.player.content.actions.skills.fletching;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;
import com.citelic.networking.codec.decode.impl.InventoryOptionsHandler;

public class BoltTipFletching extends Action {

	private Tips gem;
	private int quantity;

	public BoltTipFletching(Tips gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	private static void cut(Player player, Tips gem) {
		if (player.getInventory().getItems()
				.getNumberOf(new Item(gem.getGem(), 1)) <= 1) {
			player.getActionManager().setAction(new BoltTipFletching(gem, 1));
		} else {
			player.getDialogueManager().startDialogue("BoltTipFletchingD", gem);
		}
	}

	public static boolean isCutting(Player player, int gemId) {
		for (Tips gem : Tips.values()) {
			if (gem.gem == gemId) {
				BoltTipFletching.cut(player, gem);
				return true;
			}
		}
		return false;
	}

	public static boolean isCutting(Player player, Item item1, Item item2) {
		Item gem = InventoryOptionsHandler.contains(1755, item1, item2);
		if (gem == null)
			return false;
		return BoltTipFletching.isCutting(player, gem.getId());
	}

	public boolean checkAll(Player player) {
		if (!player.getInventory().containsItemToolBelt(1755)) {
			player.getPackets().sendGameMessage(
					"You do not have the required items to cut this.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.FLETCHING) < gem
				.getLevelRequired()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a fletching level of " + gem.getLevelRequired()
							+ " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getGem())) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You don't have any "
							+ ItemDefinitions.getItemDefinitions(gem.getGem())
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
		player.getInventory().deleteItem(gem.getGem(), 1);
		player.getInventory().addItem(gem.getTip(), 12);
		player.getSkills().addXp(Skills.FLETCHING, gem.getExperience());
		player.getPackets().sendGameMessage(
				"You cut the "
						+ ItemDefinitions.getItemDefinitions(gem.getGem())
								.getName().toLowerCase() + ".", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote()));
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

	public enum Tips {
		OPAL(1609, 45, 1.5, 11, 886),

		JADE(1611, 9187, 2, 26, 886),

		TOPAZ(1613, 9188, 3.9, 48, 887),

		SAPPHIRE(1607, 9189, 4, 56, 888),

		EMERALD(1605, 9190, 5.5, 58, 889),

		RUBY(1603, 9191, 6.3, 63, 887),

		DIAMOND(1601, 9192, 7, 65, 890),

		DRAGON(1615, 9193, 8.2, 71, 885),

		ONYX(6573, 9194, 7.3, 94, 2717);

		private double experience;
		private int levelRequired;
		private int gem, tip;

		private int emote;

		private Tips(int gem, int tip, double experience, int levelRequired,
				int emote) {
			this.gem = gem;
			this.tip = tip;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
		}

		public int getEmote() {
			return emote;
		}

		public double getExperience() {
			return experience;
		}

		public int getGem() {
			return gem;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getTip() {
			return tip;
		}

	}
}