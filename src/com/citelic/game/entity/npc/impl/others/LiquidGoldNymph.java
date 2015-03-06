package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class LiquidGoldNymph extends NPC {

	private Player target;
	private long createTime;

	public LiquidGoldNymph(Tile tile, Player target) {
		super(14, tile, -1, true, true);
		this.target = target;
		createTime = Utilities.currentTimeMillis();
	}

	public void giveReward(final Player player) {
		if (player != target || player.isLocked())
			return;
		player.lock();
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.unlock();
				player.getInventory().addItem(new Item(20789, 1));
				player.getInventory().addItem(new Item(20791, 1));
				player.getInventory().addItem(new Item(20790, 1));
				player.getInventory().addItem(new Item(20788, 1));
				player.getInventory().addItem(new Item(20787, 1));
				finish();
			}
		}, 2);
	}

	@Override
	public void processNPC() {
		if (target.hasFinished()
				|| createTime + 300000 < Utilities.currentTimeMillis())
			finish();
	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == target && super.withinDistance(tile, distance);
	}

}