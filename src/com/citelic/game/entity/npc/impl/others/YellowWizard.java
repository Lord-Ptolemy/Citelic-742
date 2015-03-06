package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.RunespanController;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class YellowWizard extends NPC {

	public static void giveReward(Player player) {

	}

	private RunespanController controler;
	private long spawnTime;

	public YellowWizard(Tile tile, RunespanController controler) {
		super(15430, tile, -1, true, true);
		spawnTime = Utilities.currentTimeMillis();
		this.controler = controler;
	}

	@Override
	public void finish() {
		controler.removeWizard();
		super.finish();
	}

	@Override
	public void processNPC() {
		if (spawnTime + 300000 < Utilities.currentTimeMillis())
			finish();
	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == controler.getPlayer()
				&& super.withinDistance(tile, distance);
	}

}
