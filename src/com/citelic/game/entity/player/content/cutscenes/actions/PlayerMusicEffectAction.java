package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.entity.player.Player;

public class PlayerMusicEffectAction extends CutsceneAction {

	private int id;

	public PlayerMusicEffectAction(int id, int actionDelay) {
		super(-1, actionDelay);
		this.id = id;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.getPackets().sendMusicEffect(id);
	}

}
