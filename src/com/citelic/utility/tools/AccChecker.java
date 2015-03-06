package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.SerializableFilesManager;

public class AccChecker {

	public static void main(String[] args) {
		File dir = new File("./checkacc/");
		File[] accs = dir.listFiles();
		for (File acc : accs) {
			Player player = null;
			try {
				player = (Player) SerializableFilesManager
						.loadSerializedFile(acc);
				System.out.println(player.isDonator());
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			try {
				SerializableFilesManager.storeSerializableClass(player, acc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}