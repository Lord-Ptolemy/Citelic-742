package com.citelic.game.entity.player.item;

import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class FloorItem extends Item {

	private Tile tile;
	private Player owner;
	private boolean invisible;
	private boolean grave;

	public FloorItem(int id) {
		super(id);
	}

	public FloorItem(Item item, Tile tile, Player owner, boolean underGrave,
			boolean invisible) {
		super(item.getId(), item.getAmount());
		this.tile = tile;
		this.owner = owner;
		grave = underGrave;
		this.invisible = invisible;
	}

	public Player getOwner() {
		return owner;
	}

	public Tile getTile() {
		return tile;
	}

	public boolean isGrave() {
		return grave;
	}

	public boolean isInvisible() {
		return invisible;
	}

	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

}
