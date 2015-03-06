package com.citelic.game.entity.player.content.controllers.impl.events;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.citelic.GameConstants;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.impl.others.GraveStone;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public class DeathEvent extends Controller {
	public static final Tile[] HUBS = {
			// Lumbridge
			new Tile(3222, 3219, 0)
			// Varrock
			, new Tile(3212, 3422, 0)
			// EDGEVILLE
			, new Tile(3094, 3502, 0)
			// FALADOR
			, new Tile(2965, 3386, 0)
			// SEERS VILLAGE
			, new Tile(2725, 3491, 0)
			// ARDOUDGE
			, new Tile(2662, 3305, 0)
			// YANNILE
			, new Tile(2605, 3093, 0)
			// KELDAGRIM
			, new Tile(2845, 10210, 0)
			// DORGESH-KAN
			, new Tile(2720, 5351, 0)
			// LYETYA
			, new Tile(2341, 3171, 0)
			// ETCETERIA
			, new Tile(2614, 3894, 0)
			// DAEMONHEIM
			, new Tile(3450, 3718, 0)
			// CANIFIS
			, new Tile(3496, 3489, 0)
			// THZAAR AREA
			, new Tile(4651, 5151, 0)
			// BURTHORPE
			, new Tile(2889, 3528, 0)
			// ALKARID
			, new Tile(3275, 3166, 0)
			// DRAYNOR VILLAGE
			, new Tile(3079, 3250, 0) };
	// 3796 - 0 - Lumbridge Castle - {1=Falador Castle, 2=Camelot, 3=Soul Wars,
	// 4=Burthorpe}
	public static final Tile[] RESPAWN_LOCATIONS = { new Tile(3222, 3219, 0),
			new Tile(2971, 3343, 0), new Tile(2758, 3486, 0),
			new Tile(1891, 3177, 0), new Tile(2889, 3528, 0) };
	private int[] boundChuncks;
	private Stages stage;
	private Integer[][] slots;
	private int currentHub;

	public static int getCurrentHub(Tile tile) {
		int nearestHub = -1;
		int distance = 0;
		for (int i = 0; i < DeathEvent.HUBS.length; i++) {
			int d = Utilities.getDistance(DeathEvent.HUBS[i], tile);
			if (nearestHub == -1 || d < distance) {
				distance = d;
				nearestHub = i;
			}
		}
		return nearestHub;
	}

	public static Tile getRespawnHub(Player player) {
		return DeathEvent.HUBS[DeathEvent.getCurrentHub(player)];
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		return false;
	}

	public boolean canPlayerOption2(Player target) {
		return false;
	}

	public boolean canPlayerOption3(Player target) {
		return false;
	}

	public boolean canPlayerOption4(Player target) {
		return false;
	}

	public boolean canTakeItem(FloorItem item) {
		return false;
	}

	public void destroyRoom() {
		if (stage != Stages.RUNNING)
			return;
		stage = Stages.DESTROYING;
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8,
							8);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void forceClose() {
		destroyRoom();
	}

	public Tile getDeathTile() {
		if (getArguments() == null || getArguments().length < 2)
			return GameConstants.START_PLAYER_LOCATION;
		return (Tile) getArguments()[0];
	}

	public int getProtectSlots() {
		return -1;
	}

	public void getReadyToRespawn() {
		slots = GraveStone.getItemSlotsKeptOnDeath(player, false, hadSkull(),
				player.getPrayer().isProtectingItem());
		player.getInterfaceManager().sendInterface(18);
		if (slots[0].length > 0) {
			player.getPackets().sendConfigByFile(9227, slots[0].length);
			sendProtectedItems();
		} else {
			player.getPackets().sendConfigByFile(9222, -1);
			player.getPackets().sendConfigByFile(9227, 1);
		}
		player.getPackets().sendConfigByFile(668, 1); // unlocks camelot respawn
		// spot
		player.getPackets().sendConfig(105, -1);
		player.getPackets().sendConfigByFile(9231,
				currentHub = DeathEvent.getCurrentHub(getDeathTile()));
		player.getPackets().sendUnlockIComponentOptionSlots(18, 9, 0,
				slots[0].length, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(18, 17, 0, 100, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(18, 45, 0,
				DeathEvent.RESPAWN_LOCATIONS.length, 0);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				Tile respawnTile = currentHub >= 256 ? DeathEvent.RESPAWN_LOCATIONS[currentHub - 256]
						: DeathEvent.HUBS[currentHub];
				synchronized (slots) {
					player.sendItemsOnDeath(null, getDeathTile(), respawnTile,
							false, slots);
				}
				player.setCloseInterfacesEvent(null);
				Magic.sendObjectTeleportSpell(player, true, respawnTile);
			}

		});
	}

	public boolean hadSkull() {
		if (getArguments() == null || getArguments().length < 2)
			return false;
		return (boolean) getArguments()[1];
	}

	public void loadRoom() {
		stage = Stages.LOADING;
		player.lock(); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boundChuncks = MapBuilder.findEmptyChunkBound(2, 2);
					MapBuilder.copyMap(246, 662, boundChuncks[0],
							boundChuncks[1], 2, 2, new int[1], new int[1]);
					player.reset();
					player.setNextTile(new Tile(boundChuncks[0] * 8 + 10,
							boundChuncks[1] * 8 + 6, 0));
					// 1delay because player cant walk while teleing :p, +
					// possible
					// issues avoid
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.getMusicsManager().playMusic(683);
							player.getPackets().sendMiniMapStatus(2);
							sendInterfaces();
							player.unlock(); // unlocks player
							stage = Stages.RUNNING;
						}

					}, 1);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});
	}

	@Override
	public boolean login() {
		loadRoom();
		return false;
	}

	@Override
	public boolean logout() {
		player.setLocation(new Tile(1978, 5302, 0));
		destroyRoom();
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		destroyRoom();
		player.getPackets().sendMiniMapStatus(0);
		player.getInterfaceManager().sendCombatStyles();
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getInterfaceManager().sendTaskSystem();
		player.getInterfaceManager().sendSkills();
		player.getInterfaceManager().sendInventory();
		player.getInventory().unlockInventoryOptions();
		player.getInterfaceManager().sendEquipment();
		player.getInterfaceManager().sendPrayerBook();
		player.getPrayer().unlockPrayerBookButtons();
		player.getInterfaceManager().sendMagicBook();
		player.getInterfaceManager().sendEmotes();
		player.getEmotesManager().unlockEmotesBook();
		removeController();
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 18) {
			if (componentId == 9) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					unprotect(slotId);
				}
			} else if (componentId == 17) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					protect(slotId2);
				}
			} else if (componentId == 45) {
				// slotid - 1
				if (slotId > DeathEvent.RESPAWN_LOCATIONS.length)
					return false;
				currentHub = 255 + slotId;
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return false;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 45803) {
			if (getArguments() == null || getArguments().length < 2) {
				Magic.sendObjectTeleportSpell(player, true,
						GameConstants.START_PLAYER_LOCATION);
			} else {
				getReadyToRespawn();
			}
			return false;
		}
		return true;
	}

	public void protect(int itemId) {
		synchronized (slots) {
			int slot = -1;
			for (int i = 0; i < slots[1].length; i++) {

				Item item = slots[1][i] >= 16 ? player.getInventory().getItem(
						slots[1][i] - 16) : player.getEquipment().getItem(
						slots[1][i] - 1);
				if (item == null) {
					continue;
				}
				if (item.getId() == itemId) {
					slot = i;
					break;
				}
			}
			if (slot == -1 || getProtectSlots() <= slots[0].length)
				return;
			slots[0] = Arrays.copyOf(slots[0], slots[0].length + 1);
			slots[0][slots[0].length - 1] = slots[1][slot];
			Integer[] lItems = new Integer[slots[1].length - 1];
			System.arraycopy(slots[1], 0, lItems, 0, slot);
			System.arraycopy(slots[1], slot + 1, lItems, slot, lItems.length
					- slot);
			slots[1] = lItems;
			sendProtectedItems();
		}

	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().closeCombatStyles();
		player.getInterfaceManager().closeTaskSystem();
		player.getInterfaceManager().closeSkills();
		player.getInterfaceManager().closeInventory();
		player.getInterfaceManager().closeEquipment();
		player.getInterfaceManager().closePrayerBook();
		player.getInterfaceManager().closeMagicBook();
		player.getInterfaceManager().closeEmotes();
	}

	public void sendProtectedItems() {
		for (int i = 0; i < getProtectSlots(); i++) {
			player.getPackets().sendConfigByFile(9222 + i,
					i >= slots[0].length ? -1 : slots[0][i]);
		}
	}

	@Override
	public void start() {
		loadRoom();
	}

	public void unprotect(int slot) {
		synchronized (slots) {
			if (slot >= slots[0].length)
				return;
			slots[1] = Arrays.copyOf(slots[1], slots[1].length + 1);
			slots[1][slots[1].length - 1] = slots[0][slot];
			Integer[] pItems = new Integer[slots[0].length - 1];
			System.arraycopy(slots[0], 0, pItems, 0, slot);
			System.arraycopy(slots[0], slot + 1, pItems, slot, pItems.length
					- slot);
			slots[0] = pItems;
			sendProtectedItems();
		}

	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

}
