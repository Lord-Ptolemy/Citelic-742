package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.utility.Utilities;

public class UnbanAll {

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File dir = new File("./data/characters/");
		File[] accs = dir.listFiles();
		for (File acc : accs) {
			String name = Utilities.formatPlayerNameForProtocol(acc.getName()
					.replace(".p", ""));
			System.out.println(acc);
			if (Utilities.containsInvalidCharacter(name)) {
				acc.delete();
				return;
			}
		}
	}
}
