package com.citelic.game.entity.player.content.actions.skills.crafting;

import java.util.HashMap;
import java.util.Map;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;
import com.citelic.utility.Utilities;

public class LeatherCrafting extends Action {

	public static final Item NEEDLE = new Item(1733);
	public static final Item THREAD = new Item(1734);
	public static final int LEATHER[] = { 1745, 2505, 2507, 2509, 24374 };
	public static final int PRODUCTS[][] = { { 1065, 1099, 1135 },
			{ 2487, 2493, 2499 }, { 2489, 2495, 2501 }, { 2491, 2497, 2503 },
			{ 24376, 24379, 24381 } };
	public final Animation CRAFT_ANIMATION = new Animation(1249);
	private int quantity;
	private LeatherData data;
	private int removeThread = 5;

	public LeatherCrafting(LeatherData data, int quantity) {
		this.data = data;
		this.quantity = quantity;
	}

	public static int getIndex(Player player) {
		int leather = (Integer) player.getTemporaryAttributtes().get(
				"leatherType");
		if (leather == LeatherCrafting.LEATHER[0])
			return 0;
		if (leather == LeatherCrafting.LEATHER[1])
			return 1;
		if (leather == LeatherCrafting.LEATHER[2])
			return 2;
		return -1;
	}

	public static boolean handleItemOnItem(Player player, Item itemUsed,
			Item usedWith) {
		for (int i = 0; i < LeatherCrafting.LEATHER.length; i++) {
			if (itemUsed.getId() == LeatherCrafting.LEATHER[i]
					|| usedWith.getId() == LeatherCrafting.LEATHER[i]) {
				player.getTemporaryAttributtes().put("leatherType",
						LeatherCrafting.LEATHER[i]);
				int index = LeatherCrafting.getIndex(player);
				if (index == -1)
					return true;
				player.getDialogueManager().startDialogue("LeatherCraftingD",
						LeatherData.forId(LeatherCrafting.PRODUCTS[index][0]),
						LeatherData.forId(LeatherCrafting.PRODUCTS[index][1]),
						LeatherData.forId(LeatherCrafting.PRODUCTS[index][2]));
				return true;
			}
		}
		return false;
	}

	private boolean checkAll(Player player) {
		if (data.getRequiredLevel() > player.getSkills().getLevel(
				Skills.CRAFTING)) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a crafting level of " + data.getRequiredLevel()
							+ " to craft this hide.");
			return false;
		}
		if (player.getInventory().getItems().getNumberOf(data.getLeatherId()) < data
				.getLeatherAmount()) {
			player.getPackets().sendGameMessage(
					"You don't have enough amount of hides in your inventory.");
			return false;
		}
		if (!player.getInventory().getItems()
				.containsOne(LeatherCrafting.THREAD)) {
			player.getPackets()
					.sendGameMessage("You need a thread to do this.");
			return false;
		}
		if (!player.getInventory().getItems()
				.containsOne(LeatherCrafting.NEEDLE)) {
			player.getPackets().sendGameMessage(
					"You need a needle to craft leathers.");
			return false;
		}
		if (!player.getInventory().containsOneItem(data.getLeatherId())) {
			player.getPackets().sendGameMessage(
					"You've ran out of "
							+ ItemDefinitions
									.getItemDefinitions(data.getLeatherId())
									.getName().toLowerCase() + ".");
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
		player.getInventory().deleteItem(data.getLeatherId(),
				data.getLeatherAmount());
		player.getInventory().addItem(data.getFinalProduct(), 1);
		player.getSkills().addXp(Skills.CRAFTING, data.getExperience());
		player.getPackets().sendGameMessage(
				"You make a " + data.getName().toLowerCase() + ".");
		quantity--;
		removeThread--;
		if (removeThread == 0) {
			removeThread = 5;
			player.getInventory().removeItems(LeatherCrafting.THREAD);
			player.getPackets().sendGameMessage(
					"You use up a reel of your thread.");
		}
		if (Utilities.getRandom(30) <= 3) {
			player.getInventory().removeItems(LeatherCrafting.NEEDLE);
			player.getPackets().sendGameMessage("Your needle has broken.");
		}
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(CRAFT_ANIMATION);
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		setActionDelay(player, 1);
		player.setNextAnimation(CRAFT_ANIMATION);
		return true;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public enum LeatherData {
		GREEN_D_HIDE_VAMBS(1745, 1, 1065, 57, 62),

		GREEN_D_HIDE_CHAPS(1745, 2, 1099, 60, 124),

		GREEN_D_HIDE_BODY(1745, 3, 1135, 63, 186),

		BLUE_D_HIDE_VAMBS(2505, 1, 2487, 66, 70),

		BLUE_D_HIDE_CHAPS(2505, 2, 2493, 68, 140),

		BLUE_D_HIDE_BODY(2505, 3, 2499, 71, 210),

		RED_D_HIDE_VAMBS(2507, 1, 2489, 73, 78),

		RED_D_HIDE_CHAPS(2507, 2, 2495, 75, 156),

		RED_D_HIDE_BODY(2507, 3, 2501, 77, 234),

		BLACK_D_HIDE_VAMBS(2509, 1, 2491, 79, 86),

		BLACK_D_HIDE_CHAPS(2509, 2, 2497, 82, 172),

		BLACK_D_HIDE_BODY(2509, 3, 2503, 84, 258),

		ROYAL_D_HIDE_VAMBS(24374, 1, 24376, 87, 94),

		ROYAL_D_HIDE_CHAPS(24374, 2, 24379, 89, 188),

		ROYAL_D_HIDE_BODY(24374, 3, 24382, 93, 282);
		private static Map<Integer, LeatherData> leatherItems = new HashMap<Integer, LeatherData>();

		static {
			for (LeatherData leather : LeatherData.values()) {
				LeatherData.leatherItems.put(leather.finalProduct, leather);
			}
		}

		private int leatherId, leatherAmount, finalProduct, requiredLevel;
		private double experience;
		private String name;

		private LeatherData(int leatherId, int leatherAmount, int finalProduct,
				int requiredLevel, double experience) {
			this.leatherId = leatherId;
			this.leatherAmount = leatherAmount;
			this.finalProduct = finalProduct;
			this.requiredLevel = requiredLevel;
			this.experience = experience;
			name = ItemDefinitions.getItemDefinitions(getFinalProduct())
					.getName().replace("d'hide", "");
		}

		public static LeatherData forId(int id) {
			return LeatherData.leatherItems.get(id);
		}

		public double getExperience() {
			return experience;
		}

		public int getFinalProduct() {
			return finalProduct;
		}

		public int getLeatherAmount() {
			return leatherAmount;
		}

		public int getLeatherId() {
			return leatherId;
		}

		public String getName() {
			return name;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}
	}

}
