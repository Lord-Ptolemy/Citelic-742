package com.citelic.game.entity.player.managers;

import java.sql.*;

import com.citelic.GameConstants;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.utility.Logger;

public class HiscoreManager {

	public static Connection con = null;
	public static Statement stmt;
	public static boolean connectionMade;

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String IP = "";
			String DB = "";
			String User = "";
			String Pass = "";
			HiscoreManager.con = DriverManager.getConnection("jdbc:mysql://"
					+ IP + "/" + DB, User, Pass);
			HiscoreManager.stmt = HiscoreManager.con.createStatement();
		} catch (Exception e) {
			if (GameConstants.DEBUG) {
				Logger.log("Highscores", "Connection to SQL database failed!");
			}
			e.printStackTrace();
		}
	}

	public static void destroyConnection() {
		try {
			HiscoreManager.stmt.close();
			HiscoreManager.con.close();
		} catch (Exception e) {
		}
	}

	public static ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = HiscoreManager.stmt.executeQuery(s);
				return rs;
			} else {
				HiscoreManager.stmt.executeUpdate(s);
			}
			return null;
		} catch (Exception e) {
			HiscoreManager.destroyConnection();
		}
		return null;
	}

	public static boolean saveHighScore(Player player) {
		try {
			HiscoreManager.createConnection();
			Skills skills = player.getSkills();
			HiscoreManager.query("DELETE FROM `hs_users` WHERE username = '"
					+ player.getDisplayName() + "';");
			HiscoreManager
					.query("INSERT INTO `hs_users` (`username`, `rights`, `overall_xp`,`attack_xp`,`defence_xp`,`strength_xp`,`constitution_xp`,`ranged_xp`,`prayer_xp`,`magic_xp`,`cooking_xp`,`woodcutting_xp`,`fletching_xp`,`fishing_xp`,`firemaking_xp`,`crafting_xp`,`smithing_xp`,`mining_xp`,`herblore_xp`,`agility_xp`,`thieving_xp`,`slayer_xp`,`farming_xp`,`runecrafting_xp`,`hunter_xp`,`construction_xp`,`summoning_xp`,`dungeoneering_xp`) VALUES ('"
							+ player.getDisplayName()
							+ "',"
							+ player.getRights()
							+ ","
							+ player.getSkills().getTotalXp(player)
							+ ","
							+ skills.getXp(0)
							+ ","
							+ skills.getXp(1)
							+ ","
							+ skills.getXp(2)
							+ ","
							+ skills.getXp(3)
							+ ","
							+ skills.getXp(4)
							+ ","
							+ skills.getXp(5)
							+ ","
							+ skills.getXp(6)
							+ ","
							+ skills.getXp(7)
							+ ","
							+ skills.getXp(8)
							+ ","
							+ skills.getXp(9)
							+ ","
							+ skills.getXp(10)
							+ ","
							+ skills.getXp(11)
							+ ","
							+ skills.getXp(12)
							+ ","
							+ skills.getXp(13)
							+ ","
							+ skills.getXp(14)
							+ ","
							+ skills.getXp(15)
							+ ","
							+ skills.getXp(16)
							+ ","
							+ skills.getXp(17)
							+ ","
							+ skills.getXp(18)
							+ ","
							+ skills.getXp(19)
							+ ","
							+ skills.getXp(20)
							+ ","
							+ skills.getXp(21)
							+ ","
							+ skills.getXp(22)
							+ ","
							+ skills.getXp(23)
							+ ","
							+ skills.getXp(24) + ");");
			Logger.log("Highscores",
					"Highscores saved for " + player.getDisplayName() + ".");
			HiscoreManager.destroyConnection();
		} catch (Exception e) {
			Logger.log("Highscores", "Error, could not save highscores for "
					+ player.getDisplayName() + ".");
			return false;
		}
		return true;
	}
}