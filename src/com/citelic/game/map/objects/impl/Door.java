package com.citelic.game.map.objects.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.objects.GameObject;

public class Door {

	/**
	 * Parameters containing the close of a door with 2 parameter values.
	 *
	 * @param p
	 *            - represents the player using the door
	 * @param o
	 *            - represents an in-game object
	 * @return handleDoor - Closing doors after 60000 milliseconds
	 */
	public static boolean handleDoor(Player p, GameObject o) {
		return Door.handleDoor(p, o, 60000);
	}

	/**
	 * Parameters for handling the doors
	 *
	 * @param p
	 *            - represents the player using the door
	 * @param o
	 *            - represents an in-game object
	 * @param time
	 *            - represents the time in milliseconds
	 * @return - represents the values of the boolean
	 */

	public static boolean handleDoor(Player p, GameObject o, long time) {
		if (Engine.isSpawnedObject(o))
			return false;
		GameObject openDoor = new GameObject(o.getId(), o.getType(),
				o.getRotation() + 1, o.getX(), o.getY(), o.getZ());
		switch (o.getRotation()) {
		case 0:
			openDoor.moveLocation(-1, 0, 0);
			break;
		case 1:
			openDoor.moveLocation(0, 1, 0);
			break;
		case 2:
			openDoor.moveLocation(1, 0, 0);
			break;
		case 3:
			openDoor.moveLocation(0, -1, 0);
			break;
		default:
			return false;
		}
		if (Engine.removeTemporaryObject(o, time, true)) {
			p.faceObject(openDoor);
			Engine.spawnObjectTemporary(openDoor, time);
			return true;
		}
		return false;
	}
}
