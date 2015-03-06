package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class FireSpirit extends NPC {

	private Player target;
	private long createTime;

	public FireSpirit(Tile tile, Player target) {
		super(15451, tile, -1, true, true);
		this.target = target;
		createTime = Utilities.currentTimeMillis();
	}

	public void giveReward(final Player player) {
		if (player != target || player.isLocked())
			return;
		player.lock();
		player.setNextAnimation(new Animation(16705));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.unlock();
				player.getInventory().addItem(
						new Item(12158, Utilities.random(1, 6)));
				player.getInventory().addItem(
						new Item(12159, Utilities.random(1, 6)));
				player.getInventory().addItem(
						new Item(12160, Utilities.random(1, 6)));
				player.getInventory().addItem(
						new Item(12163, Utilities.random(1, 6)));
				player.getPackets()
						.sendGameMessage(
								"The fire spirit gives you a reward to say thank you for freeing it, before disappearing.");
				finish();

			}

		}, 2);
	}

	@Override
	public void processNPC() {
		if (target.hasFinished()
				|| createTime + 60000 < Utilities.currentTimeMillis())
			finish();
	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == target && super.withinDistance(tile, distance);
	}

}
