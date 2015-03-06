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
 * Handles the Queen Black Dragon's melee attack.
 * 
 * @author Emperor
 * 
 */
public final class MeleeAttack implements QueenAttack {

	/**
	 * The default melee animation.
	 */
	private static final Animation DEFAULT = new Animation(16717);

	/**
	 * The east melee animation.
	 */
	private static final Animation EAST = new Animation(16744);

	/**
	 * The west melee animation.
	 */
	private static final Animation WEST = new Animation(16743);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		if (victim.getX() < npc.getBase().getX() + 31) {
			npc.setNextAnimation(WEST);
		} else if (victim.getX() > npc.getBase().getX() + 35) {
			npc.setNextAnimation(EAST);
		} else {
			npc.setNextAnimation(DEFAULT);
		}
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				stop();
				int hit = 0;
				if (victim.getPrayer().usingPrayer(1, 9)) {
					victim.setNextAnimation(new Animation(12573));
					victim.setNextGraphics(new Graphics(2230));
					victim.getPackets()
							.sendGameMessage(
									"You are unable to reflect damage back to this creature.");
					hit = 0;
				} else if (victim.getPrayer().usingPrayer(0, 19)) {
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
					hit = 0;
				} else {
					hit = Utilities.random(0 + Utilities.random(150), 360);
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED
						: HitLook.MELEE_DAMAGE));
			}
		});
		return Utilities.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return victim.getY() > npc.getBase().getY() + 32;
	}

}