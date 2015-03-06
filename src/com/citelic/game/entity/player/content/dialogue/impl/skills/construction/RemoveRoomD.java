package com.citelic.game.entity.player.content.dialogue.impl.skills.construction;

import com.citelic.game.entity.player.content.actions.skills.construction.House.RoomReference;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.Utilities;

public class RemoveRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == Dialogue.OPTION_1) {
			player.getHouse().removeRoom(room);
		}
		end();
	}

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		sendOptionsDialogue(
				"Remove the "
						+ Utilities.formatPlayerNameForDisplay(room.getRoom()
								.toString()) + "?", "Yes.", "No.");
	}

}
