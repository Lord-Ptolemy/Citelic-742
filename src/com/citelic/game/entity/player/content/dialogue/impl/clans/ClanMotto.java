package com.citelic.game.entity.player.content.dialogue.impl.clans;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class ClanMotto extends Dialogue {

	@Override
	public void start() {
		player.getInterfaceManager().sendChatBoxInterface(1103);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();

	}

	@Override
	public void finish() {

	}

}
