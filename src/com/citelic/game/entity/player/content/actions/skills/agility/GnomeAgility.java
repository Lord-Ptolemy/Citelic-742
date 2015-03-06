package com.citelic.game.entity.player.content.actions.skills.agility;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

/**
 * 
 * @author Marko Knol
 *
 */
public class GnomeAgility {

	/**
	 * 
	 * @param player
	 */
	public static void climbDownGnomeTreeBranch(final Player player) {
		player.getPackets().sendGameMessage("You climb down the tree...", true);
		player.useStairs(828, new Tile(2487, 3421, 0), 1, 2,
				"You land on the ground.");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 3)
					setGnomeStage(player, 4);
				player.getSkills().addXp(Skills.AGILITY, 5);
			}
		}, 1);
	}

	/**
	 * 
	 * @param player
	 */
	public static void climbGnomeObstacleNet(final Player player) {
		if (player.getY() != 3426)
			return;
		player.getPackets().sendGameMessage("You climb the netting.", true);
		player.useStairs(828, new Tile(player.getX(), 3423, 1), 1, 2);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 0)
					setGnomeStage(player, 1);
				player.getSkills().addXp(Skills.AGILITY, 7.5);
			}
		}, 1);
	}

	/**
	 * 
	 * @param player
	 */
	public static void climbGnomeObstacleNet2(final Player player) {
		if (player.getY() != 3425)
			return;
		player.getPackets().sendGameMessage("You climb the netting.", true);
		player.useStairs(828, new Tile(player.getX(),
				player.getY() == 3425 ? 3428 : 3425, 0), 1, 2);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 4)
					setGnomeStage(player, 5);
				player.getSkills().addXp(Skills.AGILITY, 7.5);
			}
		}, 1);
	}

	public static void climbUpGnomeTreeBranch(final Player player) {
		player.getPackets().sendGameMessage("You climb the tree...", true);
		player.useStairs(828, new Tile(2473, 3420, 2), 1, 2,
				"... to the platform above.");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 1)
					setGnomeStage(player, 2);
				player.getSkills().addXp(Skills.AGILITY, 5);
			}
		}, 1);
	}

	/**
	 * 
	 * @param player
	 */
	public static void climbUpTree(final Player player) {
		if (!Agility.hasLevel(player, 85))
			return;
		player.getPackets().sendGameMessage("You climb the tree...", true);
		player.useStairs(828, new Tile(player.getX(), 3420, 3), 1, 2, "... to an even higher platform.");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				if (getGnomeStage(player) == 1)
					setGnomeStage(player, 2);
				player.getSkills().addXp(Skills.AGILITY, 25);
			}
		}, 1);
	}

	/**
	 * 
	 * @param player
	 * @param objectX
	 * @param objectY
	 */
	public static void enterGnomePipe(final Player player, int objectX,
			int objectY) {
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(objectX, objectY == 3431 ? 3437 : 3430, -1, false);
		player.getPackets().sendGameMessage(
				"You pulled yourself through the pipes.", true);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getGlobalPlayerUpdate().setRenderEmote(295);
				} else {
					player.getGlobalPlayerUpdate().setRenderEmote(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY, 7.5);
					if (getGnomeStage(player) == 5) {
						removeGnomeStage(player);
						player.getSkills().addXp(Skills.AGILITY, 39);
					}
					player.unlock();
					stop();
				}
			}
		}, 0, 6);
	}

	/**
	 * 
	 * @param player
	 * @return stage
	 */
	public static int getGnomeStage(Player player) {
		Integer stage = (Integer) player.getTemporaryAttributtes().get(
				"GnomeCourse");
		if (stage == null)
			return -1;
		return stage;
	}

	/**
	 * 
	 * @param player
	 * @param object
	 */
	public static void jumpDown(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 85))
			return;
		player.lock();
		final Tile toTile = new Tile(2485, 3436, 0);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondLoop;

			@Override
			public void run() {
				if (!secondLoop) {
					player.setNextForceMovement(new ForceMovement(player, 0,
							toTile, 5, ForceMovement.NORTH));
					player.setNextAnimation(new Animation(2923));
					secondLoop = true;
				} else {
					player.setNextAnimation(new Animation(2924));
					player.setNextTile(toTile);
					if (getGnomeStage(player) == 4) {
						player.getSkills().addXp(Skills.AGILITY, 605);
						setGnomeStage(player, 0);
                        if (player.getGnomeAdvancedLaps() <= 251) {
                        	player.setGnomeAdvancedLaps(player
    								.getGnomeAdvancedLaps() + 1);
                        }
                        if (player.getGnomeAdvancedLaps() == 250) {
                            player.sendMessage("You've completed 250 perfect laps! Speak to a gnome trainer for your reward.");
                        }
					}
					player.getSkills().addXp(Skills.AGILITY, 25);
					player.unlock();
					stop();
				}
			}
		}, 1, 2);
	}

	/**
	 * 
	 * @param player
	 * @param object
	 */
	public static void preSwing(final Player player, final GameObject object) {
		if (player.getX() != 2486 || player.getY() != 3418
				|| player.getZ() != 3)
			player.lock();
		player.setNextAnimation(new Animation(11784));
		final Tile toTile = new Tile(player.getX(), 3421, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2,
				ForceMovement.NORTH));
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 1) {
					player.setNextTile(toTile);
					player.setNextAnimation(new Animation(11785));
					swing(player, object);
					stop();
				}
				stage++;
			}
		}, 0, 1);
	}

	/**
	 * 
	 * @param player
	 */
	public static void removeGnomeStage(Player player) {
		player.getTemporaryAttributtes().remove("GnomeCourse");
	}

	/**
	 * 
	 * @param player
	 * @param object
	 */
	public static void runGnomeBoard(final Player player,
			final GameObject object) {
		if (player.getX() != 2477 || player.getY() != 3418
				|| player.getZ() != 3)
			return;
		player.lock(4);
		player.setNextAnimation(new Animation(2922));
		final Tile toTile = new Tile(2484, 3418, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
				ForceMovement.EAST));
		player.getSkills().addXp(Skills.AGILITY, 25);
		player.getPackets().sendGameMessage(
				"You skilfully run across the Board", true);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(toTile);
				setGnomeStage(player, 3);
			}
		}, 1);
	}

	/**
	 * 
	 * @param player
	 * @param stage
	 */
	public static void setGnomeStage(Player player, int stage) {
		player.getTemporaryAttributtes().put("GnomeCourse", stage);
	}

	/**
	 * 
	 * @param player
	 * @param object
	 */
	public static void swing(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 85))
			return;
		player.lock();
		final Tile toTile = new Tile(player.getX(), 3425, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 1,
				ForceMovement.NORTH));
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(11789));
					player.setNextTile(toTile);
				} else if (stage == 1) {
					swing1(player, object);
					stop();
				}
				player.unlock();
				stage++;
			}
		}, 0, 1);
	}

	/**
	 * 
	 * @param player
	 * @param object
	 */
	public static void swing1(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 85))
			return;
		player.lock();
		final Tile NextTile = new Tile(player.getX(), 3429, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 2, NextTile, 3,
				ForceMovement.NORTH));
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 3) {
					player.setNextTile(NextTile);
					swing2(player, object);
					stop();
				}
				stage++;
			}
		}, 0, 1);
	}

	public static void swing2(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 85))
			return;
		player.lock();
		final Tile LastTile = new Tile(player.getX(), 3432, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 0, LastTile, 1,
				ForceMovement.NORTH));
		player.getSkills().addXp(Skills.AGILITY, 25);
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 2) {
					player.setNextTile(LastTile);
					if (getGnomeStage(player) == 3)
						setGnomeStage(player, 4);
					player.unlock();
					stop();
				}
				stage++;
			}
		}, 0, 1);
	}

	public static void walkBackGnomeRope(final Player player) {
		if (player.getX() != 2483 || player.getY() != 3420
				|| player.getZ() != 2)
			return;
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(2477, 3420, -1, false);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getGlobalPlayerUpdate().setRenderEmote(155);
				} else {
					player.getGlobalPlayerUpdate().setRenderEmote(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY, 7.5);
					player.getPackets().sendGameMessage(
							"You passed the obstacle succesfully.", true);
					player.unlock();
					stop();
				}
			}
		}, 0, 5);
	}

	public static void walkGnomeLog(final Player player) {
		if (player.getX() != 2474 || player.getY() != 3436)
			return;
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(2474, 3429, -1, false);
		player.getPackets().sendGameMessage(
				"You walk carefully across the slippery log...", true);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getGlobalPlayerUpdate().setRenderEmote(155);
				} else {
					player.getGlobalPlayerUpdate().setRenderEmote(-1);
					player.setRunHidden(running);
					setGnomeStage(player, 0);
					player.getSkills().addXp(Skills.AGILITY, 7.5);
					player.getPackets().sendGameMessage(
							"... and make it safely to the other side.", true);
					player.unlock();
					stop();
				}
			}
		}, 0, 6);
	}

	public static void walkGnomeRope(final Player player) {
		if (player.getX() != 2477 || player.getY() != 3420
				|| player.getZ() != 2)
			return;
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(2483, 3420, -1, false);
		EngineTaskManager.schedule(new EngineTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getGlobalPlayerUpdate().setRenderEmote(155);
				} else {
					player.getGlobalPlayerUpdate().setRenderEmote(-1);
					player.setRunHidden(running);
					if (getGnomeStage(player) == 2)
						setGnomeStage(player, 3);
					player.getSkills().addXp(Skills.AGILITY, 7);
					player.getPackets().sendGameMessage(
							"You passed the obstacle succesfully.", true);
					player.unlock();
					stop();
				}
			}
		}, 0, 5);
	}
}
