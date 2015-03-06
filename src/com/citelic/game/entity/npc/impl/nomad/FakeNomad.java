package com.citelic.game.entity.npc.impl.nomad;

import com.citelic.game.entity.Hit;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class FakeNomad extends NPC {

	private Nomad nomad;

	public FakeNomad(Tile tile, Nomad nomad) {
		super(8529, tile, -1, true, true);
		this.nomad = nomad;
		setForceMultiArea(true);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
