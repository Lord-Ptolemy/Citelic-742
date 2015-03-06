package com.citelic.game.entity.npc.impl.pest;

import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.PestControl;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class Ravager extends PestMonsters {

	boolean destroyingObject = false;

	public Ravager(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, -1, false, false, index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();

	}
}
