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

public class KetZekCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size
				|| distanceY < -1) {
			commenceMagicAttack(npc, target, hit);
			return defs.getAttackDelay();
		}
		int attackStyle = Utilities.getRandom(1);
		switch (attackStyle) {
		case 0:
			hit = getRandomMaxHit(npc, defs.getMaxHit(),
					NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, hit));
			break;
		case 1:
			commenceMagicAttack(npc, target, hit);
			break;
		}
		return defs.getAttackDelay();
	}

	private void commenceMagicAttack(final NPC npc, final Entity target, int hit) {
		hit = getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit() - 50,
				NPCCombatDefinitions.MAGE, target);
		npc.setNextAnimation(new Animation(16136));
		// npc.setNextGraphics(new Graphics(1622, 0, 96 << 16));
		Engine.sendProjectile(npc, target, 2984, 34, 16, 30, 35, 16, 0);
		delayHit(npc, 2, target, getMagicHit(npc, hit));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(2983, 0, 96 << 16));
			}
		}, 2);
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}// anims: DeathEmote: 9257 DefEmote: 9253 AttackAnim: 9252 gfxs: healing:
		// 444 - healer
}
