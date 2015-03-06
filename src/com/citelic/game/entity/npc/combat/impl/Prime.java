package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;

public class Prime extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(2854));
		for (Entity t : npc.getPossibleTargets()) {
			delayHit(
					npc,
					1,
					t,
					getMagicHit(
							npc,
							getRandomMaxHit(npc, 800,
									NPCCombatDefinitions.MAGE, t)));
			Engine.sendProjectile(npc, t, 2707, 18, 18, 50, 50, 3, 0);
		}
		return 4;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 2882 };
	}
}