package com.citelic.game.lobby;

import java.util.HashMap;

public class WorldList {

	public static final HashMap<Integer, WorldEntry> WORLDS = new HashMap<Integer, WorldEntry>();

	public static WorldEntry getWorld(int worldId) {
		return WORLDS.get(worldId);
	}

	/**
	 * @param WORLDS - {@link HashMap} for Worlds
	 * 
	 */
	public static void init() {
		WORLDS.put(1, new WorldEntry("World 1 Main", "127.0.0.1", 191,
				"Sweden", true));
		WORLDS.put(2, new WorldEntry("World 2 PvP", "127.0.0.1", 191, "Sweden",
				true));
	}

}