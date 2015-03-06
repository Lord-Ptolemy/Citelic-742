package com.citelic.game;

public final class SecondBar {

	private int timer, startHp, endHp;

	/**
	 * Constructs a new {@code SecondBar} instance.
	 * 
	 * @param time
	 */
	public SecondBar(int time) {
		this.setTimer(5);
		this.startHp = 1;
		this.endHp = time;
	}

	/**
	 * @return the endHp
	 */
	public int getEndHp() {
		return endHp;
	}

	/**
	 * @return the startHp
	 */
	public int getStartHp() {
		return startHp;
	}

	/**
	 * @return the timer
	 */
	public int getTimer() {
		return timer;
	}

	/**
	 * @param endHp
	 *            the endHp to set
	 */
	public void setEndHp(int endHp) {
		this.endHp = endHp;
	}

	/**
	 * @param startHp
	 *            the startHp to set
	 */
	public void setStartHp(int startHp) {
		this.startHp = startHp;
	}

	/**
	 * @param timer
	 *            the timer to set
	 */
	public void setTimer(int timer) {
		this.timer = timer;
	}

}