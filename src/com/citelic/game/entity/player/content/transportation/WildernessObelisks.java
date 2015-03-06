package com.citelic.game.entity.player.content.transportation;

import java.util.Random;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class WildernessObelisks {
	
	public enum Locations {
		
		LOCATION_1(3154, 3158, 3618, 3622, new Tile(3156, 3620, 0), 65619),
		LOCATION_2(3217, 3221, 3654, 3658, new Tile(3219, 3656, 0), 65620),
		LOCATION_3(3033, 3037, 3730, 3734, new Tile(3035, 3732, 0), 65617),
		LOCATION_4(3104, 3108, 3792, 3796, new Tile(3106, 3794, 0), 65618),
		LOCATION_5(2978, 2982, 3864, 3868, new Tile(2980, 3866, 0), 65616),
		LOCATION_6(3305, 3309, 3914, 3918, new Tile(3307, 3916, 0), 65621);
		
		private int topLeftX, bottomRightX, bottomRightY, topLeftY;
		private Tile location;
		private int objectId;
		
		Locations(int topLeftX, int bottomRightX, int bottomRightY, int topLeftY, Tile location, int objectId) {
			this.topLeftX = topLeftX;
			this.bottomRightX = bottomRightX;
			this.bottomRightY = bottomRightY;
			this.topLeftY = topLeftY;
			this.location = location;
			this.objectId = objectId;
		}
		
		public int getTopLeftX() {
			return topLeftX;
		}
		
		public int getBottomRightX() {
			return bottomRightX;
		}
		
		public int getBottomRightY() {
			return bottomRightY;
		}
		
		public int getTopLeftY() {
			return topLeftY;
		}
		
		public Tile getLocation() {
			return location;
		}
		
		public int getObjectId() {
			return objectId;
		}
	}
	
	/**
	 * Pre-Teleport - 4 Second wait until the player is teleported
	 */
	public static void preTeleport(Player player, final GameObject object) {
		player.print("You activate the obelisk and hear a faint rumbling sound.");
		EngineTaskManager.schedule(new EngineTask() {
			int timer;
			@Override
			public void run() {
				if (timer == 4) {
					handleObelisk(object);
					stop();
				}
				timer++;
			}
		}, 0, 1); 
	}
	
	/**
	 * Teleports all of the players standing on the Obelisk
	 */
	private static void handleObelisk(GameObject object) {
		final Locations loc = randomLocation();
		for (Locations location : Locations.values()) {
			if (location.getObjectId() == object.getId()) {
				for (final Player p : Engine.getPlayers()) {
					if (isOnObelisk(p, location)) {
						EngineTaskManager.schedule(new EngineTask() {
							int timer;
							@Override
							public void run() {
								p.setNextGraphics(new Graphics(661));
								p.setNextAnimation(new Animation(8939));
								if (timer == 1) {
									p.setNextTile(new Tile(Utilities.random(loc.getLocation().getX() - 1, loc.getLocation().getX() + 1), 
											Utilities.random(loc.getLocation().getY() - 1, loc.getLocation().getY() + 1), 0));
									p.setNextAnimation(new Animation(8941));
									stop();
								}
								timer++;
							}
						}, 0, 1); 
					}
				}
			}
		}
	}
	
	/**
	 * Generates a random location from the Locations enum
	 */
	private static Locations randomLocation() {
	    int pick = new Random().nextInt(Locations.values().length);
	    return Locations.values()[pick];
	}
	
	/**
	 * Checks if the player is within the Obelisk
	 */
	private static boolean isOnObelisk(Tile tile, Locations ob) {
		return (tile.getX() >= ob.getTopLeftX() && tile.getX() <= ob.getBottomRightX() && tile.getY() >= ob.getBottomRightY() && tile.getY() <= ob.getTopLeftY());
	}
	
}