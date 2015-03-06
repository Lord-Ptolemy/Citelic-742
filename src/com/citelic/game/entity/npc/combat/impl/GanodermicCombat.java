package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class GanodermicCombat extends CombatScript {

	@Override
	public int attack(NPC n, Entity target) {
		NPCCombatDefinitions defs = n.getCombatDefinitions();
		n.setNextAnimation(new Animation(15470));
		n.setNextGraphics(new Graphics(2034));
		Engine.sendProjectile(n, target, 2034, 10, 18, 50, 50, 0, 0);
		delayHit(n, 1, target, getMagicHit(n, Utilities.random(400)));
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 14696, 14697 };
	}
}