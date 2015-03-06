package com.citelic.game.entity.player.content.miscellaneous;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.map.objects.GameObject;
import com.citelic.utility.Utilities;

public final class LivingRockCavern {

	private static enum Rocks {
		COAL_ROCK_1(new GameObject(5999, 10, 1, 3690, 5146, 0)), COAL_ROCK_2(
				new GameObject(5999, 10, 2, 3690, 5125, 0)), COAL_ROCK_3(
				new GameObject(5999, 10, 0, 3687, 5107, 0)), COAL_ROCK_4(
				new GameObject(5999, 10, 1, 3674, 5098, 0)), COAL_ROCK_5(
				new GameObject(5999, 10, 2, 3664, 5090, 0)), COAL_ROCK_6(
				new GameObject(5999, 10, 3, 3615, 5090, 0)), COAL_ROCK_7(
				new GameObject(5999, 10, 1, 3625, 5107, 0)), COAL_ROCK_8(
				new GameObject(5999, 10, 3, 3647, 5142, 0)), GOLD_ROCK_1(
				new GameObject(45076, 10, 1, 3667, 5075, 0)), GOLD_ROCK_2(
				new GameObject(45076, 10, 0, 3637, 5094, 0)), GOLD_ROCK_3(
				new GameObject(45076, 10, 0, 3677, 5160, 0)), GOLD_ROCK_4(
				new GameObject(45076, 10, 1, 3629, 5148, 0));

		private GameObject rock;

		private Rocks(GameObject rock) {
			this.rock = rock;
		}

	}

	public static void init() {
		for (Rocks rock : Rocks.values())
			respawnRock(rock);
	}

	private static void removeRock(final Rocks rock) {
		Engine.destroySpawnedObject(rock.rock, false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				respawnRock(rock);
			}

		}, 3, TimeUnit.MINUTES);
	}

	private static void respawnRock(final Rocks rock) {
		Engine.spawnObject(rock.rock, false);
		CoresManager.slowExecutor.schedule(new Runnable() {

			@Override
			public void run() {
				removeRock(rock);
			}
		}, Utilities.random(8) + 3, TimeUnit.MINUTES);
	}

	private LivingRockCavern() {

	}
}
