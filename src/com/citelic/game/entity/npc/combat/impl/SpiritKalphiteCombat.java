package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.familiar.Familiar;

public class SpiritKalphiteCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// TODO find special
			npc.setNextAnimation(new Animation(8519));
			npc.setNextGraphics(new Graphics(8519));
			damage = getRandomMaxHit(npc, 20, NPCCombatDefinitions.MELEE,
					target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(8519));
			damage = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MELEE,
					target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 6995, 6994 };
	}
}
