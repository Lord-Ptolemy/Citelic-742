package com.citelic.networking.codec.decode;

import com.citelic.GameConstants;
import com.citelic.cache.Cache;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.networking.Session;
import com.citelic.networking.streaming.InputStream;
import com.citelic.utility.AntiFlood;
import com.citelic.utility.IPBanL;
import com.citelic.utility.Logger;
import com.citelic.utility.MACBan;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.Encrypt;
import com.citelic.utility.cryptology.IsaacKeyPair;

public final class LoginPacketsDecoder extends Decoder {

	private static final Object LOCK = new Object();

	public LoginPacketsDecoder(Session session) {
		super(session);
	}

	@Override
	public void decode(Session session, InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		if (Engine.exiting_start != 0) {
			session.getLoginPackets().sendClientPacket(14);
			return;
		}
		int packetSize = stream.readUnsignedShort();
		if (packetSize != stream.getRemaining()) {
			session.getChannel().close();
			return;
		}
		int revision = stream.readInt();
		int sub = stream.readInt();
		String computerAdress = stream.readString();
		if (revision != GameConstants.REVISION
				|| sub != GameConstants.SUB_REVISION) {
			session.getLoginPackets().sendClientPacket(6);
			return;
		}
		if (packetId == 16 || packetId == 18) {
			decodeWorldLogin(stream, computerAdress);
		} else {
			if (GameConstants.DEBUG)
				Logger.log(this, "PacketId " + packetId);
			session.getChannel().close();
		}
	}

	public void decodeWorldLogin(InputStream stream, String computerAdress) {
		stream.readUnsignedByte();
		String computerAdressVaildation = stream.readString();
		if (!computerAdressVaildation.equalsIgnoreCase(computerAdress)
				|| computerAdressVaildation == "0") {
			Logger.log("Fake login request was detected from IP: "
					+ session.getIP() + ". Client Information: MAC1: "
					+ computerAdressVaildation + ", MAC2: " + computerAdress);
			IPBanL.ipList.add(session.getIP());
			session.getChannel().disconnect();
			session.getLoginPackets().sendClientPacket(22);
			return;
		}
		if (MACBan.checkMac(computerAdress)) {
			session.getLoginPackets().sendClientPacket(26);
			return;
		}
		int rsaBlockSize = stream.readUnsignedShort();
		if (rsaBlockSize > stream.getRemaining()) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(data);
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		int[] xtea = { rsaStream.readInt(), rsaStream.readInt(),
				rsaStream.readInt(), rsaStream.readInt() };
		if (rsaStream.readLong() != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		String password = rsaStream.readString();
		if (password.length() > 30 || password.length() < 3) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}
		password = Encrypt.encryptSHA1(password);
		rsaStream.readLong();
		rsaStream.readLong(); // random value
		rsaStream.readLong(); // random value
		stream.decodeXTEA(xtea, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utilities
				.formatPlayerNameForProtocol(stringUsername ? stream
						.readString() : Utilities.longToString(stream
						.readLong()));
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		stream.readUnsignedByte();
		stream.skip(24);
		stream.readString();
		stream.readInt();
		stream.skip(stream.readUnsignedByte());
		// write machine info here
		for (byte i = 0; i < 24; i = (byte) (i + 1)) {
			stream.readByte();
		}
		// end of machine info
		stream.readInt();
		stream.readLong();
		stream.readString();
		if (stream.readUnsignedByte() == 1) {
			stream.readString();
		}
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readByte();
		stream.readInt();
		final String token = stream.readString();
		if (token.length() > 0
				&& !token.equals("MpanIDx68ZShS/0wQc60lSvsuExhgYKEW")) {
			Logger.log(this.getClass(), "Invalid server token: " + token + " on IP: " + session.getIP());
			session.getLoginPackets().sendClientPacket(20);
			return;
		}
		stream.readUnsignedByte();
		stream.readInt();
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			int receivedCRC = stream.readInt();
			if (index == 0 && receivedCRC == 0) {
				// session.getLoginPackets().sendClientPacket(6);
				// return;
			}
		}
		if (Utilities.invalidAccountName(username)) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}
		if (Engine.getPlayers().size() >= GameConstants.SV_PLAYERS_LIMIT - 10) {
			session.getLoginPackets().sendClientPacket(7);
			return;
		}
		if (AntiFlood.getSessionsIP(session.getIP()) > GameConstants.MAX_CONNECTED_SESSIONS_PER_IP) {
			session.getLoginPackets().sendClientPacket(9);
			return;
		}
		synchronized (LOCK) {
			if (Engine.containsPlayer(username)) {
				session.getLoginPackets().sendClientPacket(5);
				return;
			}
			Player player;
			if (!SerializableFilesManager.containsPlayer(username)) {
				player = new Player(password, computerAdress);
			} else {
				player = SerializableFilesManager.loadPlayer(username);
				if (player == null) {
					session.getLoginPackets().sendClientPacket(20);
					return;
				}
				if (!SerializableFilesManager.createBackup(username)) {
					session.getLoginPackets().sendClientPacket(20);
					return;
				}
				if (!password.equals(player.getPassword())) {
					session.getLoginPackets().sendClientPacket(3);
					return;
				}
				if (Engine.containsPlayer(username)) {
					session.getLoginPackets().sendClientPacket(5);
					return;
				}
			}
			if (player.isPermBanned()
					|| player.getBanned() > Utilities.currentTimeMillis()) {
				session.getLoginPackets().sendClientPacket(4);
				return;
			}
			player.init(player, session, username, computerAdress, displayMode,
					screenWidth, screenHeight, null, new IsaacKeyPair(xtea));
			session.getLoginPackets().sendLoginDetails(player);
			session.setDecoder(3, player);
			session.setEncoder(2, player);
			player.start();
		}
	}
}