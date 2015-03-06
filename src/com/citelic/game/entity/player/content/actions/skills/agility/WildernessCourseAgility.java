package com.citelic.game.entity.player.content.actions.skills.agility;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class WildernessCourseAgility {

	public static void climbCliff(final Player player, GameObject object) {
		if (player.getY() != 3939)
			return;
		player.lock();
		player.setNextAnimation(new Animation(3378));
		final Tile toTile = new Tile(2995, 3935, 0);
		player.getPackets().sendGameMessage("You climb up the rock.", true);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (Wilderness.isAtWild(player)) {
					player.setNextTile(toTile);
					player.setNextAnimation(new Animation(-1));
					int stage = getStage(player);
					if (stage == 3) {
						removeStage(player);
						player.getSkills().addXp(Skills.AGILITY,
								increasedExperience(player, 499));
					}
					player.unlock();
				}
			}
		}, 5);
	}

	public static void enterObstaclePipe(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		if (player.getY() == 3937 || player.getY() == 3938) {
			player.lock();
			player.faceObject(object);
			player.addWalkSteps(3004, 3938, -1, false);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.setNextAnimation(new Animation(10580));
					stop();
				}
			}, 1);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(3004, 3944, 0));
					}
					player.setNextAnimation(new Animation(10580));
					stop();
				}
			}, 3);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(3004, 3948, 0));
					}
					player.setNextAnimation(new Animation(10580));
					stop();
				}
			}, 5);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.getSkills().addXp(Skills.AGILITY,
							increasedExperience(player, 12));
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(3004, 3949, 0));
					}
					setStage(player, 0);
					player.unlock();
					stop();
				}
			}, 6);
		}
	}

	public static int getStage(Player player) {
		Integer stage = (Integer) player.getTemporaryAttributtes().get(
				"WildernessAgilityCourse");
		if (stage == null)
			return -1;
		return stage;
	}

	public static double increasedExperience(Player player, double totalXp) {
		if (Wilderness.isAtWild(player)
				&& player.getEquipment().getGlovesId() == 13849)
			totalXp *= 1.030;
		return totalXp;
	}

	public static void removeStage(Player player) {
		player.getTemporaryAttributtes().remove("WildernessAgilityCourse");
	}

	public static void setStage(Player player, int stage) {
		player.getTemporaryAttributtes().put("WildernessAgilityCourse", stage);
	}

	public static void steppingStone(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		player.lock();
		player.faceObject(object);
		player.setNextTile(new Tile(3001, 3960, 0));
		player.setNextAnimation(new Animation(741));
		EngineTaskManager.schedule(new EngineTask() {
			int jumpStone;

			@Override
			public void run() {
				if (jumpStone == 1) {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(3000, 3960, 0));
					}
					player.setNextAnimation(new Animation(741));
				}
				if (jumpStone == 2) {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(2999, 3960, 0));
					}
					player.setNextAnimation(new Animation(741));
				}
				if (jumpStone == 3) {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(2998, 3960, 0));
					}
					player.setNextAnimation(new Animation(741));
				}
				if (jumpStone == 4) {
					if (Wilderness.isAtWild(player)) {
						player.setNextTile(new Tile(2997, 3960, 0));
					}
					player.setNextAnimation(new Animation(741));
				}
				if (jumpStone == 5) {
					if (getStage(player) == 1)
						setStage(player, 2);
					player.setNextTile(new Tile(2996, 3960, 0));
					player.setNextAnimation(new Animation(741));
					player.getSkills().addXp(Skills.AGILITY,
							increasedExperience(player, 20));
					player.unlock();
				}
				if (jumpStone == 6) {
					jumpStone = 0;
					stop();
				}
				jumpStone++;
			}
		}, 0, 1);
	}

	public static void swingOnRopeSwing(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		if (player.getY() == 3958)
			return;
		player.lock(2);
		player.setNextAnimation(new Animation(751));
		Engine.sendObjectAnimation(player, object, new Animation(497));
		final Tile toTile = new Tile(object.getX(), 3958, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
				ForceMovement.NORTH));
		player.getSkills().addXp(Skills.AGILITY,
				increasedExperience(player, 20));
		player.getPackets()
				.sendGameMessage("You skilfully swing across.", true);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getStage(player) == 0)
					setStage(player, 1);
				player.setNextTile(toTile);
			}
		}, 1);
	}

	public static void walkAcrossLogBalance(final Player player) {
		if (!Agility.hasLevel(player, 52))
			return;
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(2994, 3945, -1, false);
		player.getPackets().sendGameMessage(
				"You walk carefully across the balance log...", true);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getGlobalPlayerUpdate().setRenderEmote(155);
				} else {
					if (getStage(player) == 2)
						setStage(player, 3);
					player.getGlobalPlayerUpdate().setRenderEmote(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY,
							increasedExperience(player, 20));
					player.getPackets().sendGameMessage(
							"... and make it safely to the other side.", true);
					player.unlock();
					stop();
				}
			}
		}, 0, 5);
	}

	public static void walkBackGate(final Player player, GameObject object) {
		player.faceObject(object);
		player.lock();
		player.getGlobalPlayerUpdate().setRenderEmote(155);
		player.addWalkSteps(2998, 3916, -1, false);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getGlobalPlayerUpdate().setRenderEmote(-1);
				player.getPackets().sendGameMessage(
						"You made it safely to the other side.", true);
				player.unlock();
				stop();
			}
		}, 9);
	}

	public static void walkGate(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		player.faceObject(object);
		player.lock();
		player.getGlobalPlayerUpdate().setRenderEmote(155);
		player.addWalkSteps(2998, 3931, -1, false);
		player.getPackets().sendGameMessage(
				"You go through the gate and try to edge over the ridge...",
				true);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getGlobalPlayerUpdate().setRenderEmote(-1);
				player.getPackets().sendGameMessage(
						"You skilfully balance across the ridge.", true);
				player.unlock();
				stop();
			}
		}, 9);
	}
}
