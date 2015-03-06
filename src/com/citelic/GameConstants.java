package com.citelic;

import com.citelic.game.map.tile.Tile;

public final class GameConstants {

	public static final String SERVER_NAME = "Citelic";
	/* Server management */
	public static final int REVISION = 742; // revision of the client
	public static final int SUB_REVISION = 2; // client build
	public static final int MINIMUM_RAM_ALLOCATED = 100000000; // 100 megabyte

	public static final int WORLD_CYCLE_TIME = 600; // recycle 600 milliseconds
	/* Data location file */
	public static final String CACHE_PATH = "data/cache/";

	public static final String LOGS_PATH = "data/playersaves/logs/";
	/* Server limits */
	public static final int SV_RECEIVE_DATA_LIMIT = 10000; // bytes
	public static final int SV_PACKET_SIZE_LIMIT = 10000; // bytes
	public static final long SV_MAX_PACKETS_DECODER_PING_DELAY = 30000; // 30
	// seconds
	public static final int SV_PLAYERS_LIMIT = 1000;
	public static final int SV_LOCAL_PLAYERS_LIMIT = 250;
	public static final int SV_NPCS_LIMIT = Short.MAX_VALUE;

	public static final int SV_LOCAL_NPCS_LIMIT = 250;

	// MAX SESSIONS
	public static int MAX_CONNECTED_SESSIONS_PER_IP = 3;

	// Degrading
	public static int DEGRADE_GEAR_RATE = 1;

	/* Map configuration size */
	public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };

	// CLIENT TOKEN -1
	public static final String GRAB_SERVER_TOKEN = "MpanIDx68ZShS/0wQc60lSvsuEhgYKEW";

	public static final int START_PLAYER_HITPOINTS = 100;
	public static final Tile START_PLAYER_LOCATION = new Tile(2892, 3526, 0);
	public static final String START_CONTROLER = "null";

	public static final Tile RESPAWN_PLAYER_LOCATION = new Tile(2892, 3526, 0);

	public static final String[] ADMINISTRATORS = { "Ridiculous", "Joris",
			"Theerik009" };

	/* Game configuration */
	public static final int AIR_GUITAR_MUSICS_COUNT = 50;
	public static final int QUESTS = 183;
	public static final int CHARM_RATE = 1;

	public static final int DROP_RATE = 1;
	/* Community configuration */
	public static boolean DOUBLE_EXPERIENCE = false;

	public static boolean DOUBLE_DROPS = false;
	// Website links
	public static String HOMEPAGE = "http://citelic.com";
	public static String WEBSHOP = "http://citelic.com/donate/";
	public static String FORUMS = "http://citelic.com/community/";

	public static String VOTE_URL = "http://citelic.com/vote";

	public static boolean ECONOMY_MODE;
	public static boolean DEBUG;

	public static boolean SERVER_PUBLIC;

	// MySQL
	public static boolean USING_MYSQL_SERVICE = false;

	// Squeal Of Fortune
	public static boolean IS_SQUEAL_OF_FORTUNE_ENABLED = false;
	// XP configuration
	public static final double COMBAT_XP_RATE = 150, SKILLING_XP_RATE = 100,
			RUNECRAFTING_XP_RATE = 75;

	public static final double CRAFTING_XP_RATE = 50, FISHING_XP_RATE = 50,
			SUMMONING_XP_RATE = 35;

	// NPC Settings
	public static final int[] NON_WALKING_NPCS = { 14620, 13955, 6892, 6539,
			1862, 1597, 557, 3820, 2759, 1918, 1783, 1167, 461, 683, 659, 568,
			598, 495, 553, 538, 540, 541, 548, 549, 552, 554, 576, 587, 2676 };

	// MAX SELL PRICE
	public static final int MAX_SELL_RRICE = 100000000;

	public static String yellChangedBy;

	// Community Event
	public static String eventType;
	public static boolean eventActive;
	public static int communityEventX, communityEventY, communityEventP;

	// MISC
	public static boolean LENDING_DISABLED = false;
	public static int HOME_REGION_ID = 11575;

	public static final String[] FORBIDDEN_SOUL_WARS_ITEMS = { "torva",
			"virtus", "pernix", "divine spirit shield", "arcane spirit shield",
			"spectral spirit shield", "elysian spirit shield", "ganodermic",
			"primal", "knife" };

}