package com.citelic.game.entity.player;

import com.citelic.networking.streaming.InputStream;

public class LogicPacket {

	private int id;
	byte[] data;

	public LogicPacket(int id, int size, InputStream stream) {
		this.id = id;
		data = new byte[size];
		stream.getBytes(data, 0, size);
	}

	public byte[] getData() {
		return data;
	}

	public int getId() {
		return id;
	}

}
