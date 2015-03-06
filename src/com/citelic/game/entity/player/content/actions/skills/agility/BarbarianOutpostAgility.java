package com.citelic.game.entity.player.content.actions.skills.agility;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class BarbarianOutpostAgility {

	public static void climbObstacleNet(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 35) || player.getY() < 3545
				|| player.getY() > 3547)
			return;
		player.getPackets().sendGameMessage("You climb the netting...", true);
		player.getSkills().addXp(Skills.AGILITY, 8.2);
		player.useStairs(828, new Tile(object.getX() - 1, player.getY(), 1), 1,
				2);
		if (getStage(player) == 1)
			setStage(player, 2);
	}

	public static void climbOverCrumblingWall(final Player player,
			GameObject object) {
		if (!Agility.hasLevel(player, 35))
			return;
		if (player.getX() >= object.getX()) {
			player.getPackets().sendGameMessage(
					"You cannot climb that from this side.");
			return;
		}
		player.getPackets().sendGameMessage("You climb the low wall...", true);
		player.lock();
		player.setNextAnimation(new Animation(4853));
		final Tile toTile = new Tile(object.getX() + 1, object.getY(),
				object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2,
				ForceMovement.EAST));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 13.7);
				int stage = getStage(player);
				if (stage == 3)
					setStage(player, 4);
				else if (stage == 4) {
					removeStage(player);
					player.getSkills().addXp(Skills.AGILITY, 150.2);
				}
				player.unlock();
			}

		}, 1);
	}

	public static void climbUpWall(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.useStairs(10023, new Tile(2536, 3546, 3), 2, 3);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(11794));
				player.getSkills().addXp(Skills.AGILITY, 15);
			}

		}, 1);
	}

	public static void crossBalanceBeam(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.lock();
		final Tile toTile = new Tile(2536, 3553, 3);
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
				ForceMovement.EAST));
		player.setNextAnimation(new Animation(16079));
		player.getGlobalPlayerUpdate().setRenderEmote(330);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 15);
				player.setNextAnimation(new Animation(-1));
				player.unlock();
				stop();
			}

		}, 2);
	}

	public static void enterObstaclePipe(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 35))
			return;
		player.lock();
		player.setNextAnimation(new Animation(10580));
		final Tile toTile = new Tile(object.getX(),
				player.getY() >= 3561 ? 3558 : 3561, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2,
				player.getY() >= 3561 ? ForceMovement.SOUTH
						: ForceMovement.NORTH));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.setNextTile(toTile);
				player.unlock();
			}

		}, 1);
	}

	public static void fireSpringDevice(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.lock();
		player.addWalkSteps(2533, 3547, -1, false);
		final Tile toTile = new Tile(2532, 3553, 3);
		EngineTaskManager.schedule(new EngineTask() {

			boolean secondLoop;

			@Override
			public void run() {
				if (!secondLoop) {
					player.setNextForceMovement(new ForceMovement(player, 1,
							toTile, 3, ForceMovement.NORTH));
					player.setNextAnimation(new Animation(4189));
					Engine.sendObjectAnimation(player, object, new Animation(
							11819));

					secondLoop = true;
				} else {
					player.setNextTile(toTile);
					player.getSkills().addXp(Skills.AGILITY, 15);
					player.unlock();
					stop();
				}
			}

		}, 1, 1);
	}

	public static int getStage(Player player) {
		Integer stage = (Integer) player.getTemporaryAttributtes().get(
				"BarbarianOutpostCourse");
		if (stage == null)
			return -1;
		return stage;
	}

	public static void jumpOverGap(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.lock();
		player.setNextAnimation(new Animation(2586));
		player.getGlobalPlayerUpdate().setRenderEmote(-1);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(new Tile(2538, 3553, 2));
				player.setNextAnimation(new Animation(2588));
				player.getSkills().addXp(Skills.AGILITY, 15);
				player.unlock();
				stop();
			}

		}, 0);
	}

	public static void removeStage(Player player) {
		player.getTemporaryAttributtes().remove("BarbarianOutpostCourse");
	}

	public static void runUpWall(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.lock();
		final Tile toTile = new Tile(2538, 3545, 2);
		EngineTaskManager.schedule(new EngineTask() {

			boolean secondLoop;

			@Override
			public void run() {

				if (!secondLoop) {
					player.setNextForceMovement(new ForceMovement(player, 7,
							toTile, 8, ForceMovement.NORTH));
					player.setNextAnimation(new Animation(10492));
					secondLoop = true;
				} else {
					player.setNextAnimation(new Animation(10493));
					player.setNextTile(toTile);
					player.getSkills().addXp(Skills.AGILITY, 15);
					player.unlock();
					stop();
				}

			}

		}, 1, 6);
	}

	public static void setStage(Player player, int stage) {
		player.getTemporaryAttributtes().put("BarbarianOutpostCourse", stage);
	}

	public static void slideDownRoof(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 90))
			return;
		player.lock();
		player.setNextAnimation(new Animation(11792));
		final Tile toTile = new Tile(2544, player.getY(), 0);
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 5,
				ForceMovement.EAST));
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextTile(new Tile(2541, player.getY(), 1));
					player.setNextAnimation(new Animation(11790));
					stage = 1;
				} else if (stage == 1) {
					stage = 2;
				} else if (stage == 2) {
					player.setNextAnimation(new Animation(11791));
					stage = 3;
				} else if (stage == 3) {
					player.setNextTile(toTile);
					player.setNextAnimation(new Animation(2588));
					player.getSkills().addXp(Skills.AGILITY, 15);
					if (getStage(player) == 1) {
						removeStage(player);
						player.getSkills().addXp(Skills.AGILITY, 615);
						player.setBarbarianAdvancedLaps(player
								.getBarbarianAdvancedLaps() + 1);
						if (player.getBarbarianAdvancedLaps() == 250) {
							if (player.getInventory().getFreeSlots() == 0) {
								player.getDialogueManager()
										.startDialogue(
												"SimpleMessage",
												"You've ran 250 advanced barbarian laps and as a reward you gain Agile top. [NOTE] You didnt have enough inventory space so it has been banked.");
								player.getBank().addItem(14936, 1, true);
							} else {
								player.getDialogueManager()
										.startDialogue("SimpleMessage",
												"You've ran 250 advanced barbarian laps and as a reward you gain Agile top.");
								player.getInventory().addItem(14936, 1);
							}
						}
					}
					player.unlock();
					stop();
				}
			}

		}, 0, 0);
	}

	public static void swingOnRopeSwing(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 35))
			return;
		if (player.getY() != 3556) {
			player.getPackets().sendGameMessage(
					"You'll need to get closer to make this jump.");
			return;
		}
		player.lock();
		player.setNextAnimation(new Animation(751));
		Engine.sendObjectAnimation(player, object, new Animation(497));
		final Tile toTile = new Tile(object.getX(), 3549, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
				ForceMovement.SOUTH));
		player.getSkills().addXp(Skills.AGILITY, 22);
		player.getPackets()
				.sendGameMessage("You skilfully swing across.", true);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.setNextTile(toTile);
				player.unlock();
				setStage(player, 0);
			}

		}, 1);
	}

	public static void walkAcrossBalancingLedge(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 35))
			return;
		player.getPackets().sendGameMessage(
				"You put your food on the ledge and try to edge across...",
				true);
		player.lock();
		player.setNextAnimation(new Animation(753));
		player.getGlobalPlayerUpdate().setRenderEmote(157);
		final Tile toTile = new Tile(2532, object.getY(), object.getZ());
		player.setRun(true);
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(759));
				player.getGlobalPlayerUpdate().setRenderEmote(-1);
				player.getSkills().addXp(Skills.AGILITY, 22);
				player.getPackets().sendGameMessage(
						"You skilfully edge across the gap.", true);
				if (getStage(player) == 2)
					setStage(player, 3);
				player.unlock();
			}
		}, 3);
	}

	public static void walkAcrossLogBalance(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 35))
			return;
		if (player.getY() != object.getY()) {
			player.addWalkSteps(2551, 3546, -1, false);
			player.lock();
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					walkAcrossLogBalanceEnd(player, object);
				}
			}, 1);
		} else
			walkAcrossLogBalanceEnd(player, object);
	}

	private static void walkAcrossLogBalanceEnd(final Player player,
			GameObject object) {
		player.getPackets().sendGameMessage(
				"You walk carefully across the slippery log...", true);
		player.lock();
		player.setNextAnimation(new Animation(9908));
		final Tile toTile = new Tile(2541, object.getY(), object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 16,
				ForceMovement.WEST));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 13);
				player.getPackets().sendGameMessage(
						"... and make it safely to the other side.", true);
				if (getStage(player) == 0)
					setStage(player, 1);
				player.unlock();
			}

		}, 15);
	}
}
