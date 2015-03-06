package com.citelic.game;

import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class ForceMovement {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	private Tile toFirstTile;
	private Tile toSecondTile;
	private int firstTileTicketDelay;
	private int secondTileTicketDelay;
	protected int direction;

	/*
	 * USE: moves to firsttile firstTileTicketDelay: the delay in game tickets
	 * between your tile and first tile the direction
	 */
	public ForceMovement(Tile toFirstTile, int firstTileTicketDelay,
			int direction) {
		this(toFirstTile, firstTileTicketDelay, null, 0, direction);
	}

	/*
	 * USE: moves to firsttile and from first tile to second tile
	 * firstTileTicketDelay: the delay in game tickets between your tile and
	 * first tile secondTileTicketDelay: the delay in game tickets between first
	 * tile and second tile the direction
	 */
	public ForceMovement(Tile toFirstTile, int firstTileTicketDelay,
			Tile toSecondTile, int secondTileTicketDelay, int direction) {
		this.toFirstTile = toFirstTile;
		this.firstTileTicketDelay = firstTileTicketDelay;
		this.toSecondTile = toSecondTile;
		this.secondTileTicketDelay = secondTileTicketDelay;
		this.direction = direction;
	}

	public int getDirection() {
		switch (direction) {
		case NORTH:
			return Utilities.getFaceDirection(0, 1);
		case EAST:
			return Utilities.getFaceDirection(1, 0);
		case SOUTH:
			return Utilities.getFaceDirection(0, -1);
		case WEST:
		default:
			return Utilities.getFaceDirection(-1, 0);
		}
	}

	public int getFirstTileTicketDelay() {
		return firstTileTicketDelay;
	}

	public int getSecondTileTicketDelay() {
		return secondTileTicketDelay;
	}

	public Tile getToFirstTile() {
		return toFirstTile;
	}

	public Tile getToSecondTile() {
		return toSecondTile;
	}

}
