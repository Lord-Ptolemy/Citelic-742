package com.citelic.game.entity.player.content.dialogue;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.player.Player;

public final class SkillsDialogue {

	public static final int MAKE = 0, MAKE_SETS = 1, COOK = 2, ROAST = 3,
			OFFER = 4, SELL = 5, BAKE = 6, CUT = 7, DEPOSIT = 8,
			MAKE_NO_ALL_NO_CUSTOM = 9, TELEPORT = 10, SELECT = 11, TAKE = 13;
	private static int[] items;

	private SkillsDialogue() {

	}

	public static int getItem(int slot) {
		return SkillsDialogue.items[slot];
	}

	public static int getItemSlot(int componentId) {
		if (componentId < 14)
			return 0;
		return componentId - 14;
	}

	public static int getMaxQuantity(Player player) {
		Integer maxQuantity = (Integer) player.getTemporaryAttributtes().get(
				"SkillsDialogueMaxQuantity");
		if (maxQuantity == null)
			return 0;
		return maxQuantity;
	}

	public static int getQuantity(Player player) {
		Integer quantity = (Integer) player.getTemporaryAttributtes().get(
				"SkillsDialogueQuantity");
		if (quantity == null)
			return 0;
		return quantity;
	}

	public static void handleSetQuantityButtons(Player player, int componentId) {
		if (componentId == 5) {
			SkillsDialogue.setQuantity(player, 1, false);
		} else if (componentId == 6) {
			SkillsDialogue.setQuantity(player, 5, false);
		} else if (componentId == 7) {
			SkillsDialogue.setQuantity(player, 10, false);
		} else if (componentId == 8) {
			SkillsDialogue.setQuantity(player,
					SkillsDialogue.getMaxQuantity(player), false);
		} else if (componentId == 19) {
			SkillsDialogue.setQuantity(player,
					SkillsDialogue.getQuantity(player) + 1, false);
		} else if (componentId == 20) {
			SkillsDialogue.setQuantity(player,
					SkillsDialogue.getQuantity(player) - 1, false);
		}
	}

	public static void sendSkillsDialogue(Player player, int option,
			String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter) {
		SkillsDialogue.sendSkillsDialogue(player, option, explanation,
				maxQuantity, items, filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option,
			String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter, boolean sendQuantitySelector) {
		player.getInterfaceManager().sendChatBoxInterface(905);
		player.getPackets().sendInterface(true, 905, 4, 916);
		if (!sendQuantitySelector) {
			maxQuantity = -1;
			player.getPackets().sendHideIComponent(916, 4, true);
			player.getPackets().sendHideIComponent(916, 9, true);
		} else {
			if (option != SkillsDialogue.MAKE_SETS
					&& option != SkillsDialogue.MAKE_NO_ALL_NO_CUSTOM) {
				player.getPackets().sendUnlockIComponentOptionSlots(916, 8, -1,
						0, 0); // unlocks
				// all
				// option
			}
		}
		player.getPackets().sendIComponentText(916, 1, explanation);
		player.getPackets().sendGlobalConfig(754, option);
		for (int i = 0; i < 10; i++) {
			if (i >= items.length) {
				player.getPackets().sendGlobalConfig(
						i >= 6 ? 1139 + i - 6 : 755 + i, -1);
				continue;
			}
			player.getPackets().sendGlobalConfig(
					i >= 6 ? 1139 + i - 6 : 755 + i, items[i]);
			String name = ItemDefinitions.getItemDefinitions(items[i])
					.getName();
			if (filter != null) {
				name = filter.rename(name);
			}
			player.getPackets().sendGlobalString(
					i >= 6 ? 280 + i - 6 : 132 + i, name);
		}
		SkillsDialogue.setMaxQuantity(player, maxQuantity);
		SkillsDialogue.setQuantity(player, maxQuantity);
		SkillsDialogue.setItem(items);
	}

	public static void setItem(int... item) {
		SkillsDialogue.items = item;
	}

	public static void setMaxQuantity(Player player, int maxQuantity) {
		player.getTemporaryAttributtes().put("SkillsDialogueMaxQuantity",
				maxQuantity);
		player.getPackets().sendConfigByFile(8094, maxQuantity);
	}

	public static void setQuantity(Player player, int quantity) {
		SkillsDialogue.setQuantity(player, quantity, true);
	}

	public static void setQuantity(Player player, int quantity, boolean refresh) {
		int maxQuantity = SkillsDialogue.getMaxQuantity(player);
		if (quantity > maxQuantity) {
			quantity = maxQuantity;
		} else if (quantity < 0) {
			quantity = 0;
		}
		player.getTemporaryAttributtes()
				.put("SkillsDialogueQuantity", quantity);
		if (refresh) {
			player.getPackets().sendConfigByFile(8095, quantity);
		}
	}

	public static interface ItemNameFilter {

		public String rename(String name);
	}

}
