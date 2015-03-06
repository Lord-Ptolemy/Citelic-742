package com.citelic.game.entity.player.managers;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Logger;

public class LoyaltyManager {

	private transient Player player;

	public int countDown = 1800;

	public LoyaltyManager(Player player) {
		this.player = player;
	}

	public void process() {
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				try {
					if (player.hasFinished()) {
						stop(); // Less stress for the engine, useful if you got
						// alot players :)
					}
					if (countDown == 1) {
						if (player.isVipRank()) {
							player.setLoyaltyPoints(player.getLoyaltyPoints() + 1);
						} else {
							player.setLoyaltyPoints(player.getLoyaltyPoints() + 1);
						}
						countDown = 1800;
					}
					if (countDown > 0) {
						countDown--;
					}
					// System.out.println(countDown);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1);
	}
}