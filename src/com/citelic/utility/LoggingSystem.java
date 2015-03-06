package com.citelic.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import com.citelic.GameConstants;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;

public class LoggingSystem {

	public static void logDeath(Player player, Player killer,
			CopyOnWriteArrayList<Item> containedItems) {
		// TODO Auto-generated method stub
		try {
			String FILE_PATH = GameConstants.LOGS_PATH + "/deathlogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[Death session started]");
			writer.newLine();
			writer.write("Killer Information: Username: "
					+ killer.getUsername() + ". IP: " + killer.getLastIP()
					+ ". Current Mac: " + killer.getCurrentMac()
					+ ". Location: " + killer.getX() + ", " + killer.getY()
					+ ", " + killer.getZ() + ".");
			writer.newLine();
			writer.write("Player Information: Username: "
					+ player.getUsername() + ". IP "
					+ player.getSession().getIP() + ". Current Mac: "
					+ player.getCurrentMac() + ". Location: " + player.getX()
					+ ", " + player.getY() + ", " + player.getZ() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : containedItems) {
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(player.getUsername() + " dropped: " + name
						+ ", amount: " + item.getAmount());
			}
			writer.newLine();
			writer.write("[Death session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	public static void logDuelStake(Player player, Player victor,
			CopyOnWriteArrayList<Item> wonItems) {
		// TODO Auto-generated method stub
		try {
			String FILE_PATH = GameConstants.LOGS_PATH + "/stakelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ victor.getUsername() + ".txt", true));
			writer.write("[Stake session started]");
			writer.newLine();
			writer.write("Stake Winner Information: Username: "
					+ victor.getUsername() + ". IP "
					+ victor.getSession().getIP() + ". Current Mac: "
					+ victor.getCurrentMac() + ". Location: " + victor.getX()
					+ ", " + victor.getY() + ", " + victor.getZ() + ".");
			writer.newLine();
			writer.write("Stake Loser Information: Username: "
					+ player.getUsername() + ". IP: " + player.getLastIP()
					+ ". Current Mac: " + player.getCurrentMac()
					+ ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getZ() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : wonItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(victor.getUsername() + " won: " + name
						+ ", amount: " + item.getAmount() + " from "
						+ player.getUsername() + ".");
			}
			writer.newLine();
			writer.write("[Stake session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	public static void logIP(Player player) {
		if (!player.isAdministrator()) {
			String FILE_PATH = GameConstants.LOGS_PATH + "/iplogs/";
			player.setLastIP(player.getSession().getIP());
			try {
				DateFormat dateFormat = new SimpleDateFormat(
						"MM/dd/yy HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						FILE_PATH + player.getUsername() + ".txt", true));
				writer.write("[" + dateFormat.format(cal.getTime()) + "] IP: "
						+ player.getSession().getIP());
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.log(player, e);
			}
		}
	}

	public static void logLostDuelStake(Player loser, Player victor,
			CopyOnWriteArrayList<Item> lostItems) {
		// TODO Auto-generated method stub
		try {
			String FILE_PATH = GameConstants.LOGS_PATH + "/stakelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ loser.getUsername() + ".txt", true));
			writer.write("[Stake session started]");
			writer.newLine();
			writer.write("Stake Winner Information: Username: "
					+ victor.getUsername() + ". IP "
					+ victor.getSession().getIP() + ". Current Mac: "
					+ victor.getCurrentMac() + ". Location: " + victor.getX()
					+ ", " + victor.getY() + ", " + victor.getZ() + ".");
			writer.newLine();
			writer.write("Stake Loser Information: Username: "
					+ loser.getUsername() + ". IP: " + loser.getLastIP()
					+ ". Current Mac: " + loser.getCurrentMac()
					+ ". Location: " + loser.getX() + ", " + loser.getY()
					+ ", " + loser.getZ() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : lostItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(loser.getUsername() + " lost: " + name
						+ ", amount: " + item.getAmount() + " to "
						+ victor.getUsername() + ".");
			}
			writer.newLine();
			writer.write("[Stake session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(loser, e);
		}
	}

	public static void logPm(Player player, Player p2, String fixChatMessage) {
		// TODO Auto-generated method stub
		String FILE_PATH = GameConstants.LOGS_PATH + "/pmlogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: "
					+ player.getSession().getIP() + "] : " + fixChatMessage
					+ ". To user: " + p2.getUsername());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	public static void logPublicChat(Player player, String message) {
		// TODO Auto-generated method stub
		String FILE_PATH = GameConstants.LOGS_PATH + "/chatlogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: "
					+ player.getSession().getIP() + "] : " + message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	public static void logTrade(Player player, Player oldTarget,
			CopyOnWriteArrayList<Item> containedItems) {
		// TODO Auto-generated method stub
		try {
			if (containedItems == null)
				return;
			String FILE_PATH = GameConstants.LOGS_PATH + "/tradelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ oldTarget.getUsername() + ".txt", true));
			writer.write("[Trade session started]");
			writer.newLine();
			writer.write("Trader Information: Username: "
					+ oldTarget.getUsername() + ". IP "
					+ oldTarget.getSession().getIP() + ". Current Mac: "
					+ oldTarget.getCurrentMac() + ". Location: "
					+ oldTarget.getX() + ", " + oldTarget.getY() + ", "
					+ oldTarget.getZ() + ".");
			writer.newLine();
			writer.write("Player Information: Username: "
					+ player.getUsername() + ". IP: " + player.getLastIP()
					+ ". Current Mac: " + player.getCurrentMac()
					+ ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getZ() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : containedItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(oldTarget.getUsername() + " Gave: " + name
						+ ", amount: " + item.getAmount());
				if (item.getId() == 995 && item.getAmount() >= 50000000) {
					Engine.sendWorldMessage(
							"<img=7><col=ff0000>Trade Log: "
									+ oldTarget.getDisplayName() + " gave "
									+ player.getDisplayName() + ": " + name
									+ " X: " + item.getAmount() + ".", true);
				}
				if (name.contains("partyhat") || name.contains("ticket")
						|| name.contains("arcane") || name.contains("torva")
						|| name.contains("santa") || name.contains("bandos")
						|| name.contains("pernix") || name.contains("virtus")
						|| name.contains("spirit") || name.contains("mask")
						|| name.contains("santa") || name.contains("rusty")
						|| name.contains("steadfast") || name.contains("fury")
						|| name.contains("ragefire") || name.contains(" (i)")
						|| name.contains("glaiven") || name.contains("spirit")
						|| name.contains("saradomin's")
						|| name.contains("swift") || name.contains("goliath")
						|| name.contains("dragonbone")
						|| name.contains("spellcaster")
						|| name.contains("primal") || name.contains("bandos")
						|| name.contains("armadyl")
						|| name.contains("godsword") || name.contains("claws")
						|| name.contains("Third-age")) {
					Engine.sendWorldMessage(
							"<img=7><col=ff0000>Trade Log: "
									+ oldTarget.getDisplayName() + " traded "
									+ player.getDisplayName() + ": " + name
									+ " X: " + item.getAmount() + ".", true);
				}
			}
			writer.newLine();
			writer.write("[Trade session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}

	}

	public static void pouchOverFlow(Player player, int amount) {
		// TODO Auto-generated method stub
		try {
			String FILE_PATH = GameConstants.LOGS_PATH + "/moneypouch/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + "] :"
					+ player.getDisplayName() + "(IP: "
					+ player.getSession().getIP()
					+ ") Overflowed pouch with amount: " + amount
					+ ", Inventory: " + player.getInventory().getNumberOf(995)
					+ ".");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

}
