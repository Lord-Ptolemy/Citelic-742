package com.citelic.game.entity.npc.impl.godwars;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class GodWarMinion extends NPC {

	int ticks = 10;

	public GodWarMinion(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getCombat().underCombat()) {
			if (ticks-- == 0) {
				ticks = 10;
				getCombat().removeTarget();
			}
		}
	}

	public void respawn() {
		setFinished(false);
		Engine.addNPC(this);
		setLastRegionId(0);
		Engine.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
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
					GodWarMinion.this.setNextAnimation(new Animation(defs
							.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					GodWarMinion.this.drop();
					GodWarMinion.this.reset();
					GodWarMinion.this.setLocation(GodWarMinion.this
							.getRespawnTile());
					GodWarMinion.this.finish();
					GodWarMinion.this.setRespawnTask();
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
	}
}