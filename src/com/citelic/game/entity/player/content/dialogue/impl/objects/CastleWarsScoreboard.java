package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.CastleWars;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class CastleWarsScoreboard extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();

	}

	@Override
	public void start() {
		CastleWars.viewScoreBoard(player);

	}

}
