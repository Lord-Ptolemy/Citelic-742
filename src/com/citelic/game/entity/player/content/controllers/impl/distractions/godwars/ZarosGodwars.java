package com.citelic.game.entity.player.content.controllers.impl.distractions.godwars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.npc.impl.godwars.zaros.NexMinion;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public final class ZarosGodwars {

	private static final List<Player> playersOn = Collections
			.synchronizedList(new ArrayList<Player>());
	public static Nex nex;
	public static NexMinion fumus;
	public static NexMinion umbra;
	public static NexMinion cruor;
	public static NexMinion glacies;

	private ZarosGodwars() {

	}

	public static void addPlayer(Player player) {
		// synchronized(LOCK) {
		if (ZarosGodwars.playersOn.contains(player))
			// System.out.println("ERROR DOUBLE ENTRY!");
			return;
		ZarosGodwars.playersOn.add(player);
		ZarosGodwars.startWar();
		// }
	}

	public static void breakCruorBarrier() {
		// synchronized(LOCK) {
		if (ZarosGodwars.cruor == null)
			return;
		ZarosGodwars.cruor.breakBarrier();
		// }
	}

	public static void breakFumusBarrier() {
		// synchronized(LOCK) {
		if (ZarosGodwars.fumus == null)
			return;
		ZarosGodwars.fumus.breakBarrier();
		// }
	}

	public static void breakGlaciesBarrier() {
		// synchronized(LOCK) {
		if (ZarosGodwars.glacies == null)
			return;
		ZarosGodwars.glacies.breakBarrier();
		// }
	}

	public static void breakUmbraBarrier() {
		// synchronized(LOCK) {
		if (ZarosGodwars.umbra == null)
			return;
		ZarosGodwars.umbra.breakBarrier();
		// }
	}

	private static void cancelWar() {
		if (ZarosGodwars.getPlayersCount() == 0) {
			ZarosGodwars.deleteNPCS();
		}
	}

	public static void deleteNPCS() {
		if (ZarosGodwars.nex != null) {
			ZarosGodwars.nex.killBloodReavers();
			ZarosGodwars.nex.finish();
			ZarosGodwars.nex = null;
		}
		if (ZarosGodwars.fumus != null) {
			ZarosGodwars.fumus.finish();
			ZarosGodwars.fumus = null;
		}
		if (ZarosGodwars.umbra != null) {
			ZarosGodwars.umbra.finish();
			ZarosGodwars.umbra = null;
		}
		if (ZarosGodwars.cruor != null) {
			ZarosGodwars.cruor.finish();
			ZarosGodwars.cruor = null;
		}
		if (ZarosGodwars.glacies != null) {
			ZarosGodwars.glacies.finish();
			ZarosGodwars.glacies = null;
		}
	}

	// private static final Object LOCK = new Object();

	public static void endWar() {
		// synchronized(LOCK) {
		ZarosGodwars.deleteNPCS();
		CoresManager.slowExecutor.schedule(new Runnable() {

			@Override
			public void run() {
				ZarosGodwars.startWar();
			}

		}, 1, TimeUnit.MINUTES);
		// }
	}

	public static int getPlayersCount() {
		return ZarosGodwars.playersOn.size();
	}

	public static ArrayList<Entity> getPossibleTargets() {
		// synchronized(LOCK) {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(
				ZarosGodwars.playersOn.size());
		for (Player player : ZarosGodwars.playersOn) {
			if (player == null || player.isDead() || player.hasFinished()
					|| !player.isRunning()) {
				continue;
			}
			possibleTarget.add(player);
			/*
			 * if (player.getFamiliar() != null &&
			 * player.getFamiliar().isAgressive())
			 * possibleTarget.add(player.getFamiliar());
			 */
		}
		return possibleTarget;
		// }
	}

	public static void moveNextStage() {
		// synchronized(LOCK) {
		if (ZarosGodwars.nex == null)
			return;
		ZarosGodwars.nex.moveNextStage();
		// }
	}

	public static void removePlayer(Player player) {
		// synchronized(LOCK) {
		ZarosGodwars.playersOn.remove(player);
		ZarosGodwars.cancelWar();
		// }
	}

	private static void startWar() {
		if (ZarosGodwars.getPlayersCount() >= 1) {
			if (ZarosGodwars.nex == null) {
				Engine.spawnNPC(13447, new Tile(2924, 5202, 0), -1, true, true);
				EngineTaskManager.schedule(new EngineTask() {
					private int count = 0;

					@Override
					public void run() {
						// synchronized(LOCK) {
						if (ZarosGodwars.nex == null) {
							stop();
							return;
						}
						if (count == 1) {
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"AT LAST!"));
							ZarosGodwars.nex.setNextAnimation(new Animation(
									17412));
							ZarosGodwars.nex
									.setNextGraphics(new Graphics(1217));
							ZarosGodwars.nex.playSound(3295, 2);
						} else if (count == 3) {
							Engine.spawnNPC(13451, new Tile(2913, 5215, 0), -1,
									true, true);
							ZarosGodwars.fumus.setDirection(Utilities
									.getFaceDirection(1, -1));
							ZarosGodwars.nex.setNextFaceTile(new Tile(
									ZarosGodwars.fumus
											.getCoordFaceX(ZarosGodwars.fumus
													.getSize()),
									ZarosGodwars.fumus
											.getCoordFaceY(ZarosGodwars.fumus
													.getSize()), 0));
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"Fumus!"));
							ZarosGodwars.nex.setNextAnimation(new Animation(
									17413));
							Engine.sendProjectile(ZarosGodwars.fumus,
									ZarosGodwars.nex, 2244, 18, 18, 60, 30, 0,
									0);
							ZarosGodwars.nex.playSound(3325, 2);
						} else if (count == 5) {
							Engine.spawnNPC(13452, new Tile(2937, 5215, 0), -1,
									true, true);
							ZarosGodwars.umbra.setDirection(Utilities
									.getFaceDirection(-1, -1));
							ZarosGodwars.nex.setNextFaceTile(new Tile(
									ZarosGodwars.umbra
											.getCoordFaceX(ZarosGodwars.umbra
													.getSize()),
									ZarosGodwars.umbra
											.getCoordFaceY(ZarosGodwars.umbra
													.getSize()), 0));
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"Umbra!"));
							ZarosGodwars.nex.setNextAnimation(new Animation(
									17413));
							Engine.sendProjectile(ZarosGodwars.umbra,
									ZarosGodwars.nex, 2244, 18, 18, 60, 30, 0,
									0);
							ZarosGodwars.nex.playSound(3313, 2);
						} else if (count == 7) {
							Engine.spawnNPC(13453, new Tile(2937, 5191, 0), -1,
									true, true);
							ZarosGodwars.cruor.setDirection(Utilities
									.getFaceDirection(-1, 1));
							ZarosGodwars.nex.setNextFaceTile(new Tile(
									ZarosGodwars.cruor
											.getCoordFaceX(ZarosGodwars.cruor
													.getSize()),
									ZarosGodwars.cruor
											.getCoordFaceY(ZarosGodwars.cruor
													.getSize()), 0));
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"Cruor!"));
							ZarosGodwars.nex.setNextAnimation(new Animation(
									17413));
							Engine.sendProjectile(ZarosGodwars.cruor,
									ZarosGodwars.nex, 2244, 18, 18, 60, 30, 0,
									0);
							ZarosGodwars.nex.playSound(3299, 2);
						} else if (count == 9) {
							Engine.spawnNPC(13454, new Tile(2913, 5191, 0), -1,
									true, true);
							ZarosGodwars.glacies.setNextFaceTile(new Tile(
									ZarosGodwars.glacies
											.getCoordFaceX(ZarosGodwars.glacies
													.getSize()),
									ZarosGodwars.glacies
											.getCoordFaceY(ZarosGodwars.glacies
													.getSize()), 0));
							ZarosGodwars.glacies.setDirection(Utilities
									.getFaceDirection(1, 1));
							ZarosGodwars.nex.setNextFaceTile(new Tile(
									ZarosGodwars.glacies
											.getCoordFaceX(ZarosGodwars.glacies
													.getSize()),
									ZarosGodwars.glacies
											.getCoordFaceY(ZarosGodwars.glacies
													.getSize()), 0));
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"Glacies!"));
							ZarosGodwars.nex.setNextAnimation(new Animation(
									17413));
							Engine.sendProjectile(ZarosGodwars.glacies,
									ZarosGodwars.nex, 2244, 18, 18, 60, 30, 0,
									0);
							ZarosGodwars.nex.playSound(3304, 2);
						} else if (count == 11) {
							ZarosGodwars.nex.setNextForceTalk(new ForceTalk(
									"Fill my soul with smoke!"));
							Engine.sendProjectile(ZarosGodwars.fumus,
									ZarosGodwars.nex, 2244, 18, 18, 60, 30, 0,
									0);

							ZarosGodwars.nex.playSound(3310, 2);
						} else if (count == 13) {
							ZarosGodwars.nex.setCantInteract(false);
							stop();
							return;
						}
						count++;
					}
					// }
				}, 0, 1);
			}
		}
	}
}
