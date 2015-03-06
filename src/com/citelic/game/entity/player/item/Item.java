package com.citelic.game.entity.player.item;

import java.io.Serializable;

import com.citelic.cache.impl.item.ItemDefinitions;

/**
 * Represents a single item.
 * <p/>
 * 
 * @author Graham / edited by Dragonkk(Alex)
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	private short id;
	protected int amount;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int amount) {
		this(id, amount, false);
	}

	public Item(int id, int amount, boolean amt0) {
		this.id = (short) id;
		this.amount = amount;
		if (this.amount <= 0 && !amt0) {
			this.amount = 1;
		}
	}

    public Item(Item item) {
	this.id = (short) item.getId();
	this.amount = item.getAmount();
    }
    
	@Override
	public Item clone() {
		return new Item(id, amount);
	}

	public int getAmount() {
		return amount;
	}

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(id);
	}

	public int getEquipId() {
		// in 742 they no longer use equip ids, they just draw item ids onto the
		// player.
		return id;
		// return ItemsEquipIds.getEquipId(id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return getDefinitions().getName();
	}

	public void setAmount(int amount) {
		if (this.amount + amount < 0 || amount > Integer.MAX_VALUE) {
			return;
		}
		this.amount = amount;
	}

	public void setId(int id) {
		this.id = (short) id;
	}

}
