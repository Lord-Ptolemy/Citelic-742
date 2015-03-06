package com.citelic.game.entity.player.content.controllers.impl.distractions;

import java.util.HashMap;
import java.util.Map;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.FadingScreen;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class SorceressGarden extends Controller {

	public enum Gate {

		WINTER(21709, 1, new Tile(2902, 5470, 0), new Tile(2903, 5470, 0)), SPRING(
				21753, 25, new Tile(2921, 5473, 0), new Tile(2920, 5473, 0)), AUTUMN(
				21731, 45, new Tile(2913, 5462, 0), new Tile(2913, 5463, 0)), SUMMER(
				21687, 65, new Tile(2910, 5481, 0), new Tile(2910, 5480, 0));

		private int objectId;
		private int levelReq;
		private Tile inside, outside;

		private static Map<Integer, Gate> Gates = new HashMap<Integer, Gate>();

		static {
			for (Gate gate : Gate.values()) {
				Gates.put(gate.getObjectId(), gate);
			}
		}

		public static Gate forId(int id) {
			return Gates.get(id);
		}

		/**
		 * 
		 * @param player
		 *            the Player
		 * @param objectId
		 *            Object id
		 * @param lvlReq
		 *            Level required for entrance
		 * @param toTile
		 *            Where the player will be spawned
		 */
		public static void handleGates(Player player, int objectId, int lvlReq,
				Tile toTile) {
			if (lvlReq > player.getSkills().getLevelForXp(Skills.THIEVING))
				player.getDialogueManager().startDialogue(
						"SimpleMessage",
						"You need " + objectId
								+ " thieving level to pick this gate.");
			player.setNextTile(toTile);
		}

		private Gate(int objectId, int lvlReq, Tile inside, Tile outside) {
			this.objectId = objectId;
			this.levelReq = lvlReq;
			this.inside = inside;
			this.outside = outside;
		}

		public Tile getInsideTile() {
			return inside;
		}

		public int getLeveLReq() {
			return levelReq;
		}

		public int getObjectId() {
			return objectId;
		}

		public Tile getOutsideTile() {
			return outside;
		}
	}

	/**
	 * Checks if the player is at Autumn Garden or not
	 */
	public static boolean inAutumnGarden(Tile tile) {
		return ((tile.getX() >= 2896 && tile.getX() <= 2919) && (tile.getY() >= 5446 && tile
				.getY() <= 5462));
	}

	/**
	 * Checks if the player is in any garden
	 */
	public static boolean inGarden(Tile tile) {
		return ((tile.getX() >= 2880 && tile.getX() <= 2943) && (tile.getY() >= 5440 && tile
				.getY() <= 5503));
	}

	public static boolean inGardens(Tile tile) {
		return inWinterGarden(tile) || inAutumnGarden(tile)
				|| inSpringGarden(tile) || inSummerGarden(tile);
	}

	/**
	 * Checks if the player is at Summer Garden or not
	 */
	public static boolean inSpringGarden(Tile tile) {
		return ((tile.getX() >= 2921 && tile.getX() <= 2937) && (tile.getY() >= 5456 && tile
				.getY() <= 5479));
	}

	/**
	 * Checks if the player is at Spring Garden or not
	 */
	public static boolean inSummerGarden(Tile tile) {
		return ((tile.getX() >= 2904 && tile.getX() <= 2927) && (tile.getY() >= 5481 && tile
				.getY() <= 5497));
	}

	/**
	 * Checks if the player is at Winter Garden or not
	 */
	public static boolean inWinterGarden(Tile tile) {
		return ((tile.getX() >= 2886 && tile.getX() <= 2902) && (tile.getY() >= 5464 && tile
				.getY() <= 5487));
	}

	@Override
	public boolean login() {
		if (!inGarden(player))
			return true;
		return false;
	}

	@Override
	public boolean logout() {
		if (!inGarden(player))
			return true;
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 21764) {
			player.setNextAnimation(new Animation(5796));
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					player.reset();
					Magic.sendNormalTeleportSpell(player, 0, 0, new Tile(3321,
							3141, 0));
					removeController();
				}

			}, 1);
			return false;
		} else if (object.getId() == 21768) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(10846, 1);
			EngineTaskManager.schedule(new EngineTask() {
				int i = 0;

				@Override
				public void run() {
					if (i == 0) {
						FadingScreen.fade(player, new Runnable() {
							@Override
							public void run() {
								player.getPackets().sendMiniMapStatus(0);
								player.getPackets().sendConfig(1241, 0);
							}

						});
					} else if (i == 2) {
						player.reset();
						player.getPackets()
								.sendGameMessage(
										"An elemental force enamating from the garden teleports you away.");
						player.setNextTile(new Tile(2913, 5467, 0));
					}
					i++;
				}

			}, 0, 1);
			return false;
		} else if (object.getId() == 21769) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(10847, 1);
			EngineTaskManager.schedule(new EngineTask() {
				int i = 0;

				@Override
				public void run() {
					if (i == 0) {
						FadingScreen.fade(player, new Runnable() {
							@Override
							public void run() {
								player.getPackets().sendMiniMapStatus(0);
								player.getPackets().sendConfig(1241, 0);
							}

						});
					} else if (i == 2) {
						player.reset();
						player.getPackets()
								.sendGameMessage(
										"An elemental force enamating from the garden teleports you away.");
						player.setNextTile(new Tile(2907, 5470, 0));
					}
					i++;
				}

			}, 0, 1);
			return false;
		} else if (object.getId() == 21766) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(10845, 1);
			EngineTaskManager.schedule(new EngineTask() {
				int i = 0;

				@Override
				public void run() {
					if (i == 0) {
						FadingScreen.fade(player, new Runnable() {
							@Override
							public void run() {
								player.getPackets().sendMiniMapStatus(0);
								player.getPackets().sendConfig(1241, 0);
							}

						});
					} else if (i == 2) {
						player.reset();
						player.getPackets()
								.sendGameMessage(
										"An elemental force enamating from the garden teleports you away.");
						player.setNextTile(new Tile(2910, 5476, 0));
					}
					i++;
				}

			}, 0, 1);
			return false;
		} else if (object.getId() == 21767) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(10844, 1);
			EngineTaskManager.schedule(new EngineTask() {
				int i = 0;

				@Override
				public void run() {
					if (i == 0) {
						FadingScreen.fade(player, new Runnable() {
							@Override
							public void run() {
								player.getPackets().sendMiniMapStatus(0);
								player.getPackets().sendConfig(1241, 0);
							}

						});
					} else if (i == 2) {
						player.reset();
						player.getPackets()
								.sendGameMessage(
										"An elemental force emanating from the garden teleports you away.");
						player.setNextTile(new Tile(2916, 5473, 0));
					}
					i++;
				}

			}, 0, 1);
			return false;
		} else if (object.getDefinitions().name.toLowerCase().contains("gate")) {
			final Gate gate = Gate.forId(object.getId());
			if (gate != null) {
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						Gate.handleGates(player, gate.getObjectId(), gate
								.getLeveLReq(),
								inGardens(player) ? gate.getOutsideTile()
										: gate.getInsideTile());
						stop();
					}
				}, 1);
				return false;
			}
		} else if (object.getDefinitions().name.toLowerCase().equals("herbs")) {
			player.setNextAnimation(new Animation(827));
			// for (int i = 0; i < 2; i++)

			return false;
		}
		return true;
	}

	@Override
	public void start() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new Tile(new Tile(2916,
				5475, 0), 2));
	}

}