package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.entity.player.Player;

public class PlayerTransformAction extends CutsceneAction {

	private int npcId;

	public PlayerTransformAction(int npcId, int actionDelay) {
		super(-1, actionDelay);
		this.npcId = npcId;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.getPlayerAppearance().transformIntoNPC(npcId);
	}

}
