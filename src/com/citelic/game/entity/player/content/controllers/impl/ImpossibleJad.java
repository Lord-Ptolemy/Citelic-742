package com.citelic.game.entity.player.content.controllers.impl;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.impl.impossiblejad.AgrithNaNa;
import com.citelic.game.entity.npc.impl.impossiblejad.Culinaromancer;
import com.citelic.game.entity.npc.impl.impossiblejad.Dessourt;
import com.citelic.game.entity.npc.impl.impossiblejad.Flambeed;
import com.citelic.game.entity.npc.impl.impossiblejad.ImpossibleJadNPC;
import com.citelic.game.entity.npc.impl.impossiblejad.Karamel;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class ImpossibleJad extends Controller {

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	public static final Tile OUTSIDE = new Tile(4610, 5130, 0);
	private int[] boundChuncks;
	private Stages stage;

	public boolean spawned;

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave() {
		stage = Stages.DESTROYING;
		player.setNextTile(new Tile(1868, 5348, 0));
		player.setForceMultiArea(false);
		player.reset();
		removeController();
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void forceClose() {
		/*
		 * shouldnt happen
		 */
		if (stage != Stages.RUNNING)
			return;
		exitCave();
	}

	public int getCurrentWave() {
		if (getArguments() == null || getArguments().length == 0)
			return 0;
		return (Integer) getArguments()[0];
	}

	public Tile getSpawnTile() {
		return getWorldTile(8, 9);
	}

	/*
	 * gets worldtile inside the map
	 */
	public Tile getWorldTile(int mapX, int mapY) {
		return new Tile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY,
				2);
	}

	public void loadCave() {
		stage = Stages.LOADING;
		player.lock(); // locks player
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		MapBuilder.copyAllPlanesMap(237, 669, boundChuncks[0], boundChuncks[1],
				8);
		player.setNextTile(getWorldTile(9, 8));
		player.setForceMultiArea(true);
		player.unlock(); // unlocks player
		stage = Stages.RUNNING;
		startWave();
	}

	@Override
	public boolean login() {
		loadCave();
		return false;
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean logout() {
		/*
		 * only can happen if dungeon is loading and system update happens
		 */
		if (stage != Stages.RUNNING)
			return false;
		exitCave();
		return false;

	}

	@Override
	public void magicTeleported(int type) {
		exitCave();
	}

	@Override
	public void moved() {
		if (stage != Stages.RUNNING)
			return;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (stage != Stages.RUNNING)
			return false;
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
		if (object.getId() == 12356) {
			if (stage != Stages.RUNNING)
				return false;
			exitCave();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		return false;
	}

	public void removeNPC() {
		if (!player.isKilledAgrithNaNa()) {
			player.setKilledAgrithNaNa(true);
		} else if (!player.isKilledFlambeed()) {
			player.setKilledFlamBeed(true);
		} else if (!player.isKilledFlambeed()) {
			player.setKilledKaramel(true);
		} else if (!player.isKilledKaramel()) {
			player.setKilledKaramel(true);
		} else if (!player.isKilledDessourt()) {
			player.setKilledDessourt(true);
		}
		startWave();
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear you have died!");
				} else if (loop == 3) {
					player.reset();
					exitCave();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void setCurrentWave(int wave) {
		if (getArguments() == null || getArguments().length == 0)
			this.setArguments(new Object[1]);
		getArguments()[0] = wave;
	}

	public void spawnHealers() {
		if (stage != Stages.RUNNING)
			return;
		for (int i = 0; i < 4; i++)
			new ImpossibleJadNPC(2746, getSpawnTile());
	}

	@Override
	public void start() {
		if (player.isKilledCulinaromancer()) {
			removeController();
			return;
		}
		player.prayer.closeAllPrayers();
		loadCave();
	}

	public void startWave() {
		if (stage != Stages.RUNNING)
			return;
		if (!player.isKilledAgrithNaNa()) {
			new AgrithNaNa(3493, getSpawnTile(), this);
			return;
		}
		if (!player.isKilledFlambeed()) {
			new Flambeed(3494, getSpawnTile(), this);
			return;
		}
		if (!player.isKilledKaramel()) {
			new Karamel(3495, getSpawnTile(), this);
			return;
		}
		if (!player.isKilledDessourt()) {
			new Dessourt(3496, getSpawnTile(), this);
			return;
		}
		if (!player.isKilledCulinaromancer()) {
			new Culinaromancer(3491, getSpawnTile(), this);
			return;
		}
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		player.setKilledCulinaromancer(true);
		player.setNextAnimation(new Animation(862));
		player.getInterfaceManager().sendInterface(277);
		player.getPackets().sendIComponentText(277, 4,
				"You have completed Recipe for Diaster!");
		player.getPackets().sendIComponentText(277, 9,
				"Full access to the Cunliaromancer's Chest.");
		player.getPackets().sendIComponentText(277, 7, "1");
		player.getPackets().sendIComponentText(277, 10, "");
		player.getPackets().sendIComponentText(277, 10,
				"Requierment for Completionist Cape.");
		player.getPackets().sendIComponentText(277, 11, "");
		player.getPackets().sendIComponentText(277, 12, "");
		player.getPackets().sendIComponentText(277, 13, "");
		player.getPackets().sendIComponentText(277, 14, "");
		player.getPackets().sendIComponentText(277, 15, "");
		player.getPackets().sendIComponentText(277, 16, "");
		player.getPackets().sendIComponentText(277, 17, "");
		player.getPackets().sendItemOnIComponent(277, 5, 7462, 1);
		exitCave();
	}
}