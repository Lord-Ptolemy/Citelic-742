package com.citelic.networking.codec.decode;

import com.citelic.GameConstants;
import com.citelic.networking.Session;
import com.citelic.networking.streaming.InputStream;

public final class ClientPacketsDecoder extends Decoder {

	public ClientPacketsDecoder(Session connection) {
		super(connection);
	}

	@Override
	public final void decode(Session session, InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		switch (packetId) {
		case 14:
			decodeLogin(stream);
			break;
		case 15:
			decodeGrab(stream);
			break;
		default:
			session.getChannel().close();
			break;
		}
	}

	private final void decodeGrab(InputStream stream) {
		int size = stream.readUnsignedByte();
		if (stream.getRemaining() < size) {
			session.getChannel().close();
			return;
		}
		session.setEncoder(0);
		if (stream.readInt() != GameConstants.REVISION
				|| stream.readInt() != GameConstants.SUB_REVISION) {
			session.setDecoder(-1);
			session.getGrabPackets().sendOutdatedClientPacket();
			return;
		}
		if (!stream.readString().equals("MpanIDx68ZShS/0wQc60lSvsuExhgYKEW")) {
			session.getChannel().close();
			return;
		}
		session.setDecoder(1);
		session.getGrabPackets().sendStartUpPacket();
	}

	private final void decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
			session.getChannel().close();
			return;
		}
		session.setDecoder(2);
		session.setEncoder(1);
		session.getLoginPackets().sendStartUpPacket();
	}
}
