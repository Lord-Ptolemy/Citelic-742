package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class TheRaptor extends Dialogue {

	private int npcId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"Fighting", "Skilling", "Minigames", "Bosses",
					"Player vs. Player");
		} else if (stage == 0) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 1;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Training", "Slayer");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting", "Mining", "Smithing", "Fishing",
						"Next Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 3;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Barrows", "Dominion Tower", "Duel Arena", "Godwars",
						"Next Page");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 4;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"King Black Dragon", "Queen Black Dragon",
						"Kalphite Queen", "Corporeal Beast", "Next Page");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 5;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Edgeville", "Mage Bank");
			}
		} else if (stage == 1) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 11;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Beginner", "Intermediate", "Advanced");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 12;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Masters", "Dungeons", "Slayer Tower");
			}
		} else if (stage == 2) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 21;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Spinning Wheel", "Pottery", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge Mining Sites", "Varrock Mining Sites",
						"Rimmington Mine", "Dwarven Mines", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 23;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Burthorpe Smithing", "Edgeville Smithing",
						"Lumbridge Smithing", "Varrock Smithing",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 24;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Draynor Fishing", "Barbarian Fishing",
						"Catherby Fishing", "Next Page");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 28;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Woodcutting", "Thieving", "Farming", "Agility",
						"Next Page");
			}
		} else if (stage == 3) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3565, 3312, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3374, 3086, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3368, 3267, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2915, 3728, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 44;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Fight Caves", "Fight Kiln", "Fight Pits", "Rune Span",
						"Next Page");
			}
		} else if (stage == 4) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3051, 3519, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(1196, 6499, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3228, 3104, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2966, 4383, 2);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 45;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Tormented Demons", "Dagannoth Kings", "Glacors",
						"Next Page");
			}
		} else if (stage == 5) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3087, 3502, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2538, 4716, 0);
			}
		} else if (stage == 11) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 114;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Goblins", "Giant Rats", "Rock Crabs", "Hill Giants",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 115;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Hellhounds", "Fire Giants", "Dagannoths",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 116;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Demons", "Dragons", "Strykewyrms", "Previous Page");
			}
		} else if (stage == 12) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 112;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Turael", "Mazchna", "Vannaka", "Next Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 50;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Fremennik Slayer Dungeon", "Brimhaven Dungeon",
						"Taverly Dungeon", "Deep Wilderness Dungeon",
						"Next Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3429, 3525, 0);
			}
		} else if (stage == 50) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2807, 10002, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2708, 9567, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2885, 9797, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3078, 10058, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 51;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Ancient Cavern", "Smoke dungeon", "Jadinko Lair",
						"Grotworm Lair", "Next Page");
			}
		} else if (stage == 51) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(1660, 5257, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3749, 2973, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2949, 2954, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(1205, 6371, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 52;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Polypore Dungeon", "Kalphite Hive",
						"Asgarnia Ice Dungeon", "Chaos Dwarf Battlefield",
						"First Page");
			}
		} else if (stage == 52) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3409, 3326, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3421, 9509, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3011, 3151, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(1488, 4704, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 50;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Fremennik Slayer Dungeon", "Brimhaven Dungeon",
						"Taverly Dungeon", "Deep Wilderness Dungeon",
						"Next Page");
			}
		} else if (stage == 21) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2885, 3494, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2884, 3503, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting", "Mining", "Smithing", "Fishing",
						"Next Page");
			}
		} else if (stage == 22) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 25;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge South-East Mine",
						"Lumbridge South-West Mine", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 26;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Varrock South-East Mine", "Varrock South-West Mine",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2971, 3247, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3022, 3337, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting", "Mining", "Smithing", "Fishing",
						"Next Page");
			}
		} else if (stage == 23) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2890, 3503, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3105, 3499, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3225, 3251, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3187, 3428, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting", "Mining", "Smithing", "Fishing",
						"Next Page");
			}
		} else if (stage == 24) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3088, 3230, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3104, 3430, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2848, 3431, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 124;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Karamja Fishing", "Living Rock Caverns",
						"Previous Page");
			}
		} else if (stage == 25) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3232, 3152, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3150, 3150, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge Mining Sites", "Varrock Mining Sites",
						"Rimmington Mine", "Dwarven Mines", "Previous Page");
			}
		} else if (stage == 26) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3286, 3371, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3181, 3368, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge Mining Sites", "Varrock Mining Sites",
						"Rimmington Mine", "Dwarven Mines", "Previous Page");
			}
		} else if (stage == 27) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3232, 3152, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3150, 3150, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge Mining Sites", "Varrock Mining Sites",
						"Rimmington Mine", "Dwarven Mines", "Previous Page");
			}
		} else if (stage == 28) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 29;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Regular Trees", "Evergreens", "Achey trees",
						"Oak trees", "Next Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 30;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Mans", "H.A.M Members", "Draynor Square",
						"Ardougne Square", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 31;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Vegetables", "Flowers", "Herbs", "Trees",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 32;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Gnome Stronghold Course", "Barbarian Outpost",
						"Wilderness Course", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 122;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Summoning", "Hunter", "Runecrafting", "Next Page");
			}
		} else if (stage == 29) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3161, 3224, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2979, 3455, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2476, 3015, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3068, 3353, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 47;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Willow trees", "Maple trees", "Yew trees",
						"Magic trees", "Previous Page");
			}
		} else if (stage == 30) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3096, 3509, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3158, 9633, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3079, 3250, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2663, 3307, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 28;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Woodcutting", "Thieving", "Farming", "Agility",
						"Next Page");
			}
		} else if (stage == 31) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 35;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Catherby", "East Ardougne", "Port Phasmatys",
						"South of Falador", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 36;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Catherby", "East Ardougne", "Port Phasmatys",
						"South of Falador", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 37;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Catherby", "East Ardougne", "Port Phasmatys",
						"South of Falador", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 38;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lumbridge", "Varrock Castle", "Taverly",
						"Falador Park", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 28;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Woodcutting", "Thieving", "Farming", "Agility",
						"Next Page");
			}
		} else if (stage == 32) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2469, 3438, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2548, 3567, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2998, 3914, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 28;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Woodcutting", "Thieving", "Farming", "Agility",
						"Next Page");
			}
		} else if (stage == 34) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3091, 3232, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2725, 3504, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2087, 3476, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2702, 3397, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 28;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Woodcutting", "Thieving", "Farming", "Agility",
						"Next Page");
			}
		} else if (stage == 35 || stage == 36 || stage == 37) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2805, 3464, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2663, 3375, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3603, 3532, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3056, 3309, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 31;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Vegetables", "Flowers", "Herbs", "Trees",
						"Previous Page");
			}
		} else if (stage == 38) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3196, 3231, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3226, 3459, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2927, 3424, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3007, 3373, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 31;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Vegetables", "Flowers", "Herbs", "Trees",
						"Previous Page");
			}
		} else if (stage == 44) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(4615, 5130, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(4743, 5166, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3603, 5062, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3106, 3162, 1);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 46;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Castle Wars", "Clan Wars", "Pest Control", "Soulwars",
						"First Page");
			}
		} else if (stage == 45) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2575, 5733, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2900, 4449, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(4183, 5726, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 111;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Leeuni", "Blink", "First Page");
			}
		} else if (stage == 46) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2444, 3089, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2995, 9679, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2658, 2660, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3081, 3477, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 3;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Barrows", "Dominion Tower", "Duel Arena", "Godwars",
						"Next Page");
			}
		} else if (stage == 47) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3164, 3273, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2725, 3499, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2757, 3425, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2701, 3395, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 29;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Regular Trees", "Evergreens", "Achey trees",
						"Oak trees", "Next Page");
			}
		} else if (stage == 111) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3720, 2974, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2085, 4461, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 4;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"King Black Dragon", "Queen Black Dragon",
						"Kalphite Queen", "Corporeal Beast", "Next Page");
			}
		} else if (stage == 112) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2912, 3423, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3511, 3507, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3145, 9912, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 113;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Sumona", "Duradel", "Kuradal", "Previous Page");
			}
		} else if (stage == 113) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3359, 2993, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2869, 2982, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(1740, 5313, 1);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 112;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Turael", "Mazchna", "Vannaka", "Next Page");
			}
		} else if (stage == 114) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3255, 3245, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3233, 3173, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2673, 3710, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3116, 9848, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 11;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Beginner", "Immediate", "Advanced");
			}
		} else if (stage == 115) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2867, 9839, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2657, 9493, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2442, 10147, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 11;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Beginner", "Immediate", "Advanced");
			}
		} else if (stage == 116) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 117;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Lesser Demons", "Greater Demons", "Black Demons",
						"Abbysal Demons", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 118;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Leather Dragons", "Metal Dragons", "Frost Dragons",
						"Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 121;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Ice Strykewyrms", "Desert Strykewyrms",
						"Jungle Strykewyrms", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 11;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Beginner", "Immediate", "Advanced");
			}
		} else if (stage == 117) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2836, 9574, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2859, 9747, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2872, 9785, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(3412, 3555, 2);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 116;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Demons", "Dragons", "Strykewyrms", "Previous Page");
			}
		} else if (stage == 118) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 119;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Green Dragons", "Blue Dragons", "Red Dragons",
						"Black Dragons", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 120;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Bronze Dragons", "Iron Dragons", "Steel Dragons",
						"Mithril Dragons", "Previous Page");
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3033, 9597, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 116;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Demons", "Dragons", "Strykewyrms", "Previous Page");
			}
		} else if (stage == 119) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3358, 3702, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2895, 9793, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2688, 9507, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2826, 9826, 0);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 118;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Leather Dragons", "Metal Dragons", "Previous Page");
			}
		} else if (stage == 120) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2722, 9486, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2709, 9474, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2709, 9474, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(1766, 5339, 1);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 118;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Leather Dragons", "Metal Dragons", "Previous Page");
			}
		} else if (stage == 121) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(3422, 5668, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3376, 3158, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2457, 2913, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 116;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Demons", "Dragons", "Strykewyrms", "Previous Page");
			}
		} else if (stage == 122) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2927, 3448, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 123;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Snare Trap", "Box Trap");
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(3040, 4844, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 72;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Construction", "Dungeoneering", "First Page");
			}
		} else if (stage == 72) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2956, 3225, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3450, 3700, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting", "Mining", "Smithing", "Fishing",
						"Next Page");
			}
		} else if (stage == 123) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 110;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crimson Swift", "Cerulean Twitch", "Tropic Wagtail",
						"Wimpy bird");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 111;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Chinchompa", "Carnivorous Chinchompa");
			}
		} else if (stage == 110) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2606, 2893, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2719, 3780, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				player.sendTeleport(2520, 2916, 0);
			}
			if (componentId == Dialogue.OPTION_4) {
				player.sendTeleport(2530, 2822, 0);
			}
		} else if (stage == 111) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2319, 3540, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(2558, 2914, 0);
			}
		} else if (stage == 124) {
			if (componentId == Dialogue.OPTION_1) {
				player.sendTeleport(2924, 3175, 0);
			}
			if (componentId == Dialogue.OPTION_2) {
				player.sendTeleport(3655, 5133, 0);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 24;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Draynor Fishing", "Barbarian Fishing",
						"Catherby Fishing", "Next Page");
			}
		}
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello young traveler,"
				+ " where would you like to go?");
	}
}