package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.utility.Utilities;

public class OrkLegionCombat extends CombatScript {

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!",
			"All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(cdef.getAttackEmote()));
		if (Utilities.getRandom(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utilities
					.getRandom(messages.length > 3 ? 3 : 0)]));
		delayHit(npc, 0, target, getMeleeHit(npc, cdef.getMaxHit()));
		return cdef.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

}
