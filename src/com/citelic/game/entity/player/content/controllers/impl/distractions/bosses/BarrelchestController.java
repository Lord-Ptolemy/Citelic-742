package com.citelic.game.entity.player.content.controllers.impl.distractions.bosses;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.impl.barrelchest.BarrelChest;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class BarrelchestController extends Controller {

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	public static final Tile OUTSIDE = new Tile(4610, 5130, 0);
	private int[] boundChuncks;
	private Stages stage;
	private boolean logoutAtEnd;

	public boolean spawned;

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave() {
		stage = Stages.DESTROYING;
		player.setNextTile(new Tile(3804, 2844, 0));
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
		return getWorldTile(12, 5);
	}

	/*
	 * gets worldtile inside the map
	 */
	public Tile getWorldTile(int mapX, int mapY) {
		return new Tile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY,
				0);
	}

	public void loadCave() {
		stage = Stages.LOADING;
		player.lock(); // locks player
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		MapBuilder.copyAllPlanesMap(475, 355, boundChuncks[0], boundChuncks[1],
				8);
		player.setNextTile(getWorldTile(12, 5));
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

	public void nextWave() {
		setCurrentWave(getCurrentWave() + 1);
		if (logoutAtEnd) {
			player.forceLogout();
			return;
		}
		startWave();
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
		if (object.getId() == 22119) {
			if (stage != Stages.RUNNING)
				return false;
			exitCave();
			return false;
		}
		return false;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		return false;
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

	@Override
	public void start() {
		loadCave();
	}

	public void startWave() {
		if (stage != Stages.RUNNING)
			return;
		new BarrelChest(5666, getSpawnTile(), this);
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		exitCave();
	}
}