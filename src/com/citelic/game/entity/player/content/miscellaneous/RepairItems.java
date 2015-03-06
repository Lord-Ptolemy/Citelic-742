package com.citelic.game.entity.player.content.miscellaneous;

import java.util.HashMap;
import java.util.Map;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.utility.Utilities;

public class RepairItems {

	public enum BrokenItems {
		// Main structure NAME(Broken itemId, repaired itemId,Price to repair
		// it)
		TORVA_HELM(20138, 20135, 20000000),

		TORVA_PLATE(20142, 20139, 20000000),

		TORVA_LEGS(20146, 20143, 20000000),

		TORVA_GLOVES(24979, 24977, 20000000),

		TORVA_BOOTS(24985, 24983, 20000000),

		PERNIX_COWL(20150, 20147, 15000000),

		PERNIX_BODY(20154, 20151, 15000000),

		PERNIX_CHAPS(20158, 20155, 15000000),

		PERNIX_GLOVES(24976, 24974, 15000000),

		PERNIX_BOOTS(24991, 24989, 15000000),

		ZARYTE_BOW(20174, 20171, 20000000),

		VIRTUS_MASK(20162, 20159, 10000000),

		VIRTUS_TOP(20166, 20163, 10000000),

		VIRTUS_LEGS(20170, 20167, 10000000),

		VIRTUS_GLOVES(24982, 24980, 10000000),

		VIRTUS_BOOTS(24988, 24986, 10000000);

		private int id;
		private int id2;
		private int Price;

		private static Map<Integer, BrokenItems> BROKENITEMS = new HashMap<Integer, BrokenItems>();

		static {
			for (BrokenItems brokenitems : BrokenItems.values()) {
				BROKENITEMS.put(brokenitems.getId(), brokenitems);
			}
		}

		public static BrokenItems forId(int id) {
			return BROKENITEMS.get(id);
		}

		private BrokenItems(int id, int id2, int Price) {
			this.id = id;
			this.id2 = id2;
			this.Price = Price;
		}

		public int getId() {
			return id;
		}

		public int getId2() {
			return id2;
		}

		public int getPrice() {
			return Price;
		}
	}

	public static void Repair(Player player, int itemId, int amount) {
		final BrokenItems brokenitems = BrokenItems.forId(itemId);
		Item item = new Item(brokenitems.getId(), 1);
		int price = brokenitems.getPrice();
		if (amount == 1) {
			if (player.getInventory().containsItem(995, price)) {
				player.getInventory().deleteItem(itemId, 1);
				player.getInventory().deleteItem(995, price);
				player.getInventory().addItem(brokenitems.getId2(), 1);
				player.getDialogueManager().startDialogue(
						"SimpleMessage",
						"Bob repaired your item: " + item.getName() + " for "
								+ Utilities.getFormattedNumber(price)
								+ " coins.");
				return;
			} else {
				player.getDialogueManager().startDialogue(
						"SimpleNPCMessage",
						519,
						" You dont have enough money to repair this item. "
								+ "It costs "
								+ Utilities.getFormattedNumber(price)
								+ " coins to repair it.");
				return;
			}
		} else {
			if (player.getInventory().containsItem(995, price * amount)) {
				player.getInventory().deleteItem(itemId, amount);
				player.getInventory().deleteItem(995, price * amount);
				player.getInventory().addItem(brokenitems.getId2(), amount);
				player.getDialogueManager().startDialogue(
						"SimpleMessage",
						"Bob repaired your item(s): " + amount + " X "
								+ item.getName() + " for "
								+ Utilities.getFormattedNumber(price * amount)
								+ " coins.");
				return;
			} else {
				player.getDialogueManager().startDialogue(
						"SimpleNPCMessage",
						519,
						"You dont have enough money to repair these items. "
								+ "It costs "
								+ Utilities.getFormattedNumber(price)
								+ " coins to repair it.");
				return;
			}
		}
	}

	/*
	 * public static void CheckPrice(Player player,int itemId, int amount) {
	 * final BrokenItems brokenitems = BrokenItems.forId(itemId); int price =
	 * brokenitems.getPrice();
	 * player.getDialogueManager().startDialogue("SimpleNPCMessage", 945,
	 * "these items will cost you " +player.getFormattedNumber(price *
	 * amount)+" coins."); return; }
	 */
}