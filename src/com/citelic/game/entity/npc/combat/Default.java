package com.citelic.game.entity.npc.combat;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;

public class Default extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = defs.getAttackStyle();
		if (attackStyle == NPCCombatDefinitions.MELEE) {
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(), attackStyle,
									target)));
		} else {
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), attackStyle,
					target);
			delayHit(
					npc,
					2,
					target,
					attackStyle == NPCCombatDefinitions.RANGE ? getRangeHit(
							npc, damage) : getMagicHit(npc, damage));
			if (defs.getAttackProjectile() != -1)
				Engine.sendProjectile(npc, target, defs.getAttackProjectile(),
						41, 16, 41, 35, 16, 0);
		}
		if (defs.getAttackGfx() != -1)
			npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}
}
