package com.citelic.game.entity.player.content.transportation;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.map.tile.Tile;

public class FairyRing {

    private final static String[][] LETTERS = new String[][] { { "a", "b", "c", "d" }, { "i", "j", "k", "l" }, { "p", "q", "r", "s" } };
    private final static Tile FAIRY_SOURCE = new Tile(2412, 4434, 0);
    private final static int FIRST_ANIMATION = 3254, SECOND_ANIMATION = 3255;
    private final static int FIRST_GRAPHICS = 2670, SECOND_GRAPHICS = 2671;

    public static enum Rings {
	AIQ(new Tile(2996, 3114, 0)), AJQ(new Tile(2735, 5221, 0)), AJR(new Tile(2780, 3613, 0)), AKQ(new Tile(2319, 3619, 0)), AKS(new Tile(2571, 2956, 0)), ALP(new Tile(2468, 4189, 0)), ALQ(new Tile(3597, 3495, 0)), ALS(new Tile(2644, 3495, 0)), BIP(new Tile(3410, 3324, 0)), BIQ(new Tile(3251, 3095, 0)), BJQ(new Tile(1737, 5342, 0)), BJR(new Tile(2650, 4730, 0)), BKP(new Tile(2385, 3035, 0)), BKR(new Tile(3469, 3431, 0)), BLP(new Tile(4622, 5147, 0)), BLR(new Tile(2740, 3351, 0)), CIP(new Tile(2513, 3884, 0)), CIQ(new Tile(2528, 3127, 0)), CJR(new Tile(2705, 3576, 0)), CKR(new Tile(2801, 3003, 0)), CKS(new Tile(3447, 3470, 0)), CLR(new Tile(2735, 2742, 0)), CLS(new Tile(2682, 3081, 0)), DIP(new Tile(3763, 2930, 0)), DIS(new Tile(3108, 3149, 0)), DJP(new Tile(2658, 3230, 0)), DJR(new Tile(2676, 3587, 0)), DKP(new Tile(2900, 3111, 0)), DKQ(new Tile(4183, 5726, 0)), DKR(new Tile(3129, 3496, 0)), DKS(new Tile(2744, 3719, 0)), DLQ(new Tile(3423, 3016, 0)), DLS(new Tile(3501, 9821, 0)), AIR(new Tile(2700, 3247, 0)), AJS(new Tile(2500, 3896, 0)), ALR(new Tile(3059, 4875, 0)), BIR(new Tile(2455, 4396, 0)), BIS(new Tile(2635, 3266, 0)), BKQ(new Tile(3041, 4532, 0)), BLQ(new Tile(2229, 4244, 0)), CKP(new Tile(2075, 4848, 0)), CLP(new Tile(3082, 3206, 0)), DIR(new Tile(3038, 5348, 0)), DLR(new Tile(2213, 3099, 0)), AIS(null), AIP(null), AKP(null);

	private Tile tile;

	private Rings(Tile tile) {
	    this.tile = tile;
	}

	public Tile getTile() {
	    return tile;
	}
    }

    public static boolean checkAll(Player player) {
	if (player.getEquipment().getWeaponId() == 772 || player.getEquipment().getWeaponId() == 9084)
	    return true;
	player.getPackets().sendGameMessage("The fairy ring only works for those who wield fairy magic.");
	return false;
    }

    public static boolean openRingInterface(Player player, Tile tile, boolean source) {
	if (checkAll(player)) {
	    player.addWalkSteps(tile.getX(), tile.getY(), -1, true);
	    if (source) {
		player.getInterfaceManager().sendInterface(734);
		sendTravelLog(player);
		resetRingHash(player);
	    } else
		sendTeleport(player, FAIRY_SOURCE);
	    return true;
	}
	return false;
    }

    private static void sendTravelLog(Player player) {
	player.getInterfaceManager().sendInventoryInterface(735);
	player.getPackets().sendIComponentText(735, 15, "          Asgarnia: Mudskipper Point");
	player.getPackets().sendIComponentText(735, 16, "          Islands: South of Witchaven");
	player.getPackets().sendIComponentText(735, 19, "          Dungeons: Dark cave south of Dorgesh-Kaan");
	player.getPackets().sendIComponentText(735, 20, "          Kandarin: Slayer cave south-east of Rellekka");
	player.getPackets().sendIComponentText(735, 21, "          Islands: Penguins near Miscellania");
	player.getPackets().sendIComponentText(735, 23, "          Kandarin: Piscatoris Hunter area");
	player.getPackets().sendIComponentText(735, 25, "          Feldip Hills: Feldip Hunter area");
	player.getPackets().sendIComponentText(735, 26, "          Kandarin: Feldip Hills");
	player.getPackets().sendIComponentText(735, 27, "          Morytania: Haunted Woods east of Canifis");
	player.getPackets().sendIComponentText(735, 28, "          Other Realms: Abyss");
	player.getPackets().sendIComponentText(735, 29, "          Kandarin: McGrubor's Wood");
	player.getPackets().sendIComponentText(735, 30, "          Islands: Polypore Dungeon");
	player.getPackets().sendIComponentText(735, 31, "          Kharidian Desert: Near Kalphite hive");
	player.getPackets().sendIComponentText(735, 32, "          Sparse Plane");
	player.getPackets().sendIComponentText(735, 33, "          Kandarin: Ardougne Zoo unicorns");
	player.getPackets().sendIComponentText(735, 35, "          Dungeons: Ancient Dungeon");
	player.getPackets().sendIComponentText(735, 36, "          Fisher Realm");
	player.getPackets().sendIComponentText(735, 38, "          Feldip Hills: South of Castle Wars");
	player.getPackets().sendIComponentText(735, 39, "          Other Realms: Enchanted Valley");
	player.getPackets().sendIComponentText(735, 40, "          Morytania: Mort Myre, south of Canifis");
	player.getPackets().sendIComponentText(735, 42, "          Dungeons: TzHaar area");
	player.getPackets().sendIComponentText(735, 43, "          Yu'biusk");
	player.getPackets().sendIComponentText(735, 44, "          Kandarin: Legend's Guild");
	player.getPackets().sendIComponentText(735, 46, "          North-west Miscellania");
	player.getPackets().sendIComponentText(735, 47, "          Kandarin: North-west of Yanille");
	player.getPackets().sendIComponentText(735, 52, "          Kandarin: Sinclair Mansion");
	player.getPackets().sendIComponentText(735, 56, "          Karamja: South of Tai Bwo Wannai Village");
	player.getPackets().sendIComponentText(735, 57, "          Morytania: Canifis");
	player.getPackets().sendIComponentText(735, 60, "          Ape Atoll: West of the Ape Atoll Agility Course");
	player.getPackets().sendIComponentText(735, 61, "          Islands: Jungle spiders near Yanille");
	player.getPackets().sendIComponentText(735, 62, "          Mos Le'Harmless: Isle on the coast of Mos Le'Harmless");
	player.getPackets().sendIComponentText(735, 65, "          Misthalin: Wizards' Tower");
	player.getPackets().sendIComponentText(735, 66, "          Kandarin: Tower of Life");
	player.getPackets().sendIComponentText(735, 68, "          Kandarin: Sinclair Mansion");
	player.getPackets().sendIComponentText(735, 70, "          Karamja: South of Musa Point");
	player.getPackets().sendIComponentText(735, 71, "          Glacor Cave");
	player.getPackets().sendIComponentText(735, 72, "          Misthalin: Edgeville");
	player.getPackets().sendIComponentText(735, 73, "          Kandarin: Snowy Hunter area");
	player.getPackets().sendIComponentText(735, 75, "          Kharidian Desert: North of Nardah");
	player.getPackets().sendIComponentText(735, 77, "          Dungeons: Myreque hideout under The Hollows");
	player.getPackets().sendIComponentText(735, 54, "          Other Realms: Cosmic Entity's plane");
	player.getPackets().sendIComponentText(735, 58, "          Islands: South of Draynor Village");
	player.getPackets().sendIComponentText(735, 64, "          Other Realms: The Gorak Plane");
	player.getPackets().sendIComponentText(735, 76, "          Islands: Poison Waste south of Isafdar");
    }

    public static boolean confirmRingHash(Player player) {
	int[] locationArray = (int[]) player.getTemporaryAttributtes().remove("location_array");
	if (locationArray == null)
	    return false;
	StringBuilder string = new StringBuilder();
	int index = 0;
	for (int letterValue : locationArray)
	    string.append(LETTERS[index++][letterValue]);
	return sendRingTeleport(player, string.toString().toUpperCase());
    }

    public static boolean sendRingTeleport(Player player, int hash) {
	int letter1 = hash / 16;
	hash -= letter1 * 16;
	int letter2 = hash / 4;
	hash -= letter2 * 4;
	int letter3 = hash;
	StringBuilder string = new StringBuilder();
	string.append(LETTERS[0][letter1]);
	string.append(LETTERS[1][letter2]);
	string.append(LETTERS[2][letter3]);
	return sendRingTeleport(player, string.toString().toUpperCase());
    }

    public static boolean sendRingTeleport(Player player, String hash) {
	Rings ring;
	try {
	    ring = Rings.valueOf(hash);
	}
	catch (Throwable e) {
	    ring = null;
	}
	if (ring == null || ring.getTile() == null) {
	    sendTeleport(player, new Tile(FAIRY_SOURCE, 2));
	    return false;
	}
	sendTeleport(player, ring.getTile());
	return true;
    }

    private static void resetRingHash(Player player) {
	player.getTemporaryAttributtes().put("location_array", new int[3]);
	for (int i = 0; i < 3; i++)
		player.getPackets().sendConfigByFile(2341 + i, 0);
    }

    private static void sendTeleport(final Player player, final Tile tile) {
	Magic.sendTeleportSpell(player, FIRST_ANIMATION, SECOND_ANIMATION, FIRST_GRAPHICS, SECOND_GRAPHICS, 0, 0, tile, 2, false, Magic.OBJECT_TELEPORT);
    }

    public static void handleDialButtons(final Player player, int componentId) {
	int[] locationArray = (int[]) player.getTemporaryAttributtes().get("location_array");
	if (locationArray == null) {
	    player.closeInterfaces();
	    return;
	}
	if (player.getTemporaryAttributtes().get("location_changing") != null)
	    return;
	int index = (componentId - 23) / 2;
	if (componentId % 2 == 0)
	    locationArray[index]++;
	else
	    locationArray[index]--;
	locationArray = getCorrectValues(locationArray);
	player.getTemporaryAttributtes().put("location_array", locationArray);
	player.getTemporaryAttributtes().put("location_changing", true);
	for (int i = 0; i < 3; i++)
	    player.getPackets().sendConfigByFile(2341 + i, locationArray[i] == 1 ? 3 : locationArray[i] == 3 ? 1 : locationArray[i]);
	EngineTaskManager.schedule(new EngineTask() {

	    @Override
	    public void run() {
		player.getTemporaryAttributtes().remove("location_changing");
	    }
	}, 3);
    }

    private static int[] getCorrectValues(int[] locationArray) {
	int loop = 0;
	for (int values : locationArray) {
	    locationArray[loop++] = values & 0x3;
	}
	return locationArray;
    }
}
