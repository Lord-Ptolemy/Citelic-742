package com.citelic.utility.tools;

import java.io.File;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.SerializableFilesManager;

public class Unglitch {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File dir = new File("./unban/");
		File[] accs = dir.listFiles();
		for (File acc : accs) {
			Player player = (Player) SerializableFilesManager
					.loadSerializedFile(acc);
			if (player.getRights() > 0) {
				player.setPermBanned(false);
				player.setBanned(0);
				System.out.println(player.getUsername());
				SerializableFilesManager.storeSerializableClass(player, acc);
			}
		}
	}

}
