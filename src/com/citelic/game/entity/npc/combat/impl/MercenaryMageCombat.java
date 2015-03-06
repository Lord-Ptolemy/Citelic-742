package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class MercenaryMageCombat extends CombatScript {

	public static final String[] ATTACKS = new String[] {
			"I will make you suffer!", "Death is your only option!",
			"Why fight back?", "It is time for you to die.",
			"IS THIS ALL YOU'VE GOT?" };

	@Override
	public int attack(final NPC npc, Entity target) {
		int attackStyle = Utilities.random(5);
		if (attackStyle == 0) {
			npc.setNextAnimation(new Animation(1979));
			final Tile center = new Tile(target);
			Engine.sendGraphics(npc, new Graphics(2929), center);
			npc.setNextForceTalk(new ForceTalk("Obliterate!"));
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					for (Player player : Engine.getPlayers()) { // lets just
																// loop
						// all players
						// for massive
						// moves
						if (player == null || player.isDead()
								|| player.hasFinished())
							continue;
						if (player.withinDistance(center, 3)) {
							if (!player.getMusicsManager().hasMusic(843))
								player.getMusicsManager().playMusic(843);
							delayHit(npc, 0, player,
									new Hit(npc, Utilities.random(1500),
											HitLook.REGULAR_DAMAGE));
						}
					}
				}

			}, 4);
		} else if (attackStyle == 1) {
			npc.setNextAnimation(new Animation(1979));
			final Tile center = new Tile(target);
			Engine.sendGraphics(npc, new Graphics(2191), center);
			npc.setNextForceTalk(new ForceTalk("How are the burns?"));
			EngineTaskManager.schedule(new EngineTask() {
				int count = 0;

				@Override
				public void run() {
					for (Player player : Engine.getPlayers()) { // lets just
																// loop
						// all players
						// for massive
						// moves
						if (player == null || player.isDead()
								|| player.hasFinished())
							continue;
						if (player.withinDistance(center, 1)) {
							delayHit(npc, 0, player,
									new Hit(npc, Utilities.random(300),
											HitLook.REGULAR_DAMAGE));
						}
					}
					if (count++ == 10) {
						stop();
						return;
					}
				}
			}, 0, 0);
		} else if (attackStyle == 2) {
			npc.setNextAnimation(new Animation(1979));
			final int dir = Utilities
					.random(Utilities.DIRECTION_DELTA_X.length);
			final Tile center = new Tile(npc.getX()
					+ Utilities.DIRECTION_DELTA_X[dir] * 5, npc.getY()
					+ Utilities.DIRECTION_DELTA_Y[dir] * 5, 0);
			npc.setNextForceTalk(new ForceTalk(
					"I think it's time to clean my room!"));
			EngineTaskManager.schedule(new EngineTask() {
				int count = 0;

				@Override
				public void run() {
					for (Player player : Engine.getPlayers()) { // lets just
																// loop
						// all players
						// for massive
						// moves
						if (Utilities.DIRECTION_DELTA_X[dir] == 0) {
							if (player.getX() != center.getX())
								continue;
						}
						if (Utilities.DIRECTION_DELTA_Y[dir] == 0) {
							if (player.getY() != center.getY())
								continue;
						}
						if (Utilities.DIRECTION_DELTA_X[dir] != 0) {
							if (Math.abs(player.getX() - center.getX()) > 5)
								continue;
						}
						if (Utilities.DIRECTION_DELTA_Y[dir] != 0) {
							if (Math.abs(player.getY() - center.getY()) > 5)
								continue;
						}
						delayHit(npc, 0, player,
								new Hit(npc, Utilities.random(1500),
										HitLook.REGULAR_DAMAGE));
					}
					if (count++ == 5) {
						stop();
						return;
					}
				}
			}, 0, 0);
			Engine.sendProjectile(npc, center, 2196, 0, 0, 5, 35, 0, 0);
		} else if (attackStyle == 3) {
			delayHit(
					npc,
					2,
					target,
					getMagicHit(
							npc,
							getRandomMaxHit(npc, Utilities.random(3000),
									NPCCombatDefinitions.MAGE, target)));
			Engine.sendProjectile(npc, target, 2873, 34, 16, 40, 35, 16, 0);
			npc.setNextAnimation(new Animation(14221));
			npc.setNextForceTalk(new ForceTalk(ATTACKS[Utilities
					.random(ATTACKS.length)]));
		} else if (attackStyle == 4) {
			npc.setNextAnimation(new Animation(1979));
			npc.setNextGraphics(new Graphics(444));
			npc.heal(3000);
		}
		return 5;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 8335 };
	}

}
