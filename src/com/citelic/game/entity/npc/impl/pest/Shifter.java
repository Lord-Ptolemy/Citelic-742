package com.citelic.game.entity.npc.impl.pest;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.PestControl;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class Shifter extends PestMonsters {

	public Shifter(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned,
				index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = this.getPossibleTargets().get(0);
		if (this.getCombat().process() && !this.withinDistance(target, 10)
				|| Utilities.random(15) == 0)
			teleportSpinner(target);
	}

	private void teleportSpinner(Tile tile) { // def 3902, death 3903
		setNextTile(tile);
		setNextAnimation(new Animation(3904));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				setNextGraphics(new Graphics(654));// 1502
			}
		});
	}
}
