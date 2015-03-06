package com.citelic.game.entity.player.content.actions.skills.dungeoneering;

import com.citelic.game.map.tile.Tile;

public class DungeoneeringConstants {

    public static Tile DAEMONHEIM_LOBBY = new Tile(3450, 3725, 0), DAEMONHEIM_FLOOR = new Tile(3461, 3721, 1);
    public static int[][] FROZEN_ROOMS = new int[][]{{14, 624}, {18, 532}, {28, 624}};
    public static int[][] ABANDONED_ROOMS = new int[][]{{14, 640}, {18, 548}, {26, 640}};
    public static int[][] FURNISHED_ROOMS = new int[][]{{14, 656}, {18, 564}, {24, 656}};
    public static int[][] OCCULT_ROOMS = new int[][]{{14, 672}, {18, 580}, {24, 672}};
    public static int[][] WARPED_ROOMS = new int[][]{{14, 688}, {18, 596}, {30, 688}};
    public static int[] randomLongsword = new int[]{16383, 16385, 16387, 16389, 16391, 16393, 16395, 16397, 16399, 16401, 16403};
    public static int[] randomPlatebody = new int[]{17239, 17241, 17243, 17245, 17247, 17249, 17251, 17253, 17255, 17257, 17259};
    public static int[] randomPlatelegs = new int[]{16669, 16671, 16673, 16675, 16677, 16679, 16681, 16683, 16685, 16687, 16689};
    public static int[] randomMaul = new int[]{16405, 16407, 16409, 16411, 16413, 16415, 16417, 16419, 16421, 16423, 16425};
    public static int[] randomRapier = new int[]{16935, 16937, 16939, 16941, 16943, 16945, 16947, 16949, 16951, 16953, 16955};
    public static int[] random2h = new int[]{16889, 16891, 16893, 16895, 16897, 16899, 16901, 16903, 16905, 16907, 16909};
    public static int[] randomFullHelm = new int[]{16691, 16693, 16695, 16697, 16699, 16701, 16703, 16705, 16707, 16709, 16711};
    public static int[] randomRare = new int[]{17291, 17281, 17283};
    public static int[] MONSTER1 = new int[]{10408, 10254, 10306, 10904, 10547};
    public static int[] MONSTER2 = new int[]{10320, 10338, 10349, 10357, 10363};
    public static int[] BOSS = new int[]{9950, 10116, 9903, 11879, 12825};

    public enum Room {
        START, MIDDLE, BOSS;
    }
}
