package com.citelic.game.entity.player.content.miscellaneous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;

public class ServerMessages {

	private static LinkedList<String> messages = new LinkedList<String>();

	static {
		readFromTextFile();
	}

	public static final Random RANDOM = new Random();

	public static void execute() {
		String string = messages.get((int) (Math.random() * messages.size()));
		for (Iterator<?> iterator = Engine.getPlayers().iterator(); iterator
				.hasNext();) {
			Player players = (Player) iterator.next();
			if (!players.isHideServerMessages()) {
				players.print("<img=5><col=F63817> " + string);
			}
		}
	}

	public static void readFromTextFile() {
		try {
			messages.clear();
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					"./data/messages.txt")));
			String text;
			while ((text = reader.readLine()) != null) {
				messages.add(text);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}