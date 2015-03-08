package com.citelic.game.entity.player.content.actions.skills.agility;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class ApeAtollAgility {

	public static void climbDownTropicalTree(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock();
		final Tile toTile = new Tile(2769, 2746, 1);
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 7,
				ForceMovement.NORTH));
		player.getPlayerAppearance().setRenderEmote(760);
		player.getPackets().sendGameMessage("You climb the vine...");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"..And make it carefully to the end of it.");
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 55);
				player.getPlayerAppearance().setRenderEmote(-1);
				player.unlock();
				stop();
			}
		}, 2);
	}

	public static void climbDownVine(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final Tile toTile = new Tile(player.getX(), player.getY(), 0);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage("You climb down the vine.");
				player.setNextAnimation(new Animation(1381));
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 36);
				stop();
			}
		}, 1);
	}

	public static void climbUpSkullSlope(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final Tile toTile = new Tile(2743, 2741, 0);
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 3,
				ForceMovement.WEST));
		player.getPlayerAppearance().setRenderEmote(739);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"You climb up the skull slope.");
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 45);
				player.getPlayerAppearance().setRenderEmote(-1);
				stop();
			}
		}, 2);
	}

	public static void climbUpTropicalTree(final Player player,
			GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final Tile toTile = new Tile(2752, 2742, 2);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage("You climb up the tree...");
				player.setNextAnimation(new Animation(1382));
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 25);
				stop();
			}
		}, 1);
	}

	public static void crossMonkeyBars(final Player player,
			final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final Tile toTile = new Tile(2747, 2741, 0);
		final Tile toTile2 = new Tile(2747, 2741, 2);
		player.setNextForceMovement(new ForceMovement(player, 0, toTile2, 4,
				ForceMovement.WEST));
		player.getPlayerAppearance().setRenderEmote(744);
		player.getPackets().sendGameMessage("You jump to the monkey bars...");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"..And made it carefully to the other side.");
				player.getPlayerAppearance().setRenderEmote(-1);
				player.setNextTile(toTile);
				player.getSkills().addXp(Skills.AGILITY, 35);
				stop();
			}
		}, 3);
	}

	public static void jumpToSteppingStone(final Player player,
			GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final Tile toTile = new Tile(object.getX(), object.getY(),
				object.getZ());
		final Tile toTile2 = new Tile(player.getX() == 2755 ? 2753 : 2755,
				2742, object.getZ());
		final Tile WaterTile = new Tile(2756, 2746, object.getZ());
		final Tile Land = new Tile(2757, 2746, object.getZ());
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"You jump to the stepping stone...");
				player.setNextAnimation(new Animation(1381));
				player.setNextTile(toTile);
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						if (Utilities.random(5) == 0) {
							player.setNextAnimation(new Animation(1381));
							player.getPackets().sendGameMessage(
									"..And accidently fall to the water.");
							player.applyHit(new Hit(player, Utilities
									.random(200), HitLook.REGULAR_DAMAGE));
							player.setNextForceMovement(new ForceMovement(
									player, 0, WaterTile, 3,
									ForceMovement.NORTH));
							player.getPlayerAppearance().setRenderEmote(741);
							EngineTaskManager.schedule(new EngineTask() {
								@Override
								public void run() {
									player.getPlayerAppearance()
											.setRenderEmote(-1);
									player.setNextTile(Land);
									stop();
								}
							}, 1);
							stop();
						} else {
							player.setNextAnimation(new Animation(1381));
							player.setNextTile(toTile2);
							player.getSkills().addXp(Skills.AGILITY, 15);
							player.getPackets()
									.sendGameMessage(
											"..And made it carefully to the other side.");
							stop();
						}
					}
				}, 1);
				stop();
			}
		}, 1);
	}

	public static void swingRope(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getX() == 2756)
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets()
					.sendGameMessage(
							"You need to be a ninja monkey to be able to do this agility.");
			return;
		}
		player.lock(4);
		player.setNextAnimation(new Animation(1392));
		//Engine.sendObjectAnimation(player, object, new Animation(497));
		final Tile toTile = new Tile(2756, 2731, object.getZ());
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
				ForceMovement.EAST));
		player.getSkills().addXp(Skills.AGILITY, 22);
		player.getPackets()
				.sendGameMessage("You skilfully swing across.", true);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextTile(toTile);
				stop();
			}
		}, 1);
	}
}