package com.citelic.game.entity.player.content.actions.skills.fishing;

import java.util.HashMap;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;

public class FishingSpotsHandler {

	public static final HashMap<Integer, Integer> moveSpots = new HashMap<Integer, Integer>();

	public static void init() {
		FishingSpotsHandler.moveSpots.put(
				new Tile(2836, 3431, 0).getTileHash(),
				new Tile(2845, 3429, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2853, 3423, 0).getTileHash(),
				new Tile(2860, 3426, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3110, 3432, 0).getTileHash(),
				new Tile(3104, 3423, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3104, 3424, 0).getTileHash(),
				new Tile(3110, 3433, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3632, 5082, 0).getTileHash(),
				new Tile(3621, 5087, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3625, 5083, 0).getTileHash(),
				new Tile(3617, 5087, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3621, 5119, 0).getTileHash(),
				new Tile(3617, 5123, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3628, 5136, 0).getTileHash(),
				new Tile(3633, 5137, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3637, 5139, 0).getTileHash(),
				new Tile(3634, 5148, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3652, 5141, 0).getTileHash(),
				new Tile(3658, 5145, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(3680, 5110, 0).getTileHash(),
				new Tile(3675, 5114, 0).getTileHash());

		/**
		 * Karamja Fishing
		 */

		FishingSpotsHandler.moveSpots.put(
				new Tile(2926, 3176, 0).getTileHash(),
				new Tile(2926, 3177, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2926, 3179, 0).getTileHash(),
				new Tile(2926, 3178, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2926, 3180, 0).getTileHash(),
				new Tile(2925, 3181, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2924, 3181, 0).getTileHash(),
				new Tile(2923, 3180, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2923, 3179, 0).getTileHash(),
				new Tile(2923, 3178, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2921, 3179, 0).getTileHash(),
				new Tile(2918, 3179, 0).getTileHash());

		/**
		 * Shilo Village
		 */

		FishingSpotsHandler.moveSpots.put(
				new Tile(2860, 2976, 0).getTileHash(),
				new Tile(2855, 2977, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2860, 2972, 0).getTileHash(),
				new Tile(2855, 2974, 0).getTileHash());

		/**
		 * Seers' Village
		 */

		FishingSpotsHandler.moveSpots.put(
				new Tile(2714, 3532, 0).getTileHash(),
				new Tile(2714, 3533, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2716, 3530, 0).getTileHash(),
				new Tile(2715, 3530, 0).getTileHash());
		FishingSpotsHandler.moveSpots.put(
				new Tile(2727, 3524, 0).getTileHash(),
				new Tile(2726, 3524, 0).getTileHash());
	}

	public static boolean moveSpot(NPC npc) {
		int key = npc.getTileHash();
		Integer spot = FishingSpotsHandler.moveSpots.get(key);
		if (spot == null && FishingSpotsHandler.moveSpots.containsValue(key)) {
			for (Integer k : FishingSpotsHandler.moveSpots.keySet()) {
				Integer v = FishingSpotsHandler.moveSpots.get(k);
				if (v == key) {
					spot = k;
					break;
				}
			}
		}
		if (spot == null)
			return false;
		npc.setNextTile(new Tile(spot));
		return true;
	}

}
