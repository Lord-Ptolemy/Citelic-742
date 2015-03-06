package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer;
import com.citelic.utility.Utilities;

public class DustDevil extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { 1624 };
    }

    @Override
    public int attack(NPC npc, Entity target) {
	NPCCombatDefinitions def = npc.getCombatDefinitions();
	if (!Slayer.hasMask(target)) {
	    Player targetPlayer = (Player) target;
	    int randomSkill = Utilities.random(0, 6);
	    int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
	    targetPlayer.getSkills().set(randomSkill, currentLevel < 5 ? 0 : currentLevel - Utilities.random(20));
	    targetPlayer.getPackets().sendGameMessage("The dust devil's smoke suffocates you.");
	    delayHit(npc, 1, target, getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 4));
	} else
	    delayHit(npc, 1, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
	npc.setNextAnimation(new Animation(def.getAttackEmote()));
	return def.getAttackDelay();
    }

}
