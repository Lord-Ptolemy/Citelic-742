package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.others.TormentedDemon;
import com.citelic.utility.Utilities;

public class TormentedDemonCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		TormentedDemon torm = (TormentedDemon) npc;
		int hit = 0;
		int attackStyle = torm.getFixedAmount() == 0 ? Utilities.getRandom(2)
				: torm.getFixedCombatType();
		if (torm.getFixedAmount() == 0)
			torm.setFixedCombatType(attackStyle);
		switch (attackStyle) {
		case 0:
			if (npc.withinDistance(target, 3)) {
				hit = getRandomMaxHit(npc, 189, NPCCombatDefinitions.MELEE,
						target);
				npc.setNextAnimation(new Animation(10922));
				npc.setNextGraphics(new Graphics(1886));
				delayHit(npc, 1, target, getMeleeHit(npc, hit));
			}
			return defs.getAttackDelay();
		case 1:
			hit = getRandomMaxHit(npc, 270, NPCCombatDefinitions.MAGE, target);
			npc.setNextAnimation(new Animation(10918));
			npc.setNextGraphics(new Graphics(1883, 0, 96 << 16));
			Engine.sendProjectile(npc, target, 1884, 34, 16, 30, 35, 16, 0);
			delayHit(npc, 1, target, getMagicHit(npc, hit));
			break;
		case 2:
			hit = getRandomMaxHit(npc, 270, NPCCombatDefinitions.RANGE, target);
			npc.setNextAnimation(new Animation(10919));
			npc.setNextGraphics(new Graphics(1888));
			Engine.sendProjectile(npc, target, 1887, 34, 16, 30, 35, 16, 0);
			delayHit(npc, 1, target, getRangeHit(npc, hit));
			break;
		}
		torm.setFixedAmount(torm.getFixedAmount() + 1);
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tormented demon" };
	}
}
