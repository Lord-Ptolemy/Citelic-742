package com.citelic.networking.codec.encode;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.citelic.GameConstants;
import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.HintIcon;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.chat.ChatMessage;
import com.citelic.game.entity.player.chat.PublicChatMessage;
import com.citelic.game.entity.player.chat.QuickChatMessage;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;
import com.citelic.game.entity.player.content.socialization.clans.ClansManager;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemsContainer;
import com.citelic.game.map.DynamicRegion;
import com.citelic.game.map.Region;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.Session;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.Huffman;
import com.citelic.utility.map.MapArchiveKeys;

public class WorldPacketsEncoder extends Encoder {

	private Player player;

	public WorldPacketsEncoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	public void closeInterface(int windowComponentId) {
		closeInterface(
				player.getInterfaceManager().getTabWindow(windowComponentId),
				windowComponentId);
		player.getInterfaceManager().removeTab(windowComponentId);
	}

	public void closeInterface(int windowId, int windowComponentId) {
		OutputStream stream = new OutputStream(5);
		stream.writeFixedPacket(player, 148);
		stream.writeIntV2(windowId << 16 | windowComponentId);
		session.write(stream);
	}

	public OutputStream createWorldTileStream(Tile tile) {
		OutputStream stream = new OutputStream(4);
		stream.writeFixedPacket(player, 6);
		stream.writeByte(tile.getZ());
		stream.writeByteC(tile.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize()) >> 3);
		stream.writeByteC(tile.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize()) >> 3);
		return stream;
	}

	public void sendSwitchWorld(int worldId, String IP) {

	}

	public Player getPlayer() {
		return player;
	}

	public void receiveFriendChatMessage(String name, String display,
			int rights, String chatName, String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 86);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		stream.writeLong(Utilities.stringToLong(chatName));
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utilities.getRandom(255));
		stream.writeByte(rights);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveFriendChatQuickMessage(String name, String display,
			int rights, String chatName, QuickChatMessage message) {

	}

	public void receivePrivateChatQuickMessage(String name, String display,
			int rights, QuickChatMessage message) {

	}

	public void receivePrivateMessage(String name, String display, int rights,
			String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 136);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utilities.getRandom(255));
		stream.writeByte(rights);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void resetItems(int key, boolean negativeKey, int size) {
		sendItems(key, negativeKey, new Item[size]);
	}

    public void sendAccessMask(Player player, int min, int max, int interfaceId, int childId, int... options) {
        int hash = 0;
        for (int slot : options)
            hash |= 2 << slot;
        sendAccessMask(player, min, max, interfaceId, childId, hash);
    }
    
	public void sendAccessMask(Player player, int min, int max,
			int interfaceId, int childId, int hash) {
		OutputStream stream = new OutputStream(13);
		stream.writeFixedPacket(player, 118);
		stream.writeShort(min);
		stream.writeIntLE(hash);
		stream.writeIntV2(interfaceId << 16 | childId);
		stream.writeShortLE(max);
		session.write(stream);
	}

	/**
	 * This will blackout specified area.
	 * 
	 * @param byte area = area which will be blackout (0 = unblackout; 1 =
	 *        blackout orb; 2 = blackout map; 5 = blackout orb and map)
	 */
	public void sendMiniMapStatus(int area) {
		OutputStream out = new OutputStream(2);
		out.writeFixedPacket(player, 159);
		out.writeByte(area);
		session.write(out);
	}

	// instant
	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ) {
		sendCameraLook(viewLocalX, viewLocalY, viewZ, -1, -1);
	}

	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ,
			int speed1, int speed2) {
		OutputStream stream = new OutputStream(6);
		stream.writeFixedPacket(player, 109);
		stream.writeShort128(viewZ >> 2);
		stream.writeByte(viewLocalY);
		stream.writeByte128(speed1);
		stream.writeByteC(speed2);
		stream.write128Byte(viewLocalX);
		// session.write(stream);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ) {
		sendCameraPos(moveLocalX, moveLocalY, moveZ, -1, -1);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ,
			int speed1, int speed2) {
		OutputStream stream = new OutputStream(6);
		stream.writeFixedPacket(player, 25);
		stream.writeByte(speed1);
		stream.writeByte128(moveLocalX);
		stream.write128Byte(speed2);
		stream.writeShortLE128(moveZ >> 2);
		stream.write128Byte(moveLocalY);
		// session.write(stream);
	}

	public void sendCameraRotation(int unknown1, int unknown2) {
		OutputStream stream = new OutputStream(5);
		stream.writeFixedPacket(player, 123);
		stream.writeShort(unknown1);
		stream.writeShortLE(unknown1);
		// session.write(stream);
	}

	public void sendCameraShake(int slotId, int b, int c, int d, int e) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 91);
		stream.writeShort128(b);
		stream.writeByte(d);
		stream.writeByteC(c);
		stream.writeByteC(slotId);
		stream.write128Byte(e);
		session.write(stream);
	}

	public void sendClanWarsRequestMessage(Player p) {
		sendMessage(101, "wishes to challenge your clan to a clan war.", p);
	}

	public void sendClientConsoleCommand(String command) {

	}

	public void sendConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			sendConfig2(id, value);
		} else {
			sendConfig1(id, value);
		}
	}

	public void sendConfig1(int id, int value) {
		OutputStream stream = new OutputStream(4);
		stream.writeFixedPacket(player, 10);
		stream.writeShortLE(id);
		stream.writeByte(value);
		session.write(stream);
	}

	public void sendConfig2(int id, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 82);
		stream.writeIntV2(value);
		stream.writeShort128(id);
		session.write(stream);
	}

	public void sendConfigByFile(int fileId, int value) {
		OutputStream stream = new OutputStream();
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			stream.writeFixedPacket(player, 99);
			stream.writeShort128(fileId);
			stream.writeInt(value);
		} else {
			stream.writeFixedPacket(player, 156);
			stream.writeByte128(value);
			stream.writeShortLE128(fileId);
		}
		session.write(stream);
	}

	public void sendCutscene(int id) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 143);
		stream.writeShort(id);
		stream.writeShort(20); // xteas count
		for (int count = 0; count < 20; count++)
			// xteas
			for (int i = 0; i < 4; i++)
				stream.writeInt(0);
		byte[] appearence = player.getPlayerAppearance().getAppeareanceData();
		stream.writeByte(appearence.length);
		stream.writeBytes(appearence);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendDelayedGraphics(Graphics graphics, int delay, Tile tile) {

	}

	public void sendDestroyObject(GameObject object) {
		int chunkRotation = Engine.getRotation(object.getZ(), object.getX(),
				object.getY());
		if (chunkRotation == 1) {
			object = new GameObject(object);
			ObjectDefinitions defs = ObjectDefinitions
					.getObjectDefinitions(object.getId());
			object.moveLocation(0, -(defs.getSizeY() - 1), 0);
		} else if (chunkRotation == 2) {
			object = new GameObject(object);
			ObjectDefinitions defs = ObjectDefinitions
					.getObjectDefinitions(object.getId());
			object.moveLocation(-(defs.getSizeY() - 1), 0, 0);
		}
		OutputStream stream = createWorldTileStream(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeFixedPacket(player, 37);
		stream.writeByteC((offsetX << 4) | offsetY);
		stream.writeByte128((object.getType() << 2)
				+ (object.getRotation() & 0x3));
		session.write(stream);
	}

	public void sendDuelChallengeRequestMessage(Player p, boolean friendly) {
		sendMessage(101, "wishes to duel with you("
				+ (friendly ? "friendly" : "stake") + ").", p);
	}

	public void sendDungDuoRequestMessage(Player p, boolean friendly) {
		sendMessage(101, "wishes to play a duo Dungeoneering.", p);
	}

	/*
	 * dynamic map region
	 */
	public void sendDynamicMapRegion(boolean sendLswp) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 153);
		if (sendLswp)
			player.getLocalPlayerUpdate().init(stream);
		int regionX = player.getChunkX();
		int regionY = player.getChunkY();
		stream.writeByteC(2);
		stream.writeShortLE128(regionY);
		stream.write128Byte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.writeByte(player.getMapSize());
		stream.writeShort128(regionX);
		stream.initBitAccess();
		int mapHash = GameConstants.MAP_SIZES[player.getMapSize()] >> 4;
		int[] realRegionIds = new int[4 * mapHash * mapHash];
		int realRegionIdsCount = 0;
		for (int plane = 0; plane < 4; plane++) {
			for (int thisRegionX = (regionX - mapHash); thisRegionX <= ((regionX + mapHash)); thisRegionX++) {
				for (int thisRegionY = (regionY - mapHash); thisRegionY <= ((regionY + mapHash)); thisRegionY++) {
					int regionId = (((thisRegionX / 8) << 8) + (thisRegionY / 8));
					Region region = Engine.getRegions().get(regionId);
					int realRegionX;
					int realRegionY;
					int realPlane;
					int rotation;
					if (region instanceof DynamicRegion) {
						DynamicRegion dynamicRegion = (DynamicRegion) region;
						int[] regionCoords = dynamicRegion.getRegionCoords()[plane][thisRegionX
								- ((thisRegionX / 8) * 8)][thisRegionY
								- ((thisRegionY / 8) * 8)];
						realRegionX = regionCoords[0];
						realRegionY = regionCoords[1];
						realPlane = regionCoords[2];
						rotation = regionCoords[3];
					} else {
						realRegionX = thisRegionX;
						realRegionY = thisRegionY;
						realPlane = plane;
						rotation = 0;
					}
					if (realRegionX == 0 || realRegionY == 0)
						stream.writeBits(1, 0);
					else {
						stream.writeBits(1, 1);
						stream.writeBits(26, (rotation << 1)
								| (realPlane << 24) | (realRegionX << 14)
								| (realRegionY << 3));
						int realRegionId = (((realRegionX / 8) << 8) + (realRegionY / 8));
						boolean found = false;
						for (int index = 0; index < realRegionIdsCount; index++)
							if (realRegionIds[index] == realRegionId) {
								found = true;
								break;
							}
						if (!found)
							realRegionIds[realRegionIdsCount++] = realRegionId;
					}

				}
			}
		}
		stream.finishBitAccess();
		for (int index = 0; index < realRegionIdsCount; index++) {
			int[] xteas = MapArchiveKeys.getMapKeys(realRegionIds[index]);
			if (xteas == null)
				xteas = new int[4];
			for (int keyIndex = 0; keyIndex < 4; keyIndex++)
				stream.writeInt(xteas[keyIndex]);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendEntityOnIComponent(boolean isPlayer, int entityId,
			int interfaceId, int componentId) {
		if (isPlayer)
			sendPlayerOnIComponent(interfaceId, componentId);
		else
			sendNPCOnIComponent(interfaceId, componentId, entityId);
	}

	public void sendFriend(String username, String displayName, int world,
			boolean putOnline, boolean warnMessage) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 140);
		sendFriend(username, displayName, world, putOnline, warnMessage, stream);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendFriend(String username, String displayName, int world,
			boolean putOnline, boolean warnMessage, OutputStream stream) {
		stream.writeByte(warnMessage ? 0 : 1);
		// equalsIgnoreCase(username) fixes the problem with client thinking the
		// players got a display name because getDisplayName() returns real
		// username if displayName is null, so getDisplayName() cannot be null.
		if (displayName == null || displayName.equalsIgnoreCase(username)) {
			stream.writeString(username);
			stream.writeString("");
		} else {
			stream.writeString(username);
			stream.writeString(displayName);
		}
		stream.writeShort(putOnline ? 1 : 0);
		stream.writeByte(player.getFriendsIgnores().getRank(
				Utilities.formatPlayerNameForProtocol(username)));
		stream.writeByte(0);
		if (putOnline) {
			stream.writeString("Citelic");
			stream.writeByte(1);
			stream.writeInt(0);
		}
	}

	public void sendFriends() {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 140);
		for (String username : player.getFriendsIgnores().getFriends()) {
			String displayName;
			Player p2 = Engine.getPlayerByDisplayName(username);
			if (p2 == null)
				p2 = Engine.getLobbyPlayerByDisplayName(username);
			if (p2 != null)
				displayName = p2.getDisplayName();
			else
				displayName = Utilities.formatPlayerNameForDisplay(username);
			player.getPackets().sendFriend(
					Utilities.formatPlayerNameForDisplay(username),
					displayName, 1,
					p2 != null && player.getFriendsIgnores().isOnline(p2),
					false, stream);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendFriendsChatChannel() {
		FriendChatsManager manager = player.getCurrentFriendChat();
		OutputStream stream = new OutputStream(manager == null ? 3
				: manager.getDataBlock().length + 3);
		stream.writePacketVarShort(player, 23);
		if (manager != null)
			stream.writeBytes(manager.getDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendGameBarStages() {
		sendConfig(1054, 0);
		sendConfig(1055, 0);
		sendConfig(1056, 0);
		sendConfig(2159, 0);
		sendOtherGameBarStages();
		sendPrivateGameBarStage();
	}

	public void sendOtherGameBarStages() {
		OutputStream stream = new OutputStream(3);
		stream.writeFixedPacket(player, 75);
		stream.writeByteC(player.getTradeStatus());
		stream.writeByte128(player.getPublicStatus());
		session.write(stream);
	}

	public void sendPrivateGameBarStage() {
		OutputStream stream = new OutputStream(2);
		stream.writeFixedPacket(player, 76);
		stream.writeByte(player.getFriendsIgnores().getPrivateStatus());
		session.write(stream);
	}

	public void sendGameMessage(String text) {
		sendGameMessage(text, false);
	}

	public void sendGameMessage(String text, boolean filter) {
		sendMessage(filter ? 109 : 0, text, null);
	}

	public void sendGlobalConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendGlobalConfig2(id, value);
		else
			sendGlobalConfig1(id, value);
	}

	public void sendGlobalConfig1(int id, int value) {
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 22);
		stream.writeShort128(id);
		stream.write128Byte(value);
		session.write(stream);
	}

	public void sendGlobalConfig2(int id, int value) {
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 152);
		stream.writeShortLE128(id);
		stream.writeIntLE(value);
		session.write(stream);
	}

	public void sendGlobalString(int id, String string) {
		OutputStream stream = new OutputStream();
		if (string.length() > 253) {
			stream.writePacketVarShort(player, 55);
			stream.writeShortLE128(id);
			stream.writeString(string);
			stream.endPacketVarShort();
		} else {
			stream.writePacketVarByte(player, 50);
			stream.writeShortLE(id);
			stream.writeString(string);
			stream.endPacketVarByte();
		}
		session.write(stream);
	}

	public void sendGraphics(Graphics graphics, Object target) {
		OutputStream stream = new OutputStream(13);
		int hash = 0;
		if (target instanceof Player) {
			Player p = (Player) target;
			hash = p.getIndex() & 0xffff | 1 << 28;
		} else if (target instanceof NPC) {
			NPC n = (NPC) target;
			hash = n.getIndex() & 0xffff | 1 << 29;
		} else {
			Tile tile = (Tile) target;
			hash = tile.getZ() << 28 | tile.getX() << 14 | tile.getY() & 0x3fff
					| 1 << 30;
		}
		stream.writeFixedPacket(player, 85);
		stream.write128Byte(0); // slot id used for entitys
		stream.writeByte128(hash);
		stream.writeShort128(graphics.getHeight());
		stream.writeShort(graphics.getSpeed());
		stream.writeInt(graphics.getSettings2Hash());
		stream.writeShort(graphics.getId());
		session.write(stream);
	}

	public void sendGroundItem(FloorItem item) {
		OutputStream stream = createWorldTileStream(item.getTile());
		int localX = item.getTile().getLocalX(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = item.getTile().getLocalY(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeFixedPacket(player, 30);
		stream.writeShortLE(item.getAmount());
		stream.writeShortLE(item.getId());
		stream.writeByteC((offsetX << 4) | offsetY);
		session.write(stream);
	}

	public void sendHideIComponent(int interfaceId, int componentId,
			boolean hidden) {
		OutputStream stream = new OutputStream(6);
		stream.writeFixedPacket(player, 113);
		stream.write128Byte(hidden ? 1 : 0);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendHintIcon(HintIcon icon) {
		OutputStream stream = new OutputStream(15);
		stream.writeFixedPacket(player, 74);
		stream.writeByte((icon.getTargetType() & 0x1f) | (icon.getIndex() << 5));
		if (icon.getTargetType() == 0)
			stream.skip(13);
		else {
			stream.writeByte(icon.getArrowType());
			if (icon.getTargetType() == 1 || icon.getTargetType() == 10) {
				stream.writeShort(icon.getTargetIndex());
				stream.writeShort(2500); // how often the arrow flashes, 2500
				// ideal, 0 never
				stream.skip(4);
			} else if ((icon.getTargetType() >= 2 && icon.getTargetType() <= 6)) { // directions
				stream.writeByte(icon.getPlane()); // unknown
				stream.writeShort(icon.getCoordX());
				stream.writeShort(icon.getCoordY());
				stream.writeByte(icon.getDistanceFromFloor() * 4 >> 2);
				stream.writeShort(-1); // distance to start showing on minimap,
				// 0 doesnt show, -1 infinite
			}
			stream.writeInt(icon.getModelId());
		}
		session.write(stream);

	}

	public void sendIComponentAnimation(int emoteId, int interfaceId,
			int componentId) {
		OutputStream stream = new OutputStream(9);
		stream.writeFixedPacket(player, 17);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.writeIntV2(emoteId);
		session.write(stream);
	}

	public void sendIComponentModel(int interfaceId, int componentId,
			int modelId) {
		OutputStream stream = new OutputStream(9);
		stream.writeFixedPacket(player, 134);
		stream.writeIntLE(modelId);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendIComponentSettings(int interfaceId, int componentId,
			int fromSlot, int toSlot, int settingsHash) {
		OutputStream stream = new OutputStream(13);
		stream.writeFixedPacket(player, 118);
		stream.writeShort(fromSlot);
		stream.writeIntLE(settingsHash);
		stream.writeIntV2(interfaceId << 16 | componentId);
		stream.writeShortLE(toSlot);
		session.write(stream);
	}

	public void sendInterfaceConfig(int interfaceId, int componentId,
			boolean hide) {
		OutputStream stream = new OutputStream(6);
		stream.writeFixedPacket(player, 113);
		stream.write128Byte(hide ? 1 : 0);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendIComponentText(int interfaceId, int componentId, String text) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 107);
		stream.writeString(text);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendSpecialStringOnInterface(int interfaceId, int componentId,
			String text) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 146);
		stream.writeString(text);
		stream.writeShort(interfaceId << 16 | componentId);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendIgnore(String name, String display, boolean updateName) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 98);
		if (name != null) {
			stream.writeByte(player.getFriendsIgnores().getIgnores().size());
			stream.writeString(display.equals(name) ? name : display);
			stream.writeString("");
			stream.writeString(display.equals(name) ? "" : name);
			stream.writeString("");
			stream.endPacketVarShort();
		}
		session.write(stream);
	}

	public void sendIgnores() {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 98);
		for (String username : player.getFriendsIgnores().getIgnores()) {
			String display;
			Player p2 = Engine.getPlayerByDisplayName(username);
			if (username != null) {
				if (p2 == null)
					p2 = Engine.getLobbyPlayerByDisplayName(username);
				if (p2 != null)
					display = p2.getDisplayName();
				else
					display = Utilities.formatPlayerNameForDisplay(username);
				String name = Utilities.formatPlayerNameForDisplay(username);
				stream.writeByte(player.getFriendsIgnores().getIgnores().size());
				stream.writeString(display.equals(name) ? name : display);
				stream.writeString("");
				stream.writeString(display.equals(name) ? "" : name);
				stream.writeString("");
			}
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendInputIntegerScript(boolean integerEntryOnly, String message) {
		sendRunScript(108, new Object[] { message });
	}

	public void sendInputIntegerScript(String message) {
		sendRunScript(108, new Object[] { message });
	}

	public void sendInputLongTextScript(String message) {
		sendRunScript(110, new Object[] { message });
	}

	public void sendInputNameScript(String message) {
		sendRunScript(109, new Object[] { message });
	}

	public void sendOverlay(int interfaceId) {
		sendInterface(false, 548, 47, interfaceId);
	}

	public void sendInterface(boolean nocliped, int windowId,
			int windowComponentId, int interfaceId) {
		if (interfaceId == 10 || interfaceId == 161) {
			Logger.log(this, "Error adding interface: " + windowId + " , "
					+ windowComponentId + " , " + interfaceId);
			return;
		}
		// chatbox
		if (!(windowId == 752 && (windowComponentId == 9 || windowComponentId == 12))) { // if
			// chatbox
			if (player.getInterfaceManager().containsInterface(
					windowComponentId, interfaceId))
				closeInterface(windowComponentId);
			if (!player.getInterfaceManager().addInterface(windowId,
					windowComponentId, interfaceId)) {
				Logger.log(this, "Error adding interface: " + windowId + " , "
						+ windowComponentId + " , " + interfaceId);
				return;
			}
		}
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 47);
		stream.write128Byte(nocliped ? 1 : 0);
		stream.writeShortLE128(interfaceId);
		stream.writeIntV2(windowId << 16 | windowComponentId);
		stream.writeIntV2(0);
		stream.writeIntV1(0);
		stream.writeIntV2(0);
		stream.writeInt(0);
		session.write(stream);
	}

	public void sendInterFlashScript(int interfaceId, int componentId,
			int width, int height, int slot) {

	}

	public void sendInterSetItemsOptionsScript(int interfaceId,
			int componentId, int key, boolean negativeKey, int width,
			int height, String... options) {
		Object[] parameters = new Object[6 + options.length];
		int index = 0;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = -1; // dunno but always this
		parameters[index++] = 0;// dunno but always this, maybe startslot?
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = key;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScript(negativeKey ? 695 : 150, parameters); // scriptid 150 does
		// that the method
		// name says*/
	}

	public void sendInterSetItemsOptionsScript(int interfaceId,
			int componentId, int key, int width, int height, String... options) {
		sendInterSetItemsOptionsScript(interfaceId, componentId, key, false,
				width, height, options);
	}

	public void sendInventoryMessage(int border, int slotId, String message) {
		sendGameMessage(message);
		sendRunScript(948, border, slotId, message);
	}

	public void sendItemMessage(int border, int colour, FloorItem item,
			String message) {
		sendGameMessage(message);
		sendGlobalString(306, message);
		sendGlobalConfig(1699, colour);
		sendGlobalConfig(1700, border);
		sendGlobalConfig(1695, 1);
		sendItemInterface(item, item.getTile(), true, 746, 0, 1177);
	}

	public void sendItemInterface(Item item, Tile tile, boolean noclip,
			int windowId, int windowComponent, int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(30);
		stream.writePacketVarByte(player, 36);
		stream.writeByte128(noclip ? 1 : 0);
		stream.writeIntV2(xteas[0]);
		stream.writeIntLE((windowId << 16) | windowComponent);
		stream.writeIntV1(xteas[1]);
		stream.writeShort(item.getId());
		stream.writeIntV1(xteas[2]);
		stream.writeInt(xteas[3]);
		stream.writeShortLE(interfaceId);
		stream.writeInt((tile.getZ() << 28) | (tile.getX() << 14) | tile.getY());
		session.write(stream);
	}

	public void sendItemOnIComponent(int interfaceid, int componentId, int id,
			int amount) {
		OutputStream stream = new OutputStream(11);
		stream.writeFixedPacket(player, 42);
		stream.writeIntLE(interfaceid << 16 | componentId);
		stream.writeShortLE(id);
		stream.writeInt(amount);
		session.write(stream);
	}

	public void sendItems(int key, boolean negativeKey, Item[] items) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 67);
		stream.writeShort(key); // negativeKey ? -key : key
		stream.writeByte(negativeKey ? 1 : 0);
		stream.writeShort(items.length);
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			int id = -1;
			int amount = 0;
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeByte128(amount >= 255 ? 255 : amount);
			if (amount >= 255)
				stream.writeIntV1(amount);
			stream.writeShort128(id + 1);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	// Yeah weight lelelelele
	public void sendWeight(double weight) {
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 54);
		stream.writeShort((int) Math.floor(weight + 0.5d));
		session.write(stream);
	}

	public void sendItems(int key, boolean negativeKey,
			ItemsContainer<Item> items) {
		sendItems(key, negativeKey, items.getItems());
	}

	public void sendItems(int key, Item[] items) {
		sendItems(key, key < 0, items);
	}

	public void sendItems(int key, ItemsContainer<Item> items) {
		sendItems(key, key < 0, items);
	}

	public void sendItemsLook() {
		OutputStream stream = new OutputStream(2);
		stream.writeFixedPacket(player, 163);
		stream.writeByte(player.isOldItemsLook() ? 1 : 0);
		session.write(stream);
	}

	/*
	 * sends local npcs update
	 */
	public void sendLocalNPCsUpdate() {
		session.write(player.getLocalNPCUpdate().createPacketAndProcess());
	}

	/*
	 * sends local players update
	 */
	public void sendLocalPlayersUpdate() {
		session.write(player.getLocalPlayerUpdate().createPacketAndProcess());
	}

	public void sendLogout() {
		OutputStream stream = new OutputStream(1);
		stream.writeFixedPacket(player, 21);
		ChannelFuture future = session.write(stream);
		if (future != null)
			future.addListener(ChannelFutureListener.CLOSE);
		else
			session.getChannel().close();
	}

	/*
	 * normal map region
	 */
	// try this
	public void sendMapRegion() {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 89);
		if (!player.isActive()) {
			player.getLocalPlayerUpdate().init(stream);
		}
		stream.writeByte(player.getMapSize());
		stream.writeShort(player.getChunkY());
		stream.write128Byte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.writeShortLE128(player.getChunkX());
		for (int regionId : player.getMapRegionsIds()) {
			int[] xteas = MapArchiveKeys.getMapKeys(regionId);
			if (xteas == null)
				xteas = new int[4];
			for (int index = 0; index < 4; index++)
				stream.writeInt(xteas[index]);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendMessage(int type, String text, Player p) {
		int maskData = 0;
		if (p != null) {
			maskData |= 0x1;
			if (p.hasDisplayName())
				maskData |= 0x2;
		}
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 2);
		stream.writeSmart(type);
		stream.writeInt(player.getTileHash()); // junk, not used by client
		stream.writeByte(maskData);
		if ((maskData & 0x1) != 0) {
			stream.writeString(p.getDisplayName());
			if (p.hasDisplayName())
				stream.writeString(Utilities.formatPlayerNameForDisplay(p
						.getUsername()));
		}
		stream.writeString(text);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendNPCMessage(int border, NPC npc, String message) {
		sendGameMessage(message);
	}

	public void sendNPCOnIComponent(int interfaceId, int componentId, int npcId) {
		OutputStream stream = new OutputStream(9);
		stream.writeFixedPacket(player, 134);
		stream.writeIntLE(npcId);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}
	
	public void sendObjectAnimation(GameObject object, Animation animation) {
		OutputStream stream = new OutputStream(10);
		stream.writeFixedPacket(player, 129);
		stream.writeByteC((object.getType() << 2)
				+ (object.getRotation() & 0x3));
		stream.writeInt(object.getTileHash());
		stream.writeIntV1(animation.getIds()[0]);
		session.write(stream);
	}

	public void sendOpenURL(String url) {
		OutputStream stream = new OutputStream(1);
		stream.writePacketVarShort(player, 61);
		stream.writeByte(0);
		stream.writeString(url);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendPanelBoxMessage(String text) {
		sendMessage(player.getRights() == 2 ? 99 : 0, text, null);
	}

	public void sendPlayerOnIComponent(int interfaceId, int componentId) {
		OutputStream stream = new OutputStream(5);
		stream.writeFixedPacket(player, 49);
		stream.writeIntV1(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendPlayerOption(String option, int slot, boolean top) {
		sendPlayerOption(option, slot, top, -1);
	}

	public void sendPlayerOption(String option, int slot, boolean top,
			int cursor) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 151);
		stream.writeString(option);
		stream.write128Byte(slot);
		stream.writeShort128(65535);
		stream.writeByte128(top ? 1 : 0);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendPlayerUnderNPCPriority(boolean priority) {
		OutputStream stream = new OutputStream(2);
		stream.writeFixedPacket(player, 73);
		stream.writeByte(priority ? 1 : 0);
		session.write(stream);
	}

	public void sendPrivateMessage(String username, String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 137);
		stream.writeString(username);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendPrivateQuickMessageMessage(String username,
			QuickChatMessage message) {

	}

	public void sendProjectile(Entity receiver, Tile startTile, Tile endTile,
			int gfxId, int startHeight, int endHeight, int speed, int delay,
			int curve, int startDistanceOffset, int creatorSize) {
		OutputStream stream = createWorldTileStream(startTile);
		stream.writeFixedPacket(player, 127);
		int localX = startTile.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = startTile.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeByte((offsetX << 3) | offsetY);
		stream.writeByte(endTile.getX() - startTile.getX());
		stream.writeByte(endTile.getY() - startTile.getY());
		stream.writeShort(receiver == null ? 0
				: (receiver instanceof Player ? -(receiver.getIndex() + 1)
						: receiver.getIndex() + 1));
		stream.writeShort(gfxId);
		stream.writeByte(startHeight);
		stream.writeByte(endHeight);
		stream.writeShort(delay);
		int duration = (Utilities.getDistance(startTile.getX(),
				startTile.getY(), endTile.getX(), endTile.getY()) * 30 / ((speed / 10) < 1 ? 1
				: (speed / 10)))
				+ delay;
		stream.writeShort(duration);
		stream.writeByte(curve);
		stream.writeShort(0);
		stream.writeShort(0);
		session.write(stream);
	}

	public void sendPublicMessage(Player p, PublicChatMessage message) {
		OutputStream stream = new OutputStream(1);
		stream.writePacketVarByte(player, 57);
		stream.writeShort(p.getIndex());
		stream.writeShort(message.getEffects());
		stream.writeByte(p.getMessageIcon());
		if (message instanceof QuickChatMessage) {
			QuickChatMessage qcMessage = (QuickChatMessage) message;
			stream.writeShort(qcMessage.getFileId());
			if (qcMessage.getMessage() != null)
				stream.writeBytes(message.getMessage().getBytes());
		} else {
			byte[] chatStr = new byte[250];
			chatStr[0] = (byte) message.getMessage().length();
			int offset = 1 + Huffman.encryptMessage(1, message.getMessage()
					.length(), chatStr, 0, message.getMessage().getBytes());
			stream.writeBytes(chatStr, 0, offset);
		}
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendRandomOnIComponent(int interfaceId, int componentId, int id) {
		/*
		 * OutputStream stream = new OutputStream(); stream.writePacket(player,
		 * 235); stream.writeShort(id); stream.writeIntV1(interfaceId << 16 |
		 * componentId); stream.writeShort(interPacketsCount++);
		 * session.write(stream);
		 */
	}

	public void sendRemoveGroundItem(FloorItem item) {
		OutputStream stream = createWorldTileStream(item.getTile());
		int localX = item.getTile().getLocalX(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = item.getTile().getLocalY(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeFixedPacket(player, 71);
		stream.writeShortLE(item.getId());
		stream.write128Byte((offsetX << 4) | offsetY);
		session.write(stream);

	}

	public void sendResetCamera() {

	}

	public void sendRunEnergy() {
		OutputStream stream = new OutputStream(2);
		stream.writeFixedPacket(player, 69);
		stream.writeByte((byte) player.getRunEnergy());
		session.write(stream);
	}

	public void sendRunScript(int scriptId, Object... params) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 29);
		String parameterTypes = "";
		if (params != null) {
			for (int count = params.length - 1; count >= 0; count--) {
				if (params[count] instanceof String)
					parameterTypes += "s"; // string
				else
					parameterTypes += "i"; // integer
			}
		}
		stream.writeString(parameterTypes);
		if (params != null) {
			int index = 0;
			for (int count = parameterTypes.length() - 1; count >= 0; count--) {
				if (parameterTypes.charAt(count) == 's')
					stream.writeString((String) params[index++]);
				else
					stream.writeInt((Integer) params[index++]);
			}
		}
		stream.writeInt(scriptId);
		stream.endPacketVarShort();
		session.write(stream);
	}
	
    public void sendClientScript(Player player, int id, String type, Object... params) {
        OutputStream out = new OutputStream(1);
        out.writePacketVarShort(player, 29);
        out.writeString(type);
        for (int i = type.length() - 1; i >= 0; i--) {
            Object param = params[i];
            if (param instanceof String)
                out.writeString((String) param);
            else
                out.writeInt((Integer) param);
        }
        out.writeInt(id);
        session.write(out);
    }

	public void sendSetMouse(String walkHereReplace, int cursor) {

	}

	public void sendSkillLevel(int skill) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 4);
		stream.write128Byte(skill);
		stream.writeByte128(player.getSkills().getLevel(skill));
		stream.writeIntLE((int) player.getSkills().getXp(skill));
		session.write(stream);
	}

	// effect type 1 or 2(index4 or index14 format, index15 format unusused by
	// jagex for now)
	public void sendSound(int id, int delay, int effectType) {
		if (effectType == 1)
			sendIndex14Sound(id, delay);
		else if (effectType == 2)
			sendIndex15Sound(id, delay);
	}

	public void sendVoice(int id) {
		resetSounds();
		sendSound(id, 0, 2);
	}

	public void resetSounds() {
		OutputStream stream = new OutputStream(1);
		stream.writeFixedPacket(player, 145);
		session.write(stream);
	}

	public void sendIndex14Sound(int id, int delay) {
		OutputStream stream = new OutputStream(9);
		stream.writeFixedPacket(player, 15);
		stream.writeShort(id);
		stream.writeByte(1);// repeated amount
		stream.writeShort(delay);
		stream.writeByte(255);
		stream.writeShort(256);
		session.write(stream);
	}

	public void sendIndex15Sound(int id, int delay) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 26);
		stream.writeShort(id);
		stream.writeByte(1); // amt of times it repeats
		stream.writeShort(delay);
		stream.writeByte(255); // volume
		session.write(stream);
	}

	public void sendMusicEffect(int id) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 119);
		stream.write24BitIntegerV2(0);
		stream.writeShort(id);
		stream.write128Byte(255); // volume
		session.write(stream);
	}

	public void sendMusic(int id) {
		sendMusic(id, 100, 255);
	}

	public void sendMusic(int id, int delay, int volume) {
		OutputStream stream = new OutputStream(7);
		stream.writeFixedPacket(player, 39);
		stream.writeByte(delay);
		stream.writeShortLE128(id);
		stream.writeByte128(volume);
		session.write(stream);
	}

	public void sendSpawnedObject(GameObject object) {
		int chunkRotation = Engine.getRotation(object.getZ(), object.getX(),
				object.getY());
		if (chunkRotation == 1) {
			object = new GameObject(object);
			ObjectDefinitions defs = ObjectDefinitions
					.getObjectDefinitions(object.getId());
			object.moveLocation(0, -(defs.getSizeY() - 1), 0);
		} else if (chunkRotation == 2) {
			object = new GameObject(object);
			ObjectDefinitions defs = ObjectDefinitions
					.getObjectDefinitions(object.getId());
			object.moveLocation(-(defs.getSizeY() - 1), 0, 0);
		}
		OutputStream stream = createWorldTileStream(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeFixedPacket(player, 43);
		stream.writeIntV2(object.getId());
		stream.write128Byte((offsetX << 4) | offsetY); // the hash
		// for
		// coords,
		// useless
		stream.writeByte128((object.getType() << 2)
				+ (object.getRotation() & 0x3));
		session.write(stream);

	}

	public void sendStopCameraShake() {
		OutputStream stream = new OutputStream(1);
		stream.writeFixedPacket(player, 28);
		session.write(stream);
	}

	public void sendSystemUpdate(int delay) {
		OutputStream stream = new OutputStream(3);
		stream.writeFixedPacket(player, 147);
		stream.writeShort(delay * 50 / 30);
		session.write(stream);
	}

	// CUTSCENE PACKETS START

	public void sendTileMessage(String message, Tile tile, int color) {
		sendTileMessage(message, tile, 5000, 255, color);
	}

	public void sendTileMessage(String message, Tile tile, int delay,
			int height, int color) {

	}

	public void sendTradeRequestMessage(Player p) {
		sendMessage(100, "wishes to trade with you.", p);
	}

	public void sendUnlockIComponentOptionSlots(int interfaceId,
			int componentId, int fromSlot, int toSlot, int... optionsSlots) {
		int settingsHash = 0;
		for (int slot : optionsSlots)
			settingsHash |= 2 << slot;
		sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot,
				settingsHash);
	}

	public void sendUnlockIComponentOptionSlots(int interfaceId,
			int componentId, int fromSlot, int toSlot, boolean unlockEvent,
			int... optionsSlots) {
		int settingsHash = unlockEvent ? 1 : 0;
		for (int slot : optionsSlots) {
			settingsHash |= 2 << slot;
		}
		sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot,
				settingsHash);
	}

	/*
	 * useless, sending friends unlocks it
	 */
	public void sendUnlockIgnoreList() {
		OutputStream stream = new OutputStream(1);
		stream.writeFixedPacket(player, 18);
		session.write(stream);
	}

	public void sendUpdateItems(int key, boolean negativeKey, Item[] items,
			int... slots) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 94);
		stream.writeShort(key);
		stream.writeByte(negativeKey ? 1 : 0);
		for (int slotId : slots) {
			if (slotId >= items.length)
				continue;
			stream.writeSmart(slotId);
			int id = -1;
			int amount = 0;
			Item item = items[slotId];
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeShort(id + 1);
			if (id != -1) {
				stream.writeByte(amount >= 255 ? 255 : amount);
				if (amount >= 255)
					stream.writeInt(amount);
			}
		}
		stream.endPacketVarShort();
		session.write(stream);
	}
	

    public void sendItemsD(Player player, int type, Item[] items, boolean split) {
        int id, amount;
        OutputStream out = new OutputStream(1);
        out.writePacketVarShort(player, 67);
        out.writeShort(type);
        out.writeByte(split ? 1 : 0);
        out.writeShort(items.length);
        for (Item item : items) {
            if (item != null) {
                id = item.getId();
                amount = item.getAmount();
            } else {
                id = -1;
                amount = 0;
            }
            if (amount >= 0xff) {
                out.addByteA(0xff);
                out.writeIntV1(amount);
            } else {
                out.addByteA(amount);
            }
            out.writeShort128(id + 1);
        }
        session.write(out);
    }

	public void sendUpdateItems(int key, Item[] items, int... slots) {
		sendUpdateItems(key, key < 0, items, slots);
		player.getPackets().sendWeight(Utilities.getWeight(player));
	}

	public void sendUpdateItems(int key, ItemsContainer<Item> items,
			int... slots) {
		sendUpdateItems(key, items.getItems(), slots);
	}

	/*
	 * sets the pane interface
	 */
	public void sendWindowsPane(int id, int type) {
		player.getInterfaceManager().setWindowsPane(id);
		OutputStream stream = new OutputStream(20);
		stream.writeFixedPacket(player, 45);
		stream.writeIntV1(0);
		stream.writeByteS(type);
		stream.writeInt(0);
		stream.writeIntLE(0);
		stream.writeShort128(id);
		stream.writeIntV2(0);
		session.write(stream);
	}

	public void sendWorldList(boolean full) {

	}

	public void sendWorldTile(Tile tile) {
		session.write(createWorldTileStream(tile));
	}

	/**
	 * Sends data to G.E interface slots.
	 * 
	 * @param slot
	 * @param progress
	 * @param item
	 * @param price
	 * @param amount
	 * @param currentAmount
	 */
	public void sendGE(int slot, int progress, int item, int price, int amount,
			int currentAmount) {
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 68);// packet id
		stream.writeByte(slot);
		stream.writeByte(progress);
		stream.writeShort(item);
		stream.writeInt(price);
		stream.writeInt(amount);
		stream.writeInt(currentAmount);
		stream.writeInt(price * currentAmount);
		session.write(stream);
	}

	public void resetGE(int i) {
		OutputStream stream = new OutputStream();
		stream.writeFixedPacket(player, 68);// packet id
		stream.writeByte(i);
		stream.writeByte(0);
		stream.writeShort(0);
		stream.writeInt(0);
		stream.writeInt(0);
		stream.writeInt(0);
		stream.writeInt(0);
		session.write(stream);
	}

	public void sendMinimapFlag(int x, int y) {
		OutputStream stream = new OutputStream(3);
		stream.writeFixedPacket(player, 101);
		stream.writeByte128(y);
		stream.writeByteC(x);
		session.write(stream);
	}

	public void sendObjectInterface(GameObject object, boolean nocliped,
			int windowId, int windowComponentId, int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(33);
		stream.writeFixedPacket(player, 143);
		stream.writeIntV2(xteas[1]);
		stream.writeByte(nocliped ? 1 : 0);
		stream.writeIntLE(xteas[2]);
		stream.writeIntV1(object.getId());
		stream.writeByte128(object.getType() << 2 | object.getRotation() & 0x3);
		stream.writeInt(object.getZ() << 28 | object.getX() << 14
				| object.getY());
		stream.writeIntV2(windowId << 16 | windowComponentId);
		stream.writeShort(interfaceId);
		stream.writeInt(xteas[3]);
		stream.writeInt(xteas[0]);
		session.write(stream);
	}

	public void sendObjectMessage(int border, int color, GameObject object,
			String message) {
		this.sendGameMessage(message);
		sendGlobalString(306, message);
		sendGlobalConfig(1699, color);
		sendGlobalConfig(1700, border);
		sendGlobalConfig(1695, 1);
		sendObjectInterface(object, true, 746, 0, 1177);
	}

	public void sendResetMinimapFlag() {
		OutputStream stream = new OutputStream(3);
		stream.writeFixedPacket(player, 101);
		stream.writeByte128(255);
		stream.writeByteC(255);
		session.write(stream);
	}

	public void sendClanChannel(ClansManager manager, boolean myClan) {
		OutputStream stream = new OutputStream(manager == null ? 4
				: manager.getClanChannelDataBlock().length + 4);
		stream.writePacketVarShort(player, 123);
		stream.writeByte(myClan ? 1 : 0);
		if (manager != null)
			stream.writeBytes(manager.getClanChannelDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendClanSettings(ClansManager manager, boolean myClan) {
		OutputStream stream = new OutputStream(manager == null ? 4
				: manager.getClanSettingsDataBlock().length + 4);
		stream.writePacketVarShort(player, 1);
		stream.writeByte(myClan ? 1 : 0);
		if (manager != null)
			stream.writeBytes(manager.getClanSettingsDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendClanInviteMessage(Player p) {
		sendMessage(117, p.getDisplayName()
				+ " is inviting you to join their clan.", p);
	}

	public void sendIComponentSprite(int interfaceId, int componentId,
			int spriteId) {// try
		OutputStream stream = new OutputStream(8);
		stream.writeFixedPacket(player, 97);
		stream.writeIntLE(spriteId);
		stream.writeIntV1(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void receiveClanChatQuickMessage(boolean myClan, String display,
			int rights, QuickChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 60);
		stream.writeByte(myClan ? 1 : 0);
		stream.writeString(display);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utilities.getRandom(255));
		stream.writeByte(rights);
		stream.writeShort(message.getFileId());
		if (message.getMessage() != null)
			stream.writeBytes(message.getMessage().getBytes());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveClanChatMessage(boolean myClan, String display,
			int rights, ChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 120);
		stream.writeByte(myClan ? 1 : 0);
		stream.writeString(display);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utilities.getRandom(255));
		stream.writeByte(rights);
		Huffman.sendEncryptMessage(stream, message.getMessage(false));
		stream.endPacketVarByte();
		session.write(stream);
	}
}
