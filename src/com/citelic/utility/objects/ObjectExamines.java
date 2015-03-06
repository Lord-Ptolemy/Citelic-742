package com.citelic.utility.objects;

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

import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.game.map.objects.GameObject;
import com.citelic.utility.Logger;

public class ObjectExamines {

	private final static HashMap<Integer, String> objectExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/map/packedObjectExamines.mk";
	private final static String UNPACKED_PATH = "data/map/unpackedObjectExamines.txt";

	public static final String getExamine(GameObject object) {
		@SuppressWarnings("unused")
		ObjectDefinitions objectDef = new ObjectDefinitions();
		String examine = ObjectExamines.objectExamines.get(object.getId());
		if (examine != null)
			return examine;
		return "It's a " + object.getDefinitions().name + ".";
	}

	public static final void initiate() {
		if (new File(ObjectExamines.PACKED_PATH).exists()) {
			ObjectExamines.readPackedExamineBuffer();
		} else {
			ObjectExamines.readExamineBuffer();
		}
	}

	private static void readPackedExamineBuffer() {
		try {
			RandomAccessFile in = new RandomAccessFile(
					ObjectExamines.PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				ObjectExamines.objectExamines.put(buffer.getShort() & 0xffff,
						ObjectExamines.readBufferString(buffer));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void readExamineBuffer() {
		Logger.log("ObjectExamines", "Packing the Object Examines...");
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					ObjectExamines.UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					ObjectExamines.PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("//")) {
					continue;
				}
				if (line.contains("It's a null")) {
					continue;
				}
				line = line.replace("﻿", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2) {
					in.close();
					throw new RuntimeException(
							"Invalid list for object examine line: " + line);
				}
				int objectId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255) {
					continue;
				}
				out.writeShort(objectId);
				ObjectExamines.writeStringBytes(out, splitedLine[1]);
				ObjectExamines.objectExamines.put(objectId, splitedLine[1]);
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

	public static String readBufferString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeStringBytes(DataOutputStream out, String string)
			throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}
}
