package com.citelic.game.map;

import com.citelic.utility.Utilities;

/*
 * @author Dragonkk/Alex
 */
public class MapUtils {

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
	public static int[] convert(Structure from, Structure to, int... xy) {
		return new int[] { xy[0] * from.getWidth() / to.getWidth(),
				xy[1] * from.getHeight() / to.getHeight() };
	}

	public static int encode(Structure structure, int... xyp) {
		return structure.encode(xyp[0], xyp[1], xyp.length == 3 ? xyp[2] : 0);
	}

	public static Area getArea(int minX, int minY, int maxX, int maxY) {
		return MapUtils.getArea(Structure.TILE, minX, minY, maxX, maxY);
	}

	public static Area getArea(Structure structure, int minX, int minY,
			int maxX, int maxY) {
		return new Area(structure, minX, minY, maxX - minY, maxY - minY);
	}

	public static enum Structure {

		TILE(null, 1, 1, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return y | x << 14 | plane << 28;
			}
		}), CHUNK(TILE, 8, 8, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return x << 14 | y << 3 | plane << 24;
			}
		}), REGION(CHUNK, 8, 8, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return x << 8 | y | plane << 16;
			}
		}), MAP(REGION, 255, 255);

		private Structure child;
		private int width, height;
		private StructureEncoder encoder;

		/*
		 * width * height squares. For instance 4x4: S S S S S S S S S S S S
		 */

		private Structure(Structure child, int width, int height) {
			this(child, width, height, null);
		}

		private Structure(Structure child, int width, int height,
				StructureEncoder encode) {
			this.child = child;
			this.width = width;
			this.height = height;
			encoder = encode;
		}

		public int encode(int x, int y) {
			return this.encode(x, y, 0);
		}

		public int encode(int x, int y, int plane) {
			return encoder == null ? -1 : encoder.encode(x, y, plane);
		}

		public int getChildHeight() {
			return width;
		}

		public int getChildWidth() {
			return width;
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

	private static interface StructureEncoder {

		public abstract int encode(int x, int y, int plane);

	}

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
			return structure.encode(x, y, 0);
		}

		@Override
		public String toString() {
			return "Structure: " + structure.toString() + ", x: " + x + ", y: "
					+ y + ", width: " + width + ", height: " + height;
		}
	}

}