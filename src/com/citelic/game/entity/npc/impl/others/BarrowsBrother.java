package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.Barrows;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class BarrowsBrother extends NPC {

	private Barrows barrows;

	public BarrowsBrother(int id, Tile tile, Barrows barrows) {
		super(id, tile, -1, true, true);
		this.barrows = barrows;
	}

	public void disapear() {
		barrows = null;
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (barrows != null) {
			barrows.targetFinishedWithoutDie();
			barrows = null;
		}
		super.finish();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return getId() != 2030 ? 0 : Utilities.random(3) == 0 ? 1 : 0;
	}

	@Override
	public void sendDeath(Entity source) {
		if (barrows != null) {
			barrows.targetDied();
			barrows = null;
		}
		super.sendDeath(source);
	}

}
