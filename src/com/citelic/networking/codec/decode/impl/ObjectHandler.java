package com.citelic.networking.codec.decode.impl;

import com.citelic.GameConstants;
import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.game.ForceMovement;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.CowMilkingAction;
import com.citelic.game.entity.player.content.actions.WaterFilling;
import com.citelic.game.entity.player.content.actions.combat.PlayerCombat;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.agility.Agility;
import com.citelic.game.entity.player.content.actions.skills.agility.ApeAtollAgility;
import com.citelic.game.entity.player.content.actions.skills.agility.BarbarianOutpostAgility;
import com.citelic.game.entity.player.content.actions.skills.agility.GnomeAgility;
import com.citelic.game.entity.player.content.actions.skills.agility.WildernessCourseAgility;
import com.citelic.game.entity.player.content.actions.skills.cooking.Cooking;
import com.citelic.game.entity.player.content.actions.skills.cooking.Cooking.Cookables;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.Dungeoneering;
import com.citelic.game.entity.player.content.actions.skills.farming.PatchConstants;
import com.citelic.game.entity.player.content.actions.skills.farming.PatchConstants.WorldPatches;
import com.citelic.game.entity.player.content.actions.skills.firemaking.Bonfire;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction.HunterEquipment;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction.HunterNPC;
import com.citelic.game.entity.player.content.actions.skills.hunter.Hunter;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.actions.skills.mining.EssenceMining;
import com.citelic.game.entity.player.content.actions.skills.mining.EssenceMining.EssenceDefinitions;
import com.citelic.game.entity.player.content.actions.skills.mining.GemMining;
import com.citelic.game.entity.player.content.actions.skills.mining.Mining;
import com.citelic.game.entity.player.content.actions.skills.mining.Mining.RockDefinitions;
import com.citelic.game.entity.player.content.actions.skills.mining.MiningBase;
import com.citelic.game.entity.player.content.actions.skills.prayer.BonesOnAltar;
import com.citelic.game.entity.player.content.actions.skills.prayer.BonesOnAltar.Bones;
import com.citelic.game.entity.player.content.actions.skills.runecrafting.RuneCrafting;
import com.citelic.game.entity.player.content.actions.skills.runecrafting.SiphionActionNodes;
import com.citelic.game.entity.player.content.actions.skills.smithing.JewelrySmithing;
import com.citelic.game.entity.player.content.actions.skills.smithing.Smithing.ForgingBar;
import com.citelic.game.entity.player.content.actions.skills.smithing.Smithing.ForgingInterface;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.actions.skills.thieving.Thieving;
import com.citelic.game.entity.player.content.actions.skills.woodcutting.Woodcutting;
import com.citelic.game.entity.player.content.actions.skills.woodcutting.Woodcutting.TreeDefinitions;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightCaves;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightKiln;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightPits;
import com.citelic.game.entity.player.content.controllers.impl.distractions.WarriorsGuild;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.Barrows;
import com.citelic.game.entity.player.content.controllers.impl.distractions.crucible.Crucible;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.GodWars;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.Lander;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.CastleWars;
import com.citelic.game.entity.player.content.dialogue.impl.npcs.MiningGuildDwarf;
import com.citelic.game.entity.player.content.miscellaneous.ClueScrolls;
import com.citelic.game.entity.player.content.miscellaneous.CrystalChest;
import com.citelic.game.entity.player.content.miscellaneous.PartyRoom;
import com.citelic.game.entity.player.content.miscellaneous.Pickables;
import com.citelic.game.entity.player.content.transportation.FairyRing;
import com.citelic.game.entity.player.content.transportation.WildernessObelisks;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.objects.OwnedObjectManager;
import com.citelic.game.map.objects.impl.Door;
import com.citelic.game.map.pathfinding.RouteEvent;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.streaming.InputStream;
import com.citelic.utility.Logger;
import com.citelic.utility.PkRank;
import com.citelic.utility.ShopsHandler;
import com.citelic.utility.Utilities;

public final class ObjectHandler {

	private static boolean handleGate(Player player, GameObject object) {
		if (Engine.isSpawnedObject(object))
			return false;
		if (object.getRotation() == 0) {

			boolean south = true;
			GameObject otherDoor = Engine.getObject(new Tile(object.getX(),
					object.getY() + 1, object.getZ()), object.getType());
			if (otherDoor == null
					|| otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object
							.getDefinitions().name)) {
				otherDoor = Engine.getObject(
						new Tile(object.getX(), object.getY() - 1, object
								.getZ()), object.getType());
				if (otherDoor == null
						|| otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name
								.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			GameObject openedDoor1 = new GameObject(object.getId(),
					object.getType(), object.getRotation() + 1, object.getX(),
					object.getY(), object.getZ());
			GameObject openedDoor2 = new GameObject(otherDoor.getId(),
					otherDoor.getType(), otherDoor.getRotation() + 1,
					otherDoor.getX(), otherDoor.getY(), otherDoor.getZ());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.moveLocation(-1, 0, 0);
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor2.setRotation(3);
			}

			if (Engine.removeTemporaryObject(object, 60000, true)
					&& Engine.removeTemporaryObject(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				Engine.spawnTemporaryObject(openedDoor1, 60000, true);
				Engine.spawnTemporaryObject(openedDoor2, 60000, true);
				return true;
			}
		} else if (object.getRotation() == 2) {

			boolean south = true;
			GameObject otherDoor = Engine.getObject(new Tile(object.getX(),
					object.getY() + 1, object.getZ()), object.getType());
			if (otherDoor == null
					|| otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object
							.getDefinitions().name)) {
				otherDoor = Engine.getObject(
						new Tile(object.getX(), object.getY() - 1, object
								.getZ()), object.getType());
				if (otherDoor == null
						|| otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name
								.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			GameObject openedDoor1 = new GameObject(object.getId(),
					object.getType(), object.getRotation() + 1, object.getX(),
					object.getY(), object.getZ());
			GameObject openedDoor2 = new GameObject(otherDoor.getId(),
					otherDoor.getType(), otherDoor.getRotation() + 1,
					otherDoor.getX(), otherDoor.getY(), otherDoor.getZ());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			}
			if (Engine.removeTemporaryObject(object, 60000, true)
					&& Engine.removeTemporaryObject(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				Engine.spawnTemporaryObject(openedDoor1, 60000, true);
				Engine.spawnTemporaryObject(openedDoor2, 60000, true);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			GameObject otherDoor = Engine.getObject(new Tile(object.getX() - 1,
					object.getY(), object.getZ()), object.getType());
			if (otherDoor == null
					|| otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object
							.getDefinitions().name)) {
				otherDoor = Engine.getObject(
						new Tile(object.getX() + 1, object.getY(), object
								.getZ()), object.getType());
				if (otherDoor == null
						|| otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name
								.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			GameObject openedDoor1 = new GameObject(object.getId(),
					object.getType(), object.getRotation() + 1, object.getX(),
					object.getY(), object.getZ());
			GameObject openedDoor2 = new GameObject(otherDoor.getId(),
					otherDoor.getType(), otherDoor.getRotation() + 1,
					otherDoor.getX(), otherDoor.getY(), otherDoor.getZ());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.setRotation(0);
				openedDoor1.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			}
			if (Engine.removeTemporaryObject(object, 60000, true)
					&& Engine.removeTemporaryObject(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				Engine.spawnTemporaryObject(openedDoor1, 60000, true);
				Engine.spawnTemporaryObject(openedDoor2, 60000, true);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			GameObject otherDoor = Engine.getObject(new Tile(object.getX() - 1,
					object.getY(), object.getZ()), object.getType());
			if (otherDoor == null
					|| otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object
							.getDefinitions().name)) {
				otherDoor = Engine.getObject(
						new Tile(object.getX() + 1, object.getY(), object
								.getZ()), object.getType());
				if (otherDoor == null
						|| otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name
								.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			GameObject openedDoor1 = new GameObject(object.getId(),
					object.getType(), object.getRotation() + 1, object.getX(),
					object.getY(), object.getZ());
			GameObject openedDoor2 = new GameObject(otherDoor.getId(),
					otherDoor.getType(), otherDoor.getRotation() + 1,
					otherDoor.getX(), otherDoor.getY(), otherDoor.getZ());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			}
			if (Engine.removeTemporaryObject(object, 60000, true)
					&& Engine.removeTemporaryObject(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				Engine.spawnTemporaryObject(openedDoor1, 60000, true);
				Engine.spawnTemporaryObject(openedDoor2, 60000, true);
				return true;
			}
		}
		return false;
	}

	public static void handleItemOnObject(final Player player,
			final GameObject object, final int interfaceId, final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.faceObject(object);
				if (!player.getControllerManager().handleItemOnObject(object,
						item))
					return;
				if (itemId == player.getDwarfCannon().ITEMS[4]) {
					player.getDwarfCannon().loadDwarfCannon(object);
				} else if (itemId == 1438 && object.getId() == 2452) {
					RuneCrafting.enterAirAltar(player);
				} else if (itemId == 1440 && object.getId() == 2455) {
					RuneCrafting.enterEarthAltar(player);
				} else if (itemId == 1442 && object.getId() == 2456) {
					RuneCrafting.enterFireAltar(player);
				} else if (itemId == 1444 && object.getId() == 2454) {
					RuneCrafting.enterWaterAltar(player);
				} else if (itemId == 1446 && object.getId() == 2457) {
					RuneCrafting.enterBodyAltar(player);
				} else if (itemId == 1448 && object.getId() == 2453) {
					RuneCrafting.enterMindAltar(player);
				} else if (object.getId() == 733 || object.getId() == 64729) {
					player.setNextAnimation(new Animation(PlayerCombat
							.getWeaponAttackEmote(-1, 0)));
					slashWeb(player, object);
				} else if (item.getId() == PatchConstants.SPADE) {
					WorldPatches patch = WorldPatches.forId(object.getId());
					if (patch != null && player.getFarming() != null) {
						player.getFarming().patches[patch.getArrayIndex()]
								.handleClear(player);
						return;
					}
				} else if (item.getDefinitions().getName().toLowerCase()
						.contains("plant cure")) {
					WorldPatches patch = WorldPatches.forId(object.getId());
					if (patch != null && player.getFarming() != null) {
						player.getFarming().patches[patch.getArrayIndex()]
								.handleCuring(player, itemId);
						return;
					}
				} else if (item.getDefinitions().getName().toLowerCase()
						.contains("compost")) {
					WorldPatches patch = WorldPatches.forId(object.getId());
					if (patch != null && player.getFarming() != null) {
						player.getFarming().patches[patch.getArrayIndex()]
								.handleCompost(player, itemId);
						return;
					}
				} else if (item.getDefinitions().getName().toLowerCase()
						.contains("watering can")) {
					WorldPatches patch = WorldPatches.forId(object.getId());
					if (patch != null && player.getFarming() != null) {
						player.getFarming().patches[patch.getArrayIndex()]
								.handleWatering(player, itemId);
						return;
					}
				} else if (item.getDefinitions().getName().toLowerCase()
						.contains("seed")
						|| item.getDefinitions().getName().toLowerCase()
								.contains("mushroom spore")
						|| item.getDefinitions().getName().toLowerCase()
								.contains("acorn")
						|| item.getDefinitions().getName().toLowerCase()
								.contains("sapling")) {
					WorldPatches patch = WorldPatches.forId(object.getId());
					if (patch != null && player.getFarming() != null) {
						player.getFarming().patches[patch.getArrayIndex()]
								.handlePlanting(player, itemId);
						return;
					}
				} else if (object.getId() == 48803 && itemId == 954) {
					if (player.isKalphiteLairSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLair();
				} else if (object.getDefinitions().name.toLowerCase().contains(
						"furnace")) {
					if (item.getId() == 2357) {
						JewelrySmithing.openInterface(player);
					}
				} else if (object.getId() == 48802 && itemId == 954) {
					if (player.isKalphiteLairEntranceSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLairEntrance();
				} else if (object.getId() == 409) {
					Bones bone = BonesOnAltar.isGood(item);
					if (bone != null) {
						player.getDialogueManager().startDialogue("PrayerD",
								bone, object);
						return;
					} else {
						player.getPackets().sendGameMessage(
								"Nothing interesting happens.");
						return;
					}
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "fountain":
					case "well":
					case "sink":
						if (WaterFilling.isFilling(player, itemId, false))
							return;
						break;
					case "anvil":
						ForgingBar bar = ForgingBar.forId(itemId);
						if (bar != null)
							ForgingInterface.sendSmithingInterface(player, bar);
						break;
					case "fire":
						if (objectDef.containsOption(4, "Add-logs")
								&& Bonfire.addLog(player, object, item))
							return;
					case "range":
					case "cooking range":
					case "stove":
						Cookables cook = Cooking.isCookingSkill(item);
						if (cook != null) {
							player.getDialogueManager().startDialogue(
									"CookingD", cook, object);
							return;
						}
						player.getDialogueManager()
								.startDialogue(
										"SimpleMessage",
										"You can't cook that on a "
												+ (objectDef.name
														.equals("Fire") ? "fire"
														: "range") + ".");
						break;
					default:
						player.getPackets().sendGameMessage(
								"Nothing interesting happens.");
						break;
					}
					if (GameConstants.DEBUG)
						System.out.println("Item on object: " + object.getId());
				}
			}
		}, true));
	}

	private static boolean handleLadder(Player player, GameObject object,
			int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getZ() == 3)
				return false;
			if (object.getId() == 39191)
				return false;
			if (object.getId() == 10708) {
				player.useStairs(828, new Tile(2005, 4431, 1), 1, 2);
				return false;
			}
			if (object.getId() == 10707)
				player.useStairs(828, new Tile(2010, 4431, 1), 1, 2);
			else
				player.useStairs(828, new Tile(player.getX(), player.getY(),
						player.getZ() + 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getZ() == 0)
				return false;
			if (object.getId() == 10708) {
				player.useStairs(828, new Tile(2005, 4431, 0), 1, 2);
				return false;
			}
			if (object.getId() == 10707)
				player.useStairs(828, new Tile(2010, 4431, 0), 1, 2);
			else
				player.useStairs(828, new Tile(player.getX(), player.getY(),
						player.getZ() - 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getZ() == 3 || player.getZ() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbEmoteStairs",
					new Tile(player.getX(), player.getY(), player.getZ() + 1),
					new Tile(player.getX(), player.getY(), player.getZ() - 1),
					"Climb up the ladder.", "Climb down the ladder.", 828);
		} else
			return false;
		return true;
	}

	public static void handleOption(final Player player, InputStream stream,
			int option) {
		if (!player.isActive() || !player.clientHasLoadedMapRegion()
				|| player.isDead())
			return;
		long currentTime = Utilities.currentTimeMillis();
		if (player.getLockDelay() >= currentTime
				|| player.getEmotesManager().getNextEmoteEnd() >= currentTime)
			return;
		final int id = stream.readIntV2();
		int y = stream.readShort();
		int x = stream.readShort128();
		boolean forceRun = stream.read128Byte() == 1;
		int rotation = 0;
		if (player.isAtDynamicRegion()) {
			rotation = Engine.getRotation(player.getZ(), x, y);
			if (rotation == 1) {
				ObjectDefinitions defs = ObjectDefinitions
						.getObjectDefinitions(id);
				y += defs.getSizeY() - 1;
			} else if (rotation == 2) {
				ObjectDefinitions defs = ObjectDefinitions
						.getObjectDefinitions(id);
				x += defs.getSizeY() - 1;
			}
		}
		final Tile tile = new Tile(x, y, player.getZ());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		GameObject mapObject = Engine.getRegion(regionId).getObject(id, tile);
		if (mapObject == null || mapObject.getId() != id)
			return;
		if (player.isAtDynamicRegion()
				&& Engine.getRotation(player.getZ(), x, y) != 0) { // temp
			// fix
			ObjectDefinitions defs = ObjectDefinitions.getObjectDefinitions(id);
			if (defs.getSizeX() > 1 || defs.getSizeY() > 1) {
				for (int xs = 0; xs < defs.getSizeX() + 1
						&& (mapObject == null || mapObject.getId() != id); xs++) {
					for (int ys = 0; ys < defs.getSizeY() + 1
							&& (mapObject == null || mapObject.getId() != id); ys++) {
						tile.setLocation(x + xs, y + ys, tile.getZ());
						mapObject = Engine.getRegion(regionId).getObject(id,
								tile);
					}
				}
			}
			if (mapObject == null || mapObject.getId() != id)
				return;
		}
		final GameObject object = !player.isAtDynamicRegion() ? mapObject
				: new GameObject(id, mapObject.getType(),
						(mapObject.getRotation() + rotation % 4), x, y,
						player.getZ());
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		switch (option) {
		case 1:
			handleOption1(player, object);
			break;
		case 2:
			handleOption2(player, object);
			break;
		case 3:
			handleOption3(player, object);
			break;
		case 4:
			handleOption4(player, object);
			break;
		case 5:
			handleOption5(player, object);
			break;
		case -1:
			handleOptionExamine(player, object);
			break;
		}
	}

	private static void handleOption1(final Player player,
			final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		final int x = object.getX();
		final int y = object.getY();
		if (id == 67044) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					// unreachable objects exception
					player.faceObject(object);
					player.useStairs(-1, new Tile(2927, 3406, 0), 0, 1);
				}
			}, true));
			return;
		}
		if (id == 75463) {
		    if (object.withinDistance(player, 7)) {
			final boolean withinArmadyl = GodWars.inArmadylPrepare(player);
			final Tile tile = new Tile(2872, withinArmadyl ? 5280 : 5272, 0);
			EngineTaskManager.schedule(new EngineTask() {

			    int ticks = 0;

			    @Override
			    public void run() {
				ticks++;
				if (ticks == 1) {
				    player.setNextAnimation(new Animation(827));
				    player.setNextFaceTile(tile);
				    player.lock();
				} else if (ticks == 3)
				    player.setNextAnimation(new Animation(385));
				else if (ticks == 5) {
				    player.setNextAnimation(new Animation(16635));
				} else if (ticks == 6) {
				    player.getAppearence().setHidden(true);
				    Engine.sendProjectile(player, tile, 2699, 18, 18, 20, 50, 175, 0);
				    player.setNextForceMovement(new ForceMovement(player, 1, tile, 6, withinArmadyl ? ForceMovement.NORTH : ForceMovement.SOUTH));
				} else if (ticks == 9) {
				    player.getAppearence().setHidden(false);
				    player.setNextAnimation(new Animation(16672));
				    player.setNextTile(tile);
				    player.unlock();
				    stop();
				    return;
				}
			    }
			}, 0, 1);
		    }
		    return;
		} if (id == 43529 && player.getX() >= 2484 && player.getY() >= 3417
				&& player.getX() <= 2487 && player.getX() <= 3422
				&& player.getZ() == 3)
			GnomeAgility.preSwing(player, object);
		if (ClueScrolls.objectSpot(player, object))
			return;
		if (SiphionActionNodes.siphion(player, object))
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControllerManager().processObjectClick1(object))
					return;
				if (CastleWars.handleObjects(player, id))
					return;
				if (Dungeoneering.useStairs(player, id))
					return;
				if (Pickables.handlePickable(player, object))
					return;
				if (id >= 65616 && id <= 65622) {
					WildernessObelisks.preTeleport(player, object);
				}
				if (player.getLodeStones().processLodestone(object)) {
					return;
				}
				WorldPatches patch = WorldPatches.forId(object.getId());
				if (patch != null && player.getFarming() != null) {
					player.getFarming().patches[patch.getArrayIndex()]
							.handleOption1(player);
					return;
				}
				HunterNPC hunterNpc = HunterNPC.forObjectId(id);
				if (hunterNpc != null) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(hunterNpc.getEquipment()
								.getPickUpAnimation());
						player.getInventory().getItems()
								.addAll(hunterNpc.getItems());
						player.getInventory().addItem(
								hunterNpc.getEquipment().getId(), 1);
						player.getSkills().addXp(Skills.HUNTER,
								hunterNpc.getXp());
					} else {
						player.getPackets().sendGameMessage(
								"This isn't your trap.");
					}
				} else if (id == HunterEquipment.BOX.getObjectId()
						|| id == 19192) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(new Animation(5208));
						player.getInventory().addItem(
								HunterEquipment.BOX.getId(), 1);
					} else
						player.getPackets().sendGameMessage(
								"This isn't your trap.");
				}
				if (id == HunterEquipment.BRID_SNARE.getObjectId()
						|| id == 19174) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(new Animation(5207));
						player.getInventory().addItem(
								HunterEquipment.BRID_SNARE.getId(), 1);
					} else
						player.getPackets().sendGameMessage(
								"This isn't your trap.");
				} 
				switch (id) {
				case 48496:
					player.getDialogueManager().startDialogue("FloorSelection",
							player);
					break;
				case 1317:
					player.getDialogueManager().startDialogue(!player.isLocked() ? "SpiritTree" : null);
					break;
				case 6:
					player.getDwarfCannon().preRotationSetup(object);
					break;
				case 15791:
					if (object.getX() == 3829)
						player.useStairs(-1, new Tile(3830, 9461, 0), 1, 2);
					else if (object.getX() == 3814)
						player.useStairs(-1, new Tile(3815, 9461, 0), 1, 2);
					player.getControllerManager().startController(
							"UndergroundDungeon", false, true);
					break;
				case 6898:
					player.setNextAnimation(new Animation(10578));
					player.useStairs(-1, object, 1, 2);
					player.useStairs(10579, new Tile(3221, 9618, 0), 1, 2);
					player.getControllerManager().startController(
							"UndergroundDungeon", false, true);
					player.getPackets().sendGameMessage(
							"You squeeze through the hole.");
					break;
				case 36002:
					player.getControllerManager().startController(
							"UndergroundDungeon", true, false);
					player.useStairs(833, new Tile(3206, 9379, 0), 1, 2);
					break;
				case 24991:
					player.getControllerManager().startController("PuroPuro");
					break;
				case 2406:
					if (FairyRing.checkAll(player))
						player.useStairs(-1, new Tile(2452, 4473, 0), 1, 2);
					break;
				case 16944:
					FairyRing.openRingInterface(player, object, false);
					break;
				case 11195:
				case 11194:
				case 11364:
					player.getActionManager().setAction(new GemMining(object));
					break;
				case 42793:
					if (x == 2737 && y == 3729)
						player.useStairs(-1, new Tile(3421, 5511, 0), 0, 1);
					break;
				case 42891:
					player.useStairs(-1, new Tile(2736, 3731, 0), 0, 1);
					break;
				case 52626:
				case 52627:
					player.addWalkSteps(player.getX(), 2937, -1, false);
					break;
				case 52628:
				case 52629:
					player.addWalkSteps(player.getX(), 2940, -1, false);
					break;
				case 52624:
				case 52625:
					player.addWalkSteps(3754, player.getY(), -1, false);
					break;
				case 52622:
				case 52623:
					player.addWalkSteps(3751, player.getY(), -1, false);
					break;
				case 42794:
					player.useStairs(-1, new Tile(object.getX(),
							object.getY() + 7, 0), 0, 1);
					break;
				case 42795:
					player.useStairs(-1, new Tile(object.getX(),
							object.getY() - 6, 0), 0, 1);
					break;
				case 48188:
					player.useStairs(-1, new Tile(3435, 5646, 0), 0, 1);
					break;
				case 48189:
					player.useStairs(-1, new Tile(3509, 5515, 0), 0, 1);
					break;
				case 12328:
					player.useStairs(3527, new Tile(3012, 9275, 0), 5, 6);
					player.setNextForceMovement(new ForceMovement(player, 3,
							object, 2, ForceMovement.WEST));
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							player.setNextFaceTile(new Tile(3012, 9274, 0));
							player.setNextAnimation(new Animation(11043));
							player.getControllerManager().startController(
									"JadinkoLair");
						}
					}, 4);
					break;
				case 12290: // start of jadinkos
					player.setFavorPoints(1 - player.getFavorPoints());
				    player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.STRAIT_VINE));
					break;
				case 12277:
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.STRAIT_VINE_COLLECTABLE));
					break;
				case 12291:
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MUTATED_VINE));
					break;
				case 12274:
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURLY_VINE));
					break;
				case 12279:
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURLY_VINE_COLLECTABLE));
					break;
				case 15653:
					if (Engine.isSpawnedObject(object)
							|| !WarriorsGuild.canEnter(player))
						return;
					player.lock(2);
					GameObject opened = new GameObject(object.getId(), object
							.getType(), object.getRotation() - 1,
							object.getX(), object.getY(), object.getZ());
					Engine.spawnObjectTemporary(opened, 600);
					player.addWalkSteps(object.getX() - 1, player.getY(), 2,
							false);
					break;
				case 77834:
					player.getDialogueManager().startDialogue("KBDArtifact");
					break;
				case 24360:
					player.setNextTile(new Tile(3190, 9833, 0));
					break;
				case 24365:
					player.setNextTile(new Tile(3188, 3433, 0));
					break;
				case 66796:
					player.setNextTile(new Tile(2840, 3534, 2));
					break;
				case 19205:
					Hunter.createLoggedObject(player, object, true);
					break;
				case 2878:
				case 2879: {
					player.getDialogueManager()
					.startDialogue(
							"SimpleMessage",
							"You step into the pool of sparkling water. You feel the energy rush through your veins.");
			final boolean isLeaving = id == 2879;
			final Tile tile = isLeaving ? new Tile(2509, 4687, 0)
					: new Tile(2542, 4720, 0);
			player.setNextForceMovement(new ForceMovement(player, 1,
					tile, 2, isLeaving ? ForceMovement.SOUTH
							: ForceMovement.NORTH));
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					player.setNextAnimation(new Animation(13842));
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.setNextTile(isLeaving ? new Tile(
									2542, 4718, 0) : new Tile(2509,
									4689, 0));
						}
					}, 2);
				}
			});
				}
					break;
				case 52860:
					if (player.getSkills().getLevelForXp(Skills.MINING) < 75) {
						player.getPackets()
								.sendGameMessage(
										"You need atleast 75 mining to enter that dungeon.");
						return;
					}
					player.setNextTile(new Tile(1181, 4515, 0));
					break;
				case 52872:
					player.setNextTile(new Tile(3298, 3307, 0));
					break;
				case 16543:
					if (player.getSkills().getLevelForXp(Skills.THIEVING) < 85) {
						player.getPackets()
								.sendGameMessage(
										"You need atleast 85 thieving to enter the pyramid.");
						return;
					}
					player.getControllerManager().startController(
							"PyramidPlunder");
					break;
				case 66876:
					player.useStairs(-1, new Tile(2292, 4516, 0), 1, 2);
					break;
				case 67002:
					player.useStairs(-1, new Tile(2876, 3502, 0), 1, 2);
					break;
				case 66533:
					player.useStairs(-1, new Tile(2208, 4364, 0), 1, 2);
					break;
				case 66534:
					player.useStairs(-1, new Tile(2878, 3573, 0), 1, 2);
					break;
				case 42220:
					player.useStairs(-1, new Tile(3082, 3475, 0), 0, 1);
					break;
				case 67043:
					player.useStairs(-1, new Tile(2219, 4532, 0), 0, 1);
					break;
				case 66991:
					player.useStairs(-1, new Tile(2885, 9795, 0), 1, 2);
					break;
				case 74864:
					player.useStairs(-1, new Tile(2885, 3395, 0), 1, 2);
					break;
				case 9294: {
					if (!Agility.hasLevel(player, 80))
						return;
					final boolean isRunning = player.getRun();
					final boolean isSouth = player.getY() > 9813;
					final Tile tile = isSouth ? new Tile(2878, 9812, 0)
							: new Tile(2881, 9814, 0);
					player.setRun(true);
					player.addWalkSteps(isSouth ? 2881 : 2877, isSouth ? 9814
							: 9812);
					EngineTaskManager.schedule(new EngineTask() {
						int ticks = 0;

						@Override
						public void run() {
							ticks++;
							if (ticks == 2)
								player.setNextFaceTile(object);
							else if (ticks == 3) {
								player.setNextAnimation(new Animation(1995));
								player.setNextForceMovement(new ForceMovement(
										player, 0, tile, 4,
										Utilities.getFaceDirection(
												object.getX() - player.getX(),
												object.getY() - player.getY())));
							} else if (ticks == 4)
								player.setNextAnimation(new Animation(1603));
							else if (ticks == 7) {
								player.setNextTile(tile);
								player.setRun(isRunning);
								stop();
								return;
							}
						}
					}, 0, 0);
				}
					break;
				case 67346:
					if (x == 2908 && y == 3512)
						player.useStairs(-1, new Tile(2912, 3514, 2), 0, 1);
					break;
				case 67694:
					if (x == 2911 && y == 3513)
						player.useStairs(-1, new Tile(2907, 3514, 1), 0, 1);
					break;
				case 67690:
					if (x == 2905 && y == 3516)
						player.useStairs(-1, new Tile(2893, 9907, 0), 0, 1);
					break;
				case 67691:
					if (x == 2892 && y == 9907)
						player.useStairs(-1, new Tile(2906, 3516, 0), 0, 1);
					break;
				case 41435:
					if (x == 2732 && y == 3377)
						player.useStairs(-1, new Tile(2732, 3380, 1), 0, 1);
					break;
				case 41436:
					if (x == 2732 && y == 3378)
						player.useStairs(-1, new Tile(2732, 3376, 0), 0, 1);
					break;
				case 41425:
					if (x == 2724 && y == 3374)
						player.useStairs(-1, new Tile(2720, 9775, 0), 0, 1);
					break;
				case 32048:
					if (x == 2717 && y == 9773)
						player.useStairs(-1, new Tile(2723, 3375, 0), 0, 1);
					break;
				case 41449:
				case 66449:
					player.getBank().openBank();
					break;
				case 2938:
					for (Item item : player.getInventory().getItems()
							.getItems()) {
						if (item == null)
							continue;
						if (item.getId() >= 11120 && item.getId() <= 11126
								&& item.getId() % 2 == 0)
							item.setId(11118);
						else if (item.getId() >= 11107 && item.getId() <= 11113
								&& item.getId() % 2 != 0)
							item.setId(11105);
					}
					player.getInventory().refresh();
					player.getDialogueManager()
							.startDialogue(
									"ItemMessage",
									"Your combat bracelet and skill necklace have all been recharged.",
									11105);
					break;
				case 65203:
					if (x == 3118 && y == 3570)
						player.useStairs(827, new Tile(3249, 5491, 0), 1, 2);
					else if (x == 3058 && y == 3550)
						player.useStairs(827, new Tile(3184, 5471, 0), 1, 2);
					else if (x == 3129 && y == 3587)
						player.useStairs(827, new Tile(3235, 5560, 0), 1, 2);
					else if (x == 3176 && y == 3585)
						player.useStairs(827, new Tile(3290, 5539, 0), 1, 2);
					else if (x == 3164 && y == 3561)
						player.useStairs(827, new Tile(3292, 5479, 0), 1, 2);
					break;
				case 77745:
					player.addWalkSteps(object.getX(), object.getY(), -1, false);
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							if (getRepeatedTele(player, 3285, 5474, 0, 3286,
									5470, 0))
								return;
							else if (getRepeatedTele(player, 3302, 5469, 0,
									3290, 5463, 0))
								return;
							else if (getRepeatedTele(player, 3280, 5460, 0,
									3273, 5460, 0))
								return;
							else if (getRepeatedTele(player, 3299, 5450, 0,
									3296, 5455, 0))
								return;
							else if (getRepeatedTele(player, 3283, 5448, 0,
									3287, 5448, 0))
								return;
							else if (getRepeatedTele(player, 3260, 5491, 0,
									3266, 5446, 0))
								return;
							else if (getRepeatedTele(player, 3239, 5498, 0,
									3244, 5495, 0))
								return;
							else if (getRepeatedTele(player, 3238, 5507, 0,
									3232, 5501, 0))
								return;
							else if (getRepeatedTele(player, 3222, 5488, 0,
									3218, 5497, 0))
								return;
							else if (getRepeatedTele(player, 3222, 5474, 0,
									3224, 5479, 0))
								return;
							else if (getRepeatedTele(player, 3215, 5475, 0,
									3218, 5478, 0))
								return;
							else if (getRepeatedTele(player, 3210, 5477, 0,
									3208, 5471, 0))
								return;
							else if (getRepeatedTele(player, 3212, 5452, 0,
									3214, 5456, 0))
								return;
							else if (getRepeatedTele(player, 3235, 5457, 0,
									3229, 5454, 0))
								return;
							else if (getRepeatedTele(player, 3204, 5445, 0,
									3197, 5448, 0))
								return;
							else if (getRepeatedTele(player, 3191, 5495, 0,
									3194, 5490, 0))
								return;
							else if (getRepeatedTele(player, 3185, 5478, 0,
									3191, 5482, 0))
								return;
							else if (getRepeatedTele(player, 3186, 5472, 0,
									3192, 5472, 0))
								return;
							else if (getRepeatedTele(player, 3189, 5444, 0,
									3187, 5460, 0))
								return;
							else if (getRepeatedTele(player, 3178, 5460, 0,
									3168, 5456, 0))
								return;
							else if (getRepeatedTele(player, 3171, 5478, 0,
									3167, 5478, 0))
								return;
							else if (getRepeatedTele(player, 3171, 5473, 0,
									3167, 5471, 0))
								return;
							else if (getRepeatedTele(player, 3142, 5489, 0,
									3141, 5480, 0))
								return;
							else if (getRepeatedTele(player, 3142, 5462, 0,
									3154, 5462, 0))
								return;
							else if (getRepeatedTele(player, 3155, 5449, 0,
									3143, 5443, 0))
								return;
							else if (getRepeatedTele(player, 3303, 5477, 0,
									3299, 5484, 0))
								return;
							else if (getRepeatedTele(player, 3318, 5481, 0,
									3322, 5480, 0))
								return;
							else if (getRepeatedTele(player, 3307, 5496, 0,
									3317, 5496, 0))
								return;
							else if (getRepeatedTele(player, 3265, 5491, 0,
									3260, 5491, 0))
								return;
							else if (getRepeatedTele(player, 3297, 5510, 0,
									3300, 5514, 0))
								return;
							else if (getRepeatedTele(player, 3325, 5518, 0,
									3323, 5531, 0))
								return;
							else if (getRepeatedTele(player, 3321, 5554, 0,
									3315, 5552, 0))
								return;
							else if (getRepeatedTele(player, 3291, 5555, 0,
									3285, 5556, 0))
								return;
							else if (getRepeatedTele(player, 3285, 5508, 0,
									3280, 5501, 0))
								return;
							else if (getRepeatedTele(player, 3285, 5527, 0,
									3282, 5531, 0))
								return;
							else if (getRepeatedTele(player, 3289, 5532, 0,
									3288, 5536, 0))
								return;
							else if (getRepeatedTele(player, 3266, 5552, 0,
									3262, 5552, 0))
								return;
							else if (getRepeatedTele(player, 3268, 5534, 0,
									3261, 5536, 0))
								return;
							else if (getRepeatedTele(player, 3248, 5547, 0,
									3253, 5561, 0))
								return;
							else if (getRepeatedTele(player, 3256, 5561, 0,
									3252, 5543, 0))
								return;
							else if (getRepeatedTele(player, 3244, 5526, 0,
									3241, 5529, 0))
								return;
							else if (getRepeatedTele(player, 3230, 5547, 0,
									3226, 5553, 0))
								return;
							else if (getRepeatedTele(player, 3206, 5553, 0,
									3204, 5546, 0))
								return;
							else if (getRepeatedTele(player, 3211, 5533, 0,
									3214, 5533, 0))
								return;
							else if (getRepeatedTele(player, 3208, 5527, 0,
									3211, 5523, 0))
								return;
							else if (getRepeatedTele(player, 3201, 5531, 0,
									3197, 5529, 0))
								return;
							else if (getRepeatedTele(player, 3202, 5516, 0,
									3196, 5512, 0))
								return;
						}
					});
					break;
				case 28779:
					player.addWalkSteps(object.getX(), object.getY(), -1, false);
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							if (getRepeatedTele(player, 3197, 5529, 0, 3201,
									5531, 0))
								return;
							else if (getRepeatedTele(player, 3165, 5515, 0,
									3173, 5530, 0))
								return;
							else if (getRepeatedTele(player, 3156, 5523, 0,
									3152, 5520, 0))
								return;
							else if (getRepeatedTele(player, 3148, 5533, 0,
									3153, 5537, 0))
								return;
							else if (getRepeatedTele(player, 3143, 5535, 0,
									3147, 5541, 0))
								return;
							else if (getRepeatedTele(player, 3158, 5561, 0,
									3162, 5557, 0))
								return;
							else if (getRepeatedTele(player, 3162, 5545, 0,
									3166, 5553, 0))
								return;
							else if (getRepeatedTele(player, 3168, 5541, 0,
									3171, 5542, 0))
								return;
							else if (getRepeatedTele(player, 3190, 5549, 0,
									3190, 5554, 0))
								return;
							else if (getRepeatedTele(player, 3180, 5557, 0,
									3174, 5558, 0))
								return;
							else if (getRepeatedTele(player, 3190, 5519, 0,
									3190, 5515, 0))
								return;
							else if (getRepeatedTele(player, 3185, 5518, 0,
									3181, 5517, 0))
								return;
							else if (getRepeatedTele(player, 3196, 5512, 0,
									3202, 5516, 0))
								return;
						}
					});
					break;
				case 29392:
					player.useStairs(-1, new Tile(3061, 3335, 0), 0, 1);
					break;
				case 29385:
				case 29386:
					player.useStairs(-1, new Tile(3067, 9710, 0), 0, 1);
					break;
				case 29387:
					player.useStairs(-1, new Tile(3035, 9713, 0), 0, 1);
					break;
				case 29391:
					player.useStairs(-1, new Tile(3037, 3342, 0), 0, 1);
					break;
				case 30572:
					if (x == 3405 && y == 3507)
						player.useStairs(827, new Tile(3405, 9906, 0), 1, 2);
					break;
				case 30575:
					if (x == 3405 && y == 9907)
						player.useStairs(828, new Tile(3405, 3506, 0), 1, 2);
					break;
				case 3443:
					player.useStairs(-1, new Tile(3423, 3484, 0), 0, 1);
					break;
				case 30574:
					if (x == 3422 & y == 3484)
						player.useStairs(827, new Tile(3440, 9887, 0), 1, 2);
					break;
				case 4493:
					if (x == 3434 && y == 3537)
						player.useStairs(-1, new Tile(3433, 3538, 1), 0, 1);
					break;
				case 4494:
					if (x == 3434 && y == 3537)
						player.useStairs(-1, new Tile(3438, 3538, 0), 0, 1);
					break;
				case 4495:
					if (x == 3413 && y == 3540)
						player.useStairs(-1, new Tile(3417, 3541, 2), 0, 1);
					break;
				case 4496:
					if (x == 3415 && y == 3540)
						player.useStairs(-1, new Tile(3412, 3541, 1), 0, 1);
					break;
				case 9472:
					if (x == 3008 && y == 3150)
						player.useStairs(-1, new Tile(3009, 9550, 0), 0, 1);
					break;
				case 32015:
					if (x == 3008 && y == 9550)
						player.useStairs(-1, new Tile(3009, 3150, 0), 0, 1);
					break;
				case 33173:
					player.useStairs(-1, new Tile(3056, 9555, 0), 0, 1);
					break;
				case 33174:
					player.useStairs(-1, new Tile(3056, 9562, 0), 0, 1);
					break;
				case 5947:
					player.useStairs(540, new Tile(3170, 9571, 0), 8, 9);
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							player.getControllerManager().startController(
									"UndergroundDungeon", false, true);
							player.setNextAnimation(new Animation(-1));
						}
					}, 8);
					break;
				case 6658:
					player.useStairs(-1, new Tile(3226, 9542, 0), 1, 2);
					player.getControllerManager().startController(
							"UndergroundDungeon", false, true);
					break;
				case 29375: {
					final boolean isNorth = player.getY() > 9964;
					final Tile tile = new Tile(player.getX(), player.getY()
							+ (isNorth ? -7 : 7), 0);
					player.setNextAnimation(new Animation(745));
					player.setNextForceMovement(new ForceMovement(player, 1,
							tile, 5, isNorth ? ForceMovement.SOUTH
									: ForceMovement.NORTH));
					EngineTaskManager.schedule(new EngineTask() {
						int ticks = 0;

						@Override
						public void run() {
							ticks++;
							if (ticks > 1)
								player.setNextAnimation(new Animation(744));
							if (ticks == 5) {
								player.setNextTile(tile);
								stop();
								return;
							}
						}
					}, 0, 0);
					break;
					}
				case 44339: {
					if (!Agility.hasLevel(player, 81))
						return;
					boolean isEast = player.getX() > 2772;
					final Tile tile = new Tile(isEast ? 2768 : 2775, 10002, 0);
					EngineTaskManager.schedule(new EngineTask() {

						int ticks = -1;

						@Override
						public void run() {
							ticks++;
							if (ticks == 0)
								player.setNextFaceTile(object);
							else if (ticks == 1) {
								player.setNextAnimation(new Animation(10738));
								player.setNextForceMovement(new ForceMovement(
										player, 0, tile, 5,
										Utilities.getFaceDirection(
												object.getX() - player.getX(),
												object.getY() - player.getY())));
							} else if (ticks == 3)
								player.setNextTile(tile);
							else if (ticks == 4) {
								player.getPackets().sendGameMessage(
										"Your feet skid as you land floor.");
								stop();
								return;
							}
						}
					}, 0, 0);
					break;
				}
				case 77052: {
					if (!Agility.hasLevel(player, 70))
						return;
					boolean isEast = player.getX() > 2734;
					final Tile tile = new Tile(isEast ? 2730 : 2735, 10008, 0);
					EngineTaskManager.schedule(new EngineTask() {

						int ticks = -1;

						@Override
						public void run() {
							ticks++;
							if (ticks == 0)
								player.setNextFaceTile(object);
							else if (ticks == 1)
								player.setNextAnimation(new Animation(17811));
							else if (ticks == 9)
								player.setNextTile(tile);
							else if (ticks == 10) {
								stop();
								return;
							}
						}
					}, 0, 0);
					break;
				}
				case 9311:
				case 9312:
					if (!Agility.hasLevel(player, 21))
						return;
					EngineTaskManager.schedule(new EngineTask() {

						int ticks = 0;

						@Override
						public void run() {
							boolean withinGE = id == 9312;
							Tile tile = withinGE ? new Tile(3139, 3516, 0)
									: new Tile(3143, 3514, 0);
							player.lock();
							ticks++;
							if (ticks == 1) {
								player.setNextAnimation(new Animation(2589));
								player.setNextForceMovement(new ForceMovement(
										object, 1,
										withinGE ? ForceMovement.WEST
												: ForceMovement.EAST));
							} else if (ticks == 3) {
								player.setNextTile(new Tile(3141, 3515, 0));
								player.setNextAnimation(new Animation(2590));
							} else if (ticks == 5) {
								player.setNextAnimation(new Animation(2591));
								player.setNextTile(tile);
							} else if (ticks == 6) {
								player.setNextTile(new Tile(tile.getX()
										+ (withinGE ? -1 : 1), tile.getY(),
										tile.getPlane()));
								player.unlock();
								stop();
							}
						}
					}, 0, 0);
					break;
				case 77574:
				case 77573: {
					boolean back = id == 77573;
					player.lock(4);
					final Tile tile = back ? new Tile(2687, 9506, 0)
							: new Tile(2682, 9506, 0);
					final boolean isRun = player.isRunning();
					player.setRun(false);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							player.setRun(isRun);
						}
					}, 4);
					break;
				}
				case 77506:
				case 77507:
					player.useStairs(-1, new Tile(player.getX(), player.getY()
							+ (id == 77506 ? -9 : 9), id == 77506 ? 2 : 0), 1,
							2);
					break;
				case 77508:
				case 77509:
					player.useStairs(-1, id == 77508 ? new Tile(2643, 9595, 2)
					: new Tile(2649, 9591, 0), 1, 2);
					break;
				case 77570:
				case 77571:
				case 77572:
					player.lock(1);
					player.setNextAnimation(new Animation(741));
					player.setNextForceMovement(new ForceMovement(player, 0,
							object, 1, Utilities.getFaceDirection(object.getX()
									- player.getX(),
									object.getY() - player.getY())));
					EngineTaskManager.schedule(new EngineTask() {

						@Override
						public void run() {
							player.setNextTile(object);
						}
					});
					break;
				case 73681:
					Tile dest = new Tile(player.getX() == 2595 ? 2598 : 2595,
							3608, 0);
					player.setNextForceMovement(new ForceMovement(player, 1,
							dest, 2, Utilities.getFaceDirection(dest.getX()
									- player.getX(),
									dest.getY() - player.getY())));
					player.useStairs(-1, dest, 1, 2);
					player.setNextAnimation(new Animation(769));
					break;
				case 77421:
					player.useStairs(-1, new Tile(2745, 3152, 0), 0, 1);
					break;
				case 17222:
				case 17223:
					player.useStairs(-1, new Tile(2402, 3419, 0), 0, 1);
					break;
				case 17209:
					player.useStairs(-1, new Tile(2408, 9812, 0), 0, 1);
					break;
				case 39468:
					player.setNextTile(new Tile(1745, 5325, 0));
					break;
				case 25337:
					player.setNextTile(new Tile(1694, 5296, 1));
					break;
				case 25338:
					player.setNextTile(new Tile(1772, 5366, 0));
					break;
				case 47237:
					if (player.getSkills().getLevel(Skills.AGILITY) < 90) {
						player.getPackets().sendGameMessage(
								"You need 90 agility to use this shortcut.");
						return;
					}
					if (player.getX() == 1641 && player.getY() == 5260
							|| player.getX() == 1641 && player.getY() == 5259
							|| player.getX() == 1640 && player.getY() == 5259) {
						player.setNextTile(new Tile(1641, 5268, 0));
					} else {
						player.setNextTile(new Tile(1641, 5260, 0));
					}
					break;
				case 66115:
				case 66116:
					player.resetWalkSteps();
					player.setNextAnimation(new Animation(830));
					player.lock();
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.unlock();
							if (Barrows.digIntoGrave(player))
								return;
							player.getPackets().sendGameMessage(
									"You find nothing.");
						}
					});
					break;
				case 2563:
				case 2562:
					player.getDialogueManager().startDialogue("CapeStand",
							id == 2562 ? true : false);
					break;
				case 47232:
					player.setNextTile(new Tile(1661, 5257, 0));
					break;
				case 2473:
					player.setNextTile(new Tile(3039, 4834, 0));
					break;
				case 64294: {
					int jumpStage;
					if (player.getX() == 4685 && player.getY() == 5476) {
						jumpStage = 4685 - 4;
					} else {
						jumpStage = 4658 + 4;
					}
					final Tile toTile = new Tile(jumpStage, player.getY(),
							player.getZ());
					player.lock(4);
					player.setNextAnimation(new Animation(15461));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextForceMovement(new ForceMovement(
									player, 0, toTile, 2, ForceMovement.EAST));
							player.setNextTile(toTile);
							player.setNextAnimation(new Animation(-1));
							stop();
						}
					}, 4);
				}
					break;
				case 64295: {
					int jumpStage;
					if (player.getX() == 4681 && player.getY() == 5476) {
						jumpStage = 4681 + 4;
					} else {
						jumpStage = 4663 - 5;
					}
					final Tile toTile = new Tile(jumpStage, player.getY(),
							player.getZ());
					player.lock(4);
					player.setNextAnimation(new Animation(15461));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextForceMovement(new ForceMovement(
									player, 0, toTile, 2, ForceMovement.EAST));
							player.setNextTile(toTile);
							player.setNextAnimation(new Animation(-1));
							stop();
						}
					}, 4);
				}
					break;
				case 64360:
					player.lock(3);
					player.setNextAnimation(new Animation(15458));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(15457));
							stop();
						}
					}, 1);
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							// player.setNextWorldTile(new WorldTile(4698, 5459,
							// 2));
							player.setNextTile(new Tile(4630, 5452, 2));
							player.setNextAnimation(new Animation(-1));
							stop();
						}
					}, 3);
					break;
				case 64359:
					player.lock(3);
					player.setNextAnimation(new Animation(15458));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(15457));
							stop();
						}
					}, 1);
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							// player.setNextWorldTile(new WorldTile(4698, 5459,
							// 2));
							player.setNextTile(new Tile(player.getX(), player
									.getY(), player.getZ() - 1));
							player.setNextAnimation(new Animation(-1));
							stop();
						}
					}, 3);
					break;
				case 64361:
					player.lock(3);
					player.setNextAnimation(new Animation(15456));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							// player.setNextWorldTile(new WorldTile(4697, 5459,
							// 3));
							if (player.getX() == 4630 && player.getY() == 5452
									|| player.getX() == 4628
									&& player.getY() == 5451) {
								player.setNextTile(new Tile(4629, 5454, 3));
							} else {
								player.setNextTile(new Tile(player.getX(),
										player.getY(), player.getZ() + 1));
							}
							player.setNextTile(new Tile(player.getX(), player
									.getY(), player.getZ() + 1));
							player.setNextAnimation(new Animation(-1));
							stop();
						}
					}, 3);
					break;
				case 22119:
					player.getControllerManager().startController(
							"BarrelchestController");
					break;
				case 47231:
					player.setNextTile(new Tile(1685, 5287, 1));
					break;
				case 75882:
					player.lock(3);
					player.addWalkSteps(player.getX() == 3335 ? 3332 : 3335,
							player.getY() == 3163 ? 3163 : 3162, 0, false);
					player.setRun(false);
					final GameObject statueLetThrough = new GameObject(45860,
							10, 3, 3334, 3161, 0);
					final GameObject statueLetThrough2 = new GameObject(45860,
							10, 1, 3334, 3163, 0);
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							if (Engine.removeTemporaryObject(object,
									player.getX() == 3335 ? 600 : 1000, true))
								Engine.spawnTemporaryObject(statueLetThrough,
										player.getX() == 3335 ? 600 : 1000,
										false);
							if (Engine.removeTemporaryObject(object,
									player.getX() == 3335 ? 600 : 1000, true))
								Engine.spawnTemporaryObject(statueLetThrough2,
										player.getX() == 3335 ? 600 : 1000,
										false);
							stop();
						}
					});
					break;
				case 47236:
					if (player.getX() == 1650 && player.getY() == 5281
					|| player.getX() == 1651 && player.getY() == 5281
					|| player.getX() == 1650 && player.getY() == 5281) {
				player.addWalkSteps(1651, 5280, 1, false);
			}
			if (player.getX() == 1652 && player.getY() == 5280
					|| player.getX() == 1651 && player.getY() == 5280
					|| player.getX() == 1653 && player.getY() == 5280) {
				player.addWalkSteps(1651, 5281, 1, false);
			}
			if (player.getX() == 1650 && player.getY() == 5301
					|| player.getX() == 1650 && player.getY() == 5302
					|| player.getX() == 1650 && player.getY() == 5303) {
				player.addWalkSteps(1649, 5302, 1, false);
			}
			if (player.getX() == 1649 && player.getY() == 5303
					|| player.getX() == 1649 && player.getY() == 5302
					|| player.getX() == 1649 && player.getY() == 5301) {
				player.addWalkSteps(1650, 5302, 1, false);
			}
			if (player.getX() == 1626 && player.getY() == 5301
					|| player.getX() == 1626 && player.getY() == 5302
					|| player.getX() == 1626 && player.getY() == 5303) {
				player.addWalkSteps(1625, 5302, 1, false);
			}
			if (player.getX() == 1625 && player.getY() == 5301
					|| player.getX() == 1625 && player.getY() == 5302
					|| player.getX() == 1625 && player.getY() == 5303) {
				player.addWalkSteps(1626, 5302, 1, false);
			}
			if (player.getX() == 1609 && player.getY() == 5289
					|| player.getX() == 1610 && player.getY() == 5289
					|| player.getX() == 1611 && player.getY() == 5289) {
				player.addWalkSteps(1610, 5288, 1, false);
			}
			if (player.getX() == 1609 && player.getY() == 5288
					|| player.getX() == 1610 && player.getY() == 5288
					|| player.getX() == 1611 && player.getY() == 5288) {
				player.addWalkSteps(1610, 5289, 1, false);
			}
			if (player.getX() == 1606 && player.getY() == 5265
					|| player.getX() == 1605 && player.getY() == 5265
					|| player.getX() == 1604 && player.getY() == 5265) {
				player.addWalkSteps(1605, 5264, 1, false);
			}
			if (player.getX() == 1606 && player.getY() == 5264
					|| player.getX() == 1605 && player.getY() == 5264
					|| player.getX() == 1604 && player.getY() == 5264) {
				player.addWalkSteps(1605, 5265, 1, false);
			}
			if (player.getX() == 1634 && player.getY() == 5254
					|| player.getX() == 1634 && player.getY() == 5253
					|| player.getX() == 1634 && player.getY() == 5252) {
				player.addWalkSteps(1635, 5253, 1, false);
			}
			if (player.getX() == 1635 && player.getY() == 5254
					|| player.getX() == 1635 && player.getY() == 5253
					|| player.getX() == 1635 && player.getY() == 5252) {
				player.addWalkSteps(1634, 5253, 1, false);
			}
					break;
				case 47223:
					if (player.getSkills().getLevel(Skills.AGILITY) < 80) {
						player.getPackets().sendGameMessage(
								"You need 80 agility to use this shortcut.");
						return;
					}
					if (player.getX() == 1633 && player.getY() == 5294) {
						return;
					}
					player.lock(3);
					player.setNextAnimation(new Animation(4853));
					final Tile toTile = new Tile(object.getX(),
							object.getY() + 1, object.getZ());
					player.setNextForceMovement(new ForceMovement(player, 0,
							toTile, 2, ForceMovement.EAST));
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextTile(toTile);
						}
					}, 1);
					break;
				case 29958:
					if (player.getSkills().getLevel(23) < player.getSkills()
							.getLevelForXp(23)) {
						player.lock(5);
						player.getPackets().sendGameMessage(
								"You feel the obelisk", true);
						player.setNextAnimation(new Animation(8502));
						player.setNextGraphics(new Graphics(1308));
						EngineTaskManager.schedule(new EngineTask() {

							@Override
							public void run() {
								player.getSkills().restoreSummoning();
								player.getPackets().sendGameMessage(
										"...and recharge all your skills.",
										true);
							}
						}, 2);
					} else {
						player.getPackets()
								.sendGameMessage(
										"You already have full summoning points.",
										true);
					}
					break;
				case 70799:
					player.useStairs(-1, new Tile(1178, 6355, 0), 1, 2);
					break;
				case 70796:
					player.useStairs(-1, new Tile(1090, 6360, 0), 1, 2);
					break;
				case 70798:
					player.useStairs(-1, new Tile(1340, 6380, 0), 1, 2);
					break;
				case 70797:
					player.useStairs(-1, new Tile(1090, 6497, 0), 1, 2);
					break;
				case 70792:
					player.useStairs(-1, new Tile(1206, 6371, 0), 1, 2);
					break;
				case 70793:
					player.useStairs(-1, new Tile(2989, 3237, 0), 1, 2);
					break;
				case 70794:
					player.useStairs(-1, new Tile(1340, 6488, 0), 1, 2);
					break;
				case 70795:
					if (!Agility.hasLevel(player, 50))
						return;
					player.getDialogueManager().startDialogue("GrotwormLairD",
							true);
					break;
				case 70812:
					player.getDialogueManager().startDialogue("GrotwormLairD",
							false);
					break;
				case 2350:
					if (object.getX() == 3352 && object.getY() == 3417 && object.getZ() == 0)
						player.useStairs(832, new Tile(3177, 5731, 0), 1, 2);
					break;
				case 2353:
					if (object.getX() == 3177 && object.getY() == 5730 && object.getZ() == 0)
						player.useStairs(828, new Tile(3353, 3416, 0), 1, 2);
					break;
				case 11554:
				case 11552:
					player.getPackets().sendGameMessage(
							"That rock is currently unavailable.");
					break;
				case 38279:
					player.getDialogueManager().startDialogue("RunespanPortalD");
					break;
				case 2491:
					player.getActionManager()
					.setAction(
							new EssenceMining(
									object,
									player.getSkills().getLevel(
											Skills.MINING) < 30 ? EssenceDefinitions.Rune_Essence
											: EssenceDefinitions.Pure_Essence));
					break;
				case 2478:
					RuneCrafting.craftEssence(player, 556, 1, 5, false, 11, 2,
							22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 88, 9, 99,
							10);
					break;
				case 2479:
					RuneCrafting.craftEssence(player, 558, 2, 5.5, false, 14,
							2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8);
					break;
				case 2480:
					RuneCrafting.craftEssence(player, 555, 5, 6, false, 19, 2,
							38, 3, 57, 4, 76, 5, 95, 6);
					break;
				case 2481:
					RuneCrafting.craftEssence(player, 557, 9, 6.5, false, 26,
							2, 52, 3, 78, 4);
					break;
				case 2482:
					RuneCrafting.craftEssence(player, 554, 14, 7, false, 35, 2,
							70, 3);
					break;
				case 2483:
					RuneCrafting.craftEssence(player, 559, 20, 7.5, false, 46,
							2, 92, 3);
					break;
				case 2484:
					RuneCrafting.craftEssence(player, 564, 27, 8, true, 59, 2);
					break;
				case 2487:
					RuneCrafting
					.craftEssence(player, 562, 35, 8.5, true, 74, 2);
					break;
				case 17010:
					RuneCrafting.craftEssence(player, 9075, 40, 8.7, true, 82,
							2);
					break;
				case 2486:
					RuneCrafting.craftEssence(player, 561, 45, 9, true, 91, 2);
					break;
				case 2485:
					RuneCrafting.craftEssence(player, 563, 50, 9.5, true);
					break;
				case 2488:
					RuneCrafting.craftEssence(player, 560, 65, 10, true);
					break;
				case 30624:
					RuneCrafting.craftEssence(player, 565, 77, 10.5, true);
					break;
				case 2452: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.AIR_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1438, 1))
						RuneCrafting.enterAirAltar(player);
					break;
				}
				case 2455: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.EARTH_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1440, 1))
						RuneCrafting.enterEarthAltar(player);
					break;
				}
				case 2456: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.FIRE_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1442, 1))
						RuneCrafting.enterFireAltar(player);
					break;
				}
				case 2454: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.WATER_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1444, 1))
						RuneCrafting.enterWaterAltar(player);
					break;
				}
				case 2457: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.BODY_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1446, 1))
						RuneCrafting.enterBodyAltar(player);
					break;
				}
				case 2453: {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.MIND_TIARA
							|| hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1448, 1))
						RuneCrafting.enterMindAltar(player);
					break;
				}
				case 47120:
					if (player.getPrayer().getPrayerpoints() < player
							.getSkills().getLevelForXp(Skills.PRAYER) * 10) {
						player.lock(12);
						player.setNextAnimation(new Animation(12563));
						player.getPrayer().setPrayerpoints(
								(int) ((player.getSkills().getLevelForXp(
										Skills.PRAYER) * 10) * 1.15));
						player.getPrayer().refreshPrayerPoints();
					}
					player.getDialogueManager().startDialogue("ZarosAltar");
					break;
				case 36786:
					player.getDialogueManager().startDialogue("Banker", 4907);
					break;
				case 42377:
				case 42378:
					player.getDialogueManager().startDialogue("Banker", 2759);
					break;
				case 42217:
				case 782:
				case 34752:
					player.getDialogueManager().startDialogue("Banker", 553);
					break;
				case 57437:
					player.getBank().openBank();
					break;
				case 42425:
					if (x == 3220 && y == 3222) {
						player.useStairs(10256, new Tile(3353, 3416, 0), 4, 5,
								"And you find yourself into a digsite.");
						player.addWalkSteps(3222, 3223, -1, false);
						player.getPackets().sendGameMessage(
								"You examine portal and it aborves you...");
					}
					break;
				case 9356:
					player.lock(3);
					FightCaves.enterFightCaves(player);
					break;
				case 68107:
					FightKiln.enterFightKiln(player, false);
					break;
				case 68223:
					FightPits.enterLobby(player, false);
					break;
				case 46500:
					if (x == 3351 && y == 3415) {
						player.useStairs(-1, new Tile(
								GameConstants.RESPAWN_PLAYER_LOCATION.getX(),
								GameConstants.RESPAWN_PLAYER_LOCATION.getY(),
								GameConstants.RESPAWN_PLAYER_LOCATION.getZ()), 2,
								3, "You found your way back to home.");
						player.addWalkSteps(3351, 3415, -1, false);
					}
					break;
				case 9293: {
					if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
						player.getPackets()
								.sendGameMessage(
										"You need an agility level of 70 to use this obstacle.",
										true);
						return;
					}
					int x = player.getX() == 2886 ? 2892 : 2886;
					EngineTaskManager.schedule(new EngineTask() {
						int count = 0;

						@Override
						public void run() {
							player.setNextAnimation(new Animation(844));
							if (count++ == 1)
								stop();
						}

					}, 0, 0);
					player.setNextForceMovement(new ForceMovement(new Tile(x,
							9799, 0), 3, player.getX() == 2886 ? 1 : 3));
					player.useStairs(-1, new Tile(x, 9799, 0), 3, 4);
					break;
				}
				case 29370:
					if ((x == 3150 || x == 3153)
						&& y == 9906) {
						if (player.getSkills().getLevel(Skills.AGILITY) < 53) {
							player.getPackets()
									.sendGameMessage(
											"You need an agility level of 53 to use this obstacle.");
							return;
						}
						final boolean running = player.getRun();
						player.setRunHidden(false);
						player.lock(8);
						player.addWalkSteps(x == 3150 ? 3155 : 3149, 9906, -1,
								false);
						player.getPackets().sendGameMessage(
								"You pulled yourself through the pipes.", true);
						EngineTaskManager.schedule(new EngineTask() {
							boolean secondloop;

							@Override
							public void run() {
								if (!secondloop) {
									secondloop = true;
									player.getGlobalPlayerUpdate().setRenderEmote(
											295);
								} else {
									player.getGlobalPlayerUpdate().setRenderEmote(
											-1);
									player.setRunHidden(running);
									stop();
								}
							}
						}, 0, 5);
					}
					break;
				case 18341:
					if (x == 3036 && y == 10172)
						player.useStairs(-1, new Tile(3039, 3765, 0), 0, 1);
					break;
				case 20599:
					if (x == 3038 && y == 3761)
						player.useStairs(-1, new Tile(3037, 10171, 0), 0, 1);
					break;
				case 18342:
					if (x == 3075 && y == 3761)
						player.useStairs(-1, new Tile(3037, 10171, 0), 0, 1);
					break;
				case 20600:
					if (x == 3072 && y == 3648)
						player.useStairs(-1, new Tile(3077, 10058, 0), 0, 1);
					break;
				case 42219:
					player.getControllerManager().startController(
							"AreaController");
					player.useStairs(-1, new Tile(1886, 3178, 0), 0, 1);
					break;
				case 8689:
					player.getActionManager().setAction(new CowMilkingAction());
					break;
				case 30942:
					if (x == 3019 && y == 3450)
						player.useStairs(828, new Tile(3020, 9850, 0), 1, 2);
					break;
				case 6226:
					if (x == 3019 && y == 9850)
						player.useStairs(833, new Tile(3018, 3450, 0), 1, 2);
					break;
				case 31002:
					player.useStairs(833, new Tile(2998, 3452, 0), 1, 2);
					break;
				case 31012:
					player.useStairs(828, new Tile(2996, 9845, 0), 1, 2);
					break;
				case 30943:
					if (x == 3059 && y == 9776)
						player.useStairs(-1, new Tile(3061, 3376, 0), 0, 1);
					break;
				case 30944:
					if (x == 3059 && y == 3376)
						player.useStairs(-1, new Tile(3058, 9776, 0), 0, 1);
					break;
				case 2112:
					if (x == 3046 && y == 9756) {
						if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
							player.getDialogueManager()
									.startDialogue(
											"SimpleNPCMessage",
											MiningGuildDwarf
													.getClosestDwarfID(player),
											"Sorry, but you need level 60 Mining to go in there.");
							return;
						}
						GameObject openedDoor = new GameObject(object.getId(),
								object.getType(), object.getRotation() - 1, object
										.getX(), object.getY() + 1, object.getZ());
						if (Engine.removeTemporaryObject(object, 1200, false)) {
							Engine.spawnTemporaryObject(openedDoor, 1200, false);
							player.lock(2);
							player.stopAll();
							player.addWalkSteps(3046,
									player.getY() > object.getY() ? object.getY()
											: object.getY() + 1, -1, false);
						}
					}
					break;
				case 2113:
					if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
						player.getDialogueManager()
								.startDialogue(
										"SimpleNPCMessage",
										MiningGuildDwarf
												.getClosestDwarfID(player),
										"Sorry, but you need level 60 Mining to go in there.");
						return;
					}
					player.useStairs(-1, new Tile(3021, 9739, 0), 0, 1);
					break;
				}
				if (id == 6226 && object.getX() == 3019
						&& object.getY() == 9740)
					player.useStairs(828, new Tile(3019, 3341, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3019
						&& object.getY() == 9738)
					player.useStairs(828, new Tile(3019, 3337, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3018
						&& object.getY() == 9739)
					player.useStairs(828, new Tile(3017, 3339, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3020
						&& object.getY() == 9739)
					player.useStairs(828, new Tile(3021, 3339, 0), 1, 2);
				else if (id == 30963)
					player.getBank().openBank();
				else if (id == 6045)
					player.getPackets().sendGameMessage(
							"You search the cart but find nothing.");
				else if (id == 5906) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 42) {
						player.getPackets()
								.sendGameMessage(
										"You need an agility level of 42 to use this obstacle.");
						return;
					}
					player.lock();
					EngineTaskManager.schedule(new EngineTask() {
						int count = 0;

						@Override
						public void run() {
							if (count == 0) {
								player.setNextAnimation(new Animation(2594));
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -2
														: +2), object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(
										tile, 4, Utilities.getMoveDirection(
												tile.getX() - player.getX(),
												tile.getY() - player.getY())));
							} else if (count == 2) {
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -2
														: +2), object.getY(), 0);
								player.setNextTile(tile);
							} else if (count == 5) {
								player.setNextAnimation(new Animation(2590));
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -5
														: +5), object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(
										tile, 4, Utilities.getMoveDirection(
												tile.getX() - player.getX(),
												tile.getY() - player.getY())));
							} else if (count == 7) {
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -5
														: +5), object.getY(), 0);
								player.setNextTile(tile);
							} else if (count == 10) {
								player.setNextAnimation(new Animation(2595));
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -6
														: +6), object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(
										tile, 4, Utilities.getMoveDirection(
												tile.getX() - player.getX(),
												tile.getY() - player.getY())));
							} else if (count == 12) {
								Tile tile = new Tile(
										object.getX()
												+ (object.getRotation() == 2 ? -6
														: +6), object.getY(), 0);
								player.setNextTile(tile);
							} else if (count == 14) {
								stop();
								player.unlock();
							}
							count++;
						}

					}, 0, 0);
				//Start of Ape Atoll Agility
				} else if (id == 12568)
					ApeAtollAgility.jumpToSteppingStone(player, object);
				else if (id == 12570)
					ApeAtollAgility.climbUpTropicalTree(player, object);
				else if (id == 12573)
					ApeAtollAgility.crossMonkeyBars(player, object);
				else if (id == 12578)
					ApeAtollAgility.swingRope(player, object);
				else if (id == 12618)
					ApeAtollAgility.climbDownTropicalTree(player, object);
				else if (id == 12622)
					ApeAtollAgility.climbDownVine(player, object);
					// BarbarianOutpostAgility start
				else if (id == 20210)
					BarbarianOutpostAgility.enterObstaclePipe(player, object);
				else if (id == 43526)
					BarbarianOutpostAgility.swingOnRopeSwing(player, object);
				else if (id == 43595 && x == 2550 && y == 3546)
					BarbarianOutpostAgility
							.walkAcrossLogBalance(player, object);
				else if (id == 20211 && x == 2538 && y == 3545)
					BarbarianOutpostAgility.climbObstacleNet(player, object);
				else if (id == 2302 && x == 2535 && y == 3547)
					BarbarianOutpostAgility.walkAcrossBalancingLedge(player,
							object);
				else if (id == 1948)
					BarbarianOutpostAgility.climbOverCrumblingWall(player,
							object);
				else if (id == 43533)
					BarbarianOutpostAgility.runUpWall(player, object);
				else if (id == 43597)
					BarbarianOutpostAgility.climbUpWall(player, object);
				else if (id == 43587)
					BarbarianOutpostAgility.fireSpringDevice(player, object);
				else if (id == 43527)
					BarbarianOutpostAgility.crossBalanceBeam(player, object);
				else if (id == 43531)
					BarbarianOutpostAgility.jumpOverGap(player, object);
				else if (id == 43532)
					BarbarianOutpostAgility.slideDownRoof(player, object);

				// rock living caverns
				else if (id == 45077) {
					player.lock();
					if (player.getX() != object.getX()
							|| player.getY() != object.getY())
						player.addWalkSteps(object.getX(), object.getY(), -1,
								false);
					EngineTaskManager.schedule(new EngineTask() {

						private int count;

						@Override
						public void run() {
							if (count == 0) {
								player.setNextFaceTile(new Tile(
										object.getX() - 1, object.getY(), 0));
								player.setNextAnimation(new Animation(12216));
								player.unlock();
							} else if (count == 2) {
								player.setNextTile(new Tile(3651, 5122, 0));
								player.setNextFaceTile(new Tile(3651, 5121, 0));
								player.setNextAnimation(new Animation(12217));
							} else if (count == 5) {
								player.unlock();
								stop();
							}
							count++;
						}

					}, 1, 0);
				} else if (id == 45076)
					player.getActionManager().setAction(
							new Mining(object, RockDefinitions.LRC_Gold_Ore));
				else if (id == 5999)
					player.getActionManager().setAction(
							new Mining(object, RockDefinitions.LRC_Coal_Ore));
				else if (id == 45078)
					player.useStairs(2413, new Tile(3012, 9832, 0), 2, 2);
				else if (id == 45079)
					player.getBank().openDepositBox();
				// champion guild
				else if (id == 24357 && object.getX() == 3188
						&& object.getY() == 3355)
					player.useStairs(-1, new Tile(3189, 3354, 1), 0, 1);
				else if (id == 24359 && object.getX() == 3188
						&& object.getY() == 3355)
					player.useStairs(-1, new Tile(3189, 3358, 0), 0, 1);
				else if (id == 1805 && object.getX() == 3191
						&& object.getY() == 3363) {
					GameObject openedDoor = new GameObject(object.getId(),
							object.getType(), object.getRotation() - 1, object
									.getX(), object.getY(), object.getZ());
					if (Engine.removeTemporaryObject(object, 1200, false)) {
						Engine.spawnTemporaryObject(openedDoor, 1200, false);
						player.lock(2);
						player.stopAll();
						player.addWalkSteps(3191, player.getY() >= object
								.getY() ? object.getY() - 1 : object.getY(),
								-1, false);
						if (player.getY() >= object.getY())
							player.getDialogueManager()
									.startDialogue(
											"SimpleNPCMessage",
											198,
											"Greetings bolt adventurer. Welcome to the guild of",
											"Champions.");
					}
				}
				// start of varrock dungeon
				else if (id == 29355 && object.getX() == 3230
						&& object.getY() == 9904) // varrock
					// dungeon
					// climb
					// to
					// bear
					player.useStairs(828, new Tile(3229, 3503, 0), 1, 2);
				else if (id == 24264)
					player.useStairs(833, new Tile(3229, 9904, 0), 1, 2);
				else if (id == 24366)
					player.useStairs(828, new Tile(3237, 3459, 0), 1, 2);
				else if (id == 882 && object.getX() == 3237
						&& object.getY() == 3458)
					player.useStairs(833, new Tile(3237, 9858, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3097
						&& object.getY() == 9867) // edge
					// dungeon
					// climb
					player.useStairs(828, new Tile(3096, 3468, 0), 1, 2);
				else if (id == 26934)
					player.useStairs(833, new Tile(3097, 9868, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3088
						&& object.getY() == 9971)
					player.useStairs(828, new Tile(3087, 3571, 0), 1, 2);
				else if (id == 65453)
					player.useStairs(833, new Tile(3089, 9971, 0), 1, 2);
				else if (id == 12389 && object.getX() == 3116
						&& object.getY() == 3452)
					player.useStairs(833, new Tile(3117, 9852, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3116
						&& object.getY() == 9852)
					player.useStairs(833, new Tile(3115, 3452, 0), 1, 2);
				else if (id == 69514)
					GnomeAgility.runGnomeBoard(player, object);
				else if (id == 69389)
					GnomeAgility.jumpDown(player, object);
				else if (id == 69526)
					GnomeAgility.walkGnomeLog(player);
				else if (id == 69383)
					GnomeAgility.climbGnomeObstacleNet(player);
				else if (id == 69506)
					GnomeAgility.climbUpTree(player);
				else if (id == 69508)
					GnomeAgility.climbUpGnomeTreeBranch(player);
				else if (id == 2312)
					GnomeAgility.walkGnomeRope(player);
				else if (id == 4059)
					GnomeAgility.walkBackGnomeRope(player);
				else if (id == 69507)
					GnomeAgility.climbDownGnomeTreeBranch(player);
				else if (id == 69384)
					GnomeAgility.climbGnomeObstacleNet2(player);
				else if (id == 69377 || id == 69378)
					GnomeAgility.enterGnomePipe(player, object.getX(),
							object.getY());
				else if (id == 65365)
					WildernessCourseAgility.walkGate(player, object);
				else if (id == 65367)
					WildernessCourseAgility.walkBackGate(player, object);
				else if (id == 65362)
					WildernessCourseAgility.enterObstaclePipe(player, object);
				else if (id == 65734)
					WildernessCourseAgility.climbCliff(player, object);
				else if (id == 64696)
					WildernessCourseAgility.swingOnRopeSwing(player, object);
				else if (id == 64699)
					WildernessCourseAgility.steppingStone(player, object);
				else if (id == 64698)
					WildernessCourseAgility.walkAcrossLogBalance(player);
				else if (object.getDefinitions().name
						.equalsIgnoreCase("Wilderness wall")) {// wild ditch
					player.getDialogueManager().startDialogue(
							"WildernessDitch", object);
				} else if (id == 42611) {// Magic Portal
					player.getDialogueManager().startDialogue("MagicPortal");
				} else if (object.getDefinitions().name
						.equalsIgnoreCase("Obelisk") && object.getY() > 3525) {
					// Who the fuck removed the controler class and the code
					// from SONIC!!!!!!!!!!
					// That was an hour of collecting coords :fp: Now ima kill
					// myself.
				} else if (id == 27254) {// Edgeville portal
					player.getPackets().sendGameMessage(
							"You enter the portal...");
					player.useStairs(10584, new Tile(3087, 3488, 0), 2, 3,
							"..and are transported to Edgeville.");
					player.addWalkSteps(1598, 4506, -1, false);
				} else if (id == 12202) {// mole entrance
					if (!player.getInventory().containsItem(952, 1)) {
						player.getPackets().sendGameMessage(
								"You need a spade to dig this.");
						return;
					}
					if (player.getX() != object.getX()
							|| player.getY() != object.getY()) {
						player.lock();
						player.addWalkSteps(object.getX(), object.getY());
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								InventoryOptionsHandler.dig(player);
							}

						}, 1);
					} else
						InventoryOptionsHandler.dig(player);
					// TREASURE CHEST
				} else if (id == 29734 && player.getPlane() == 0) {// TREASURE
																	// CHEST
					if (player.playerSafety2 == false) {
						if (player.getInventory().getFreeSlots() < 4) {
							player.print("You need some more inventory space to take the contents of this chest.");
							return;
						}
						player.getDialogueManager()
								.startDialogue(
										"SimpleItemMessage",
										12629,
										"You open the chest to find a large pile of gold, along with a pair of safety gloves and two antique lamps. also in the chest is the secret of the 'Safety First' emote.");
						player.getInventory().addItem(12627, 2);
						player.getInventory().addItem(12629, 1);
						player.getInventory().addItem(995, 10000);
						player.playerSafety2 = true;
						player.sendByFiles();
					} else {
						if (!player.getBank().containsItem(12629, 1)
								&& !player.getInventory()
										.containsItem(12629, 1)
								&& player.getEquipment().getGlovesId() != 12629) {
							player.getInventory().addItem(12629, 1);
							player.print("You find another pair of gloves in the chest.");
						} else {
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"The chest appears to be empty.");
						}
					}
					// END
				} else if (id == 29736) {// Old Lever
					player.setNextAnimation(new Animation(8804));
					if (player.playerSafety1 == false) {
						player.playerSafety1 = true;
						player.lock(3);
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								player.sendByFiles();
								player.print("You hear cogs and gears moving and a distant unlocking sound.");
								stop();
							}
						}, 3, 0);
					} else {
						player.playerSafety1 = false;
						player.lock(3);
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								player.sendByFiles();
								player.print("You hear cogs and gears moving and the sound of heavy locks falling into place.");
								stop();
							}
						}, 3, 0);
					}
					// DOOR
				} else if (id == 29624) {// Door
					if (player.getPlane() == 1 && object.getX() == 3141
							&& object.getY() == 4272) {
						if (player.playerSafety1 == false) {
							player.print("The door seems to be locked by some kind of mechanism.");
						} else {
							player.setNextTile(new Tile(3143, 4270, 0));
						}
					} else if (player.getPlane() == 0 && object.getX() == 3142
							&& object.getY() == 4270) {
						player.setNextTile(new Tile(3142, 4272, 1));
					} else if (player.getPlane() == 0 && object.getX() == 3178
							&& object.getY() == 4266) {
						player.setNextTile(new Tile(3177, 4269, 2));
					} else if (player.getPlane() == 2) {
						if (player.playerSafety1 == false) {
							player.print("The door seems to be locked by some kind of mechanism.");
						} else {
							player.setNextTile(new Tile(3177, 4266, 0));
						}
					}
				} else if (id == 29603) {// Jail entrance
					player.setNextTile(new Tile(3082, 4229, 0));
				} else if (id == 29623) {
					player.setNextTile(new Tile(3077, 4235, 0));
				} else if (id == 29602) {
					player.setNextTile(new Tile(3074, 3456, 0));
				} else if (id == 29735) {
					player.getDialogueManager().startDialogue("SafetyPoster");
				} else if (id == 1804 && object.getX() == 3115
						&& object.getY() == 3449 && player.getX() == 3115
						&& player.getY() == 3449) {
					if (!player.getInventory().containsItem(983, 1)) {
						player.getPackets().sendGameMessage(
								"The door is locked.");
						return;
					}
					GameObject openedDoor = new GameObject(object.getId(),
							object.getType(), object.getRotation() - 1, object
									.getX(), object.getY() + 1, object
									.getPlane());
					if (Engine.removeTemporaryObject(object, 1200, false)) {
						Engine.spawnTemporaryObject(openedDoor, 1200, false);
						player.lock(2);
						player.stopAll();
						player.addWalkSteps(3115,
								player.getY() > object.getY() ? object.getY()
										: object.getY() + 1, -1, false);
					}
				} else if (id == 1804 && object.getX() == 3115
						&& object.getY() == 3449 && player.getX() == 3115
						&& player.getY() == 3450) {
					if (!player.getInventory().containsItem(983, 1)) {
						player.getPackets().sendGameMessage(
								"The door is locked.");
						return;
					}
					GameObject openedDoor = new GameObject(object.getId(),
							object.getType(), object.getRotation() - 1, object
									.getX(), object.getY() + 1, object
									.getPlane());
					if (Engine.removeTemporaryObject(object, 1200, false)) {
						Engine.spawnTemporaryObject(openedDoor, 1200, false);
						player.lock(2);
						player.stopAll();
						player.addWalkSteps(3115,
								player.getY() > object.getY() ? object.getY()
										: object.getY() + 1, -1, false);
					}
				} else if (id == 29728 && player.getPlane() == 0) {// CREVICE
					if (player.playerSafety3 == true) {
						player.setNextTile(new Tile(3158, 4279, 3));
					} else {
						player.print("You are not sure to go...you should better find a different way");
					}
				} else if (id == 29656 && object.getX() == 3147
						&& object.getY() == 4246 && player.getPlane() == 1) {// Stairs
					player.setNextTile(new Tile(3149, 4244, 2));
				} else if (id == 29660 && object.getX() == 3147
						&& object.getY() == 4249 && player.getPlane() == 1) {// Stairs
					player.setNextTile(new Tile(3149, 4251, 2));
				} else if (id == 29668 && object.getX() == 3157
						&& object.getY() == 4249 && player.getPlane() == 1) {// Stairs
					player.setNextTile(new Tile(3157, 4251, 2));
				} else if (id == 29664 && object.getX() == 3157
						&& object.getY() == 4246 && player.getPlane() == 1) {// Stairs
					player.setNextTile(new Tile(3157, 4244, 2));
					// PLANE 2
				} else if (id == 29655 && player.getPlane() == 2) {// Stairs
					player.setNextTile(new Tile(3146, 4246, 1));
				} else if (id == 29659 && player.getPlane() == 2) {// Stairs
					player.setNextTile(new Tile(3146, 4249, 1));
				} else if (id == 29667 && player.getPlane() == 2) {// Stairs
					player.setNextTile(new Tile(3160, 4249, 1));
				} else if (id == 29663 && player.getPlane() == 2) {// Stairs
					player.setNextTile(new Tile(3160, 4246, 1));
					// P2
				} else if (id == 29672 && player.getPlane() == 2) {// Stairs
					player.setNextTile(new Tile(3171, 4271, 3));
				} else if (id == 29671 && player.getPlane() == 3) {// Stairs
					player.setNextTile(new Tile(3174, 4273, 2));
				} else if (id == 29729 && player.getPlane() == 3) {// ROPE UP
					player.playerSafety3 = true;
					player.setNextTile(new Tile(3077, 3462, 0));
					// PROF ROOM
				} else if (id == 29592 && player.getPlane() == 0) {// Stairs
					player.setNextTile(new Tile(3086, 4247, 0));
				} else if (id == 1729) { // STAIRCASE
					return;
				} else if (id == 24364) { // STAIRCASE
					return;
				} else if (id == 26341) {
					player.useStairs(827, new Tile(2882, 5311, 0), 2, 1,
							"You climb down the rope...");
					player.getControllerManager().startController("GodWars");
				} else if (id == 12230 && object.getX() == 1752
						&& object.getY() == 5136) {// mole
					// exit
					player.setNextTile(new Tile(2986, 3316, 0));
				} else if (id == 15522) {// portal sign
					if (player.withinDistance(new Tile(1598, 4504, 0), 1)) {// PORTAL
						// 1
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13,
								"Edgeville");
						player.getPackets()
								.sendIComponentText(
										327,
										14,
										"This portal will take you to edgeville. There "
												+ "you can multi pk once past the wilderness ditch.");
					}
					if (player.withinDistance(new Tile(1598, 4508, 0), 1)) {// PORTAL
						// 2
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13,
								"Mage Bank");
						player.getPackets()
								.sendIComponentText(
										327,
										14,
										"This portal will take you to the mage bank. "
												+ "The mage bank is a 1v1 deep wilderness area.");
					}
					if (player.withinDistance(new Tile(1598, 4513, 0), 1)) {// PORTAL
						// 3
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13,
								"Magic's Portal");
						player.getPackets()
								.sendIComponentText(
										327,
										14,
										"This portal will allow you to teleport to areas that "
												+ "will allow you to change your magic spell book.");
					}
				} else if (id == 38811 || id == 37929) {// corp beast
					if (object.getX() == 2971 && object.getY() == 4382)
						player.getInterfaceManager().sendInterface(650);
					else if (object.getX() == 2918 && object.getY() == 4382) {
						player.stopAll();
						player.setNextTile(new Tile(
								player.getX() == 2921 ? 2917 : 2921, player
										.getY(), player.getZ()));
					}
				} else if (id == 37928 && object.getX() == 2883
						&& object.getY() == 4370) {
					player.stopAll();
					player.setNextTile(new Tile(3214, 3782, 0));
					player.getControllerManager().startController("Wilderness");
				} else if (id == 38815 && object.getX() == 3209
						&& object.getY() == 3780 && object.getZ() == 0) {
					if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < 37
							|| player.getSkills().getLevelForXp(Skills.MINING) < 45
							|| player.getSkills().getLevelForXp(
									Skills.SUMMONING) < 23
							|| player.getSkills().getLevelForXp(
									Skills.FIREMAKING) < 47
							|| player.getSkills().getLevelForXp(Skills.PRAYER) < 55) {
						player.getPackets()
								.sendGameMessage(
										"You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
						return;
					}
					player.stopAll();
					player.setNextTile(new Tile(2885, 4372, 0));
					player.getControllerManager().forceStop();
				} else if (id == 9369) {
					player.getControllerManager().startController("FightPits");
				} else if (id == 2079 || id == 172) {
					if (player.getInventory().containsItem(CrystalChest.KEY, 1)) {
						CrystalChest.searchChest(player);
						player.getPackets().sendGameMessage(
								"You open the chest.");
					} else {
						player.getPackets().sendGameMessage(
								"You need a Crystal Key to open this chest!");
					}
					return;
				} else if (id == 20600) {
					player.setNextTile(new Tile(3077, 10058, 0));
				} else if (id == 18342) {
					player.setNextTile(new Tile(3071, 3649, 0));
				} else if (id == 52859) {
					if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 85) {
						player.getPackets()
								.sendGameMessage(
										"You need a level of 85 Dungeoneering to enter that dungeon");
						return;
					}
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new Tile(
							1297, 4510, 0), new int[0]);
				} else if (id == 2475) {
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new Tile(
							3186, 5725, 0), new int[0]);
					player.getControllerManager().startController("FunPk");
				} else if (id == 4493) {
					player.setNextTile(new Tile(3433, 3538, 1));
				} else if (id == 4494) {
					player.setNextTile(new Tile(3438, 3538, 0));
				} else if (id == 4496) {
					player.setNextTile(new Tile(3412, 3541, 1));
				} else if (id == 4495) {
					player.setNextTile(new Tile(3417, 3541, 2));
				} else if (id == 9319) {
					if (!Agility.hasLevel(player, x == 3422 && y == 3550 ? 61
							: 71))
						return;
					player.useStairs(828, new Tile(player.getX(),
							player.getY(), player.getPlane() + 1), 1, 2);
				} else if (id == 9320) {
					if (!Agility.hasLevel(player, 61))
						return;
					player.useStairs(827, new Tile(player.getX(),
							player.getY(), player.getPlane() - 1), 1, 2);
				} else if (id == 52875) {
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new Tile(
							3033, 9598, 0), new int[0]);
				} else if (id == 54019 || id == 54020 || id == 55301)
					PkRank.showRanks(player);
				else if (id == 1817 && object.getX() == 2273
						&& object.getY() == 4680) { // kbd
					// lever
					Magic.pushLeverTeleport(player, new Tile(3067, 10254, 0));
				} else if (id == 1816 && object.getX() == 3067
						&& object.getY() == 10252) { // kbd
					// out
					// lever
					Magic.pushLeverTeleport(player, new Tile(2273, 4681, 0));
					player.getControllerManager().forceStop();
				} else if (id == 32015 && object.getX() == 3069
						&& object.getY() == 10256) { // kbd
					// stairs
					player.useStairs(828, new Tile(3017, 3848, 0), 1, 2);
					player.getControllerManager().startController("Wilderness");
				} else if (id == 1765 && object.getX() == 3017
						&& object.getY() == 3849) { // kbd
					// out
					// stairs
					player.stopAll();
					player.setNextTile(new Tile(3069, 10255, 0));
				} else if (id == 14315) {
					if (Lander.canEnter(player, 0))
						return;
				} else if (id == 25631) {
					if (Lander.canEnter(player, 1))
						return;
				} else if (id == 25632) {
					if (Lander.canEnter(player, 2))
						return;
				} else if (id == 5959) {
					if (player.getX() == 3089 || player.getX() == 3090)
						Magic.pushLeverTeleport(player, new Tile(2539, 4712, 0));
				} else if (id == 5960) {
					Magic.pushLeverTeleport(player, new Tile(3089, 3957, 0));
				} else if (id == 1814) {
					Magic.pushLeverTeleport(player, new Tile(3155, 3923, 0));
				} else if (id == 1815) {
					Magic.pushLeverTeleport(player, new Tile(2561, 3311, 0));
				} else if (id == 62675)
					player.getCutscenesManager().play("DTPreview");
				else if (id == 62681)
					player.getDominionTower().viewScoreBoard();
				else if (id == 62678 || id == 62679)
					player.getDominionTower().openModes();
				else if (id == 62688)
					player.getDialogueManager().startDialogue("DTClaimRewards");
				else if (id == 62677)
					player.getDominionTower().talkToFace();
				else if (id == 62680)
					player.getDominionTower().openBankChest();
				else if (id == 48797)
					player.useStairs(-1, new Tile(3877, 5526, 1), 0, 1);
				else if (id == 48798)
					player.useStairs(-1, new Tile(3246, 3198, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5533)
					player.useStairs(-1, new Tile(3861, 5533, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5543)
					player.useStairs(-1, new Tile(3861, 5543, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5533)
					player.useStairs(-1, new Tile(3861, 5533, 0), 0, 1);
				else if (id == 48677 && x == 3858 && y == 5543)
					player.useStairs(-1, new Tile(3856, 5543, 1), 0, 1);
				else if (id == 48677 && x == 3858 && y == 5533)
					player.useStairs(-1, new Tile(3856, 5533, 1), 0, 1);
				else if (id == 48679)
					player.useStairs(-1, new Tile(3875, 5527, 1), 0, 1);
				else if (id == 48688)
					player.useStairs(-1, new Tile(3972, 5565, 0), 0, 1);
				else if (id == 48683)
					player.useStairs(-1, new Tile(3868, 5524, 0), 0, 1);
				else if (id == 48682)
					player.useStairs(-1, new Tile(3869, 5524, 0), 0, 1);
				else if (id == 62676) { // dominion exit
					player.useStairs(-1, new Tile(3374, 3093, 0), 0, 1);
				} else if (id == 62674) { // dominion entrance
					player.useStairs(-1, new Tile(3744, 6405, 0), 0, 1);
				} else if (id == 3192) {
					PkRank.showRanks(player);
				} else if (id == 65349) {
					player.useStairs(-1, new Tile(3044, 10325, 0), 0, 1);
				} else if (id == 32048 && object.getX() == 3043
						&& object.getY() == 10328) {
					player.useStairs(-1, new Tile(3045, 3927, 0), 0, 1);
				} else if (id == 26194) {
					player.getDialogueManager().startDialogue("PartyRoomLever");
				} else if (id == 61190 || id == 61191 || id == 61192
						|| id == 61193) {
					if (objectDef.containsOption(0, "Chop down"))
						player.getActionManager()
								.setAction(
										new Woodcutting(object,
												TreeDefinitions.NORMAL));
				} else if (id == 20573)
					player.getControllerManager().startController(
							"RefugeOfFear");
				else if (id == 46317) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getDialogueManager().startDialogue(
								"SimpleMessage", "You dont have enough space.");
						return;
					}
					player.lock(3);
					player.setNextAnimation(new Animation(881));
					player.getInventory().addItem(8794, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You take a saw from the crate.");
				} else if (id == 46297) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"You dont have enough space to pick up any logs.");
						return;
					}
					player.lock(4);
					player.setNextAnimation(new Animation(827));
					player.getInventory().addItem(1511, 5);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You take some logs.");
				} else if (id == 46300) {
					player.getInterfaceManager().sendInterface(902);
				} else if (id == 46304) {
					if (player.getInventory().containsItem(1511, 5)) {
						player.lock(3);
						player.setNextAnimation(new Animation(5));
						player.getInventory().deleteItem(1511, 5);
						player.setLoadedLogs(player.getLoadedLogs() + 5);
						player.getPackets().sendConfigByFile(4214, 30);
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"You throw some logs into the Conveyor belt hopper.");
						return;
					}
					if (player.getInventory().containsItem(1511, 1)) {
						player.lock(3);
						player.setNextAnimation(new Animation(5));
						player.getInventory().deleteItem(1511, 1);
						player.setLoadedLogs(player.getLoadedLogs() + 1);
						player.getPackets().sendConfigByFile(4214, 30);
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"You throw a log into the Conveyor belt hopper.");
						return;
					}
					if (player.getSkills().getLevel(Skills.CONSTRUCTION) >= 1) {
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"You need 5 Logs to function the Conveyor belt hopper.");
					}
				} else if (id == 46309) {
					if (player.getLoadedLogs() > 0) {
						int convertedPlanks = player.getLoadedLogs() * 2;
						player.lock(3);
						player.setNextAnimation(new Animation(827));
						player.getInventory().addItem(960,
								player.getLoadedLogs() * 2);
						player.setLoadedLogs(0);
						player.getPackets().sendConfigByFile(4214, 0);
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You take " + convertedPlanks + " planks.");
						return;
					}
				} else if (id == 3044)
					player.getDialogueManager().startDialogue("SmeltingD",
							object);
				// crucible
				else if (id == 67050) {
					player.useStairs(-1, new Tile(3359, 6110, 0), 0, 1);
				} else if (id == 67053)
					player.useStairs(-1, new Tile(3120, 3519, 0), 0, 1);
				else if (id == 67051)
					player.getDialogueManager().startDialogue("Marv", false);
				else if (id == 67052)
					Crucible.enterCrucibleEntrance(player);
				else {
					switch (objectDef.name.toLowerCase()) {
					case "obelisk":
						if (player.getRegionId() == 11573) {
							Summoning.sendInterface(player);
							player.setNextFaceTile(object);
						}
						break;
					case "nature rift":
						player.setNextTile(new Tile(2398, 4841, 0));
						break;
					case "death rift":
						player.setNextTile(new Tile(2205, 4834, 0));
						break;
					case "blood rift":
						player.setNextTile(new Tile(2457, 4895, 1));
						break;
					case "law rift":
						player.setNextTile(new Tile(2464, 4830, 0));
						break;
					case "chaos rift":
						player.setNextTile(new Tile(2271, 4840, 0));
						break;
					case "cosmic rift":
						player.setNextTile(new Tile(2142, 4831, 0));
						break;
					case "body rift":
						player.setNextTile(new Tile(2522, 4833, 0));
						break;
					case "fire rift":
						player.setNextTile(new Tile(2585, 4834, 0));
						break;
					case "earth rift":
						player.setNextTile(new Tile(2657, 4830, 0));
						break;
					case "mind rift":
						player.setNextTile(new Tile(2793, 4828, 0));
						break;
					case "air rift":
						player.setNextTile(new Tile(2841, 4829, 0));
						break;
					case "water rift":
						player.setNextTile(new Tile(3494, 4832, 0));
						break;
					case "passageway":
						/*
						 * for(Player p : battleTerrace.waiting) { if(p ==
						 * player) { battleTerraceWaiting.leaveGame(player);
						 * return; } continue; }
						 * battleTerrace.joinLobby(player);
						 */
						break;
					case "trapdoor":
					case "manhole":
						if (objectDef.containsOption(0, "Open")) {
							GameObject openedHole = new GameObject(object
									.getId() + 1, object.getType(), object
									.getRotation(), object.getX(), object
									.getY(), object.getZ());
							player.faceObject(openedHole);
							Engine.spawnTemporaryObject(openedHole, 60000, true);
						}
						break;
					case "closed chest":
						if (objectDef.containsOption(0, "Open")) {
							player.setNextAnimation(new Animation(536));
							player.lock(2);
							GameObject openedChest = new GameObject(object
									.getId() + 1, object.getType(), object
									.getRotation(), object.getX(), object
									.getY(), object.getZ());
							// if (World.removeTemporaryObject(object,
							// 60000,
							// true)) {
							player.faceObject(openedChest);
							Engine.spawnTemporaryObject(openedChest, 60000,
									true);
							// }
						}
						break;
					case "open chest":
						if (objectDef.containsOption(0, "Search"))
							player.getPackets().sendGameMessage(
									"You search the chest but find nothing.");
						break;
					case "fairy ring":
					case "enchanted land":
						FairyRing
								.openRingInterface(player, object, id == 12128);
						break;
					case "spiderweb":
						if (object.getRotation() == 2) {
							player.lock(2);
							if (Utilities.getRandom(1) == 0) {
								player.addWalkSteps(player.getX(), player
										.getY() < y ? object.getY() + 2
										: object.getY() - 1, -1, false);
								player.getPackets().sendGameMessage(
										"You squeeze though the web.");
							} else
								player.getPackets()
										.sendGameMessage(
												"You fail to squeeze though the web; perhaps you should try again.");
						}
						break;
					case "web":
						if (objectDef.containsOption(0, "Slash"))
							if (player.getEquipment().getWeaponId() > 0) {
								player.setNextAnimation(new Animation(
										PlayerCombat.getWeaponAttackEmote(
												player.getEquipment()
														.getWeaponId(), player
														.getCombatDefinitions()
														.getAttackStyle())));
								slashWeb(player, object);
							} else {
								player.getPackets()
										.sendGameMessage(
												"You need something sharp to hit through the web.");
							}
						break;
					case "anvil":
						if (objectDef.containsOption(0, "Smith")) {
							ForgingBar bar = ForgingBar.getBar(player);
							if (bar != null)
								ForgingInterface.sendSmithingInterface(player,
										bar);
							else
								player.getPackets()
										.sendGameMessage(
												"You have no bars for your smithing level.");
						}
						break;
					case "tin ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Tin_Ore));
						break;
					case "gold ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Gold_Ore));
						break;
					case "iron ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Iron_Ore));
						break;
					case "silver ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Silver_Ore));
						break;
					case "coal rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Coal_Ore));
						break;
					case "clay rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Clay_Ore));
						break;
					case "copper ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Copper_Ore));
						break;
					case "adamantite ore rocks":
						player.getActionManager()
								.setAction(
										new Mining(object,
												RockDefinitions.Adamant_Ore));
						break;
					case "runite ore rocks":
						player.getActionManager().setAction(
								new Mining(object, RockDefinitions.Runite_Ore));
						break;
					case "granite rocks":
						player.getActionManager()
								.setAction(
										new Mining(object,
												RockDefinitions.Granite_Ore));
						break;
					case "sandstone rocks":
						player.getActionManager().setAction(
								new Mining(object,
										RockDefinitions.Sandstone_Ore));
						break;
					case "mithril ore rocks":
						player.getActionManager()
								.setAction(
										new Mining(object,
												RockDefinitions.Mithril_Ore));
						break;
					case "bank deposit box":
						if (objectDef.containsOption(0, "Deposit"))
							player.getBank().openDepositBox();
						break;
					case "potter's wheel":
					case "pottery wheel":
						player.getDialogueManager().startDialogue(
								"PotteryWheel");
						break;
					case "pottery oven":
						player.getDialogueManager().startDialogue(
								"PotteryFurnace");
						break;
					case "bank":
					case "bank chest":
					case "bank booth":
					case "counter":
						if (objectDef.containsOption(0, "Bank")
								|| objectDef.containsOption(0, "Use"))
							player.getBank().openBank();
						break;
					// Woodcutting start
					case "tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.NORMAL));
						}
						break;
					case "achey tree":
						if (objectDef.containsOption(0, "Chop")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.ACHEY));
						}
						break;
					case "evergreen":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.EVERGREEN));
						}
						break;
					case "dead tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.DEAD));
						}
						break;
					case "oak":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager()
									.setAction(
											new Woodcutting(object,
													TreeDefinitions.OAK));
						}
						break;
					case "willow":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.WILLOW));
						}
						break;
					case "maple tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.MAPLE));
						}
						break;
					case "arctic pine":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.ARCTIC_PINE));
						}
						break;
					case "ivy":
						if (objectDef.containsOption(0, "Chop")) {
							player.getActionManager()
									.setAction(
											new Woodcutting(object,
													TreeDefinitions.IVY));
						}
						break;
					case "yew":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager()
									.setAction(
											new Woodcutting(object,
													TreeDefinitions.YEW));
						}
						break;
					case "magic tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.MAGIC));
						}
						break;
					case "cursed magic tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(
									new Woodcutting(object,
											TreeDefinitions.CURSED_MAGIC));
						}
						break;
					// Woodcutting end
					case "gate":
					case "large door":
					case "metal door":
						if (object.getType() == 0
								&& objectDef.containsOption(0, "Open"))
							if (!handleGate(player, object))
								Door.handleDoor(player, object);
						break;
					case "door":
						if (object.getType() == 0
								&& (objectDef.containsOption(0, "Open") || objectDef
										.containsOption(0, "Unlock")))
							Door.handleDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 1);
						break;
					case "staircase":
						handleStaircases(player, object, 1);
						break;
					case "small obelisk":
						if (objectDef.containsOption(0, "Renew-points")) {
							int summonLevel = player.getSkills().getLevelForXp(
									Skills.SUMMONING);
							if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
								player.lock(3);
								player.setNextAnimation(new Animation(8502));
								player.getSkills().set(Skills.SUMMONING,
										summonLevel);
								player.getPackets()
										.sendGameMessage(
												"You've recharged your Summoning points.",
												true);
							} else
								player.getPackets()
										.sendGameMessage(
												"You already have full Summoning points.");
						}
						break;
					case "altar":
						if (objectDef.containsOption(0, "Pray")
								|| objectDef.containsOption(0, "Pray-at")) {
							final int maxPrayer = player.getSkills()
									.getLevelForXp(Skills.PRAYER) * 10;
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.lock(5);
								player.getPackets().sendGameMessage(
										"You pray to the gods...", true);
								player.setNextAnimation(new Animation(645));
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										player.getPrayer().restorePrayer(
												maxPrayer);
										player.getPackets()
												.sendGameMessage(
														"...and recharged your prayer.",
														true);
									}
								}, 2);
							} else
								player.getPackets().sendGameMessage(
										"You already have full prayer.");
							if (id == 6552)
								player.getDialogueManager().startDialogue(
										"AncientAltar");
						}
						break;
					default:
						// player.getPackets().sendGameMessage(
						// "Nothing interesting happens.");
						break;
					}
				}
				if (GameConstants.DEBUG)
					Logger.log(
							"ObjectHandler",
							"clicked 1 at object id : " + id + ", "
									+ object.getX() + ", " + object.getY()
									+ ", " + object.getZ() + ", "
									+ object.getType() + ", "
									+ object.getRotation() + ", "
									+ object.getDefinitions().name);
			}
		}, true));
	}

	private static void handleOption2(final Player player,
			final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControllerManager().processObjectClick2(object))
					return;
				if (id == 6) {
					player.getDwarfCannon().pickUpDwarfCannon(0, object);
					return;
				}
				switch (objectDef.name.toLowerCase()) {
				case "obelisk":
					if (objectDef.containsOption(1, "Renew-points")) {
						if (player.getSkills().getLevel(23) < player
								.getSkills().getLevelForXp(23)) {
							player.lock(5);
							player.getPackets().sendGameMessage(
									"You feel the obelisk", true);
							player.setNextAnimation(new Animation(8502));
							player.setNextGraphics(new Graphics(1308));
							EngineTaskManager.schedule(new EngineTask() {

								@Override
								public void run() {
									player.getSkills().restoreSummoning();
									player.getPackets().sendGameMessage(
											"...and recharge all your skills.",
											true);
								}
							}, 2);
						} else {
							player.getPackets().sendGameMessage(
									"You already have full summoning.", true);
						}
						return;
					}
					break;
				}
				if (Pickables.handlePickable(player, object))
					return;
				if (object.getDefinitions().name.equalsIgnoreCase("furnace"))
					player.getDialogueManager().startDialogue("SmeltingD",
							object);
				if (object.getDefinitions().name.toLowerCase().contains(
						"spinning")) {
					player.getDialogueManager().startDialogue("SpinningD");
				} else if (id == 17010)
					player.getDialogueManager().startDialogue("LunarAltar");
				Animation THIEVING_ANIMATION = new Animation(881);
				if (id == 1317) {
					player.getDialogueManager().startDialogue("SpiritTree");
					return;
				}
				if (id == 4875) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory.");
						return;
					}
					if (player.getThievingDelay() > Utilities
							.currentTimeMillis()) {
						return;
					}
					if (player.getSkills().getLevel(Skills.THIEVING) >= 30) {
						player.applyHit(new Hit(player, 5,
								HitLook.REGULAR_DAMAGE, 1));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(player.getThievingDelay()
								+ Utilities.currentTimeMillis() + 1000);
						player.getInventory().addItem(1739, 1);
						player.getSkills().addXp(17, 50);
					} else {
						player.getPackets()
								.sendGameMessage(
										"You need at least 30 thieving to steal from this stall");
					}
					return;
				} else if (id == 4874) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory.");
						return;
					}
					if (player.getThievingDelay() > Utilities
							.currentTimeMillis()) {
						return;
					}
					player.applyHit(new Hit(player, 5, HitLook.REGULAR_DAMAGE,
							1));
					player.setNextAnimation(THIEVING_ANIMATION);
					player.setThievingDelay(player.getThievingDelay()
							+ Utilities.currentTimeMillis() + 1000);
					player.getInventory().addItem(950, 1);
					player.getSkills().addXp(17, 55);
					return;
				} else if (id == 4876) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory.");
						return;
					}
					if (player.getThievingDelay() > Utilities
							.currentTimeMillis()) {
						return;
					}
					if (player.getSkills().getLevel(Skills.THIEVING) >= 50) {
						player.applyHit(new Hit(player, 5,
								HitLook.REGULAR_DAMAGE, 1));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(player.getThievingDelay()
								+ Utilities.currentTimeMillis() + 1000);
						player.getInventory().addItem(1635, 1);
						player.getSkills().addXp(17, 60);
					} else {
						player.getPackets()
								.sendGameMessage(
										"You need at least 50 thieving to steal from this stall");
					}
					return;
				} else if (id == 4877) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory.");
						return;
					}
					if (player.getThievingDelay() > Utilities
							.currentTimeMillis()) {
						return;
					}
					if (player.getSkills().getLevel(Skills.THIEVING) >= 75) {
						player.applyHit(new Hit(player, 5,
								HitLook.REGULAR_DAMAGE, 1));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(player.getThievingDelay()
								+ Utilities.currentTimeMillis() + 1000);
						player.getInventory().addItem(7650, 1);
						player.getSkills().addXp(17, 65);
					} else {
						player.getPackets()
								.sendGameMessage(
										"You need at least 75 thieving to steal from this stall");
					}
					return;
				} else if (id == 4878) {
					if (player.getInventory().getFreeSlots() < 1) {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory.");
						return;
					}
					if (player.getThievingDelay() > Utilities
							.currentTimeMillis()) {
						return;
					}
					if (player.getSkills().getLevel(Skills.THIEVING) >= 85) {
						player.applyHit(new Hit(player, 5,
								HitLook.REGULAR_DAMAGE, 1));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(player.getThievingDelay()
								+ Utilities.currentTimeMillis() + 1000);
						player.getInventory().addItem(1662, 1);
						player.getSkills().addXp(17, 70);
					} else {
						player.getPackets()
								.sendGameMessage(
										"You need at least 85 thieving to steal from this stall");
					}
					return;
				} else if (id == 62677)
					player.getDominionTower().openRewards();
				else if (id == 62688)
					player.getDialogueManager().startDialogue(
							"SimpleMessage",
							"You've a Dominion Factor of "
									+ player.getDominionFactor() + ".");
				else if (id == 68107)
					FightKiln.enterFightKiln(player, true);
				else if (id == 34384 || id == 34383 || id == 14011
						|| id == 7053 || id == 34387 || id == 34386)
					Thieving.handleStalls(player, object);
				else if (id == 2418)
					PartyRoom.openPartyChest(player);
				else if (id == 2646) {
					Engine.removeTemporaryObject(object, 50000, true);
					player.getInventory().addItem(1779, 1);
					// crucible
				} else if (id == 67051)
					player.getDialogueManager().startDialogue("Marv", true);
				else {
					switch (objectDef.name.toLowerCase()) {
					case "cabbage":
						if (objectDef.containsOption(1, "Pick")
								&& player.getInventory().addItem(1965, 1)) {
							player.setNextAnimation(new Animation(827));
							player.lock(2);
							Engine.removeTemporaryObject(object, 60000, false);
						}
						break;
					case "bank":
					case "bank chest":
					case "bank booth":
					case "counter":
						if (objectDef.containsOption(1, "Bank"))
							player.getBank().openBank();
						break;
					case "gates":
					case "gate":
					case "metal door":
						if (object.getType() == 0
								&& objectDef.containsOption(1, "Open"))
							handleGate(player, object);
						break;
					case "door":
						if (object.getType() == 0
								&& objectDef.containsOption(1, "Open"))
							Door.handleDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 2);
						break;
					case "staircase":
						handleStaircases(player, object, 2);
						break;
					default:
						player.getPackets().sendGameMessage(
								"Nothing interesting happens.");
						break;
					}
				}
				if (GameConstants.DEBUG)
					Logger.log("ObjectHandler", "clicked 2 at object id : "
							+ id + ", " + object.getX() + ", " + object.getY()
							+ ", " + object.getZ());
			}
		}, true));
	}

	private static void handleOption3(final Player player,
			final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControllerManager().processObjectClick3(object))
					return;
				if (id == 62688)
					ShopsHandler.openShop(player, 50);
				switch (objectDef.name.toLowerCase()) {
				case "gate":
				case "metal door":
					if (object.getType() == 0
							&& objectDef.containsOption(2, "Open"))
						handleGate(player, object);
					break;
				case "door":
					if (object.getType() == 0
							&& objectDef.containsOption(2, "Open"))
						Door.handleDoor(player, object);
					break;
				case "ladder":
					handleLadder(player, object, 3);
					break;
				case "staircase":
					handleStaircases(player, object, 3);
					break;
				default:
					player.getPackets().sendGameMessage(
							"Nothing interesting happens.");
					break;
				}
				if (GameConstants.DEBUG)
					Logger.log("ObjectHandler", "cliked 3 at object id : " + id
							+ ", " + object.getX() + ", " + object.getY()
							+ ", " + object.getZ() + ", ");
			}
		}, true));
	}

	private static void handleOption4(final Player player,
			final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControllerManager().processObjectClick4(object))
					return;
				// living rock Caverns
				if (id == 45076)
					MiningBase
							.propect(player,
									"This rock contains a large concentration of gold.");
				else if (id == 5999)
					MiningBase
							.propect(player,
									"This rock contains a large concentration of coal.");
				else {
					switch (objectDef.name.toLowerCase()) {
					default:
						player.getPackets().sendGameMessage(
								"Nothing interesting happens.");
						break;
					}
				}
				if (GameConstants.DEBUG)
					Logger.log("ObjectHandler", "cliked 4 at object id : " + id
							+ ", " + object.getX() + ", " + object.getY()
							+ ", " + object.getZ() + ", ");
			}
		}, true));
	}

	private static void handleOption5(final Player player,
			final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControllerManager().processObjectClick5(object))
					return;
				if (id == -1) {
					// unused
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "fire":
						if (objectDef.containsOption(4, "Add-logs"))
							Bonfire.addLogs(player, object);
						break;
					default:
						player.getPackets().sendGameMessage(
								"Nothing interesting happens.");
						break;
					}
				}
				if (GameConstants.DEBUG)
					Logger.log("ObjectHandler", "cliked 5 at object id : " + id
							+ ", " + object.getX() + ", " + object.getY()
							+ ", " + object.getZ() + ", ");
			}
		}, true));
	}

	private static void handleOptionExamine(final Player player,
			final GameObject object) {
		player.getPackets().sendGameMessage(
				"It's an " + object.getDefinitions().name + ".");
		if (GameConstants.DEBUG)
			if (GameConstants.DEBUG)

				Logger.log(
						"ObjectHandler",
						"examined object id : " + object.getId() + ", "
								+ object.getX() + ", " + object.getY() + ", "
								+ object.getZ() + ", " + object.getType()
								+ ", " + object.getRotation() + ", "
								+ object.getDefinitions().name);
	}

	private static boolean handleStaircases(Player player, GameObject object,
			int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getZ() == 3)
				return false;
			player.useStairs(-1,
					new Tile(player.getX(), player.getY(), player.getZ() + 1),
					0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getZ() == 0)
				return false;
			player.useStairs(-1,
					new Tile(player.getX(), player.getY(), player.getZ() - 1),
					0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getZ() == 3 || player.getZ() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbNoEmoteStairs",
					new Tile(player.getX(), player.getY(), player.getZ() + 1),
					new Tile(player.getX(), player.getY(), player.getZ() - 1),
					"Go up the stairs.", "Go down the stairs.");
		} else
			return false;
		return false;
	}

	private static void slashWeb(Player player, GameObject object) {

		if (Utilities.getRandom(1) == 0) {
			Engine.spawnTemporaryObject(new GameObject(object.getId() + 1,
					object.getType(), object.getRotation(), object.getX(),
					object.getY(), object.getZ()), 60000, true);
			player.getPackets().sendGameMessage("You slash through the web!");
		} else
			player.getPackets().sendGameMessage(
					"You fail to cut through the web.");
	}

	private ObjectHandler() {

	}

	private static boolean getRepeatedTele(Player player, int x1, int y1,
			int p1, int x2, int y2, int p2) {
		if (player.getX() == x1 && player.getY() == y1) {
			player.useStairs(17803, new Tile(x2, y2, p2), 2, 3);
			player.setNextGraphics(new Graphics(3154));
			return true;
		} else if (player.getX() == x2 && player.getY() == y2) {
			player.useStairs(17803, new Tile(x1, y1, p1), 2, 3);
			player.setNextGraphics(new Graphics(3154));
			return true;
		}
		return false;
	}
}