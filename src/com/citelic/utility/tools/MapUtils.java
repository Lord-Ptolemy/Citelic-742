package com.citelic.utility.tools;

import com.citelic.utility.Utilities;

public class MapUtils {

	public static final class Area {

		private Structure structure;
		private int x, y, width, height;

		public Area(Structure structure, int x, int y, int width, int height) {
			this.structure = structure;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getMapHeight() {
			return height * structure.getHeight();
		}

		public int getMapWidth() {
			return width * structure.getWidth();
		}

		public int getMapX() {
			return x * structure.getWidth();
		}

		public int getMapY() {
			return y * structure.getHeight();
		}

		public Structure getStructure() {
			return structure;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public int hashCode() {
			return structure.getHash(x, y, 0);
		}

		@Override
		public String toString() {
			return "Structure: " + structure.toString() + ", x: " + x + ", y: "
					+ y + ", width: " + width + ", height: " + height;
		}
	}

	public static enum Structure {

		TILE(null, 1, 1, new StructureHash() {
			@Override
			public int generateHash(int x, int y, int plane) {
				return y | (x << 14) | (plane << 28);
			}
		}), CHUNK(TILE, 8, 8, new StructureHash() {
			@Override
			public int generateHash(int x, int y, int plane) {
				return (x << 14) | (y << 3) | (plane << 24);
			}
		}), REGION(CHUNK, 8, 8, new StructureHash() {
			@Override
			public int generateHash(int x, int y, int plane) {
				return ((x << 8) | y | (plane << 16));
			}
		}), MAP(REGION, 255, 255);

		private Structure child;
		private int width, height;
		private StructureHash hash;

		/*
		 * width * height squares. For instance 4x4: S S S S S S S S S S S S
		 */

		private Structure(Structure child, int width, int height) {
			this(child, width, height, null);
		}

		private Structure(Structure child, int width, int height,
				StructureHash hash) {
			this.child = child;
			this.width = width;
			this.height = height;
			this.hash = hash;
		}

		public int getChildHeight() {
			return width;
		}

		public int getChildWidth() {
			return width;
		}

		public int getHash(int x, int y) {
			return getHash(x, y, 0);
		}

		public int getHash(int x, int y, int plane) {
			return hash == null ? -1 : hash.generateHash(x, y, plane);
		}

		public int getHeight() {
			int y = height;
			Structure nextChild = child;
			while (nextChild != null) {
				y *= nextChild.height;
				nextChild = nextChild.child;
			}
			return y;
		}

		public int getWidth() {
			int x = width;
			Structure nextChild = child;
			while (nextChild != null) {
				x *= nextChild.width;
				nextChild = nextChild.child;
			}
			return x;
		}

		@Override
		public String toString() {
			return Utilities.formatPlayerNameForDisplay(name());
		}
	}

	private static interface StructureHash {

		public abstract int generateHash(int x, int y, int plane);

	}

	/*
	 * returns converted area
	 */
	public static Area convert(Structure to, Area area) {
		int x = area.getMapX() / to.getWidth();
		int y = area.getMapY() / to.getHeight();
		int width = area.getMapWidth() / to.getWidth();
		int height = area.getMapHeight() / to.getHeight();
		return new Area(to, x, y, width, height);
	}

	/*
	 * converted pos return converted x and y
	 */
	public static int[] convert(Structure from, Structure to, int x, int y) {
		return new int[] { x * from.getWidth() / to.getWidth(),
				y * from.getHeight() / to.getHeight() };
	}

	public static Area getArea(int minX, int minY, int maxX, int maxY) {
		return getArea(Structure.TILE, minX, minY, maxX, maxY);
	}

	public static Area getArea(Structure structure, int minX, int minY,
			int maxX, int maxY) {
		return new Area(structure, minX, minY, maxX - minY, maxY - minY);
	}

	public static int getHash(Structure structure, int x, int y, int plane) {
		return structure.getHash(x, y, plane);
	}

	public int getHash(Structure structure, int x, int y) {
		return getHash(structure, x, y, 0);
	}

}
