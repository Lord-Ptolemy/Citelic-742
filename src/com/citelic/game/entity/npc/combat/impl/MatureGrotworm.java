package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class MatureGrotworm extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size
				|| distanceY < -1) {
			mageAttack(npc, target, hit);
			return defs.getAttackDelay();
		}
		int attackStyle = Utilities.getRandom(1);
		switch (attackStyle) {
		case 0:
			hit = getRandomMaxHit(npc, Utilities.random(300),
					NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMagicHit(npc, Utilities.random(200)));
			break;
		case 1:
			mageAttack(npc, target, hit);
			break;
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mature grotworm", 15463 };
	}

	private void mageAttack(final NPC npc, final Entity target, int hit) {
		hit = getRandomMaxHit(npc, Utilities.random(300),
				NPCCombatDefinitions.MAGE, target);
		npc.setNextAnimation(new Animation(16789));
		npc.setNextGraphics(new Graphics(3163));
		Engine.sendProjectile(npc, target, 3164, 34, 16, 35, 35, 16, 0);
		delayHit(npc, 2, target, getMagicHit(npc, Utilities.random(200)));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(3165));
				stop();
			}
		}, 2);
	}
}