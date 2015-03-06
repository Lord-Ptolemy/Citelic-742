package com.citelic.game.entity.npc.impl.soulwars;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.GameTask;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.PlayerType;
import com.citelic.game.map.tile.Tile;

public class Barricade extends NPC {

	private static final long serialVersionUID = 4304917643056790264L;

	private int team;

	public Barricade(Tile tile, int team) {
		super(1532, tile, -1, true, true);
		team = this.team;
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
	}

	public void litFire() {
		transformIntoNPC(1533);
		sendDeath(this);
	}

	public void explode() {
		sendDeath(this);
	}

	@Override
	public void sendDeath(Entity killer) {
		resetWalkSteps();
		getCombat().removeTarget();
		if (this.getId() != 1533) {
			setNextAnimation(null);
			reset();
			setLocation(getRespawnTile());
			finish();
		} else {
			super.sendDeath(killer);
		}
		((GameTask) Engine.soulWars.getTasks().get(PlayerType.IN_GAME))
				.removeBarricade(this, team);
	}
}