package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class TheInadequacyCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(6325));
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				// World.sendProjectile(npc, target, 1067, 18, 18, 50, 30, 0,
				// 0);
				Engine.sendProjectile(target, npc, 1067, 34, 16, 30, 35, 16, 0);
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						// target.setNextGraphics(new Graphics(1068, 0, 100));
						delayHit(npc, 0, target,
								getMagicHit(npc, Utilities.random(350)));
					}
				}, 1);
			}
		}, 1);

		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {

		return new Object[] { 5902 };
	}

}
