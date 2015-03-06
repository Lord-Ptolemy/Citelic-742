package com.citelic.networking.codec;

public class Message {
	public final int opcode;
	public final byte[] payload;

	public Message(int opcode, byte[] payload) {
		this.opcode = opcode;
		this.payload = payload;
	}
}