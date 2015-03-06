package com.citelic.game.entity.npc.combat.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.combat.Combat;
import com.citelic.utility.Utilities;

public class FrostDragonCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		int damage;
		switch (Utilities.getRandom(3)) {
		case 0: // Melee
			if (npc.withinDistance(target, 3)) {
				damage = getRandomMaxHit(npc, defs.getMaxHit(),
						NPCCombatDefinitions.MELEE, target);
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, damage));
			} else {
				damage = Utilities.getRandom(650);
				if (Combat.hasAntiDragProtection(target)
						&& player.getFireImmune() > Utilities
								.currentTimeMillis()) {
					damage = Utilities.getRandom(150);
					player.getPackets()
							.sendGameMessage(
									"Your potion and Anti Dragon Protection absorbs most of the dragon's breath!",
									true);
				} else if (Combat.hasAntiDragProtection(target)
						&& player.getFireImmune() > Utilities
								.currentTimeMillis()
						&& player.getPrayer().usingPrayer(0, 17)
						|| player.getPrayer().usingPrayer(1, 7)) {
					damage = Utilities.getRandom(50);
					player.getPackets()
							.sendGameMessage(
									"Your potion and Anti Dragon Protection and prayer absorbs most of the dragon's breath!",
									true);
				} else if (Combat.hasAntiDragProtection(target)
						|| (player != null && (player.getPrayer().usingPrayer(
								0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
					damage = Utilities.getRandom(230);
					player.getPackets()
							.sendGameMessage(
									"Your "
											+ (Combat
													.hasAntiDragProtection(target) ? "shield"
													: "prayer")
											+ " absorbs most of the dragon's breath!",
									true);
				} else if ((!Combat.hasAntiDragProtection(target)
						|| !player.getPrayer().usingPrayer(0, 17) || !player
						.getPrayer().usingPrayer(1, 7))
						&& player.getFireImmune() > Utilities
								.currentTimeMillis()) {
					damage = Utilities.getRandom(250);
					player.getPackets().sendGameMessage(
							"Your potion absorbs most of the dragon's breath!",
							true);
				}
				npc.setNextAnimation(new Animation(13155));
				Engine.sendProjectile(npc, target, 393, 28, 16, 35, 35, 16, 0);
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			}
			break;
		case 1: // Dragon breath
			if (npc.withinDistance(target, 3)) {
				damage = Utilities.getRandom(650);
				if (Combat.hasAntiDragProtection(target)
						|| (player != null && (player.getPrayer().usingPrayer(
								0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
					damage = Utilities.getRandom(250);
					player.getPackets()
							.sendGameMessage(
									"Your "
											+ (Combat
													.hasAntiDragProtection(target) ? "shield"
													: "prayer")
											+ " absorbs most of the dragon's breath!",
									true);
				} else if ((!Combat.hasAntiDragProtection(target)
						|| !player.getPrayer().usingPrayer(0, 17) || !player
						.getPrayer().usingPrayer(1, 7))
						&& player.getFireImmune() > Utilities
								.currentTimeMillis()) {
					damage = Utilities.getRandom(350);
					player.getPackets()
							.sendGameMessage(
									"Your potion protects you some from the heat of the dragon's breath!",
									true);
				}
				npc.setNextAnimation(new Animation(13152));
				npc.setNextGraphics(new Graphics(2465));
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			} else {
				damage = Utilities.getRandom(650);
				if (Combat.hasAntiDragProtection(target)
						|| (player != null && (player.getPrayer().usingPrayer(
								0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
					damage = 0;
					player.getPackets()
							.sendGameMessage(
									"Your "
											+ (Combat
													.hasAntiDragProtection(target) ? "shield"
													: "prayer")
											+ " absorbs most of the dragon's breath!",
									true);
				} else if ((!Combat.hasAntiDragProtection(target)
						|| !player.getPrayer().usingPrayer(0, 17) || !player
						.getPrayer().usingPrayer(1, 7))
						&& player.getFireImmune() > Utilities
								.currentTimeMillis()) {
					damage = Utilities.getRandom(164);
					player.getPackets()
							.sendGameMessage(
									"Your potion fully protects you from the heat of the dragon's breath!",
									true);
				}
				npc.setNextAnimation(new Animation(13155));
				Engine.sendProjectile(npc, target, 393, 28, 16, 35, 35, 16, 0);
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			}
			break;
		case 2: // Range
			damage = Utilities.getRandom(250);
			npc.setNextAnimation(new Animation(13155));
			Engine.sendProjectile(npc, target, 2707, 28, 16, 35, 35, 16, 0);
			delayHit(npc, 1, target, getRangeHit(npc, damage));
			break;
		case 3: // Ice arrows range
			damage = Utilities.getRandom(250);
			npc.setNextAnimation(new Animation(13155));
			Engine.sendProjectile(npc, target, 369, 28, 16, 35, 35, 16, 0);
			delayHit(npc, 1, target, getRangeHit(npc, damage));
			break;
		case 4: // Orb crap
			break;
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

}
