package com.citelic.game.entity.npc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.player.Player;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.Utilities;

public final class LocalNPCUpdate {

	private Player player;
	private LinkedList<NPC> localNPCs;

	public LocalNPCUpdate(Player player) {
		this.player = player;
		localNPCs = new LinkedList<NPC>();
	}

	private void addInScreenNPCs(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> indexes = Engine.getRegion(regionId).getNPCsIndexes();
			if (indexes == null)
				continue;
			for (int npcIndex : indexes) {
				if (localNPCs.size() == GameConstants.LOCAL_NPCS_LIMIT)
					break;
				NPC n = Engine.getNPCs().get(npcIndex);
				if (n == null || n.hasFinished() || localNPCs.contains(n)
						|| !n.withinDistance(player, largeSceneView ? 126 : 14)
						|| n.isDead())
					continue;
				stream.writeBits(15, n.getIndex());
				boolean needUpdate = n.needMasksUpdate()
						|| n.getLastFaceEntity() != -1;
				int x = n.getX() - player.getX();
				int y = n.getY() - player.getY();
				if (largeSceneView) {
					if (x < 127)
						x += 256;
					if (y < 127)
						y += 256;
				} else {
					if (x < 15)
						x += 32;
					if (y < 15)
						y += 32;
				}
				stream.writeBits(1, n.hasTeleported() ? 1 : 0);
				stream.writeBits(3, (n.getDirection() >> 11) - 4);
				stream.writeBits(2, n.getZ());
				stream.writeBits(largeSceneView ? 8 : 5, y);
				stream.writeBits(15, n.getId());
				stream.writeBits(1, needUpdate ? 1 : 0);
				stream.writeBits(largeSceneView ? 8 : 5, x);
				localNPCs.add(n);
				if (needUpdate)
					appendUpdateBlock(n, updateBlockData, true);
			}
		}
	}

	private void appendUpdateBlock(NPC n, OutputStream data, boolean added) {
		int maskData = 0;
		if (n.hasChangedCombatLevel()
				|| (added && n.getCustomCombatLevel() >= 0)) {
			maskData |= 8388608;
		}
		if (n.getNextGraphics3() != null) {
			maskData |= 33554432;
		}
		if (n.getNextGraphics2() != null) {
			maskData |= 512;
		}
		if (n.getNextTransformation() != null) {
			maskData |= 128;
		}
		if (n.getNextForceMovement() != null) {
			maskData |= 32768;
		}
		if (!n.getNextHits().isEmpty()) {
			maskData |= 8;
		}
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1
				&& n.getNextWalkDirection() == -1) {
			maskData |= 2;
		}
		if (n.getNextGraphics1() != null) {
			maskData |= 64;
		}
		if (n.getNextFaceEntity() != -2
				|| (added && n.getLastFaceEntity() != -1)) {
			maskData |= 32;
		}
		if (n.getNextForceTalk() != null) {
			maskData |= 16;
		}
		if (n.getNextAnimation() != null) {
			maskData |= 1;
		}
		if (n.getNextGraphics4() != null) {
			maskData |= 16777216;
		}

		if (maskData > 0xff)
			maskData |= 0x2;
		if (maskData > 0xffff)
			maskData |= 0x8000;
		if (maskData > 0xffffff)
			maskData |= 0x10000;

		data.writeByte(maskData);

		if (maskData > 0xff)
			data.writeByte(maskData >> 8);
		if (maskData > 0xffff)
			data.writeByte(maskData >> 16);
		if (maskData > 0xffffff)
			data.writeByte(maskData >> 24);

		if (n.hasChangedCombatLevel()
				|| (added && n.getCustomCombatLevel() >= 0)) {
			applyChangeLevelMask(n, data);
		}
		if (n.getNextGraphics3() != null) {
			applyGraphicsMask3(n, data);
		}
		if (n.getNextGraphics2() != null) {
			applyGraphicsMask2(n, data);
		}
		if (n.getNextTransformation() != null) {
			applyTransformationMask(n, data);
		}
		if (n.getNextForceMovement() != null) {
			applyForceMovementMask(n, data);
		}
		if (!n.getNextHits().isEmpty()) {
			applyHitMask(n, data);
		}
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1
				&& n.getNextWalkDirection() == -1) {
			applyFaceWorldTileMask(n, data);
		}
		if (n.getNextGraphics1() != null) {
			applyGraphicsMask1(n, data);
		}
		if (n.getNextFaceEntity() != -2
				|| (added && n.getLastFaceEntity() != -1)) {
			applyFaceEntityMask(n, data);
		}
		if (n.getNextForceTalk() != null) {
			applyForceTalkMask(n, data);
		}
		if (n.getNextAnimation() != null) {
			applyAnimationMask(n, data);
		}
		if (n.getNextGraphics4() != null) {
			applyGraphicsMask4(n, data);
		}
	}

	private void applyAnimationMask(NPC n, OutputStream data) {
		for (int id : n.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByteC(n.getNextAnimation().getSpeed());
	}

	private void applyChangeLevelMask(NPC n, OutputStream data) {
		data.writeShort(n.getCombatLevel());
	}

	private void applyFaceEntityMask(NPC n, OutputStream data) {
		data.writeShort128(n.getNextFaceEntity() == -2 ? n.getLastFaceEntity()
				: n.getNextFaceEntity());
	}

	private void applyFaceWorldTileMask(NPC n, OutputStream data) {
		data.writeShort((n.getNextFaceWorldTile().getX() << 1) + 1);
		data.writeShortLE((n.getNextFaceWorldTile().getY() << 1) + 1);
	}

	private void applyForceMovementMask(NPC n, OutputStream data) {
		data.writeByteC(n.getNextForceMovement().getToFirstTile().getX()
				- n.getX());
		data.writeByte128(n.getNextForceMovement().getToFirstTile().getY()
				- n.getY());
		data.writeByteC(n.getNextForceMovement().getToSecondTile() == null ? 0
				: n.getNextForceMovement().getToSecondTile().getX() - n.getX());
		data.writeByte(n.getNextForceMovement().getToSecondTile() == null ? 0
				: n.getNextForceMovement().getToSecondTile().getY() - n.getY());
		data.writeShort((n.getNextForceMovement().getFirstTileTicketDelay() * 600) / 20);
		data.writeShort(n.getNextForceMovement().getToSecondTile() == null ? 0
				: ((n.getNextForceMovement().getSecondTileTicketDelay() * 600) / 20));
		data.writeShort(n.getNextForceMovement().getDirection());
	}

	private void applyForceTalkMask(NPC n, OutputStream data) {
		data.writeString(n.getNextForceTalk().getText());
	}

	private void applyGraphicsMask1(NPC n, OutputStream data) {
		data.writeShort128(n.getNextGraphics1().getId());
		data.writeIntLE(n.getNextGraphics1().getSettingsHash());
		data.writeByteC(n.getNextGraphics1().getSettings2Hash());
	}

	private void applyGraphicsMask2(NPC n, OutputStream data) {
		data.writeShort(n.getNextGraphics2().getId());
		data.writeIntLE(n.getNextGraphics2().getSettingsHash());
		data.write128Byte(n.getNextGraphics2().getSettings2Hash());
	}

	private void applyGraphicsMask3(NPC n, OutputStream data) {
		data.writeShort(n.getNextGraphics3().getId());
		data.writeIntV2(n.getNextGraphics3().getSettingsHash());
		data.writeByteC(n.getNextGraphics3().getSettings2Hash());
	}

	private void applyGraphicsMask4(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextGraphics4().getId());
		data.writeIntLE(n.getNextGraphics4().getSettingsHash());
		data.write128Byte(n.getNextGraphics4().getSettings2Hash());
	}

	private void applyHitMask(NPC npc, OutputStream data) {
		int offset = data.getOffset();
		int count = npc.getNextHits().size();
		data.writeByteC(count);
		int hp = npc.getHitpoints();
		int maxHp = npc.getMaxHitpoints();
		if (hp > maxHp)
			hp = maxHp;
		int hpBarPercentage = maxHp == 0 ? 0 : (hp * 255 / maxHp);
		byte hitCount = 0;
		if (count > 0) {
			for (Hit hit : npc.getNextHits()) {
				if (hit != null) {
					hitCount = (byte) (hitCount + 1);
					boolean interactingWith = hit.interactingWith(player, npc);
					if (hit.missed() && !interactingWith) {
						data.writeSmart(32766);
						data.writeByteC(0);
					} else if (hit.getSoaking() != null) {
						data.writeSmart(32767);
						data.writeSmart(hit.getMark(player, npc));
						data.writeSmart(hit.getDamage());
						data.writeSmart(hit.getSoaking().getMark(player, npc));
						data.writeSmart(hit.getSoaking().getDamage());
					} else {
						data.writeSmart(hit.getMark(player, npc));
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
				data.writeSmart(npc.getHpBarSize());
				data.writeSmart(0);
				if (type != 32767) {
					data.writeSmart(8); // 0x7fff
					data.writeByteC(hpBarPercentage);
					if (type > 0) {
						data.writeByte128(hpBarPercentage);
					}
				}
			}
		}
		int offset2 = data.getOffset();
		data.setOffset(offset);
		data.writeByteC(hitCount);
		data.setOffset(offset2);
	}

	@SuppressWarnings("unused")
	private void applyNameChangeMask(NPC npc, OutputStream data) {
		data.writeString(npc.getName());
	}

	@SuppressWarnings("unused")
	private void applySecondBarMask(NPC n, OutputStream data) {
		data.writeShortLE128(n.getSecondBar().getEndHp());
		data.writeByte(n.getSecondBar().getTimer());
		data.write128Byte(n.getSecondBar().getStartHp());
	}

	private void applyTransformationMask(NPC n, OutputStream data) {
		data.writeBigSmart(n.getNextTransformation().getToNPCId());
	}

	public OutputStream createPacketAndProcess() {
		boolean largeSceneView = player.hasLargeSceneView();
		OutputStream stream = new OutputStream();
		OutputStream updateBlockData = new OutputStream();
		stream.writePacketVarShort(player, largeSceneView ? 65 : 72);
		processLocalNPCsInform(stream, updateBlockData, largeSceneView);
		stream.writeBytes(updateBlockData.getBuffer(), 0,
				updateBlockData.getOffset());
		stream.endPacketVarShort();
		return stream;
	}

	private void processInScreenNPCs(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		stream.writeBits(8, localNPCs.size());
		for (Iterator<NPC> it = localNPCs.iterator(); it.hasNext();) {
			NPC n = it.next();
			if (n.hasFinished()
					|| !n.withinDistance(player, largeSceneView ? 126 : 14)
					|| n.hasTeleported()) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
				it.remove();
				continue;
			}
			boolean needUpdate = n.needMasksUpdate();
			boolean walkUpdate = n.getNextWalkDirection() != -1;
			stream.writeBits(1, (needUpdate || walkUpdate) ? 1 : 0);
			if (walkUpdate) {
				stream.writeBits(2, n.getNextRunDirection() == -1 ? 1 : 2);
				if (n.getNextRunDirection() != -1)
					stream.writeBits(1, 1);
				stream.writeBits(3,
						Utilities.getNpcMoveDirection(n.getNextWalkDirection()));
				if (n.getNextRunDirection() != -1)
					stream.writeBits(3, Utilities.getNpcMoveDirection(n
							.getNextRunDirection()));
				stream.writeBits(1, needUpdate ? 1 : 0);
			} else if (needUpdate)
				stream.writeBits(2, 0);
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, false);
		}
	}

	private void processLocalNPCsInform(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		stream.initBitAccess();
		processInScreenNPCs(stream, updateBlockData, largeSceneView);
		addInScreenNPCs(stream, updateBlockData, largeSceneView);
		if (updateBlockData.getOffset() > 0)
			stream.writeBits(15, 32767);
		stream.finishBitAccess();
	}

	public void reset() {
		localNPCs.clear();
	}

}
