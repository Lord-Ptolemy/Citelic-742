package com.citelic.game.entity.player.containers;

import java.io.Serializable;
import java.util.List;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.miscellaneous.ClueScrolls;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemsContainer;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;
import com.citelic.utility.item.ItemExamines;

public final class Inventory implements Serializable {

	public static final int INVENTORY_INTERFACE = 679;
	private static final long serialVersionUID = 8842800123753277093L;
	private ItemsContainer<Item> items;
	private transient Player player;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public boolean addItem(int itemId, int amount) {
		if (itemId < 0
				|| amount < 0
				|| !Utilities.itemExists(itemId)
				|| !player.getControllerManager().canAddInventoryItem(itemId,
						amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		for (int scroll : ClueScrolls.ScrollIds) {
			if (itemId == scroll) {
				for (int scroll1 : ClueScrolls.ScrollIds) {
					if (containsItem(scroll1)) {
						player.getPackets()
								.sendGameMessage(
										"A magical force stops you from picking this item up.");
						return false;
					}
					if (player.getBank().containsItem(scroll1, 1)) {
						player.getPackets()
								.sendGameMessage(
										"A magical force stops you from picking this item up.");
						return false;
					}
				}
			}
		}
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItem(Item item) {
		if (item.getId() < 0
				|| item.getAmount() < 0
				|| !Utilities.itemExists(item.getId())
				|| !player.getControllerManager().canAddInventoryItem(
						item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemDrop(int itemId, int amount) {
		return addItemDrop(itemId, amount, new Tile(player));
	}

	public boolean addItemDrop(int itemId, int amount, Tile tile) {
		if (itemId < 0
				|| amount < 0
				|| !Utilities.itemExists(itemId)
				|| !player.getControllerManager().canAddInventoryItem(itemId,
						amount))
			return false;
		if (itemId == 995)
			return player.getMoneyPouch().sendDynamicInteraction(amount, false);
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			Engine.addGroundItem(new Item(itemId, amount), tile, player, true,
					46000, 180);
		} else {
			refreshItems(itemsBefore);
		}
		return true;
	}

	public boolean addItemMoneyPouch(Item item) {
		if (item.getId() == 995)
			return player.getMoneyPouch().sendDynamicInteraction(
					item.getAmount(), false);
		return this.addItem(item);
	}

	public boolean containsItem(int itemId) {
		return items.contains(new Item(itemId, 1));
	}

	public boolean containsItem(int itemId, int amount) {
		return items.contains(new Item(itemId, amount));
	}

	public boolean containsItems(int[] itemIds, int[] amounts) {
		int size = itemIds.length > amounts.length ? amounts.length
				: itemIds.length;
		for (int i = 0; i < size; i++)
			if (!items.contains(new Item(itemIds[i], amounts[i])))
				return false;
		return true;
	}

	public boolean containsItems(Item[] item) {
		for (int i = 0; i < item.length; i++)
			if (!items.contains(item[i]))
				return false;
		return true;
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list)
			if (!items.contains(item))
				return false;
		return true;
	}

	public boolean containsItemToolBelt(int id) {
		return containsOneItem(id) || player.getToolbelt().containsItem(id);
	}

	public boolean containsItemToolBelt(int id, int amount) {
		return this.containsItem(id, amount)
				|| player.getToolbelt().containsItem(id);
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1))
					|| player.getToolbelt().containsItem(itemId))
				return true;
		}
		return false;
	}

	public boolean deleteCoins(int amount) {
		if (getNumberOf(995) >= amount) {
			this.deleteItem(995, amount);
			return true;
		} else
			return false;
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControllerManager().canDeleteInventoryItem(itemId,
				amount))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControllerManager().canDeleteInventoryItem(item.getId(),
				item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		if (!player.getControllerManager().canDeleteInventoryItem(item.getId(),
				item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	public int getCoinsAmount() {
		int coins = items.getNumberOf(995)
				+ player.getMoneyPouch().getCoinsAmount();
		return coins < 0 ? Integer.MAX_VALUE : coins;
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public int getNumberOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public void init() {
		player.getPackets().sendItems(93, items);
	}

	public void refresh() {
		player.getPackets().sendItems(93, items);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(93, items, slots);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index]) {
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		this.refresh(finalChangedSlots);
	}

	public boolean removeItemMoneyPouch(Item item) {
		if (item.getId() == 995)
			return player.getMoneyPouch().sendDynamicInteraction(
					item.getAmount(), true);
		return this.removeItems(item);
	}

	public boolean removeItems(Item... list) {
		for (Item item : list) {
			if (item == null) {
				continue;
			}
			this.deleteItem(item);
		}
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null) {
				continue;
			}
			this.deleteItem(item);
		}
		return true;
	}

	public void replaceItem(int id, int amount, int slot) {
		Item item = items.get(slot);
		if (item == null)
			return;
		item.setId(id);
		item.setAmount(amount);
		this.refresh(slot);
	}

	public void reset() {
		items.reset();
		init(); // as all slots reseted better just send all again
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize())
			return;
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.getPackets().sendInventoryMessage(0, slotId,
				ItemExamines.getExamine(item));
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void swapItems(int itemId, int itemId2) {
		this.deleteItem(itemId, 1);
		this.addItem(itemId2, 1);
	}

	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void unlockInventoryOptions() {
		player.getPackets().sendIComponentSettings(
				Inventory.INVENTORY_INTERFACE, 0, 0, 27, 4554126);
		player.getPackets().sendIComponentSettings(
				Inventory.INVENTORY_INTERFACE, 0, 28, 55, 2097152);
	}

}
