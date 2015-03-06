package com.citelic.game.entity.player.content.miscellaneous;

import com.citelic.game.entity.player.Player;

public final class AdventurersLog {

	public static void open(Player player) {
		player.getInterfaceManager().sendInterface(623);
	}

	private AdventurersLog() {

	}
}
