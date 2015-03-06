package com.citelic.game.map.tile;

import java.io.Serializable;

import com.citelic.GameConstants;
import com.citelic.utility.Utilities;

public class Tile implements Serializable {

	private static final long serialVersionUID = -6567346497259686765L;

	public static final int getCoordFaceX(int x, int sizeX, int sizeY,
			int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public static final int getCoordFaceY(int y, int sizeX, int sizeY,
			int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	private short x, y;

	protected byte plane;

	public Tile(int hash) {
		this.x = (short) (hash >> 14 & 0x3fff);
		this.y = (short) (hash & 0x3fff);
		this.plane = (byte) (hash >> 28);
	}

	public Tile(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public Tile(Tile tile) {
		this.x = tile.x;
		this.y = tile.y;
		this.plane = tile.plane;
	}

	public Tile(Tile tile, int randomize) {
		this.x = (short) (tile.x + Utilities.getRandom(randomize * 2) - randomize);
		this.y = (short) (tile.y + Utilities.getRandom(randomize * 2) - randomize);
		this.plane = tile.plane;
	}

	public int getChunkX() {
		return (x >> 3);
	}

	public int getChunkY() {
		return (y >> 3);
	}

	public int getCoordFaceX(int sizeX) {
		return getCoordFaceX(sizeX, -1, -1);
	}

	public int getCoordFaceX(int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public int getCoordFaceY(int sizeY) {
		return getCoordFaceY(-1, sizeY, -1);
	}

	public int getCoordFaceY(int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public int getLocalX() {
		return getLocalX(this);
	}

	public int getLocalX(Tile tile) {
		return getLocalX(tile, 0);
	}

	public int getLocalX(Tile tile, int mapSize) {
		return x - 8
				* (tile.getChunkX() - (GameConstants.MAP_SIZES[mapSize] >> 4));
	}

	public int getLocalY() {
		return getLocalY(this);
	}

	public int getLocalY(Tile tile) {
		return getLocalY(tile, 0);
	}

	public int getLocalY(Tile tile, int mapSize) {
		return y - 8
				* (tile.getChunkY() - (GameConstants.MAP_SIZES[mapSize] >> 4));
	}

	public int getZ() {
		if (plane > 3)
			return 3;
		return plane;
	}

	public int getRegionHash() {
		return getRegionY() + (getRegionX() << 8) + (plane << 16);
	}

	public int getRegionId() {
		return ((getRegionX() << 8) + getRegionY());
	}

	public int getRegionX() {
		return (x >> 6);
	}

	public int getRegionY() {
		return (y >> 6);
	}

	public int getTileHash() {
		return y + (x << 14) + (plane << 28);
	}

	public int getX() {
		return x;
	}

	public int getXInRegion() {
		return x & 0x3F;
	}

	public int getY() {
		return y;
	}

	public int getYInRegion() {
		return y & 0x3F;
	}

	public int getXInChunk() {
		return x & 0x7;
	}

	public int getYInChunk() {
		return y & 0x7;
	}

	/**
	 * Checks if this world tile's coordinates match the other world tile.
	 * 
	 * @param other
	 *            The world tile to compare with.
	 * @return {@code True} if so.
	 */
	public boolean matches(Tile other) {
		return x == other.x && y == other.y && plane == other.plane;
	}

	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		x += xOffset;
		y += yOffset;
		plane += planeOffset;
	}

	public final void setLocation(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public final void setLocation(Tile tile) {
		setLocation(tile.x, tile.y, tile.plane);
	}

	public Tile transform(int x, int y, int plane) {
		return new Tile(this.x + x, this.y + y, this.plane + plane);
	}

	public boolean withinDistance(Tile tile) {
		if (tile.plane != plane)
			return false;
		// int deltaX = tile.x - x, deltaY = tile.y - y;
		return Math.abs(tile.x - x) <= 14 && Math.abs(tile.y - y) <= 14;// deltaX
		// <= 14
		// &&
		// deltaX
		// >=
		// -15
		// &&
		// deltaY
		// <= 14
		// &&
		// deltaY
		// >=
		// -15;
	}

	public boolean withinDistance(Tile tile, int distance) {
		if (tile.plane != plane)
			return false;
		int deltaX = tile.x - x, deltaY = tile.y - y;
		return deltaX <= distance && deltaX >= -distance && deltaY <= distance
				&& deltaY >= -distance;
	}

	public boolean withinArea(int a, int b, int c, int d) {
		return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
	}

	public int getPlane() {
		if (plane > 3)
			return 3;
		return plane;
	}

}
