package com.citelic.game.entity.player.content.miscellaneous;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.managers.QuestManager.Quests;

public class CompletionistStand {

	public static int checkAllStats(Player player) {
		int reqs = 0;
		for (int i = 0; i < 24; i++) {
			if (player.getSkills().getLevelForXp(i) >= 99) {
				if (i == 24 && player.getSkills().getLevelForXp(24) < 120)
					reqs--;
				reqs++;
			}
		}
		return reqs;
	}

	public static boolean completionistCapeRequierment(Player player,
			Boolean trimmed) {
		if (!player.isCompletedFightKiln()) {
			return false;
		} else if (!player.isKilledCulinaromancer()) {
			return false;
		} else if (!player.isKilledBork()) {
			return false;
		} else if (!player.isKilledQueenBlackDragon()) {
			return false;
		} else if (!player.isGotInfernoAdze() == true) {
			return false;
		} else if (!player.getQuestManager().completedQuest(
				Quests.NOMADS_REQUIEM)) {
			return false;
		} else if (player.getBarbarianAdvancedLaps() < 250
				&& player.getGnomeAdvancedLaps() < 200) {
			return false;
		} else if (!player.isHasCutEnoughLogs() == true) {
			return false;
		} else if (player.getBarsDone() < 3000) {
			return false;
		} else if (player.getRocktailsCooked() < 5000) {
			return false;
		} else if (checkAllStats(player) < 24) {
			return false;
		}
		if (trimmed && player.getPestControlGames() < 100) {
			return false;
		}
		if (trimmed && !player.isHasCutMoreEnoughLogs()) {
			return false;
		}
		return true;
	}

	public static void openCapeStandInterface(Player player, boolean trimmed) {
		player.getInterfaceManager().sendInterface(275);
		for (int i = 0; i < 100; i++) {
			player.getPackets().sendIComponentText(275, i, "");
		}
		player.getPackets().sendIComponentText(
				275,
				1,
				trimmed ? "Trimmed Requirements"
						: "Completionist Cape Requirements");
		if (!trimmed) {
			if (checkAllStats(player) == 24)
				player.getPackets().sendIComponentText(275, 10,
						"<str>99 All stats and 120 Dungeoneering.");
			else
				player.getPackets().sendIComponentText(275, 10,
						"99 All stats and 120 Dungeoneering.");
			if (!player.isGotInfernoAdze() == true)
				player.getPackets().sendIComponentText(
						275,
						11,
						"Burn " + player.getMagicLogsBurned()
								+ "/2000 Magic Logs");
			else
				player.getPackets().sendIComponentText(275, 11,
						"<str>Burn 2000 Magic Logs");
			if (!player.isKilledQueenBlackDragon())
				player.getPackets().sendIComponentText(275, 12,
						"Kill Queen Black Dragon Once");
			else
				player.getPackets().sendIComponentText(275, 12,
						"<str>Kill Queen Black Dragon Once");
			if (!player.isKilledBork())
				player.getPackets().sendIComponentText(275, 13, "Slay Bork");
			else
				player.getPackets().sendIComponentText(275, 13,
						"<str>Slay Bork");
			if (!player.isKilledCulinaromancer())
				player.getPackets().sendIComponentText(275, 14,
						"Complete Recipe for Diaster");
			else
				player.getPackets().sendIComponentText(275, 14,
						"<str>Complete Recipe for Diaster");
			if (!player.isCompletedFightKiln())
				player.getPackets().sendIComponentText(275, 15,
						"Complete Fight-Kiln & Fight Caves");
			else
				player.getPackets().sendIComponentText(275, 15,
						"<str>Complete Fight-Kiln & Fight Caves");
			if (player.getBarbarianAdvancedLaps() < 250)
				player.getPackets().sendIComponentText(
						275,
						16,
						"You've ran " + player.getBarbarianAdvancedLaps()
								+ "/250 Barbarian Advanced Laps");
			else
				player.getPackets().sendIComponentText(275, 16,
						"<str>You've ran 250/250 Barbarian Advanced Laps");
			if (player.getGnomeAdvancedLaps() < 200)
				player.getPackets().sendIComponentText(
						275,
						17,
						"You've ran " + player.getGnomeAdvancedLaps()
								+ "/200 Gnome Advanced Laps");
			else
				player.getPackets().sendIComponentText(
						275,
						17,
						"<str>You've ran " + player.getGnomeAdvancedLaps()
								+ "/200 Gnome Advanced Laps");
			if (!player.isHasCutEnoughLogs() == true)
				player.getPackets().sendIComponentText(
						275,
						18,
						"You've cut " + player.getLogsCut()
								+ "/5000 Trees (Magic,Yew,Maple)");
			else
				player.getPackets().sendIComponentText(
						275,
						18,
						"<str>You've cut " + player.getLogsCut()
								+ "/5000 Magic/Yew/Maple trees");
			if (player.getBarsDone() < 3000)
				player.getPackets().sendIComponentText(
						275,
						19,
						"You've smithed " + player.getBarsDone()
								+ "/3000 Rune items");
			else
				player.getPackets().sendIComponentText(
						275,
						19,
						"<str>You've smithed " + player.getBarsDone()
								+ "/3000 Rune items");
			if (player.getRocktailsCooked() < 5000)
				player.getPackets().sendIComponentText(
						275,
						20,
						"You've cook " + player.getRocktailsCooked()
								+ "/5000 Rocktails");
			else
				player.getPackets().sendIComponentText(
						275,
						20,
						"<str>You've cook " + player.getRocktailsCooked()
								+ "/5000 Rocktails");
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM))
				player.getPackets().sendIComponentText(275, 21,
						"Nomad's Requiem miniquest");
			else
				player.getPackets().sendIComponentText(275, 21,
						"<str>Nomad's Requiem miniquest");
		} else if (trimmed) {
			if (!completionistCapeRequierment(player, false))
				player.getPackets().sendIComponentText(275, 10,
						"All Completionist Cape reqs.");
			else
				player.getPackets().sendIComponentText(275, 10,
						"<str>All Completionist Cape reqs.");
			if (player.getPestControlGames() < 100)
				player.getPackets().sendIComponentText(
						275,
						11,
						"You've won " + player.getPestControlGames()
								+ "/100 Pest Control games");
			else
				player.getPackets().sendIComponentText(
						275,
						11,
						"<str>You've won " + player.getPestControlGames()
								+ "/100 Pest Control games");
			if (!player.isHasCutMoreEnoughLogs())
				player.getPackets().sendIComponentText(
						275,
						12,
						"You've cut " + player.getLogsCut()
								+ "/8000 logs (Maple, yew and Magic)");
			else
				player.getPackets().sendIComponentText(
						275,
						12,
						"<str>You've cut " + player.getLogsCut()
								+ "/8000 logs (Maple, yew and Magic)");

			if (player.getBarbarianAdvancedLaps() < 400)
				player.getPackets().sendIComponentText(
						275,
						13,
						"You've ran " + player.getBarbarianAdvancedLaps()
								+ "/400 Barbarian Advanced Laps]");
			else
				player.getPackets().sendIComponentText(275, 13,
						"<str>You've ran 250/250 Barbarian Advanced Laps");
			if (player.getGnomeAdvancedLaps() < 400)
				player.getPackets().sendIComponentText(
						275,
						14,
						"You've ran " + player.getGnomeAdvancedLaps()
								+ "/400 Gnome Advanced Laps");
			else
				player.getPackets().sendIComponentText(
						275,
						14,
						"<str>You've ran " + player.getGnomeAdvancedLaps()
								+ "/400 Gnome Advanced Laps");

		}
	}

}