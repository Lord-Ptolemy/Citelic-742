package com.citelic.game.entity.npc.combat.impl;

import java.util.ArrayList;

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
import com.citelic.utility.Utilities;

public class WildyWyrmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 3334 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (Utilities.getRandom(10) == 0)
			npc.setNextForceTalk(new ForceTalk("You cannot be helped now!"));
		NPCCombatDefinitions cdefs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX(), distanceY = target.getY()
				- npc.getY(), size = npc.getSize();
		boolean inMeleeDistance = !(distanceX > size || distanceX < -1
				|| distanceY > size || distanceY < -1);
		switch (Utilities.getRandom(!inMeleeDistance ? 1 : 4)) {
		case 0:
		case 1:
			sendMagicAttack(npc, 12794);
			break;
		case 2:
		case 3:
		case 4:
			sendMeleeAttack(npc, target, cdefs.getAttackEmote());
			break;
		}
		if (Utilities.getRandom(3) == 0)
			sendRangedAttack(npc, 12794);
		return cdefs.getAttackDelay();
	}

	/**
	 * Sends a melee attack.
	 * 
	 * @param npc
	 *            The WildyWyrm.
	 * @param target
	 *            The target.
	 * @param emote
	 *            The emote Id.
	 */
	private void sendMeleeAttack(NPC npc, Entity target, int emote) {
		npc.setNextAnimation(new Animation(emote));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(
						npc,
						getRandomMaxHit(npc, npc.getCombatDefinitions()
								.getMaxHit(), NPCCombatDefinitions.MELEE,
								target)));
	}

	/**
	 * Sends the magic attack.
	 * 
	 * @param npc
	 *            The WildyWyrm.
	 * @param emote
	 *            The emote Id.
	 */
	private void sendMagicAttack(NPC npc, int emote) {
		final ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
		final int requiredAmount = possibleTargets.size() > 5 ? 5
				: possibleTargets.size();
		ArrayList<Entity> targets = new ArrayList<Entity>(requiredAmount);
		while (targets.size() < requiredAmount) {
			Entity e = possibleTargets.get(Utilities.random(possibleTargets
					.size()));
			if (e != null && !targets.contains(e))
				targets.add(e);
		}
		npc.setNextAnimation(new Animation(emote));
		for (final Entity target : targets) {
			if (target == null)
				continue;
			Engine.sendProjectile(npc, target, 58, 50, 25, 45, 35, 16, 0);
			final int hitAmount = 1 + (Utilities.getRandom(1) == 1 ? Utilities
					.getRandom(1) : 0);
			ArrayList<Hit> hits = new ArrayList<Hit>(hitAmount);
			hits.add(new Hit(npc, getRandomMaxHit(npc, 500,
					NPCCombatDefinitions.MAGE, target), HitLook.MAGIC_DAMAGE));
			if (hitAmount > 1) {
				hits.add(new Hit(npc, Utilities.random(50, 250),
						HitLook.DESEASE_DAMAGE));
				if (target instanceof Player) {
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							((Player) target)
									.getPackets()
									.sendGameMessage(
											"<col=ff9501>The Wildywyrm's magical attack has caused horrible damage to you. "
													+ "It seems that you're burned.");
						}
					}, 1);
				}
			}
			delayHit(npc, 1, target, hits.toArray(new Hit[hitAmount]));
		}
	}

	/**
	 * Sends the ranging attack.
	 * 
	 * @param npc
	 *            The WildyWyrm.
	 * @param emote
	 *            The emote Id.
	 */
	private void sendRangedAttack(NPC npc, int emote) {
		npc.setNextAnimation(new Animation(emote));
		for (final Entity e : npc.getPossibleTargets()) {
			if (e != null && e.withinDistance(npc.getMiddleWorldTile(), 4)
					&& Utilities.getRandom(1) == 0) {
				e.setNextGraphics(new Graphics(3002));
				delayHit(
						npc,
						1,
						e,
						getRangeHit(
								npc,
								getRandomMaxHit(npc, 450,
										NPCCombatDefinitions.RANGE, e)));
				if (e instanceof Player)
					((Player) e)
							.getPackets()
							.sendGameMessage(
									"<col=ff0000>You have been damaged by the Wildywyrm's ranged attack.");
			}
		}
	}
}