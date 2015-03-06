package com.citelic.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;

public final class DisplayNames {

	private static ArrayList<String> cachedNames;

	private static final String PATH = "data/displayNames.ser";

	@SuppressWarnings("unchecked")
	public static void init() {
		File file = new File(PATH);
		if (file.exists())
			try {
				cachedNames = (ArrayList<String>) SerializableFilesManager
						.loadSerializedFile(file);
				return;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		cachedNames = new ArrayList<String>();
	}

	public static boolean removeDisplayName(Player player) {
		if (!player.hasDisplayName())
			return false;
		synchronized (cachedNames) {
			cachedNames.remove(player.getDisplayName());
		}
		player.setDisplayName(null);
		player.getGlobalPlayerUpdate().generateAppearenceData();
		return true;
	}

	public static void save() {
		try {
			SerializableFilesManager.storeSerializableClass(cachedNames,
					new File(PATH));
		} catch (IOException e) {
			Logger.handle(e);
		}
	}

	public static boolean setDisplayName(Player player, String displayName) {
		synchronized (cachedNames) {
			if ((SerializableFilesManager.containsPlayer(Utilities
					.formatPlayerNameForProtocol(displayName))
					|| cachedNames.contains(displayName) || !cachedNames
						.add(displayName)))
				return false;
			if (player.hasDisplayName())
				cachedNames.remove(player.getDisplayName());
		}
		player.setDisplayName(player.getDisplayName());
		FriendChatsManager.refreshChat(player);
		player.getGlobalPlayerUpdate().generateAppearenceData();
		return true;
	}

	private DisplayNames() {

	}
}
