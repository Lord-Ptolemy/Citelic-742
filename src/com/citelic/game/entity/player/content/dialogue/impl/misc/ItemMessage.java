package com.citelic.game.entity.player.content.dialogue.impl.misc;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class ItemMessage extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void start() {
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { "",
				(String) parameters[0] }, IS_ITEM, (Integer) parameters[1], 1);
	}

}
