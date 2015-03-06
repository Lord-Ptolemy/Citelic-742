package com.citelic.game.map;

import com.citelic.game.map.tile.Tile;

public final class MultiRegions {

	public static final boolean isAtDungeoneeringLobby(Tile tile) {
		return (tile.getX() >= 3084 && tile.getX() <= 3092
				&& tile.getY() >= 3929 && tile.getY() <= 3937);
	}

	public static final boolean isAtForinthryDungeon(Tile tile) {
		return (tile.getX() >= 3011 && tile.getX() <= 3132
				&& tile.getY() >= 10052 && tile.getY() <= 10175);
	}

	public static final boolean isAtWilderness(Tile tile) {
		return (tile.getX() >= 3011 && tile.getX() <= 3132
				&& tile.getY() >= 10052 && tile.getY() <= 10175)
				|| (tile.getX() >= 2940 && tile.getX() <= 3395
						&& tile.getY() >= 3525 && tile.getY() <= 4000)
				|| (tile.getX() >= 3264 && tile.getX() <= 3279
						&& tile.getY() >= 3279 && tile.getY() <= 3672)
				|| (tile.getX() >= 2756 && tile.getX() <= 2875
						&& tile.getY() >= 5512 && tile.getY() <= 5627)
				|| (tile.getX() >= 3158 && tile.getX() <= 3181
						&& tile.getY() >= 3679 && tile.getY() <= 3697)
				|| (tile.getX() >= 3280 && tile.getX() <= 3183
						&& tile.getY() >= 3885 && tile.getY() <= 3888)
				|| (tile.getX() >= 3012 && tile.getX() <= 3059
						&& tile.getY() >= 10303 && tile.getY() <= 10351);
	}

	public static final boolean isAtWildernessDitch(Tile tile) {
		return (tile.getX() >= 2940 && tile.getX() <= 3395
				&& tile.getY() <= 3524 && tile.getY() >= 3523 || tile.getX() >= 3084
				&& tile.getX() <= 3092
				&& tile.getY() >= 3929
				&& tile.getY() <= 3937);
	}

	public static final boolean isAtWildernessSafeZone(Tile tile) {
		return (tile.getX() >= 2940 && tile.getX() <= 3395
				&& tile.getY() <= 3524 && tile.getY() >= 3523 || tile.getX() >= 3084
				&& tile.getX() <= 3092
				&& tile.getY() >= 3929
				&& tile.getY() <= 3937);
	}

}