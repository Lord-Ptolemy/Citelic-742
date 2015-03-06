package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;

public class PortalCombat extends CombatScript {

	/*
	 * empty
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		return 0;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 6146, 6142, 6147, 6143, 6148, 6144, 6149, 6145,
				3782, 3784, 3785 };
	}

}
