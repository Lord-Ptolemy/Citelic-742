package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.utility.Utilities;

public class KarilCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(),
				NPCCombatDefinitions.RANGE, target);
		if (damage != 0 && target instanceof Player && Utilities.random(3) == 0) {
			target.setNextGraphics(new Graphics(401, 0, 100));
			Player targetPlayer = (Player) target;
			int drain = (int) (targetPlayer.getSkills().getLevelForXp(
					Skills.AGILITY) * 0.2);
			int currentLevel = targetPlayer.getSkills()
					.getLevel(Skills.AGILITY);
			targetPlayer.getSkills().set(Skills.AGILITY,
					currentLevel < drain ? 0 : currentLevel - drain);
		}
		Engine.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16,
				41, 35, 16, 0);
		delayHit(npc, 2, target, getRangeHit(npc, damage));
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 2028 };
	}
}
