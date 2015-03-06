package com.citelic.game.entity.player.content.dialogue.impl.skills.construction;

import com.citelic.game.entity.player.content.actions.skills.construction.House.RoomReference;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.Builds;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.Room;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class CreateRoomStairsD extends Dialogue {

	private RoomReference room;
	private boolean up;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 0;
				if (room.getPlane() == 1 && !up) {
					sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
							"Skill hall", "Quest hall", "Dungeon stairs room");
				} else {
					sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
							"Skill hall", "Quest hall");
				}
				return;
			}
		} else {
			Room r = room.getPlane() == 1 && !up
					&& componentId == Dialogue.OPTION_3 ? Room.DUNGEON_STAIRS
					: componentId == Dialogue.OPTION_2 ? up ? Room.HALL_QUEST_DOWN
							: Room.HALL_QUEST
							: up ? Room.HALL_SKILL_DOWN : Room.HALL_SKILL;
			Builds stair = room.getPlane() == 1 && !up
					&& componentId == Dialogue.OPTION_3 ? Builds.STAIRCASE_2
					: componentId == Dialogue.OPTION_2 ? up ? Builds.STAIRCASE_DOWN_1
							: Builds.STAIRCASE_1
							: up ? Builds.STAIRCASE_DOWN : Builds.STAIRCASE;
			RoomReference newRoom = new RoomReference(r, room.getX(),
					room.getY(), room.getPlane() + (up ? 1 : -1),
					room.getRotation());
			int slot = room.getStaircaseSlot();
			System.out.println(slot);
			if (slot != -1) {
				newRoom.addObject(stair, slot);
				player.getHouse().createRoom(newRoom);
			}
		}
		end();

	}

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		up = (boolean) parameters[1];
		sendOptionsDialogue(
				"These stairs do not lead anywhere. Do you want to build a room at the "
						+ (up ? "top" : "bottom") + "?", "Yes.", "No.");

	}

}
