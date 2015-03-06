package com.citelic.game.entity.npc.impl.godwars.bandos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.GodWarsBosses;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class GeneralGraardor extends NPC {

	public GeneralGraardor(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setIntelligentRouteFinder(true);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = Engine.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = Engine.getPlayers().get(npcIndex);
					if (player == null
							|| player.isDead()
							|| player.hasFinished()
							|| !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| (!isAtMultiArea() || !player.isAtMultiArea())
							&& player.getAttackedBy() != this
							&& player.getAttackedByDelay() > System
									.currentTimeMillis()
							|| !this.clipedProjectile(player, false)) {
						continue;
					}
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					GeneralGraardor.this.setNextAnimation(new Animation(defs
							.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					GeneralGraardor.this.drop();
					GeneralGraardor.this.reset();
					GeneralGraardor.this.setLocation(GeneralGraardor.this
							.getRespawnTile());
					GeneralGraardor.this.finish();
					GeneralGraardor.this.setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			this.reset();
			this.setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				GeneralGraardor.this.setFinished(false);
				Engine.addNPC(npc);
				npc.setLastRegionId(0);
				Engine.updateEntityRegion(npc);
				GeneralGraardor.this.loadMapRegions();
				GeneralGraardor.this.checkMultiArea();
				GodWarsBosses.respawnBandosMinions();
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}
}
