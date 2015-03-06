package com.citelic.game.entity.npc.impl.fightkiln;

import java.util.ArrayList;
import java.util.List;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class HarAkenTentacle extends NPC {

	private HarAken aken;

	public HarAkenTentacle(int id, Tile tile, HarAken aken) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setCantFollowUnderCombat(true);
		setNextAnimation(new Animation(id == 15209 ? 16238 : 16241));
		this.aken = aken;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = Engine.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = Engine.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished()
						|| !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public void sendDeath(Entity source) {
		aken.removeTentacle(this);
		super.sendDeath(source);
	}
}
