package com.citelic.utility.tools;

import java.io.File;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.cryptology.Encrypt;

public class PasswordEncrypter {

	public static void encrypt() {
		File[] chars = new File("./checkacc/").listFiles();
		for (File acc : chars) {
			try {
				Player player = (Player) SerializableFilesManager
						.loadSerializedFile(acc);
				if (player == null || player.getPassword() == null)
					continue;
				System.out.println(player.getPassword());
				player.setPassword(Encrypt.encryptSHA1(player.getPassword()));
				System.out.println(player.getPassword());
				SerializableFilesManager.storeSerializableClass(player, acc);
			} catch (Throwable e) {
				System.out.println("failed: " + acc.getName() + ", " + e);
			}
		}
	}

	public static void main(String[] args) {
		encrypt();
	}
}