package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class RockCrabs extends NPC {

	private int realId;

	public RockCrabs(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		realId = id;
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId()) {
			setNextNPCTransformation(realId - 1);
			setHitpoints(getMaxHitpoints()); // rock/bulders have no hp
		}
		super.setTarget(entity);
	}

}