package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;

public class Rex extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(2853));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(
						npc,
						getRandomMaxHit(npc, 400, NPCCombatDefinitions.MELEE,
								target)));
		return 4;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 2883 };
	}
}