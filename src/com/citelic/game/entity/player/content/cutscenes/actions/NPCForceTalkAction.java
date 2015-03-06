package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;

public class NPCForceTalkAction extends CutsceneAction {

	private String text;

	public NPCForceTalkAction(int cachedObjectIndex, String text,
			int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextForceTalk(new ForceTalk(text));
	}

}
