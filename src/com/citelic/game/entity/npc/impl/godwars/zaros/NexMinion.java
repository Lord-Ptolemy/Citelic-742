package com.citelic.game.entity.npc.impl.godwars.zaros;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.ZarosGodwars;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class NexMinion extends NPC {

	private boolean hasNoBarrier;

	public NexMinion(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCantFollowUnderCombat(true);
		setCapDamage(0);
	}

	public void breakBarrier() {
		setCapDamage(-1);
		hasNoBarrier = true;
	}

	@Override
	public void processNPC() {
		if (isDead() || !hasNoBarrier)
			return;
		if (!getCombat().process()) {
			checkAgressivity();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		ZarosGodwars.moveNextStage();
	}

}
