package com.citelic.utility.npc;

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

import com.citelic.game.entity.npc.NPC;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public class NPCExamines {

	private final static HashMap<Integer, String> npcExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/npcs/packedNPCExamines.e";
	private final static String UNPACKED_PATH = "data/npcs/unpackedNPCExamines.txt";

	public static final String getExamine(NPC npc) {
		String examine = npcExamines.get(npc.getId());
		if (examine != null && !examine.equals("unknown"))
			return examine;
		return "It's " + Utilities.getAorAn(npc.getDefinitions().name) + " "
				+ npc.getDefinitions().name + ".";
	}

	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedNPCExamines();
		else
			loadUnpackedNPCExamines();
	}

	private static void loadPackedNPCExamines() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining())
				npcExamines.put(buffer.getShort() & 0xffff,
						readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SuppressWarnings("resource")
	private static void loadUnpackedNPCExamines() {
		Logger.log("ItemExamines", "Packing NPC examines...");
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
							"Invalid list for item examine line: " + line);
				int itemId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255)
					continue;
				out.writeShort(itemId);
				writeAlexString(out, splitedLine[1]);
				npcExamines.put(itemId, splitedLine[1]);
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

	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeAlexString(DataOutputStream out, String string)
			throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}
}
