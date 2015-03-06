package com.citelic.game.entity.npc.impl.qbd;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

/**
 * Handles the Queen Black Dragon's change armour "attack".
 * 
 * @author Emperor
 * 
 */
public final class ChangeArmour implements QueenAttack {

	@Override
	public int attack(final QueenBlackDragon npc, Player victim) {
		npc.switchState(Utilities.random(2) < 1 ? QueenState.CRYSTAL_ARMOUR
				: QueenState.HARDEN);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				npc.switchState(QueenState.DEFAULT);
			}
		}, 40);
		npc.getTemporaryAttributtes().put("_last_armour_change",
				npc.getTicks() + Utilities.random(41, 100));
		return Utilities.random(4, 10);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		Integer last = (Integer) npc.getTemporaryAttributtes().get(
				"_last_armour_change");
		return last == null || last < npc.getTicks();
	}

}