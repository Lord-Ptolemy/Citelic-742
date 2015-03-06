package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;

public class Supreme extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(2855));
		for (Entity t : npc.getPossibleTargets()) {
			delayHit(
					npc,
					1,
					t,
					getRangeHit(
							npc,
							getRandomMaxHit(npc, 400,
									NPCCombatDefinitions.RANGE, target)));
			Engine.sendProjectile(npc, t, 475, 41, 16, 60, 30, 16, 0);
		}
		return 4;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 2881 };
	}
}
