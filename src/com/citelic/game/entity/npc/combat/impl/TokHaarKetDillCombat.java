package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class TokHaarKetDillCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utilities.random(6) == 0) {
			delayHit(npc, 0, target,
					getRegularHit(npc, Utilities.random(defs.getMaxHit() + 1)));
			target.setNextGraphics(new Graphics(2999));
			if (target instanceof Player) {
				Player playerTarget = (Player) target;
				playerTarget.getPackets().sendGameMessage(
						"The TokHaar-Ket-Dill slams it's tail to the ground.");
			}
		} else {
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									defs.getAttackStyle(), target)));
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "TokHaar-Ket-Dill" };
	}
}
