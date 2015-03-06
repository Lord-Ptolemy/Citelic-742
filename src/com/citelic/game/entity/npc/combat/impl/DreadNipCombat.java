package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.others.DreadNip;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class DreadNipCombat extends CombatScript {

	private String[] DREADNIP_ATTACK_MESSAGE = {
			"Your dreadnip stunned its target!",
			"Your dreadnip poisened its target!" };

	@Override
	public int attack(NPC npc, Entity target) {
		DreadNip dreadNip = (DreadNip) npc;
		if (dreadNip.getTicks() <= 3)
			return 0;
		npc.setNextAnimation(new Animation(-1));
		int attackStyle = Utilities.random(2);
		switch (attackStyle) {
		case 0:
			break;
		case 1:
			int secondsDelay = 5 + Utilities.getRandom(3);
			target.setFreezeDelay(secondsDelay);
			if (target instanceof Player) {
				Player player = (Player) target;
				player.getActionManager().addActionDelay(secondsDelay);
			} else {
				NPC npcTarget = (NPC) target;
				npcTarget.getCombat().setCombatDelay(
						npcTarget.getCombat().getCombatDelay() + secondsDelay);
			}
			break;
		case 2:
			target.getPoison().makePoisoned(108);
			break;
		}
		if (attackStyle != 0)
			dreadNip.getOwner().getPackets()
					.sendGameMessage(DREADNIP_ATTACK_MESSAGE[attackStyle - 1]);
		delayHit(
				npc,
				0,
				target,
				new Hit(npc, getRandomMaxHit(npc, 550,
						NPCCombatDefinitions.MELEE, target),
						HitLook.REGULAR_DAMAGE));
		return 5;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 14416 };
	}
}
