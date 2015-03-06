package com.citelic.game.entity.npc.impl.qbd;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.combat.Combat;
import com.citelic.utility.Utilities;

/**
 * Handles the super dragonfire attack.
 * 
 * @author Emperor
 * 
 */
public final class SuperFireAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16745);

	/**
	 * The graphics.
	 */
	private static final Graphics GRAPHIC = new Graphics(3152);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextGraphics(GRAPHIC);
		victim.getPackets()
				.sendGameMessage(
						"<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>");
		EngineTaskManager.schedule(new EngineTask() {
			int count = 0;

			@Override
			public void run() {
				String message = FireBreathAttack.getProtectMessage(victim);
				int hit;
				if (message != null) {
					hit = Utilities.random(150 + Utilities.random(120),
							message.contains("prayer") ? 480 : 342);
					victim.getPackets().sendGameMessage(message);
				} else {
					hit = Utilities.random(400, 798);
					victim.getPackets().sendGameMessage(
							"You are horribly burned by the dragon's breath!");
				}
				int distance = Utilities.getDistance(
						npc.getBase().transform(33, 31, 0), victim);
				hit /= (distance / 3) + 1;
				victim.setNextAnimation(new Animation(Combat
						.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.REGULAR_DAMAGE));
				if (++count == 3) {
					stop();
				}
			}
		}, 4, 1);
		return Utilities.random(8, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}