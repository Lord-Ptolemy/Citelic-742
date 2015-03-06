package com.citelic.game.entity.player.content;

import com.citelic.game.entity.player.Player;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class DangerMessageObjects {

	public static boolean handleObject(GameObject object, Player player) {
		switch(object.getId()) {
		case 33173: //Cave
			player.getInterfaceManager().sendInterface(574);
			return true;
		case 33174: //Icy cavern
			player.setNextTile(new Tile(3056, 9562, 0));
			return true;
		}
		return false;
	}
	
	public static boolean handleDangerButtons(Player player, int interfaceId, int componentId) {
		switch(interfaceId) {
			case 574: //Icy cavern
				switch(componentId) {
				case 13:
					player.setNextTile(new Tile(3056, 9555, 0));
					return true;
				case 14:
					player.closeInterfaces();
					return true;
				}
			break;
		}
		return false;
	}
	
}
