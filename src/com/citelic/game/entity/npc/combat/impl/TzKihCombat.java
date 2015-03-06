package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.player.Player;

public class TzKihCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {// yoa
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		if (npc instanceof Familiar) {// TODO get anim and gfx
			Familiar familiar = (Familiar) npc;
			boolean usingSpecial = familiar.hasSpecialOn();
			if (usingSpecial) {
				for (Entity entity : npc.getPossibleTargets()) {
					damage = getRandomMaxHit(npc, 70,
							NPCCombatDefinitions.MELEE, target);
					if (target instanceof Player)
						((Player) target).getPrayer().drainPrayer(damage);
					delayHit(npc, 0, entity, getMeleeHit(npc, damage));
				}
			}
			return defs.getAttackDelay();
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		damage = getRandomMaxHit(npc, defs.getMaxHit(),
				NPCCombatDefinitions.MELEE, target);
		if (target instanceof Player)
			((Player) target).getPrayer().drainPrayer(damage + 10);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih", 7361, 7362 };
	}
}
