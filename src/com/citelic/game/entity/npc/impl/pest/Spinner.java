package com.citelic.game.entity.npc.impl.pest;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.PestControl;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class Spinner extends PestMonsters {

	private byte healTicks;

	public Spinner(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned,
				index, manager);
	}

	private void explode() {
		final NPC npc = this;
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				for (Player player : manager.getPlayers()) {
					if (!withinDistance(player, 7))
						continue;
					player.getPoison().makePoisoned(50);
					player.applyHit(new Hit(npc, 50, HitLook.REGULAR_DAMAGE));
					npc.reset();
					npc.finish();
				}
			}
		}, 1);
	}

	private void healPortal(final PestPortal portal) {
		setNextFaceEntity(portal);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				setNextAnimation(new Animation(3911));
				setNextGraphics(new Graphics(658, 0, 96 << 16));
				if (portal.getHitpoints() != 0)
					portal.heal((portal.getMaxHitpoints() / portal
							.getHitpoints()) * 45);
				healTicks = 0; /* Saves memory in the long run. Meh */
			}
		});
	}

	@Override
	public void processNPC() {
		PestPortal portal = manager.getPortals()[portalIndex];
		if (portal.isDead()) {
			explode();
			return;
		}
		if (!portal.isLocked) {
			healTicks++;
			if (!withinDistance(portal, 1))
				this.addWalkSteps(portal.getX(), portal.getY());
			else if (healTicks % 6 == 0)
				healPortal(portal);
		}
	}
}
