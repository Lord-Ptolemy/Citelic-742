package com.citelic.game.entity.player.content.dialogue.impl.simplemessages;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SimpleItemMessage extends Dialogue {

	private int itemId;
	private String message;

	@Override
	public void start() {
		itemId = (Integer) parameters[0];
		message = (String) parameters[1];
		player.getInterfaceManager().sendChatBoxInterface(1189);
		player.getPackets().sendItemOnIComponent(1189, 1, itemId, 1);
		player.getPackets().sendIComponentText(1189, 4, message);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
