package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;

public class FakeNomadCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(12697));
		boolean hit = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MAGE,
				target) != 0;
		delayHit(npc, 2, target, getRegularHit(npc, hit ? 50 : 0));
		Engine.sendProjectile(npc, target, 1657, 30, 30, 75, 25, 0, 0);
		if (hit) {
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					target.setNextGraphics(new Graphics(2278, 0, 100));
				}
			}, 1);
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

}
