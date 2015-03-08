package com.citelic.game.entity.npc.combat.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.ForceMovement;
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
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.NexCutScene;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class NexCombat extends CombatScript {

	public Tile[] NO_ESCAPE_TELEPORTS = { new Tile(2925, 5213, 0), // north
			new Tile(2935, 5203, 0), // east,
			new Tile(2925, 5193, 0), // south
			new Tile(2915, 5203, 0), }; // west

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Nex nex = (Nex) npc;
		int size = npc.getSize();
		// attacks close only
		if (nex.isFollowTarget()) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if (distanceX > size || distanceX < -1 || distanceY > size
					|| distanceY < -1)
				return 0;
			nex.setFollowTarget(Utilities.getRandom(1) == 0);
			// first stage close attacks
			if (nex.getAttacksStage() == 0) {
				// virus 1/3 probability every 1min
				if (nex.getLastVirus() < Utilities.currentTimeMillis()
						&& Utilities.getRandom(2) == 0) {
					nex.setLastVirus(Utilities.currentTimeMillis() + 60000);
					npc.setNextForceTalk(new ForceTalk(
							"Let the virus flow through you."));
					nex.playSound(3296, 2);
					npc.setNextAnimation(new Animation(17413));
					nex.sendVirusAttack(new ArrayList<Entity>(),
							npc.getPossibleTargets(), target);
					return defs.getAttackDelay();
				}
			}
			// no escape, 1/10 probability doing it
			if (Utilities.getRandom(nex.getStage() == 4 ? 5 : 10) == 0) {
				npc.setNextForceTalk(new ForceTalk("There is..."));
				nex.playSound(3294, 2);
				npc.setCantInteract(true);
				npc.getCombat().removeTarget();
				final int idx = Utilities.random(NO_ESCAPE_TELEPORTS.length);
				final Tile dir = NO_ESCAPE_TELEPORTS[idx];
				final Tile center = new Tile(2924, 5202, 0);
				EngineTaskManager.schedule(new EngineTask() {
					private int count;

					@Override
					public void run() {
						if (count == 0) {
							npc.setNextAnimation(new Animation(17411));
							npc.setNextGraphics(new Graphics(1216));
						} else if (count == 1) {
							nex.setNextTile(dir);
							nex.setNextForceTalk(new ForceTalk("NO ESCAPE!"));
							nex.playSound(3292, 2);
							nex.setNextForceMovement(new ForceMovement(dir, 1,
									center, 3, idx == 3 ? 1 : idx == 2 ? 0
											: idx == 1 ? 3 : 2));
							for (Entity entity : nex.calculatePossibleTargets(
									center, dir, idx == 0 || idx == 2)) {
								if (entity instanceof Player) {
									Player player = (Player) entity;
									player.getCutscenesManager().play(
											new NexCutScene(dir, idx));
									player.applyHit(new Hit(
											npc,
											Utilities
													.getRandom(nex.getStage() == 4 ? 800
															: 650),
											HitLook.REGULAR_DAMAGE));
									player.setNextAnimation(new Animation(17408));
									player.setNextForceMovement(new ForceMovement(
											player, 1, idx == 3 ? 3
													: idx == 2 ? 2
															: idx == 1 ? 1 : 0));
								}
							}
						} else if (count == 3) {
							nex.setNextTile(center);
						} else if (count == 4) {
							nex.setTarget(target);
							npc.setCantInteract(false);
							stop();
						}
						count++;
					}
				}, 0, 1);
				return defs.getAttackDelay();
			}
			// normal melee attack
			int damage = CombatScript.getRandomMaxHit(npc, defs.getMaxHit(),
					NPCCombatDefinitions.MELEE, target);
			CombatScript.delayHit(npc, 0, target,
					CombatScript.getMeleeHit(npc, damage));
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			return defs.getAttackDelay();
			// far attacks
		} else {
			nex.setFollowTarget(Utilities.getRandom(1) == 0);
			// drag a player to center
			if (Utilities.getRandom(15) == 0) {
				int distance = 0;
				Entity settedTarget = null;
				for (Entity t : npc.getPossibleTargets()) {
					if (t instanceof Player) {
						int thisDistance = Utilities.getDistance(t.getX(),
								t.getY(), npc.getX(), npc.getY());
						if (settedTarget == null || thisDistance > distance) {
							distance = thisDistance;
							settedTarget = t;
						}
					}
				}
				if (settedTarget != null && distance > 10) {
					final Player player = (Player) settedTarget;
					player.lock(3);
					player.setNextAnimation(new Animation(17408));
					player.setNextForceMovement(new ForceMovement(nex, 2,
							Utilities.getMoveDirection(
									npc.getCoordFaceX(player.getSize())
											- player.getX(),
									npc.getCoordFaceY(player.getSize())
											- player.getY())));
					npc.setNextAnimation(new Animation(17414));
					npc.setTarget(player);
					player.setNextAnimation(new Animation(17408));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextTile(nex);
							player.getPackets()
									.sendGameMessage(
											"You've been injured and you can't use protective curses!");
							player.setPrayerDelay(Utilities.getRandom(20000) + 5);// random
							// 20
							// seconds
							player.getPackets().sendGameMessage(
									"You're stunned.");
						}
					});
					return defs.getAttackDelay();
				}
			}
			// first stage close attacks
			if (nex.getAttacksStage() == 0) {
				npc.setNextAnimation(new Animation(17413));
				for (Entity t : npc.getPossibleTargets()) {
					Engine.sendProjectile(npc, t, 471, 41, 16, 41, 35, 16, 0);
					int damage = CombatScript.getRandomMaxHit(npc,
							defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					CombatScript.delayHit(npc, 1, t,
							CombatScript.getMagicHit(npc, damage));
					if (damage > 0 && Utilities.getRandom(5) == 0) {
						// probability
						// poisoning
						t.getPoison().makePoisoned(80);
					}
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 1) {
				if (!nex.isEmbracedShadow()) {
					nex.setEmbracedShadow(true);
					npc.setNextForceTalk(new ForceTalk("Embrace darkness!"));
					nex.playSound(3322, 2);
					npc.setNextAnimation(new Animation(17412));
					npc.setNextGraphics(new Graphics(1217));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							if (nex.getAttacksStage() != 1 || nex.hasFinished()) {
								for (Entity entity : nex.getPossibleTargets()) {
									if (entity instanceof Player) {
										Player player = (Player) entity;
										player.getPackets().sendConfigByFile(
												1435, 255);
									}
								}
								stop();
								return;
							}
							if (Utilities.getRandom(2) == 0) {
								for (Entity entity : nex.getPossibleTargets()) {
									if (entity instanceof Player) {
										Player player = (Player) entity;
										int distance = Utilities.getDistance(
												player.getX(), player.getY(),
												npc.getX(), npc.getY());
										if (distance > 30) {
											distance = 30;
										}
										player.getPackets().sendConfigByFile(
												1435, distance * 255 / 30);
									}
								}
							}
						}
					}, 0, 0);
					return defs.getAttackDelay();
				}
				if (!nex.isTrapsSettedUp() && Utilities.getRandom(2) == 0) {
					nex.setTrapsSettedUp(true);
					npc.setNextForceTalk(new ForceTalk("Fear the Shadow!"));
					nex.playSound(3314, 2);
					npc.setNextAnimation(new Animation(17407));
					npc.setNextGraphics(new Graphics(1215));
					ArrayList<Entity> possibleTargets = nex
							.getPossibleTargets();
					final HashMap<String, int[]> tiles = new HashMap<String, int[]>();
					for (Entity t : possibleTargets) {
						String key = t.getX() + "_" + t.getY();
						if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
							tiles.put(key, new int[] { t.getX(), t.getY() });
							Engine.spawnObjectTemporary(new GameObject(57261,
									10, 0, t.getX(), t.getY(), 0), 2400);
						}
					}
					EngineTaskManager.schedule(new EngineTask() {
						private boolean firstCall;

						@Override
						public void run() {
							if (!firstCall) {
								ArrayList<Entity> possibleTargets = nex
										.getPossibleTargets();
								for (int[] tile : tiles.values()) {
									Engine.sendGraphics(null,
											new Graphics(383), new Tile(
													tile[0], tile[1], 0));
									for (Entity t : possibleTargets)
										if (t.getX() == tile[0]
												&& t.getY() == tile[1]) {
											t.applyHit(new Hit(npc, Utilities
													.getRandom(400) + 400,
													HitLook.REGULAR_DAMAGE));
										}
								}
								firstCall = true;
							} else {
								nex.setTrapsSettedUp(false);
								stop();
							}
						}

					}, 3, 3);
					return defs.getAttackDelay();
				}
				npc.setNextAnimation(new Animation(17413));
				for (final Entity t : npc.getPossibleTargets()) {
					int distance = Utilities.getDistance(t.getX(), t.getY(),
							npc.getX(), npc.getY());
					if (distance <= 10) {
						int damage = 800 - distance * 800 / 11;
						Engine.sendProjectile(npc, t, 380, 41, 16, 41, 35, 16,
								0);
						CombatScript.delayHit(npc, 1, t, CombatScript
								.getRangeHit(npc, CombatScript.getRandomMaxHit(
										npc, damage,
										NPCCombatDefinitions.RANGE, t)));
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								t.setNextGraphics(new Graphics(471));
							}
						}, 1);
					}
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 2) {
				if (Utilities.getRandom(4) == 0 && target instanceof Player) {
					npc.setNextForceTalk(new ForceTalk(
							"I demand a blood sacrifice!"));
					nex.playSound(3293, 2);
					final Player player = (Player) target;
					player.getPlayerAppearances().setGlowRed(true);
					player.getPackets().sendGameMessage(
							"Nex has marked you as a sacrifice, RUN!");
					final int x = player.getX();
					final int y = player.getY();
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.getPlayerAppearances().setGlowRed(false);
							if (x == player.getX() && y == player.getY()) {
								player.getPackets()
										.sendGameMessage(
												"You didn't make it far enough in time - Nex fires a punishing attack!");
								npc.setNextAnimation(new Animation(17413));
								for (final Entity t : npc.getPossibleTargets()) {
									Engine.sendProjectile(npc, t, 374, 41, 16,
											41, 35, 16, 0);
									final int damage = CombatScript
											.getRandomMaxHit(npc, 290,
													NPCCombatDefinitions.MAGE,
													t);
									CombatScript.delayHit(npc, 1, t,
											CombatScript.getMagicHit(npc,
													damage));
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													t.setNextGraphics(new Graphics(
															376));
													nex.heal(damage / 4);
													if (t instanceof Player) {
														Player p = (Player) t;
														p.getPrayer()
																.drainPrayerOnHalf();
													}
												}
											}, 1);
								}
							}
						}
					}, defs.getAttackDelay());
					return defs.getAttackDelay() * 2;
				}
				if (nex.getLastSiphon() < Utilities.currentTimeMillis()
						&& npc.getHitpoints() <= 18000
						&& Utilities.getRandom(2) == 0) {
					nex.setLastSiphon(Utilities.currentTimeMillis() + 30000);
					nex.killBloodReavers();
					npc.setNextForceTalk(new ForceTalk(
							"A siphon will solve this!"));
					nex.playSound(3317, 2);
					npc.setNextAnimation(new Animation(17409));
					npc.setNextGraphics(new Graphics(1201));
					nex.setDoingSiphon(true);
					int bloodReaverSize = NPCDefinitions
							.getNPCDefinitions(13458).size;
					int respawnedBloodReaverCount = 0;
					int maxMinions = Utilities.getRandom(3);
					if (maxMinions != 0) {
						int[][] dirs = Utilities
								.getCoordOffsetsNear(bloodReaverSize);
						for (int dir = 0; dir < dirs[0].length; dir++) {
							final Tile tile = new Tile(new Tile(target.getX()
									+ dirs[0][dir], target.getY()
									+ dirs[1][dir], target.getZ()));
							if (Engine.isTileFree(tile.getZ(), tile.getX(),
									tile.getY(), bloodReaverSize)) { // if
								// found
								// done
								nex.getBloodReavers()[respawnedBloodReaverCount++] = new NPC(
										13458, tile, -1, true, true);
								if (respawnedBloodReaverCount == maxMinions) {
									break;
								}
							}
						}
					}
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							nex.setDoingSiphon(false);
						}
					}, 8);
					return defs.getAttackDelay();
				}
				npc.setNextAnimation(new Animation(17414));
				Engine.sendProjectile(npc, target, 374, 41, 16, 41, 35, 16, 0);
				CombatScript.delayHit(npc, 1, target, CombatScript.getMagicHit(
						npc, CombatScript.getRandomMaxHit(npc,
								defs.getMaxHit(), NPCCombatDefinitions.MAGE,
								target)));
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 3) {
				npc.setNextAnimation(new Animation(17414));
				for (final Entity t : npc.getPossibleTargets()) {
					Engine.sendProjectile(npc, t, 362, 41, 16, 41, 35, 16, 0);
					int damage = CombatScript.getRandomMaxHit(npc,
							defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					CombatScript.delayHit(npc, 1, t,
							CombatScript.getMagicHit(npc, damage));
					if (damage > 0 && Utilities.getRandom(5) == 0) {// 1/6
						// probability
						// freezing
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								t.addFreezeDelay(18000);
								t.setNextGraphics(new Graphics(369));
							}
						}, 2);

					}
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 4) {
				npc.setNextAnimation(new Animation(17413));
				for (Entity t : npc.getPossibleTargets()) {
					Engine.sendProjectile(npc, t, 471, 41, 16, 41, 35, 16, 0);
					int damage = CombatScript.getRandomMaxHit(npc,
							defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					CombatScript.delayHit(npc, 1, t,
							CombatScript.getMagicHit(npc, damage));
				}
				return defs.getAttackDelay();
			}
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nex" };
	}
}
