package com.citelic.game.entity.npc.impl.glacor.attacks;

import java.util.ArrayList;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.impl.glacor.Glacor;
import com.citelic.game.entity.npc.impl.glacor.GlacorAttacks;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

/**
 * 
 * @author Tyler <email>tyler@xlitersps.com</email> Represents the Glacors Magic
 *         Attack.
 */
public class MageAttack implements GlacorAttacks {
	// GFX: 739 962 739 963 902 905 899 634
	// Animations: 9968 9967
	ArrayList<Tile> tile = new ArrayList<Tile>();

	@Override
	public int attack(final Glacor glacor, final Entity target) {
		target.setAttackedBy(glacor);
		glacor.setNextAnimation(new Animation(9967));
		int SPEED = 25;
		glacor.setNextGraphics(new Graphics(905));
		Engine.sendProjectile(glacor, target, 634, 60, 40, SPEED, 30, 12, 0);
		int time = Utilities.getDistance(glacor, target) / SPEED;
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (target instanceof Player) {
					final Player player = (Player) target;
					if (!player.getPrayer().usingPrayer(
							player.getPrayer().isAncientCurses() ? 1 : 0,
							player.getPrayer().isAncientCurses() ? 7 : 17)
							&& Utilities.random(15) == 0) {
						player.sendMessage("<col=01A9DB>The glacor fires a freezing attack!</col>");
						if (!tile.contains(new Tile(target)))
							tile.add(new Tile(target));
						Engine.sendGraphics(target, new Graphics(369),
								tile.get(0));
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								if (target.matches(tile.get(0))
										&& !tile.isEmpty()) {
									target.applyHit(new Hit(glacor, Utilities
											.random(200, 400),
											HitLook.MAGIC_DAMAGE));
									target.addFreezeDelay(1000, false);
									player.sendMessage("You've been hit by the glacors freezing attack, it fires a punishing attack!");

								} else {
									player.sendMessage("You break free from the ice!");
								}
								this.stop();
								tile.clear();
							}

						}, 3);
					}
				}

				glacor.getCombat().delayHit(
						glacor,
						1,
						target,
						new Hit(glacor, Utilities.random(312),
								HitLook.MAGIC_DAMAGE));
				this.stop();

			}
		}, time);
		return 3;
	}

}