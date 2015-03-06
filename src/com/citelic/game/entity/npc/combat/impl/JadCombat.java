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

public class JadCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utilities.random(3);
		if (attackStyle == 2) { // melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size
					|| distanceY < -1)
				attackStyle = Utilities.random(2); // set mage
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(
						npc,
						1,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.MELEE, target)));
				return defs.getAttackDelay();
			}
		}
		if (attackStyle == 1) { // range
			npc.setNextAnimation(new Animation(16202));
			npc.setNextGraphics(new Graphics(2994));
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					target.setNextGraphics(new Graphics(3000));
					delayHit(
							npc,
							1,
							target,
							getRangeHit(
									npc,
									getRandomMaxHit(npc, defs.getMaxHit() - 2,
											NPCCombatDefinitions.RANGE, target)));
				}
			}, 3);
		} else {
			npc.setNextAnimation(new Animation(16195));
			npc.setNextGraphics(new Graphics(2995));
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					Engine.sendProjectile(npc, target, 2996, 80, 30, 40, 20, 5,
							0);
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							target.setNextGraphics(new Graphics(2741, 0, 100));
							delayHit(
									npc,
									0,
									target,
									getMagicHit(
											npc,
											getRandomMaxHit(npc,
													defs.getMaxHit() - 2,
													NPCCombatDefinitions.MAGE,
													target)));
						}

					}, 1);
				}
			}, 2);
		}

		return defs.getAttackDelay() + 2;
	}

	@Override
	public Object[] getKeys() {

		return new Object[] { 2745, 15208 };
	}

}
