package com.citelic.game.entity.npc;

public class Drop {

	public static Drop create(int itemId, double rate, int minAmount,
			int maxAmount, boolean rare) {
		return new Drop((short) itemId, rate, minAmount, maxAmount, rare);
	}

	private int itemId, minAmount, maxAmount;
	private double rate;
	private boolean rare;

	public Drop(int itemId, double rate, int minAmount, int maxAmount,
			boolean rare) {
		this.itemId = itemId;
		this.rate = rate;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.rare = rare;
	}

	public int getExtraAmount() {
		return maxAmount - minAmount;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public double getRate() {
		return rate;
	}

	public boolean isFromRareTable() {
		return rare;
	}

	public void setItemId(short itemId) {
		this.itemId = itemId;
	}

	public void setMaxAmount(int amount) {
		this.maxAmount = amount;
	}

	public void setMinAmount(int amount) {
		this.minAmount = amount;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}
}