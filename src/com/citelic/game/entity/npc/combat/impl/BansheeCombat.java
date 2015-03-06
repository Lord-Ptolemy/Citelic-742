package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer;
import com.citelic.utility.Utilities;

public class BansheeCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] {"Banshee", "Mighty banshee"};
    }
    
    
    
    
    @Override
    public int attack(NPC npc, Entity target) {
	NPCCombatDefinitions def = npc.getCombatDefinitions();
	if (!Slayer.hasEarmuffs(target)) {
	    	Player targetPlayer = (Player) target;
		if(!targetPlayer.getPrayer().isMeleeProtecting()) {
			int randomSkill = Utilities.random(0, 6);
			int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
			targetPlayer.getSkills().set(randomSkill, currentLevel < 5 ? 0 : currentLevel - 5);
			targetPlayer.getPackets().sendGameMessage("The screams of the banshee make you feel slightly weaker.");
			npc.setNextForceTalk(new ForceTalk("*EEEEHHHAHHH*"));
		}
		delayHit(npc, 0, target, getMeleeHit(npc, targetPlayer.getMaxHitpoints()/10));
		//TODO player emote hands on ears
	}else
	    delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
	npc.setNextAnimation(new Animation(def.getAttackEmote()));
	return def.getAttackDelay();
    }
}
