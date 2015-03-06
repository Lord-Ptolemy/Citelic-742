package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class MasterOfFear extends NPC {

	public MasterOfFear(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setName("Master of fear");
	}
}
