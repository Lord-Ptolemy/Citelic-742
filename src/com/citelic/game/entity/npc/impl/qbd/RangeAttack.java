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
 * Handles the Queen Black Dragon's range attack.
 * 
 * @author Emperor
 * 
 */
public final class RangeAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16718);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				stop();
				int hit;
				if (victim.getPrayer().usingPrayer(1, 8)) {
					victim.setNextAnimation(new Animation(12573));
					victim.setNextGraphics(new Graphics(2229));
					victim.getPackets()
							.sendGameMessage(
									"You are unable to reflect damage back to this creature.");
					hit = 0;
				} else if (victim.getPrayer().usingPrayer(0, 18)) {
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
					hit = 0;
				} else {
					hit = Utilities.random(0 + Utilities.random(150), 360);
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED
						: HitLook.RANGE_DAMAGE));
			}
		}, 1);
		return Utilities.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}