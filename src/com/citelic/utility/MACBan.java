package com.citelic.utility;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import com.citelic.game.entity.player.Player;

public final class MACBan {

	public static CopyOnWriteArrayList<String> macList;

	private static final String PATH = "data/mac.ser";
	private static boolean edited;

	public static void checkCurrent() {
		for (String list : macList) {
			System.out.println(list);
		}
	}

	public static boolean checkMac(String mac) {
		return macList.contains(mac);
	}

	public static CopyOnWriteArrayList<String> getList() {
		return macList;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		File file = new File(PATH);
		if (file.exists())
			try {
				macList = (CopyOnWriteArrayList<String>) SerializableFilesManager
						.loadSerializedFile(file);
				return;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		macList = new CopyOnWriteArrayList<String>();
	}

	public static void macban(Player player, boolean loggedIn) {
		if (!player.isAdministrator()) {
			player.setPermBanned(true);
			player.setMacBanned(true);
			if (loggedIn) {
				macList.add(player.getRegisteredMac());
				macList.add(player.getCurrentMac());
				player.getSession().getChannel().disconnect();
			} else {
				macList.add(player.getRegisteredMac());
				macList.add(player.getCurrentMac());
				SerializableFilesManager.savePlayer(player);
			}
			edited = true;
		}
	}

	public static final void save() {
		if (!edited)
			return;
		try {
			SerializableFilesManager.storeSerializableClass(macList, new File(
					PATH));
			edited = false;
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void unban(Player player) {
		player.setPermBanned(false);
		player.setBanned(0);
		player.setMacBanned(false);
		macList.remove(player.getRegisteredMac());
		macList.remove(player.getCurrentMac());
		edited = true;
		save();
	}

}