package com.citelic.utility.item;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

import com.citelic.game.entity.player.item.Item;
import com.citelic.utility.Logger;

public class ItemWeights {

	private final static HashMap<Integer, Double> itemWeights = new HashMap<Integer, Double>();
	private final static String PACKED_PATH = "data/items/packedWeights.e";
	private final static String UNPACKED_PATH = "data/items/unpackedWeights.txt";

	public static double getWeight(Item item) {
		return itemWeights.get(item.getId());
	}

	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedItemWeights();
		else
			loadUnpackedItemWeights();
	}

	private static void loadPackedItemWeights() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining())
				itemWeights.put(buffer.getShort() & 0xffff, buffer.getDouble());
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SuppressWarnings("resource")
	private static void loadUnpackedItemWeights() {
		Logger.log("ItemWeights", "Packing item weights...");
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("﻿", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2)
					throw new RuntimeException(
							"Invalid list for item weights line: " + line);
				int itemId = Integer.valueOf(splitedLine[0]);
				out.writeShort(itemId);
				out.writeDouble(Double.parseDouble(splitedLine[1]));
				itemWeights.put(itemId, Double.parseDouble(splitedLine[1]));
			}
			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
