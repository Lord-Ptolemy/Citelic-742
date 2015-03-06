package com.citelic.game.entity.npc.impl.dragons;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class KingBlackDragon extends NPC {

	public static boolean atKBD(Tile tile) {
		if ((tile.getX() >= 2250 && tile.getX() <= 2292)
				&& (tile.getY() >= 4675 && tile.getY() <= 4710))
			return true;
		return false;
	}

	public KingBlackDragon(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
	}

}
