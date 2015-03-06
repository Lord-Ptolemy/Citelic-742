package com.citelic.game.entity.player.content.actions.skills.construction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.Builds;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.HObject;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.POHLocation;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.Roof;
import com.citelic.game.entity.player.content.actions.skills.construction.HouseConstants.Room;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.skills.construction.HouseController;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.MapUtils;
import com.citelic.game.map.Region;
import com.citelic.game.map.RegionBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

/*
 * House class only contains house data + support methods to change that data
 * HouseController provides support between player interaction inside house and housemanager
 * HouseConstants handles the constants such as existing rooms, builds, roofs
 */
public class House implements Serializable {

	private static final long serialVersionUID = 8111719490432901786L;
	// Used for inter 396
	private static final int[] BUILD_INDEXES = { 0, 2, 4, 6, 1, 3, 5 };
	private static final int[] DOOR_DIR_X = { -1, 0, 1, 1 };
	private static final int[] DOOR_DIR_Y = { 0, 1, 0, -1 };
	public static int LOGGED_OUT = 0, KICKED = 1, TELEPORTED = 2;
	// dont name it rooms or it will null server
	private List<RoomReference> roomsR;
	private byte look;
	private POHLocation location;
	private boolean buildMode;
	private boolean arriveInPortal;
	private transient Player player;
	private transient boolean locked;
	// house loaded datas
	private transient List<Player> players;
	private transient int[] boundChuncks;
	private transient boolean loaded;
	private byte build;

	public House() {
		buildMode = false;
		roomsR = new ArrayList<RoomReference>();
		addRoom(HouseConstants.Room.GARDEN, 3, 3, 0, 0);
		getRoom(3, 3, 0).addObject(Builds.CENTREPIECE, 0);
	}

	public static void enterHouse(Player player, String username) {
		Player owner = Engine.getPlayerByDisplayName(username);
		if (owner == null || !owner.isRunning()
				|| !player.getFriendsIgnores().isOnline(owner)
				|| owner.getHouse().locked) {
			player.getPackets().sendGameMessage(
					"That player is offline, or has privacy mode enabled.");
			return;
		}
		if (owner.getHouse().location == null
				|| !player.withinDistance(owner.getHouse().location.getTile(),
						16)) {
			player.getPackets().sendGameMessage(
					"Your house is at "
							+ Utilities.formatPlayerNameForDisplay(owner
									.getHouse().location.name()) + ".");
			return;
		}
	}

	public static void leaveHouse(Player player) {
		Controller controller = player.getControllerManager().getController();
		if (controller == null || !(controller instanceof HouseController)) {
			player.getPackets().sendGameMessage("You're not in a house.");
			return;
		}
		((HouseController) controller).getHouse().leaveHouse(player,
				House.KICKED);
	}

	public boolean addRoom(HouseConstants.Room room, int x, int y, int plane,
			int rotation) {
		return roomsR.add(new RoomReference(room, x, y, plane, rotation));
	}

	public void build(int slot) {
		if (player.getInterfaceManager().containsInterface(396)) {
			for (int i = 0; i < House.BUILD_INDEXES.length; i++)
				if (slot == House.BUILD_INDEXES[i]) {
					slot = i;
					break;
				}
		}
		final Builds build = (Builds) player.getTemporaryAttributtes().get(
				"OpenedBuild");
		GameObject object = (GameObject) player.getTemporaryAttributtes().get(
				"OpenedBuildObject");
		if (build == null || object == null || build.getPieces().length <= slot)
			return;
		int roomX = object.getChunkX() - boundChuncks[0];
		int roomY = object.getChunkY() - boundChuncks[1];
		final RoomReference room = getRoom(roomX, roomY, object.getZ());
		if (room == null)
			return;
		final HObject piece = build.getPieces()[slot];
		if (player.getSkills().getLevel(Skills.CONSTRUCTION) < piece.getLevel()) {
			player.getPackets().sendGameMessage(
					"Your level of construction is too low for this build.");
			return;
		}
		/*
		 * for (Item item : piece.getRequirements())
		 * System.out.println(item.getId() + ", " + item.getAmount());
		 */
		if (!player.getInventory().containsItems(piece.getRequirements())) {
			player.getPackets().sendGameMessage(
					"You dont have the right materials.");
			return;
		}
		if (build.isWater() ? !hasWaterCan() : !player.getInventory()
				.containsItemToolBelt(HouseConstants.HAMMER)
				|| !player.getInventory().containsItemToolBelt(
						HouseConstants.SAW)) {
			player.getPackets()
					.sendGameMessage(
							build.isWater() ? "You will need a watering can with some water in it instead of hammer and saw to build plants."
									: "You will need a hammer and saw to build furniture.");
			return;
		}
		final ObjectReference oref = room.addObject(build, slot);
		player.closeInterfaces();
		player.lock();
		player.setNextAnimation(new Animation(build.isWater() ? 2293 : 3683));
		for (Item item : piece.getRequirements()) {
			player.getInventory().deleteItem(item);
		}
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getSkills().addXp(Skills.CONSTRUCTION, piece.getXP());
				if (build.isWater()) {
					player.getSkills().addXp(Skills.FARMING, piece.getXP());
				}
				refreshObject(room, oref, false);
				player.lock(1);
			}
		}, 2);
	}

	public void climbStaircase(GameObject object, boolean up) {
		int roomX = object.getChunkX() - boundChuncks[0];
		int roomY = object.getChunkY() - boundChuncks[1];
		RoomReference room = getRoom(roomX, roomY, object.getZ());
		if (room == null)
			return;
		if (room.plane == (up ? 2 : 0)) {
			player.getPackets().sendGameMessage(
					"You are on the " + (up ? "highest" : "lowest")
							+ " possible level so you cannot add a room "
							+ (up ? "above" : "under") + " here.");
			return;
		}
		RoomReference roomTo = getRoom(roomX, roomY, room.plane + (up ? 1 : -1));
		if (roomTo == null) {
			if (buildMode) {
				player.getDialogueManager().startDialogue("CreateRoomStairsD",
						room, up);
			} else {
				player.getPackets().sendGameMessage(
						"These stairs do not lead anywhere.");
			}
			// start dialogue
			return;
		}
		if (roomTo.getStaircaseSlot() == -1) {
			player.getPackets().sendGameMessage(
					"These stairs do not lead anywhere.");
			return;
		}
		player.useStairs(-1,
				new Tile(player.getX(), player.getY(), player.getZ()
						+ (up ? 1 : -1)), 0, 1);

	}

	public void createHouse(final boolean tp) {
		Object[][][][] data = new Object[4][8][8][];
		for (RoomReference reference : roomsR) {
			data[reference.plane][reference.x][reference.y] = new Object[] {
					reference.room.getChunkX(), reference.room.getChunkY(),
					reference.rotation, reference.room.isShowRoof() };
		}
		if (!buildMode) { // construct roof
			for (int x = 1; x < 7; x++) {
				skipY: for (int y = 1; y < 7; y++) {
					for (int plane = 2; plane >= 0; plane--) {
						if (data[plane][x][y] != null) {
							boolean hasRoof = (boolean) data[plane][x][y][3];
							if (hasRoof) {
								byte rotation = (byte) data[plane][x][y][2];
								// TODO find best Roof
								data[plane + 1][x][y] = new Object[] {
										Roof.ROOF1.getChunkX(),
										Roof.ROOF1.getChunkY(), rotation, true };
								continue skipY;
							}
						}
					}
				}
			}
		}
		for (int plane = 0; plane < data.length; plane++) {
			for (int x = 0; x < data[plane].length; x++) {
				for (int y = 0; y < data[plane][x].length; y++) {
					if (data[plane][x][y] != null)
						RegionBuilder.copyChunk(
								(int) data[plane][x][y][0]
										+ ((boolean) data[plane][x][y][3]
												&& look >= 4 ? 8 : 0),
								(int) data[plane][x][y][1],
								(boolean) data[plane][x][y][3] ? look % 4
										: look, boundChuncks[0] + x,
								boundChuncks[1] + y, plane,
								(byte) data[plane][x][y][2]);
					else if (plane == 0)
						RegionBuilder.copyChunk(HouseConstants.LAND[0],
								HouseConstants.LAND[1], look, boundChuncks[0]
										+ x, boundChuncks[1] + y, plane, 0);
				}
			}
		}
		final Region region = Engine.getRegion(RegionBuilder.getRegionHash(
				boundChuncks[0] / 8, boundChuncks[1] / 8));
		List<GameObject> spawnedObjects = region.getSpawnedObjects();
		if (spawnedObjects != null)
			spawnedObjects.clear();
		List<GameObject> removedObjects = region.getRemovedObjects();
		if (removedObjects != null)
			removedObjects.clear();
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				if (region.getLoadMapStage() != 2)
					return;
				((TimerTask) this).cancel();
				for (RoomReference reference : roomsR) {
					int boundX = reference.x * 8;
					int boundY = reference.y * 8;
					for (int x = boundX; x < boundX + 8; x++) {
						for (int y = boundY; y < boundY + 8; y++) {
							GameObject[] objects = region.getObjects(
									reference.plane, x, y);
							if (objects != null) {
								for (GameObject object : objects) {
									if (object == null) {
										continue;
									}
									if (object.getDefinitions().containsOption(
											4, "Build")) {
										if (isDoor(object)) {
											if (!buildMode
													&& object.getZ() == 2
													&& getRoom(
															object.getX()
																	/ 8
																	- boundChuncks[0]
																	+ House.DOOR_DIR_X[object
																			.getRotation()],
															object.getY()
																	/ 8
																	- boundChuncks[1]
																	+ House.DOOR_DIR_Y[object
																			.getRotation()],
															object.getZ()) == null) {
												// System.out.println(object.getX()
												// + ", " + object.getY() + ", "
												// + object.getRotation() + ", "
												// + object.getType());
												GameObject objectR = new GameObject(
														object);
												objectR.setId(HouseConstants.WALL_IDS[look]);
												Engine.spawnObject(objectR);
												continue;
											}
										} else {
											for (ObjectReference o : reference.objects) {
												int slot = o.build
														.getIdSlot(object
																.getId());
												if (slot != -1) {
													GameObject objectR = new GameObject(
															object);
													objectR.setId(o.getId(slot));
													Engine.spawnObject(objectR);
												}
											}
										}
										if (!buildMode) {
											Engine.removeObject(object);
										}
									} else if (object.getId() == HouseConstants.WINDOW_SPACE_ID) {
										object = new GameObject(object);
										object.setId(HouseConstants.WINDOW_IDS[look]);
										Engine.spawnObject(object);
									} else if (isDoorSpace(object)) {
										Engine.removeObject(object);
									}
								}
							}
						}
					}
				}
				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
				player.lock(1);
				player.getInterfaceManager().sendWindowPane();
				if (tp) {
					teleportPlayer(player);
				}
				loaded = true;
			}
		}, 2400, 600);
	}

	public void createRoom(int slot) {
		Room[] rooms = HouseConstants.Room.values();
		if (slot >= rooms.length)
			return;
		int[] position = (int[]) player.getTemporaryAttributtes().get(
				"CreationRoom");
		player.closeInterfaces();
		if (position == null)
			return;
		Room room = rooms[slot];
		if ((room == Room.DUNGEON_CORRIDOR || room == Room.DUNGEON_JUNCTION
				|| room == Room.DUNGEON_PIT || room == Room.DUNGEON_STAIRS)
				&& position[2] != 0) {
			player.getPackets().sendGameMessage(
					"That room can only be built underground.");
			return;
		}
		if ((room == Room.GARDEN || room == Room.FORMAL_GARDEN || room == Room.MENAGERIE)
				&& position[2] != 1) {
			player.getPackets().sendGameMessage(
					"That room can only be built on ground.");
			return;
		}
		if (room.getLevel() > player.getSkills().getLevel(Skills.CONSTRUCTION)) {
			player.getPackets().sendGameMessage(
					"You need a Construction level of " + room.getLevel()
							+ " to build this room.");
			return;
		}
		if (player.getInventory().getCoinsAmount() < room.getPrice()) {
			player.getPackets().sendGameMessage(
					"You don't have enough coins to build this room.");
			return;
		}
		player.getDialogueManager().startDialogue(
				"CreateRoomD",
				new RoomReference(room, position[0], position[1], position[2],
						0));
	}

	public void createRoom(RoomReference room) {
		if (player.getInventory().getCoinsAmount() < room.room.getPrice()) { // better
			// double
			// check
			// if
			// somehow
			// u
			// manage
			// to
			// drop
			// money
			player.getPackets().sendGameMessage(
					"You don't have enough coins to build this room.");
			return;
		}
		player.getInventory().removeItemMoneyPouch(
				new Item(995, room.room.getPrice()));
		roomsR.add(room);
		refreshNumberOfRooms();
		refreshHouse();
	}

	public void destroyHouse() {
		final int[] boundChunksCopy = boundChuncks;
		// this way a new house can be created while current house being
		// destroyed
		loaded = false;
		boundChuncks = null;
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChunksCopy[0], boundChunksCopy[1],
						8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	public void enterMyHouse() {
		joinHouse(player);
	}

	public void expelGuests() {
		if (!isOwnerInside()) {
			player.getPackets()
					.sendGameMessage(
							"You can only expel guests when you are in your own house.");
			return;
		}
		kickGuests();
	}

	/*
	 * refers to logout
	 */
	public void finish() {
		kickGuests();
		// no need to leavehouse for owner, controler does that itself
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		look = 5;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Tile getPortal() {
		for (RoomReference room : roomsR) {
			if (room.room == HouseConstants.Room.GARDEN
					|| room.room == HouseConstants.Room.FORMAL_GARDEN) {
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL)
						return new Tile(boundChuncks[0] * 8 + room.x * 8 + 3,
								boundChuncks[1] * 8 + room.y * 8 + 3,
								room.plane);
			}
		}
		// shouldnt happen
		int[] xyp = MapUtils.convert(MapUtils.Structure.CHUNK,
				MapUtils.Structure.TILE, boundChuncks);
		return new Tile(xyp[0] + 32, xyp[1] + 32, 0);
	}

	public int getPortalCount() {
		int count = 0;
		for (RoomReference room : roomsR) {
			if (room.room == HouseConstants.Room.GARDEN
					|| room.room == HouseConstants.Room.FORMAL_GARDEN) {
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL) {
						count++;
					}
			}
		}
		return count;
	}

	public RoomReference getPortalRoom() {
		for (RoomReference room : roomsR) {
			if (room.room == HouseConstants.Room.GARDEN
					|| room.room == HouseConstants.Room.FORMAL_GARDEN) {
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL)
						return room;
			}
		}
		return null;
	}

	public RoomReference getRoom(int x, int y, int plane) {
		for (RoomReference room : roomsR)
			if (room.x == x && room.y == y && room.plane == plane)
				return room;
		return null;
	}

	public boolean hasWaterCan() {
		for (int id = 5333; id <= 5340; id++)
			if (player.getInventory().containsItemToolBelt(id))
				return true;
		return false;
	}

	public void init() {
		if (build == 0) {
			reset();
		}
		players = new ArrayList<Player>();
		refreshBuildMode();
		refreshArriveInPortal();
		refreshNumberOfRooms();
	}

	public boolean isBuildMode() {
		return buildMode;
	}

	public void setBuildMode(boolean buildMode) {
		if (this.buildMode == buildMode)
			return;
		this.buildMode = buildMode;
		if (loaded) {
			expelGuests();
			if (isOwnerInside()) {
				refreshHouse();
			}
		}
		refreshBuildMode();
	}

	public boolean isDoor(GameObject object) {
		return object.getDefinitions().name.equalsIgnoreCase("Door hotspot");
	}

	public boolean isDoorSpace(GameObject object) {
		return object.getDefinitions().name.equalsIgnoreCase("Door space");
	}

	public boolean isOwner(Player player) {
		return this.player == player;
	}

	private boolean isOwnerInside() {
		return players.contains(player);
	}

	public boolean isSky(int x, int y, int plane) {
		return buildMode
				&& plane == 2
				&& getRoom(x / 8 - boundChuncks[0], y / 8 - boundChuncks[1],
						plane) == null;
	}

	public boolean isWindow(int id) {
		return id == 13830;
	}

	public boolean joinHouse(final Player player) {
		if (!isOwner(player)) { // not owner
			if (!isOwnerInside() || !loaded) {
				player.getPackets().sendGameMessage(
						"That player is offline, or has privacy mode enabled.");
				// message
				return false;
			}
			if (buildMode) {
				player.getPackets().sendGameMessage(
						"The owner currently has build mode turned on.");
				return false;
			}
		}
		players.add(player);
		sendStartInterface(player);
		player.getControllerManager().startController("HouseController", this);
		if (loaded) {
			teleportPlayer(player);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.lock(1);
					player.getInterfaceManager().sendWindowPane();
				}
			}, 4);
		} else {
			CoresManager.slowExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try { // sets bounds before finishing load therefore the
							// load boolean
						boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
						createHouse(true);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			});
		}
		return true;
	}

	public void kickGuests() {
		for (Player player : new ArrayList<Player>(players)) {
			if (isOwner(player)) {
				continue;
			}
			leaveHouse(player, House.KICKED);
		}
	}

	/*
	 * 0 - logout, 1 kicked/tele outside outside, 2 tele somewhere else
	 */
	public void leaveHouse(Player player, int type) {
		player.getControllerManager().removeControllerWithoutCheck();
		if (type == House.LOGGED_OUT) {
			player.setLocation(POHLocation.RIMMINGTON.getTile());
		} else if (type == House.KICKED) {
			player.useStairs(-1, POHLocation.RIMMINGTON.getTile(), 0, 1);
		}
		players.remove(player);
		if (players.size() == 0) {
			destroyHouse();
		}
	}

	public void openBuildInterface(GameObject object, final Builds build) {
		if (!buildMode) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You can only do that in building mode.");
			return;
		}
		int roomX = object.getChunkX() - boundChuncks[0];
		int roomY = object.getChunkY() - boundChuncks[1];
		RoomReference room = getRoom(roomX, roomY, object.getZ());
		if (room == null)
			return;
		int interId = build.getPieces().length > 3 ? 396 : 394;
		Item[] itemArray = new Item[interId == 396 ? 7 : 3];
		for (int index = 0; index < build.getPieces().length; index++) {
			HObject piece = build.getPieces()[index];
			itemArray[interId == 396 ? House.BUILD_INDEXES[index] : index] = new Item(
					piece.getItemId(), 1);
			player.getPackets().sendConfig(
					1485 + index,
					player.getSkills().getLevel(Skills.CONSTRUCTION) >= piece
							.getLevel()
							&& player.getInventory().containsItems(
									piece.getRequirements()) ? 1 : 0);
		}
		player.getPackets().sendItems(8, itemArray);
		player.getPackets().sendInterSetItemsOptionsScript(interId, 11, 8,
				interId == 396 ? 2 : 1, 4, "Build");
		player.getPackets().sendUnlockIComponentOptionSlots(interId, 11, 0,
				interId == 396 ? 7 : 3, 0);
		player.getInterfaceManager().sendInterface(interId);
		for (int i = 0; i < (interId == 396 ? 7 : 3); i++) {
			if (i >= build.getPieces().length) {
				player.getPackets().sendHideIComponent(interId,
						(interId == 394 ? 29 : 49) + i, true);
				player.getPackets().sendIComponentText(interId,
						(interId == 394 ? 32 : 56) + i, "");
				player.getPackets().sendIComponentText(interId, 14 + 5 * i, "");
				for (int i2 = 0; i2 < 4; i2++) {
					player.getPackets().sendIComponentText(interId,
							15 + i2 + 5 * i, "");
				}
			} else {
				player.getPackets().sendIComponentText(interId,
						(interId == 394 ? 32 : 56) + i,
						"Lvl " + build.getPieces()[i].getLevel());
				player.getPackets().sendIComponentText(
						interId,
						14 + 5 * i,
						ItemDefinitions.getItemDefinitions(
								build.getPieces()[i].getItemId()).getName());
				for (int i2 = 0; i2 < 4; i2++) {
					player.getPackets()
							.sendIComponentText(
									interId,
									15 + i2 + 5 * i,
									build.getPieces()[i].getRequirements().length <= i2 ? ""
											: build.getPieces()[i]
													.getRequirements()[i2]
													.getName()
													+ ": "
													+ build.getPieces()[i]
															.getRequirements()[i2]
															.getAmount());
				}
			}
		}
		player.getTemporaryAttributtes().put("OpenedBuild", build);
		player.getTemporaryAttributtes().put("OpenedBuildObject", object);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributtes().remove("OpenedBuild");
				player.getTemporaryAttributtes().remove("OpenedBuildObject");
			}

		});
	}

	public void openRemoveBuild(GameObject object) {
		if (!buildMode) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You can only do that in building mode.");
			return;
		}
		if (object.getId() == HouseConstants.HObject.EXIT_PORTAL.getId()
				&& getPortalCount() <= 1) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Your house must have at least one exit portal.");
			return;
		}
		int roomX = object.getChunkX() - boundChuncks[0];
		int roomY = object.getChunkY() - boundChuncks[1];
		RoomReference room = getRoom(roomX, roomY, object.getZ());
		if (room == null)
			return;
		ObjectReference ref = room.getObject(object);
		if (ref != null) {
			if (ref.build.toString().contains("STAIRCASE")) {
				if (object.getZ() != 1) {
					RoomReference above = getRoom(roomX, roomY, 2);
					RoomReference below = getRoom(roomX, roomY, 0);
					if (above != null && above.getStaircaseSlot() != -1
							|| below != null && below.getStaircaseSlot() != -1) {
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"You cannot remove a building that is supporting this room.");
					}
					return;
				}
			}
			player.getDialogueManager().startDialogue("RemoveBuildD", object);
		}
	}

	public void openRoomCreationMenu(GameObject door) {
		int roomX = player.getChunkX() - boundChuncks[0]; // current room
		int roomY = player.getChunkY() - boundChuncks[1]; // current room
		int xInChunk = player.getXInChunk();
		int yInChunk = player.getYInChunk();
		if (xInChunk == 7) {
			roomX += 1;
		} else if (xInChunk == 0) {
			roomX -= 1;
		} else if (yInChunk == 7) {
			roomY += 1;
		} else if (yInChunk == 0) {
			roomY -= 1;
		}
		openRoomCreationMenu(roomX, roomY, door.getZ());
	}

	/*
	 * door used to calculate where player facing to create
	 */
	public void openRoomCreationMenu(int roomX, int roomY, int plane) {
		if (!buildMode) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You can only do that in building mode.");
			return;
		}
		RoomReference room = getRoom(roomX, roomY, plane);
		if (room != null) {
			if (room.plane == 1
					&& getRoom(roomX, roomY, room.plane + 1) != null) {
				player.getDialogueManager()
						.startDialogue("SimpleMessage",
								"You can't remove a room that is supporting another room.");
				return;
			}
			if ((room.room == Room.GARDEN || room.room == Room.FORMAL_GARDEN)
					&& getPortalCount() < 2) {
				if (room == getPortalRoom()) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your house must have at least one exit portal.");
					return;
				}
			}
			player.getDialogueManager().startDialogue("RemoveRoomD", room);
		} else {
			if (roomX == 0 || roomY == 0 || roomX == 7 || roomY == 7) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You can't create a room here.");
				return;
			}
			if (plane == 2) {
				RoomReference r = getRoom(roomX, roomY, 1);
				if (r == null || r.room == Room.GARDEN
						|| r.room == Room.FORMAL_GARDEN
						|| r.room == Room.MENAGERIE) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You can't create a room here.");
					return;
				}

			}
			for (int index = 0; index < HouseConstants.Room.values().length; index++) {
				Room refRoom = HouseConstants.Room.values()[index];
				if (player.getSkills().getLevel(Skills.CONSTRUCTION) >= refRoom
						.getLevel()
						&& player.getInventory().getCoinsAmount() >= refRoom
								.getPrice()) {
					player.getPackets().sendIComponentText(402, index + 68,
							"<col=008000> " + refRoom.getPrice() + " coins");
				}
			}
			player.getInterfaceManager().sendInterface(402);
			player.getTemporaryAttributtes().put("CreationRoom",
					new int[] { roomX, roomY, plane });
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					player.getTemporaryAttributtes().remove("CreationRoom");
				}
			});
		}
	}

	public void previewRoom(RoomReference reference, boolean remove) {
		int boundX = boundChuncks[0] * 8 + reference.x * 8;
		int boundY = boundChuncks[1] * 8 + reference.y * 8;
		int realChunkX = reference.room.getChunkX();
		int realChunkY = reference.room.getChunkY();
		Region region = Engine.getRegion(MapUtils.encode(
				MapUtils.Structure.REGION, realChunkX / 8, realChunkY / 8));
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				GameObject[] objects = region.getObjects(reference.plane,
						(realChunkX & 0x7) * 8 + x, (realChunkY & 0x7) * 8 + y);
				if (objects != null) {
					for (GameObject object : objects) {
						if (object == null) {
							continue;
						}
						if (object.getDefinitions().containsOption(2, "Build")) {
							int x2 = x;
							int y2 = y;
							for (int rotate = 0; rotate < reference.rotation; rotate++) {
								int fakeChunckX = x2;
								int fakeChunckY = y2;
								x2 = fakeChunckY;
								y2 = 7 - fakeChunckX;
							}
							object = new GameObject(
									object.getId(),
									object.getType(),
									(object.getRotation() + reference.rotation) % 4,
									boundX + x2 + boundChuncks[0] * 8, boundY
											+ y2 + boundChuncks[1] * 8,
									reference.plane);
							if (remove)
								player.getPackets().sendDestroyObject(object);
							else
								player.getPackets().sendSpawnedObject(object);
						}
					}
				}
			}
		}
	}

	public void refreshArriveInPortal() {
		player.getPackets().sendConfigByFile(6450, arriveInPortal ? 1 : 0);
	}

	public void refreshBuildMode() {
		player.getPackets().sendConfigByFile(2176, buildMode ? 1 : 0);
	}

	public void refreshHouse() {
		loaded = false;
		sendStartInterface(player);
		createHouse(false);
	}

	public void refreshNumberOfRooms() {
		player.getPackets().sendGlobalConfig(944, roomsR.size());
	}

	private void refreshObject(RoomReference rref, ObjectReference oref,
			boolean remove) {
		int boundX = rref.x * 8;
		int boundY = rref.y * 8;
		int[] regionPos = MapUtils.convert(MapUtils.Structure.CHUNK,
				MapUtils.Structure.REGION, boundChuncks);
		final Region region = Engine.getRegion(
				MapUtils.encode(MapUtils.Structure.REGION, regionPos), true);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				GameObject[] objects = region.getObjects(rref.plane,
						boundX + x, boundY + y);
				if (objects != null) {
					for (GameObject object : objects) {
						if (object == null) {
							continue;
						}
						int slot = oref.build.getIdSlot(object.getId());
						if (slot == -1) {
							continue;
						}
						// System.out.println(remove+", "+object.getX()+", "+object.getY());
						if (remove) {
							Engine.spawnObject(object);
						} else {
							GameObject objectR = new GameObject(object);
							objectR.setId(oref.getId(slot));
							Engine.spawnObject(objectR);
						}
					}
				}
			}
		}
	}

	public void removeBuild(final GameObject object) {
		if (!buildMode) { // imagine u use settings to change while dialogue
			// open, cheater :p
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You can only do that in building mode.");
			return;
		}
		int roomX = object.getChunkX() - boundChuncks[0];
		int roomY = object.getChunkY() - boundChuncks[1];
		final RoomReference room = getRoom(roomX, roomY, object.getZ());
		if (room == null)
			return;
		final ObjectReference oref = room.removeObject(object);
		if (oref == null)
			return;
		player.lock();
		player.setNextAnimation(new Animation(3685));
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				Engine.removeObject(object);
				refreshObject(room, oref, true);
				player.lock(1);
			}
		}, 1);
	}

	public void removeRoom() {
		int roomX = player.getChunkX() - boundChuncks[0]; // current room
		int roomY = player.getChunkY() - boundChuncks[1]; // current room
		RoomReference room = getRoom(roomX, roomY, player.getZ());
		if (room == null)
			return;
		if (room.getPlane() != 1) {
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"You cannot remove a building that is supporting this room.");
			return;
		}

		RoomReference above = getRoom(roomX, roomY, 2);
		RoomReference below = getRoom(roomX, roomY, 0);

		RoomReference roomTo = above != null && above.getStaircaseSlot() != -1 ? above
				: below != null && below.getStaircaseSlot() != -1 ? below
						: null;
		if (roomTo == null) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"These stairs do not lead anywhere.");
			return;
		}
		openRoomCreationMenu(roomTo.getX(), roomTo.getY(), roomTo.getPlane());
	}

	public void removeRoom(RoomReference room) {
		if (roomsR.remove(room)) {
			refreshNumberOfRooms();
			refreshHouse();
		}
	}

	/*
	 * temporary
	 */
	private void reset() {
		build = 1;
		buildMode = true;
		roomsR = new ArrayList<RoomReference>();
		addRoom(HouseConstants.Room.GARDEN, 3, 3, 1, 0);
		getRoom(3, 3, 1).addObject(Builds.CENTREPIECE, 0);
	}

	public void sendStartInterface(Player player) {
		player.lock();
		player.getPackets().sendWindowsPane(399, 0);
		player.getMusicsManager().playMusic(454);
		player.getPackets().sendMusicEffect(22);
	}

	public void setArriveInPortal(boolean arriveInPortal) {
		this.arriveInPortal = arriveInPortal;
		refreshArriveInPortal();

	}

	public void switchLock(Player player) {
		if (!isOwner(player)) {
			player.getPackets().sendGameMessage(
					"You can only lock your own house.");
			return;
		}
		locked = !locked;
		if (locked) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Your house is now locked to all visistors.");
		} else if (buildMode) {
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"Visitors will be able to enter your house once you leave building mode.");
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Visistors can now enter your house.");
		}
	}

	public void teleportPlayer(Player player) {
		player.setNextTile(getPortal());
	}

	public static class ObjectReference implements Serializable {

		/**
         *
         */
		private static final long serialVersionUID = -22245200911725426L;
		private int slot;
		private Builds build;

		public ObjectReference(Builds build, int slot) {
			this.build = build;
			this.slot = slot;
		}

		public int getId() {
			return build.getPieces()[slot].getId();
		}

		public int getId(int slot2) {
			return getIds()[slot2];
		}

		public int[] getIds() {
			return build.getPieces()[slot].getIds();
		}

		public HObject getPiece() {
			return build.getPieces()[slot];
		}

	}

	public static class RoomReference implements Serializable {

		private static final long serialVersionUID = 4000732770611956015L;

		private HouseConstants.Room room;

		private byte x, y, plane, rotation;
		private List<ObjectReference> objects;

		public RoomReference(HouseConstants.Room room, int x, int y, int plane,
				int rotation) {
			this.room = room;
			this.x = (byte) x;
			this.y = (byte) y;
			this.plane = (byte) plane;
			this.rotation = (byte) rotation;
			objects = new ArrayList<ObjectReference>();
		}

		/*
		 * x,y inside the room chunk
		 */
		public ObjectReference addObject(Builds build, int slot) {
			ObjectReference ref = new ObjectReference(build, slot);
			objects.add(ref);
			return ref;
		}

		public ObjectReference getObject(GameObject object) {
			for (ObjectReference o : objects) {
				for (int id : o.getIds())
					if (object.getId() == id)
						return o;
			}
			return null;
		}

		public int getPlane() {
			return plane;
		}

		public Room getRoom() {
			return room;
		}

		public byte getRotation() {
			return rotation;
		}

		public void setRotation(int rotation) {
			this.rotation = (byte) rotation;
		}

		public int getStaircaseSlot() {
			for (ObjectReference object : objects) {
				if (object.build.toString().contains("STAIRCASE"))
					return object.slot;
			}
			return -1;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public boolean isStaircaseDown() {
			for (ObjectReference object : objects) {
				if (object.build.toString().contains("STAIRCASE_DOWN"))
					return true;
			}
			return false;
		}

		public ObjectReference removeObject(GameObject object) {
			ObjectReference r = getObject(object);
			if (r != null) {
				objects.remove(r);
				return r;
			}
			return null;
		}

	}
}
