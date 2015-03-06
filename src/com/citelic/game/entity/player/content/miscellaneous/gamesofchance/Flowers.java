package com.citelic.game.entity.player.content.miscellaneous.gamesofchance;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelArena;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelControler;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class Flowers {

	private static final int[] REGULAR_FLOWERS = { 2980, 2981, 2982, 2983,
			2984, 2985, 2986 };
	private static final int[] RARE_FLOWERS = { 2987, 2988 };

	public static boolean plantFlower(Player player) {
		if (player.isLocked() || player.isResting())
			return false;
		if (!Engine.canMoveNPC(player.getZ(), player.getX(), player.getY(), 1)
				|| Engine.getRegion(player.getRegionId()).getSpawnedObject(
						player) != null
				|| player.getControllerManager().getController() instanceof DuelArena
				|| player.getControllerManager().getController() instanceof DuelControler) {
			player.getPackets()
					.sendGameMessage("You can't plant flowers here.");
			return false;
		}
		if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
			if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1)) {
					player.addWalkSteps(player.getX(), player.getY() - 1, 1);
				}
		player.getInventory().deleteItem(299, 1);
		plantObject(player);
		return true;
	}

	public static void plantObject(final Player p) {
		final Tile tile = new Tile(p);
		final GameObject flowerObject = new GameObject(getRandomFlowerId(), 10,
				Utilities.getRandom(4), tile.getX(), tile.getY(), tile.getZ());
		Engine.spawnTemporaryObject(flowerObject, 45000);
		p.lock();
		EngineTaskManager.schedule(new EngineTask() {
			int step;

			@Override
			public void run() {
				if (p == null || p.hasFinished()) {
					stop();
				}
				if (step == 1) {
					p.getDialogueManager().startDialogue("PickupFlower",
							flowerObject);
					p.setNextFaceTile(tile);
					p.unlock();
					stop();
				}
				step++;
			}
		}, 0, 0);
	}

	public static int getRandomFlowerId() {
		int calculatedId = 0;
		int random = Utilities.random(1000);
		if (random == 1) { // White or Black flower
			calculatedId = RARE_FLOWERS[Utilities.random(getLengthF() - 1)];
		} else {
			calculatedId = REGULAR_FLOWERS[Utilities.random(getLengthR() - 1)];
		}
		return calculatedId;
	}

	public static int getLengthR() {
		return REGULAR_FLOWERS.length;
	}

	public static int getLengthF() {
		return RARE_FLOWERS.length;
	}
}
