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
 * Represents a default fire breath attack.
 * 
 * @author Emperor
 * 
 */
public final class FireBreathAttack implements QueenAttack {

	/**
	 * The animation of the attack.
	 */
	private static final Animation ANIMATION = new Animation(16721);

	/**
	 * The graphic of the attack.
	 */
	private static final Graphics GRAPHIC = new Graphics(3143);

	/**
	 * Gets the dragonfire protect message.
	 * 
	 * @param player
	 *            The player.
	 * @return The message to send, or {@code null} if the player was
	 *         unprotected.
	 */
	public static final String getProtectMessage(Player player) {
		if (Combat.hasAntiDragProtection(player)) {
			return "Your shield absorbs most of the dragon's breath!";
		}
		if (player.getFireImmune() > Utilities.currentTimeMillis()) {
			return "Your potion absorbs most of the dragon's breath!";
		}
		if (player.getPrayer().usingPrayer(0, 17)
				|| player.getPrayer().usingPrayer(1, 7)) {
			return "Your prayer absorbs most of the dragon's breath!";
		}
		return null;
	}

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextGraphics(GRAPHIC);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				super.stop();
				String message = getProtectMessage(victim);
				int hit;
				if (message != null) {
					hit = Utilities.random(60 + Utilities.random(150),
							message.contains("prayer") ? 460 : 235);
					victim.getPackets().sendGameMessage(message);
				} else {
					hit = Utilities.random(400, 710);
					victim.getPackets().sendGameMessage(
							"You are horribly burned by the dragon's breath!");
				}
				victim.setNextAnimation(new Animation(Combat
						.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.REGULAR_DAMAGE));
			}
		}, 1);
		return Utilities.random(4, 15); // Attack delay seems to be random a
										// lot.
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}
}