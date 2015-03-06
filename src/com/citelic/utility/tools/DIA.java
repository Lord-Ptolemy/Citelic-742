package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.IPBanL;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;

public class DIA {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		IPBanL.init();
		File[] chars = new File("data/characters").listFiles();
		for (File acc : chars) {
			try {
				Player player = (Player) SerializableFilesManager
						.loadSerializedFile(acc);
				String name = acc.getName().replace(".p", "");
				player.setUsername(name);
				if (player.getRights() > 0) {
					player.setRights(0);
					System.out.println("demoted: " + name);
					SerializableFilesManager.savePlayer(player);
				}
				if (player.isPermBanned()
						|| player.getBanned() > Utilities.currentTimeMillis()) {
					if (player.getMuted() > Utilities.currentTimeMillis())
						player.setMuted(0);
					IPBanL.unban(player);
					System.out.println("unbanned: " + name);
					SerializableFilesManager.savePlayer(player);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}
}
