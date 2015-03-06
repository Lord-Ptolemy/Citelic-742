package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.SerializableFilesManager;

public class AccountSimulator {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		File[] chars = new File("data/playersaves/james").listFiles();
		for (File acc : chars) {
			try {
				Player player = (Player) SerializableFilesManager
						.loadSerializedFile(acc);
				System.out.println("James Mac Adress is: "
						+ player.getRegisteredMac());
			} catch (Throwable e) {
				e.printStackTrace();
				System.out.println("failed: " + acc.getName());
			}
		}
		System.out.println("Done.");
	}
}
