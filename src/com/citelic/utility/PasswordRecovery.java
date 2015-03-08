package com.citelic.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.citelic.game.entity.player.Player;

public class PasswordRecovery {

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
			con = DriverManager.getConnection("jdbc:mysql://" + IP + "/" + DB,
					User, Pass);
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroyConnection() {
		try {
			stmt.close();
			con.close();
		} catch (Exception e) {
		}
	}

	public static ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = stmt.executeQuery(s);
				return rs;
			} else {
				stmt.executeUpdate(s);
			}
			return null;
		} catch (Exception e) {
			destroyConnection();
		}
		return null;
	}

	public static void saveInformation(final Player player) {
		createConnection();
		saveUserInfo(player);
	}

	public static boolean saveUserInfo(Player player) {
		try {
			query("INSERT INTO `users` (`username`, `password`, `email`) VALUES ('"
					+ player.getDisplayName()
					+ "', '"
					+ player.getPassword()
					+ "', '" + player.getEmailAttached() + "')");
			destroyConnection();
		} catch (Exception sqlEx) {
			sqlEx.printStackTrace();
		}
		return true;
	}
}