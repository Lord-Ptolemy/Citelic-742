package com.citelic.game.entity.player.content.dialogue.impl.skills.construction;

import com.citelic.game.entity.player.content.actions.skills.construction.House.RoomReference;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class CreateRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void finish() {
		player.getHouse().previewRoom(room, true);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == Dialogue.OPTION_4) {
			end();
			return;
		}
		if (componentId == Dialogue.OPTION_3) {
			end();
			player.getHouse().createRoom(room);
			return;
		}
		player.getHouse().previewRoom(room, true);
		room.setRotation(room.getRotation()
				+ (componentId == Dialogue.OPTION_1 ? 1 : -1) & 0x3);
		sendPreview();
	}

	public void sendPreview() {
		sendOptionsDialogue("Select an Option", "Rotate clockwise",
				"Rotate anticlockwise.", "Build.", "Cancel");
		player.getHouse().previewRoom(room, false);
	}

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		sendPreview();
	}

}
