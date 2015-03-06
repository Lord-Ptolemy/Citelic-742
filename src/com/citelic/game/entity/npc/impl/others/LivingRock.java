package com.citelic.game.entity.npc.impl.others;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class LivingRock extends NPC {

	private Entity source;
	private long deathTime;

	public LivingRock(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(4);
	}

	public boolean canMine(Player player) {
		return Utilities.currentTimeMillis() - deathTime > 60000
				|| player == source;
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					transformIntoRemains(source);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setRandomWalk(true);
		finish();
		if (!isSpawned())
			setRespawnTask();
	}

	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utilities.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformIntoNPC(remainsId);
		setRandomWalk(false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (remainsId == getId())
						takeRemains();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 3, TimeUnit.MINUTES);

	}

}
