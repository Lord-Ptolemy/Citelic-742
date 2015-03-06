package com.citelic.game.entity.player.content.actions;

import com.citelic.game.entity.player.Player;

public abstract class Action {

	public abstract boolean process(Player player);

	public abstract int processWithDelay(Player player);

	protected final void setActionDelay(Player player, int delay) {
		player.getActionManager().setActionDelay(delay);
	}

	public abstract boolean start(Player player);

	public abstract void stop(Player player);
}
