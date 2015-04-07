package com.citelic.game.entity.player;

import java.security.MessageDigest;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Hit;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.Utilities;

public final class LocalPlayerUpdate {

	/**
	 * The maximum amount of local players being added per tick. This is to
	 * decrease time it takes to load crowded places (such as home).
	 */
	private static final int MAX_PLAYER_ADD = 15;

	private Player player;

	private byte[] slotFlags;

	private Player[] localPlayers;
	private int[] localPlayersIndexes;
	private int localPlayersIndexesCount;

	private int[] outPlayersIndexes;
	private int outPlayersIndexesCount;

	private int[] regionHashes;

	private byte[][] cachedAppearencesHashes;
	private int totalRenderDataSentLength;

	/**
	 * The amount of local players added this tick.
	 */
	private int localAddedPlayers;

	public LocalPlayerUpdate(Player player) {
		this.player = player;
		slotFlags = new byte[2048];
		localPlayers = new Player[2048];
		localPlayersIndexes = new int[GameConstants.PLAYERS_LIMIT];
		outPlayersIndexes = new int[2048];
		regionHashes = new int[2048];
		cachedAppearencesHashes = new byte[GameConstants.PLAYERS_LIMIT][];
	}

	private void appendUpdateBlock(Player p, OutputStream data,
			boolean needAppearenceUpdate, boolean added) {
		int maskData = 0;

		/* Calculate mask data. */
		if (needAppearenceUpdate) {
			maskData |= 32;
		}
		if (p.getNextGraphics3() != null) {
			maskData |= 2097152;
		}
		if (p.getNextFaceEntity() != -2
				|| (added && p.getLastFaceEntity() != -1)) {
			maskData |= 16;
		}
		if (p.getNextGraphics4() != null) {
			maskData |= 4194304;
		}
		if (!p.getNextHits().isEmpty()) {
			maskData |= 4;
		}
		if (p.getNextAnimation() != null) {
			maskData |= 64;
		}
		if (added || p.isUpdateMovementType()) {
			maskData |= 128;
		}
		if (p.getNextForceTalk() != null) {
			maskData |= 1024;
		}
		if (p.getNextForceMovement() != null) {
			maskData |= 512;
		}
		if (added
				|| (p.getNextFaceWorldTile() != null
						&& p.getNextRunDirection() == -1 && p
						.getNextWalkDirection() == -1)) {
			maskData |= 2;
		}
		if (p.getNextGraphics2() != null) {
			maskData |= 256;
		}
		if (p.getTemporaryMoveType() != -1) {
			maskData |= 32768;
		}
		if (p.getNextGraphics1() != null) {
			maskData |= 1;
		}

		/* Append mask data. */

		if (maskData >= 256)
			maskData |= 8;
		if (maskData >= 65536)
			maskData |= 8192;
		data.writeByte(maskData);
		if (maskData >= 256)
			data.writeByte(maskData >> 8);
		if (maskData >= 65536)
			data.writeByte(maskData >> 16);

		/* Append mask information. */
		if (needAppearenceUpdate) {
			applyAppearanceMask(p, data);
		}
		if (p.getNextGraphics3() != null) {
			applyGraphicsMask3(p, data);
		}
		if (p.getNextFaceEntity() != -2
				|| (added && p.getLastFaceEntity() != -1)) {
			applyFaceEntityMask(p, data);
		}
		if (p.getNextGraphics4() != null) {
			applyGraphicsMask4(p, data);
		}
		if (!p.getNextHits().isEmpty()) {
			applyHitMask(p, data);
		}
		if (p.getNextAnimation() != null) {
			applyAnimationMask(p, data);
		}
		if (added || p.isUpdateMovementType()) {
			applyMoveTypeMask(p, data);
		}
		if (p.getNextForceTalk() != null) {
			applyForceTalkMask(p, data);
		}
		if (p.getNextForceMovement() != null) {
			applyForceMovementMask(p, data);
		}
		if (added
				|| (p.getNextFaceWorldTile() != null
						&& p.getNextRunDirection() == -1 && p
						.getNextWalkDirection() == -1)) {
			applyFaceDirectionMask(p, data);
		}
		if (p.getNextGraphics2() != null) {
			applyGraphicsMask2(p, data);
		}
		if (p.getTemporaryMoveType() != -1) {
			applyTemporaryMoveTypeMask(p, data);
		}
		if (p.getNextGraphics1() != null) {
			applyGraphicsMask1(p, data);
		}
	}

	private void applyAnimationMask(Player p, OutputStream data) {
		for (int id : p.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByte128(p.getNextAnimation().getSpeed());
	}

	private void applyAppearanceMask(Player p, OutputStream data) {
		byte[] renderData = p.getPlayerAppearance().getAppeareanceData();
		totalRenderDataSentLength += renderData.length;
		cachedAppearencesHashes[p.getIndex()] = p.getPlayerAppearance()
				.getMD5AppeareanceDataHash();
		data.write128Byte(renderData.length);
		data.writeBytes(renderData);

	}

	private void applyFaceDirectionMask(Player p, OutputStream data) {
		data.writeShortLE(p.getDirection()); // also works as face tile as dir
		// calced on setnextfacetile
	}

	private void applyFaceEntityMask(Player p, OutputStream data) {
		data.writeShortLE(p.getNextFaceEntity() == -2 ? p.getLastFaceEntity()
				: p.getNextFaceEntity());
	}

	private void applyForceMovementMask(Player p, OutputStream data) {
		data.writeByte(0);
		data.write128Byte(0);
		data.writeByteC(p.getNextForceMovement().getToSecondTile() == null ? 0
				: p.getNextForceMovement().getToSecondTile().getX() - p.getX());
		data.writeByteC(p.getNextForceMovement().getToSecondTile() == null ? 0
				: p.getNextForceMovement().getToSecondTile().getY() - p.getY());
		data.writeShort(p.getNextForceMovement().getFirstTileTicketDelay() * 30);
		data.writeShortLE(p.getNextForceMovement().getToSecondTile() == null ? 0
				: p.getNextForceMovement().getSecondTileTicketDelay() * 30);
		data.writeShortLE(p.getNextForceMovement().getDirection());
	}

	private void applyForceTalkMask(Player p, OutputStream data) {
		data.writeString(p.getNextForceTalk().getText());
	}

	private void applyGraphicsMask1(Player p, OutputStream data) {
		data.writeShort128(p.getNextGraphics1().getId());
		data.writeInt(p.getNextGraphics1().getSettingsHash());
		data.writeByteC(p.getNextGraphics1().getSettings2Hash());
	}

	private void applyGraphicsMask2(Player p, OutputStream data) {
		data.writeShortLE128(p.getNextGraphics2().getId());
		data.writeIntV1(p.getNextGraphics2().getSettingsHash());
		data.write128Byte(p.getNextGraphics2().getSettings2Hash());
	}

	private void applyGraphicsMask3(Player p, OutputStream data) {
		data.writeShort(p.getNextGraphics3().getId());
		data.writeIntV1(p.getNextGraphics3().getSettingsHash());
		data.writeByte128(p.getNextGraphics3().getSettings2Hash());
	}

	private void applyGraphicsMask4(Player p, OutputStream data) {
		data.writeShortLE(p.getNextGraphics4().getId());
		data.writeInt(p.getNextGraphics4().getSettingsHash());
		data.writeByte128(p.getNextGraphics4().getSettings2Hash());
	}

	private void applyHitMask(Player p, OutputStream data) {
		int offset = data.getOffset();
		int count = p.getNextHits().size();
		data.writeByte128(count);
		int hp = p.getHitpoints();
		int maxHp = p.getMaxHitpoints();
		if (hp > maxHp)
			hp = maxHp;
		int hpBarPercentage = maxHp == 0 ? 0 : (hp * 255 / maxHp);
		byte hitCount = 0;
		if (count > 0) {
			for (Hit hit : p.getNextHits()) {
				if (hit != null) {
					hitCount = (byte) (hitCount + 1);
					boolean interactingWith = hit.interactingWith(player, p);
					if (hit.missed() && !interactingWith) {
						data.writeSmart(32766);
						data.writeByte128(0);
					} else if (hit.getSoaking() != null) {
						data.writeSmart(32767);
						data.writeSmart(hit.getMark(player, p));
						data.writeSmart(hit.getDamage());
						data.writeSmart(hit.getSoaking().getMark(player, p));
						data.writeSmart(hit.getSoaking().getDamage());
					} else {
						data.writeSmart(hit.getMark(player, p));
						data.writeSmart(hit.getDamage());
					}
					data.writeSmart(0);
				}
			}
		}
		byte someSize = 1;
		data.writeByte(someSize);
		if (someSize > 0) {
			byte type = 0;
			for (byte i = 0; i < someSize; i = (byte) (i + 1)) {
				data.writeSmart(5);
				data.writeSmart(type);
				if (type != 32767) {
					data.writeSmart(0);
					data.writeByteC(hpBarPercentage);
					if (type > 0) {
						data.write128Byte(hpBarPercentage);
					}
				}
			}
		}
		int offset2 = data.getOffset();
		data.setOffset(offset);
		data.writeByte128(hitCount);
		data.setOffset(offset2);
	}

	private void applyMoveTypeMask(Player p, OutputStream data) {
		data.write128Byte(p.getRun() ? 2 : 1);
	}

	private void applyTemporaryMoveTypeMask(Player p, OutputStream data) {
		data.writeByteC(p.getTemporaryMoveType());
	}

	public OutputStream createPacketAndProcess() {
		OutputStream stream = new OutputStream();
		OutputStream updateBlockData = new OutputStream();
		stream.writePacketVarShort(player, 90);
		processLocalPlayers(stream, updateBlockData, true);
		processLocalPlayers(stream, updateBlockData, false);
		processOutsidePlayers(stream, updateBlockData, true);
		processOutsidePlayers(stream, updateBlockData, false);
		stream.writeBytes(updateBlockData.getBuffer(), 0,
				updateBlockData.getOffset());
		stream.endPacketVarShort();
		totalRenderDataSentLength = 0;
		localPlayersIndexesCount = 0;
		outPlayersIndexesCount = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			slotFlags[playerIndex] >>= 1;
			Player player = localPlayers[playerIndex];
			if (player == null)
				outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;
			else
				localPlayersIndexes[localPlayersIndexesCount++] = playerIndex;
		}
		return stream;
	}

	public Player[] getLocalPlayers() {
		return localPlayers;
	}

	public void init(OutputStream stream) {
		stream.initBitAccess();
		stream.writeBits(30, player.getTileHash());
		localPlayers[player.getIndex()] = player;
		localPlayersIndexes[localPlayersIndexesCount++] = player.getIndex();
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			if (playerIndex == player.getIndex())
				continue;
			Player player = Engine.getPlayers().get(playerIndex);
			stream.writeBits(18, regionHashes[playerIndex] = player == null ? 0
					: player.getRegionHash());
			outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;

		}
		stream.finishBitAccess();
	}

	public boolean needAppearenceUpdate(int index, byte[] hash) {
		if (totalRenderDataSentLength > ((GameConstants.PACKET_SIZE_LIMIT - 500) / 2)
				|| hash == null)
			return false;
		return cachedAppearencesHashes[index] == null
				|| !MessageDigest.isEqual(cachedAppearencesHashes[index], hash);
	}

	private boolean needsAdd(Player p) {
		return p != null
				&& !p.hasFinished()
				&& player.withinDistance(p, player.hasLargeSceneView() ? 126
						: 14) && localAddedPlayers < MAX_PLAYER_ADD;
	}

	private boolean needsRemove(Player p) {
		return (p.hasFinished() || !player.withinDistance(p,
				player.hasLargeSceneView() ? 126 : 14));
	}

	private void processLocalPlayers(OutputStream stream,
			OutputStream updateBlockData, boolean nsn0) {
		stream.initBitAccess();
		int skip = 0;
		for (int i = 0; i < localPlayersIndexesCount; i++) {
			int playerIndex = localPlayersIndexes[i];
			if (nsn0 ? (0x1 & slotFlags[playerIndex]) != 0
					: (0x1 & slotFlags[playerIndex]) == 0)
				continue;
			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = localPlayers[playerIndex];
			if (needsRemove(p)) {
				stream.writeBits(1, 1); // needs update
				stream.writeBits(1, 0); // no masks update needeed
				stream.writeBits(2, 0); // request remove
				regionHashes[playerIndex] = p.getLastWorldTile() == null ? p
						.getRegionHash() : p.getLastWorldTile().getRegionHash();
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				}
				localPlayers[playerIndex] = null;
			} else {
				boolean needAppearenceUpdate = needAppearenceUpdate(
						p.getIndex(), p.getPlayerAppearance()
								.getMD5AppeareanceDataHash());
				boolean needUpdate = p.needMasksUpdate()
						|| needAppearenceUpdate;
				if (needUpdate)
					appendUpdateBlock(p, updateBlockData, needAppearenceUpdate,
							false);
				if (p.hasTeleported()) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, needUpdate ? 1 : 0);
					stream.writeBits(2, 3);
					int xOffset = p.getX() - p.getLastWorldTile().getX();
					int yOffset = p.getY() - p.getLastWorldTile().getY();
					int planeOffset = p.getZ() - p.getLastWorldTile().getZ();
					if (Math.abs(p.getX() - p.getLastWorldTile().getX()) <= 14 // 14
							// for
							// safe
							&& Math.abs(p.getY() - p.getLastWorldTile().getY()) <= 14) { // 14
						// for
						// safe
						stream.writeBits(1, 0);
						if (xOffset < 0) // viewport used to be 15 now 16
							xOffset += 32;
						if (yOffset < 0)
							yOffset += 32;
						stream.writeBits(12, yOffset + (xOffset << 5)
								+ (planeOffset << 10));
					} else {
						stream.writeBits(1, 1);
						stream.writeBits(30, (yOffset & 0x3fff)
								+ ((xOffset & 0x3fff) << 14)
								+ ((planeOffset & 0x3) << 28));
					}
				} else if (p.getNextWalkDirection() != -1) {
					int dx = Utilities.DIRECTION_DELTA_X[p
							.getNextWalkDirection()];
					int dy = Utilities.DIRECTION_DELTA_Y[p
							.getNextWalkDirection()];
					boolean running;
					int opcode;
					if (p.getNextRunDirection() != -1) {
						dx += Utilities.DIRECTION_DELTA_X[p
								.getNextRunDirection()];
						dy += Utilities.DIRECTION_DELTA_Y[p
								.getNextRunDirection()];
						opcode = Utilities.getPlayerRunningDirection(dx, dy);
						if (opcode == -1) {
							running = false;
							opcode = Utilities
									.getPlayerWalkingDirection(dx, dy);
						} else
							running = true;
					} else {
						running = false;
						opcode = Utilities.getPlayerWalkingDirection(dx, dy);
					}
					stream.writeBits(1, 1);
					if ((dx == 0 && dy == 0)) {
						stream.writeBits(1, 1); // quick fix
						stream.writeBits(2, 0);
						if (!needUpdate) // hasnt been sent yet
							appendUpdateBlock(p, updateBlockData,
									needAppearenceUpdate, false);
					} else {
						stream.writeBits(1, needUpdate ? 1 : 0);
						stream.writeBits(2, running ? 2 : 1);
						stream.writeBits(running ? 4 : 3, opcode);
					}
				} else if (needUpdate) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, 1);
					stream.writeBits(2, 0);
				} else { // skip
					stream.writeBits(1, 0); // no update needed
					for (int i2 = i + 1; i2 < localPlayersIndexesCount; i2++) {
						int p2Index = localPlayersIndexes[i2];
						if (nsn0 ? (0x1 & slotFlags[p2Index]) != 0
								: (0x1 & slotFlags[p2Index]) == 0)
							continue;
						Player p2 = localPlayers[p2Index];
						if (needsRemove(p2)
								|| p2.hasTeleported()
								|| p2.getNextWalkDirection() != -1
								|| (p2.needMasksUpdate() || needAppearenceUpdate(
										p2.getIndex(), p2
												.getPlayerAppearance()
												.getMD5AppeareanceDataHash())))
							break;
						skip++;
					}
					skipPlayers(stream, skip);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				}

			}
		}
		stream.finishBitAccess();
	}

	private void processOutsidePlayers(OutputStream stream,
			OutputStream updateBlockData, boolean nsn2) {
		stream.initBitAccess();
		int skip = 0;
		localAddedPlayers = 0;
		for (int i = 0; i < outPlayersIndexesCount; i++) {
			int playerIndex = outPlayersIndexes[i];
			if (nsn2 ? (0x1 & slotFlags[playerIndex]) == 0
					: (0x1 & slotFlags[playerIndex]) != 0)
				continue;
			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = Engine.getPlayers().get(playerIndex);
			if (needsAdd(p)) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 0); // request add
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				}
				stream.writeBits(6, p.getXInRegion());
				stream.writeBits(6, p.getYInRegion());
				boolean needAppearenceUpdate = needAppearenceUpdate(
						p.getIndex(), p.getPlayerAppearance()
								.getMD5AppeareanceDataHash());
				appendUpdateBlock(p, updateBlockData, needAppearenceUpdate,
						true);
				stream.writeBits(1, 1);
				localAddedPlayers++;
				localPlayers[p.getIndex()] = p;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
			} else {
				int hash = p == null ? regionHashes[playerIndex] : p
						.getRegionHash();
				if (p != null && hash != regionHashes[playerIndex]) {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				} else {
					stream.writeBits(1, 0); // no update needed
					for (int i2 = i + 1; i2 < outPlayersIndexesCount; i2++) {
						int p2Index = outPlayersIndexes[i2];
						if (nsn2 ? (0x1 & slotFlags[p2Index]) == 0
								: (0x1 & slotFlags[p2Index]) != 0)
							continue;
						Player p2 = Engine.getPlayers().get(p2Index);
						if (needsAdd(p2)
								|| (p2 != null && p2.getRegionHash() != regionHashes[p2Index]))
							break;
						skip++;
					}
					skipPlayers(stream, skip);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				}
			}
		}
		stream.finishBitAccess();
	}

	private void skipPlayers(OutputStream stream, int amount) {
		stream.writeBits(2, amount == 0 ? 0 : amount > 255 ? 3
				: (amount > 31 ? 2 : 1));
		if (amount > 0)
			stream.writeBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
	}

	private void updateRegionHash(OutputStream stream, int lastRegionHash,
			int currentRegionHash) {
		int lastRegionX = lastRegionHash >> 8;
		int lastRegionY = 0xff & lastRegionHash;
		int lastPlane = lastRegionHash >> 16;
		int currentRegionX = currentRegionHash >> 8;
		int currentRegionY = 0xff & currentRegionHash;
		int currentPlane = currentRegionHash >> 16;
		int planeOffset = currentPlane - lastPlane;
		if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
			stream.writeBits(2, 1);
			stream.writeBits(2, planeOffset);
		} else if (Math.abs(currentRegionX - lastRegionX) <= 1
				&& Math.abs(currentRegionY - lastRegionY) <= 1) {
			int opcode;
			int dx = currentRegionX - lastRegionX;
			int dy = currentRegionY - lastRegionY;
			if (dx == -1 && dy == -1)
				opcode = 0;
			else if (dx == 1 && dy == -1)
				opcode = 2;
			else if (dx == -1 && dy == 1)
				opcode = 5;
			else if (dx == 1 && dy == 1)
				opcode = 7;
			else if (dy == -1)
				opcode = 1;
			else if (dx == -1)
				opcode = 3;
			else if (dx == 1)
				opcode = 4;
			else
				opcode = 6;
			stream.writeBits(2, 2);
			stream.writeBits(5, (planeOffset << 3) + (opcode & 0x7));
		} else {
			int xOffset = currentRegionX - lastRegionX;
			int yOffset = currentRegionY - lastRegionY;
			stream.writeBits(2, 3);
			stream.writeBits(18, (yOffset & 0xff) + ((xOffset & 0xff) << 8)
					+ (planeOffset << 16));
		}
	}

}