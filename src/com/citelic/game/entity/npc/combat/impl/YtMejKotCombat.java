package com.citelic.game.entity.npc.combat.impl;

import java.util.List;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;

public class YtMejKotCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(
						npc,
						getRandomMaxHit(npc, defs.getMaxHit(),
								defs.getAttackStyle(), target)));
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2) {
			if (npc.getTemporaryAttributtes().remove("Heal") != null) {
				npc.setNextGraphics(new Graphics(2980, 0, 100));
				List<Integer> npcIndexes = Engine.getRegion(npc.getRegionId())
						.getNPCsIndexes();
				if (npcIndexes != null) {
					for (int npcIndex : npcIndexes) {
						NPC n = Engine.getNPCs().get(npcIndex);
						if (n == null || n.isDead() || n.hasFinished())
							continue;
						n.heal(100);
					}
				}
			} else
				npc.getTemporaryAttributtes().put("Heal", Boolean.TRUE);
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Yt-MejKot" };
	}
}
