package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;

public class NPCAnimationAction extends CutsceneAction {

	private Animation anim;

	public NPCAnimationAction(int cachedObjectIndex, Animation anim,
			int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextAnimation(anim);
	}

}
