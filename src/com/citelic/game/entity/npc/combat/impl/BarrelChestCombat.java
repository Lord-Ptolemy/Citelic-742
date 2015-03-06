package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class BarrelChestCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		int damage;
		switch (1) {
		case 1: // Melee
			damage = Utilities.getRandom(450);
			npc.setNextAnimation(new Animation(5895));
			if (target instanceof Player) {
				player.prayer.drainPrayer(Utilities.getRandom(650));
			}
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
			break;
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Barrelchest" };
	}
}