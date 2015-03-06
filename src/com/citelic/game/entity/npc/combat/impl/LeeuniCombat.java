package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.ForceTalk;
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

public class LeeuniCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13216 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2
				&& Utilities.random(5) == 0) {
			npc.heal(30);
		}
		npc.setCombatLevel(512);
		npc.setCapDamage(700);

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utilities.getRandom(2) == 0) { // magical attack
			npc.setNextAnimation(new Animation(15042));
			for (Entity t : npc.getPossibleTargets()) {
				delayHit(
						npc,
						1,
						t,
						getMagicHit(
								npc,
								getRandomMaxHit(npc, 540,
										NPCCombatDefinitions.MAGE, t)));
				Engine.sendProjectile(npc, t, 1002, 41, 16, 41, 35, 16, 0);
				target.setNextGraphics(new Graphics(3000));
			}
		} else if (Utilities.getRandom(2) == 1) {
			npc.setNextAnimation(new Animation(15046));
			npc.setNextForceTalk(new ForceTalk("You will never kill me!"));
						int skill = Utilities.getRandom(2);
						skill = skill == 0 ? Skills.PRAYER
								: (skill == 1 ? Skills.SUMMONING
										: Skills.PRAYER);
						Player player = (Player) target;
						if (skill == Skills.PRAYER)
							player.getPrayer().drainPrayer(990);
						else {
							int lvl = player.getSkills().getLevel(skill);
							lvl -= 1 + Utilities.getRandom(4);
							player.getSkills().set(skill, lvl < 0 ? 0 : lvl);
						}
						player.getPackets().sendGameMessage("Your " + Skills.SKILL_NAME[skill]
								+ " has been dropped!");
			
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 330,
									NPCCombatDefinitions.MELEE, target)));
		} else { // melee attack
			npc.setNextAnimation(new Animation(15046));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 440,
									NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay();
	}

}