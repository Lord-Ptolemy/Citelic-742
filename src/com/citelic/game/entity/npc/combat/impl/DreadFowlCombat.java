package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.utility.Utilities;

public class DreadFowlCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7810));
			npc.setNextGraphics(new Graphics(1318));
			delayHit(
					npc,
					1,
					target,
					getMagicHit(
							npc,
							getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE,
									target)));
			Engine.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
		} else {
			if (Utilities.getRandom(10) == 0) {// 1/10 chance of random special
				// (weaker)
				npc.setNextAnimation(new Animation(7810));
				npc.setNextGraphics(new Graphics(1318));
				delayHit(
						npc,
						1,
						target,
						getMagicHit(
								npc,
								getRandomMaxHit(npc, 30,
										NPCCombatDefinitions.MAGE, target)));
				Engine.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
			} else {
				npc.setNextAnimation(new Animation(7810));
				delayHit(
						npc,
						1,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, 30,
										NPCCombatDefinitions.MELEE, target)));
			}
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}
}
