package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class BorkCombat extends CombatScript {

	public boolean spawnOrk = false;

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		if (npc.getHitpoints() <= (cdef.getHitpoints() * 0.4) && !spawnOrk) {
			if (target instanceof Player) {
				Player player = (Player) target;
				npc.setNextForceTalk(new ForceTalk("Come to my aid, brothers!"));
				player.getControllerManager().startController("BorkController",
						1, npc);
				spawnOrk = true;
			}
		}
		npc.setNextAnimation(new Animation(Utilities.getRandom(1) == 0 ? cdef
				.getAttackEmote() : 8757));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getRandomMaxHit(npc, cdef.getMaxHit(), -1, target)));
		return cdef.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bork" };
	}

}
