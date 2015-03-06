package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class KaramelCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		int damage;
		switch (1) {
		case 1: // Magic
			damage = Utilities.getRandom(250);
			npc.setNextAnimation(new Animation(1979));
			Engine.sendProjectile(npc, target, 368, 60, 32, 50, 50, 0, 0);
			target.setNextGraphics(new Graphics(369));
			delayHit(npc, 1, target, getMagicHit(npc, damage));
			if (Utilities.random(10) == 0) {
				if (target instanceof Player) {
					player.getSkills().drainLevel(0,
							player.getSkills().getLevel(0) / 15);
					player.getSkills().drainLevel(1,
							player.getSkills().getLevel(1) / 15);
					player.getSkills().drainLevel(2,
							player.getSkills().getLevel(2) / 15);
					player.getSkills().drainLevel(4,
							player.getSkills().getLevel(2) / 15);
					player.getSkills().drainLevel(6,
							player.getSkills().getLevel(2) / 15);
					player.getPackets().sendGameMessage(
							"<col=CC0033>Karamel drained your combat skills!");
				}
			}
			break;
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Karamel" };
	}

}
