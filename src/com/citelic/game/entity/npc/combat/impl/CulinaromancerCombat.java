package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class CulinaromancerCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage;
		switch (1) {
		case 1: // Magic
			damage = Utilities.getRandom(400);
			npc.setNextAnimation(new Animation(811));
			npc.setNextGraphics(new Graphics(2728));
			Engine.sendProjectile(npc, target, 2735, 18, 18, 50, 50, 3, 0);
			Engine.sendProjectile(npc, target, 2736, 18, 18, 50, 50, 20, 0);
			Engine.sendProjectile(npc, target, 2736, 18, 18, 50, 50, 110, 0);
			delayHit(npc, 1, target, getMagicHit(npc, damage));
			break;
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Culinaromancer" };
	}

}
