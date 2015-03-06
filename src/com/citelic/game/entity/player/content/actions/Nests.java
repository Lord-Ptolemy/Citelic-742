package com.citelic.game.entity.player.content.actions;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class Nests {

	public static final int[][] SEEDS = {
			{ 5312, 5283, 5284, 5313, 5285, 5286 },
			{ 5314, 5288, 5287, 5315, 5289 }, { 5316, 5290 }, { 5317 } };
	private static final int[] RINGS = { 1635, 1637, 1639, 1641, 1643 };

	private static int getRewardForId(int id) {
		if (id == 5070)
			return 5076;
		else if (id == 11966)
			return 11964;
		else if (id == 5071)
			return 5078;
		else if (id == 5072)
			return 5077;
		else if (id == 5074)
			return Nests.RINGS[Utilities.random(Nests.RINGS.length)];
		else if (id == 7413 || id == 5073) {
			double random = Utilities.random(0, 100);
			final int reward = random <= 39.69 ? 0 : random <= 56.41 ? 1
					: random <= 76.95 ? 2 : random <= 96.4 ? 3 : 1;
			return Nests.SEEDS[reward][Utilities
					.random(Nests.SEEDS[reward].length)];
		}
		return -1;
	}

	public static boolean isNest(int id) {
		return id == 5070 || id == 5071 || id == 5072 || id == 5073
				|| id == 5074 || id == 7413 || id == 11966;
	}

	public static void searchNest(final Player player, final int slot) {
		player.getPackets().sendGameMessage(
				"You search the nest...and find something in it!");
		player.lock(1);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.getInventory().addItem(
						Nests.getRewardForId(player.getInventory()
								.getItem(slot).getId()), 1);
				player.getInventory().replaceItem(5075, 1, slot);
			}
		});
	}
}
