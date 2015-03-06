package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class Kreearra extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (!npc.isUnderCombat()) {
			npc.setNextAnimation(new Animation(17396));
			delayHit(
					npc,
					1,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 260,
									NPCCombatDefinitions.MELEE, target)));
			return defs.getAttackDelay();
		}
		npc.setNextAnimation(new Animation(17397));
		for (Entity t : npc.getPossibleTargets()) {
			if (Utilities.getRandom(2) == 0)
				sendMagicAttack(npc, t);
			else {
				delayHit(
						npc,
						1,
						t,
						getRangeHit(
								npc,
								getRandomMaxHit(npc, 720,
										NPCCombatDefinitions.RANGE, t)));
				Engine.sendProjectile(npc, t, 1197, 41, 16, 41, 35, 16, 0);
				Tile teleTile = t;
				for (int trycount = 0; trycount < 10; trycount++) {
					teleTile = new Tile(t, 2);
					if (Engine.canMoveNPC(t.getZ(), teleTile.getX(),
							teleTile.getY(), t.getSize()))
						break;
				}
				t.setNextTile(teleTile);
			}
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 6222 };
	}

	private void sendMagicAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(17397));
		for (Entity t : npc.getPossibleTargets()) {
			delayHit(
					npc,
					1,
					t,
					getMagicHit(
							npc,
							getRandomMaxHit(npc, 210,
									NPCCombatDefinitions.MAGE, t)));
			Engine.sendProjectile(npc, t, 1198, 41, 16, 41, 35, 16, 0);
			target.setNextGraphics(new Graphics(1196));
		}
	}
}
