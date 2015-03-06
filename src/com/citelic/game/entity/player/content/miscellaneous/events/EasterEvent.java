package com.citelic.game.entity.player.content.miscellaneous.events;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class EasterEvent {

	public static Tile[] SPAWN = { new Tile(3156, 3502, 0),
			new Tile(3027, 3350, 0), new Tile(2974, 3237, 0),
			new Tile(2925, 3339, 0) };

	public static int[] EGG = { 7928, 7929, 7930, 7931, 7932, 7933 };

	public static String droppedArea;

	public static int getX;

	public static int getY;

	public static int spawnedEgg;

	public static int randomPlaces;

	public static void easterSpawn() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (Engine.getPlayers().size() == 0) {
					System.out
							.println("[Easter Event] Not dropping any eggs, reason: no players online.");
					return;
				}
				randomPlaces = Utilities.random(21);
				if (randomPlaces == 20) {
					setDroppedArea("Monastery");
					getX = 3049;
					getY = 3498;
				} else if (randomPlaces == 19) {
					setDroppedArea("Barbarian Village");
					getX = 3089;
					getY = 3408;
				} else if (randomPlaces == 18) {
					setDroppedArea("Draynor Manor");
					getX = 3109;
					getY = 3340;
				} else if (randomPlaces == 17) {
					setDroppedArea("Wizards' Tower");
					getX = 3117;
					getY = 3169;
				} else if (randomPlaces == 16) {
					setDroppedArea("Clan camp");
					getX = 2970;
					getY = 3274;
				} else if (randomPlaces == 15) {
					setDroppedArea("Champions' Guild");
					getX = 3208;
					getY = 3355;
				} else if (randomPlaces == 14) {
					setDroppedArea("Seers' Village");
					getX = 2697;
					getY = 3470;
				} else if (randomPlaces == 13) {
					setDroppedArea("Camelot Castle");
					getX = 2761;
					getY = 3488;
				} else if (randomPlaces == 12) {
					setDroppedArea("Taverly");
					getX = 2931;
					getY = 3461;
				} else if (randomPlaces == 11) {
					setDroppedArea("Cooking Guild");
					getX = 3142;
					getY = 3441;
				} else if (randomPlaces == 10) {
					setDroppedArea("Grand Exchange");
					getX = 3156;
					getY = 3502;
				} else if (randomPlaces == 9) {
					setDroppedArea("Draynor Village");
					getX = 3081;
					getY = 3250;
				} else if (randomPlaces == 8) {
					setDroppedArea("Port Sarim Bar");
					getX = 3053;
					getY = 3257;
				} else if (randomPlaces == 7) {
					setDroppedArea("Al Kharid");
					getX = 3284;
					getY = 3183;
				} else if (randomPlaces == 6) {
					setDroppedArea("Burthorpe");
					getX = 2901;
					getY = 3523;
				} else if (randomPlaces == 5) {
					setDroppedArea("Bandits camp");
					getX = 3174;
					getY = 2980;
				} else if (randomPlaces == 4) {
					setDroppedArea("Falador park");
					getX = 2991;
					getY = 3384;
				} else if (randomPlaces == 3) {
					setDroppedArea("Lumbridge kitchen");
					getX = 3207;
					getY = 3214;
				} else if (randomPlaces == 2) {
					setDroppedArea("Varrock east bank");
					getX = 3256;
					getY = 3425;
				} else if (randomPlaces == 1) {
					setDroppedArea("Body altar (Barbarian village)");
					getX = 3053;
					getY = 3440;
				} else if (randomPlaces == 0) {
					setDroppedArea("Ice mountain");
					getX = 3009;
					getY = 3500;
				}
				spawnedEgg = EGG[Utilities.random(EGG.length)];
				Engine.sendWorldMessage(
						"<img=7><col=E0246F>Easter Event: An egg have been dropped at "
								+ getDroppedArea() + "!", false);
				Engine.addGroundItem(new Item(spawnedEgg, 1), getSpawn());
			}
		}, 0, 20, TimeUnit.MINUTES);
	}

	public static String getDroppedArea() {
		return droppedArea;
	}

	public static Tile getSpawn() {
		return new Tile(getX, getY, 0);
	}

	public static void setDroppedArea(String droppedLocation) {
		droppedArea = droppedLocation;
	}
}