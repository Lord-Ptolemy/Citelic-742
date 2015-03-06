package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class DarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2783 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(2731));
		if (Utilities.isOnRange(target.getX(), target.getY(), npc.getX(),
				npc.getY(), npc.getSize(), 3)) {
			delayHit(npc, 0, target, getMeleeHit(npc, Utilities.random(400)));
		} else {
			Engine.sendProjectile(npc, target, 2181, 41, 16, 41, 35, 16, 0);
			delayHit(npc, 2, target, getMagicHit(npc, Utilities.random(250)));
		}
		return def.getAttackDelay();
	}
}
