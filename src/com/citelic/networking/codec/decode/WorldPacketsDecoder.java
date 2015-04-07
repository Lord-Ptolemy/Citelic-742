package com.citelic.networking.codec.decode;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.familiar.Familiar.SpecialAttack;
import com.citelic.game.entity.npc.impl.glacor.EnduringGlacyte;
import com.citelic.game.entity.npc.impl.glacor.Glacor;
import com.citelic.game.entity.npc.impl.glacor.SappingGlacyte;
import com.citelic.game.entity.npc.impl.glacor.UnstableGlacyte;
import com.citelic.game.entity.player.Commands;
import com.citelic.game.entity.player.LogicPacket;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.chat.ChatMessage;
import com.citelic.game.entity.player.chat.PublicChatMessage;
import com.citelic.game.entity.player.chat.QuickChatMessage;
import com.citelic.game.entity.player.containers.Inventory;
import com.citelic.game.entity.player.containers.Shop;
import com.citelic.game.entity.player.content.MoneyPouch;
import com.citelic.game.entity.player.content.SkillCapeCustomizer;
import com.citelic.game.entity.player.content.actions.PlayerFollow;
import com.citelic.game.entity.player.content.actions.combat.PlayerCombat;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.magic.BonesToBananas;
import com.citelic.game.entity.player.content.actions.skills.magic.BonesToPeaches;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelArena;
import com.citelic.game.entity.player.content.controllers.impl.distractions.sc.StealingCreation;
import com.citelic.game.entity.player.content.miscellaneous.pets.Pets;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;
import com.citelic.game.entity.player.content.socialization.clans.Clan;
import com.citelic.game.entity.player.content.socialization.clans.ClansManager;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.pathfinding.RouteEvent;
import com.citelic.game.map.pathfinding.RouteFinder;
import com.citelic.game.map.pathfinding.strategy.FixedTileStrategy;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.Session;
import com.citelic.networking.codec.decode.impl.ButtonHandler;
import com.citelic.networking.codec.decode.impl.InventoryOptionsHandler;
import com.citelic.networking.codec.decode.impl.NPCHandler;
import com.citelic.networking.codec.decode.impl.ObjectHandler;
import com.citelic.networking.streaming.InputStream;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.DTRank;
import com.citelic.utility.DisplayNames;
import com.citelic.utility.IPBanL;
import com.citelic.utility.IPMute;
import com.citelic.utility.Logger;
import com.citelic.utility.PkRank;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.Encrypt;
import com.citelic.utility.cryptology.Huffman;
import com.citelic.utility.item.ItemExamines;

@SuppressWarnings("unused")
public final class WorldPacketsDecoder extends Decoder {

	/**
	 * The packet sizes
	 */
	private static final byte[] PACKET_SIZES = new byte[109];

	/**
	 * The client focus packet.
	 */
	public static final int CLIENT_FOCUS_PACKET = 94;

	/**
	 * The walking packet(s)
	 */
	private final static int WALKING_PACKET = 74;
	private final static int MINI_WALKING_PACKET = 41;

	/**
	 * Player saving cycle
	 */
	private final static int SAVE_PLAYER_PACKET = 93;

	/**
	 * The friend lists packet(s)
	 */
	private final static int ADD_FRIEND_PACKET = 71;
	private final static int ADD_IGNORE_PACKET = 38;
	private final static int REMOVE_FRIEND_PACKET = 81;
	private final static int REMOVE_IGNORE_PACKET = 17;

	private final static int SEND_FRIEND_MESSAGE_PACKET = 95;

	/**
	 * The friends list packets
	 */
	private final static int JOIN_FRIEND_CHAT_PACKET = 84;
	private final static int CHANGE_FRIEND_CHAT_PACKET = 75;
	private final static int KICK_FRIEND_CHAT_PACKET = 45;

	/**
	 * The lobby packets
	 */
	private final static int WORLD_LIST_UPDATE = 78;
	private final static int LOBBY_MAIN_CLICK_PACKET = -1;
	private final static int LOBBY_FRIEND_CHAT_SETTINGS = -1;

	/**
	 * The public chat packet
	 */
	private final static int CHAT_PACKET = 86;

	/**
	 * The public quick chat packet
	 */
	private final static int PUBLIC_QUICK_CHAT_PACKET = 87;

	/**
	 * The Grand Exchange search packet
	 */
	private final static int GE_SEARCH_PACKET = 105;

	/**
	 * The action button(s) packets
	 */
	public final static int ACTION_BUTTON1_PACKET = 4;
	public final static int ACTION_BUTTON2_PACKET = 22;
	public final static int ACTION_BUTTON3_PACKET = 13;
	public final static int ACTION_BUTTON4_PACKET = 76;
	public final static int ACTION_BUTTON5_PACKET = 59;
	public final static int ACTION_BUTTON6_PACKET = 37;
	public final static int ACTION_BUTTON7_PACKET = 103;
	public final static int ACTION_BUTTON8_PACKET = 69;
	public final static int ACTION_BUTTON9_PACKET = 103;
	public final static int ACTION_BUTTON10_PACKET = 28;

	/**
	 * The packets for clan chat forum thread id
	 */
	private final static int OPEN_URL_PACKET = -1;
	private final static int FORUM_THREAD_ID_PACKET = -1;

	/**
	 * The world map click packet
	 */
	public final static int WORLD_MAP_CLICK = 66;

	/**
	 * The packet count packet
	 */
	public final static int RECEIVE_PACKET_COUNT_PACKET = 56;

	/**
	 * The player option(s) packets
	 */
	private final static int PLAYER_OPTION_1_PACKET = 44;
	private final static int PLAYER_OPTION_2_PACKET = 79;
	private final static int PLAYER_OPTION_3_PACKET = -1;
	private final static int PLAYER_OPTION_4_PACKET = 104;
	private final static int PLAYER_OPTION_6_PACKET = -1;
	private final static int PLAYER_OPTION_9_PACKET = 53;

	/**
	 * The move camera packet
	 */
	private final static int MOVE_CAMERA_PACKET = -1;

	/**
	 * The click packet
	 */
	private final static int CLICK_PACKET = -1;

	/**
	 * The move mouse packet
	 */
	private final static int MOVE_MOUSE_PACKET = -1;

	/**
	 * The key typed packet
	 */
	private final static int KEY_TYPED_PACKET = -1;

	/**
	 * The close interface packet
	 */
	private final static int CLOSE_INTERFACE_PACKET = 57;

	/**
	 * The commands packet
	 */
	private final static int COMMANDS_PACKET = 20;

	/**
	 * The item on item packet
	 */
	private final static int ITEM_ON_ITEM_PACKET = 26;

	/**
	 * The In and out screen packet
	 */
	private final static int IN_OUT_SCREEN_PACKET = -1;

	/**
	 * The Done loading region packet
	 */
	private final static int DONE_LOADING_REGION_PACKET = 64;

	/**
	 * The Response packet
	 */
	private final static int PING_PACKET = 30;

	/**
	 * The Display packet
	 */
	private final static int DISPLAY_PACKET = 31;

	/**
	 * The Chat type packet
	 */
	private final static int CHAT_TYPE_PACKET = 70;

	/**
	 * The Friend quick chat type packet
	 */
	private final static int SEND_FRIEND_QUICK_CHAT_PACKET = 52;

	/**
	 * The Object click packet(s)
	 */
	private final static int OBJECT_CLICK1_PACKET = 40;
	private final static int OBJECT_CLICK2_PACKET = 3;
	private final static int OBJECT_CLICK3_PACKET = -1;
	private final static int OBJECT_CLICK4_PACKET = -1;
	private final static int OBJECT_CLICK5_PACKET = 82;
	private final static int OBJECT_EXAMINE_PACKET = 49;

	/**
	 * The npc click packet(s)
	 */
	private final static int NPC_CLICK1_PACKET = 72;
	private final static int NPC_CLICK2_PACKET = 43;
	private final static int NPC_CLICK3_PACKET = 88;
	private final static int NPC_CLICK4_PACKET = 32;
	private static final int NPC_EXAMINE_PACKET = 0;
	private final static int ATTACK_NPC = 54;

	/**
	 * The item take packet
	 */
	private final static int ITEM_TAKE_PACKET = 62;
	private final static int GROUND_ITEM_EXAMINE_PACKET = 42;

	/**
	 * The dialogue continue packet
	 */
	private final static int DIALOGUE_CONTINUE_PACKET = 83;

	/**
	 * The enter integer packet
	 */
	private final static int ENTER_INTEGER_PACKET = 65;

	/**
	 * The enter name packet
	 */
	private final static int ENTER_NAME_PACKET = 68;

	/**
	 * The long string packet
	 */
	private final static int ENTER_LONG_TEXT_PACKET = 21;

	/**
	 * The enter string packet
	 */
	private final static int ENTER_STRING_PACKET = -1;

	/**
	 * The switch interface item packet
	 */
	private final static int SWITCH_INTERFACE_ITEM_PACKET = 7;

	/**
	 * The interface on entity packet(s)
	 */
	private final static int INTERFACE_ON_PLAYER = 106;
	private final static int INTERFACE_ON_NPC = 6;
	private final static int INTERFACE_ON_OBJECT = 107;

	/**
	 * The color id packet
	 */
	private final static int COLOR_ID_PACKET = 5;

	/**
	 * The report abuse packet
	 */
	private final static int REPORT_ABUSE_PACKET = -1;

	static {
		loadPacketSizes();
	}

	/**
	 * 
	 * @param player
	 * @param packet
	 * 
	 * All out-commented opcodes we're duplicated {@code -1}, get the real opcode and make them functional again.
	 */
	public static void decodeLogicPacket(final Player player, LogicPacket packet) {
		int packetId = packet.getId();
		InputStream stream = new InputStream(packet.getData());
		switch (packetId) {
		case WALKING_PACKET:
		case MINI_WALKING_PACKET: {
			if (!player.isActive() || !player.clientHasLoadedMapRegion()
					|| player.isDead() || player.isLocked())
				return;
			if (player.getFreezeDelay() >= Utilities.currentTimeMillis()) {
				player.getPackets().sendGameMessage(
						"A magical force prevents you from moving.");
				return;
			}
			boolean forceRun = (stream.readByte() & 0xFF) == 1;
			int bx = stream.readShortLE128() & 0xFFFF;
			int by = stream.readShort() & 0xFFFF;
			if (forceRun)
				player.setRun(forceRun);

			player.stopAll();
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
					player.getX(), player.getY(), player.getZ(),
					player.getSize(), new FixedTileStrategy(bx, by), true);
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();

			int lastStepIdx = -1;
			for (int step = steps - 1; step >= 0; step--) {
				int x = bufferX[step];
				int y = bufferY[step];
				if (!player.addWalkSteps(x, y, 25, true)) {
					break;
				}
				lastStepIdx = step;
			}

			if (lastStepIdx == -1) {
				player.getPackets().sendResetMinimapFlag();
			} else {
				Tile tile = new Tile(bufferX[lastStepIdx],
						bufferY[lastStepIdx], player.getZ());
				player.getPackets().sendMinimapFlag(
						tile.getLocalX(player.getLastLoadedMapRegionTile()),
						tile.getLocalY(player.getLastLoadedMapRegionTile()));
			}
			break;
		}
		case OBJECT_CLICK1_PACKET:
			ObjectHandler.handleOption(player, stream, 1);
			break;
		case OBJECT_CLICK2_PACKET:
			ObjectHandler.handleOption(player, stream, 2);
			break;
		/*case OBJECT_CLICK3_PACKET:
			ObjectHandler.handleOption(player, stream, 3);
			break;
		case OBJECT_CLICK4_PACKET:
			ObjectHandler.handleOption(player, stream, 4);
			break;*/
		case OBJECT_CLICK5_PACKET:
			ObjectHandler.handleOption(player, stream, 5);
			break;
		case INTERFACE_ON_PLAYER: {
			if (!player.isActive() || !player.clientHasLoadedMapRegion()
					|| player.isDead())
				return;
			if (player.getLockDelay() > Utilities.currentTimeMillis())
				return;
			int itemId = stream.readUnsignedShortLE128();
			int interfaceHash = stream.readInt();
			int playerIndex = stream.readShortLE();
			boolean run = stream.read128Byte() == 1;
			int slot = stream.readShortLE();
			int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			System.out.println("Item ID: " + itemId + ", InterfaceHash: "
					+ interfaceHash + ", PID: " + playerIndex + ", run " + run
					+ ", slot " + slot);
			if (Utilities.getInterfaceDefinitionsSize() <= interfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (componentId == 65535)
				componentId = -1;
			if (componentId != -1
					&& Utilities
							.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
				return;
			Player p2 = Engine.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished()
					|| !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			player.stopAll(false);
			if (interfaceId == 679) {
				Item item = player.getInventory().getItems().get(slot);
				if (item == null)
					return;
				player.getPackets().sendGameMessage(
						"Nothing interesting happens.");
			}
			switch (interfaceId) {
			case 1110:
				if (componentId == 87)
					ClansManager.invite(player, p2);
				break;
			case Inventory.INVENTORY_INTERFACE:// Item on player
				if (!player.getControllerManager().processItemOnPlayer(p2,
						itemId))
					return;
				InventoryOptionsHandler.handleItemOnPlayer(player, p2, itemId);
				break;
			case 662:
			case 747:
				if (player.getFamiliar() == null)
					return;
				player.resetWalkSteps();
				if ((interfaceId == 747 && componentId == 15)
						|| (interfaceId == 662 && componentId == 65)
						|| (interfaceId == 662 && componentId == 74)
						|| interfaceId == 747 && componentId == 18) {
					if ((interfaceId == 662 && componentId == 74
							|| interfaceId == 747 && componentId == 24 || interfaceId == 747
							&& componentId == 18)) {
						if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
							return;
					}
					if (!player.isCanPvp() || !p2.isCanPvp()) {
						player.getPackets()
								.sendGameMessage(
										"You can only attack players in a player-vs-player area.");
						return;
					}
					if (!player.getFamiliar().canAttack(p2)) {
						player.getPackets()
								.sendGameMessage(
										"You can only use your familiar in a multi-zone area.");
						return;
					} else {
						player.getFamiliar().setSpecial(
								interfaceId == 662 && componentId == 74
										|| interfaceId == 747
										&& componentId == 18);
						player.getFamiliar().setTarget(p2);
					}
				}
				break;
			case 193:
				switch (componentId) {
				case 28:
				case 32:
				case 24:
				case 20:
				case 30:
				case 34:
				case 26:
				case 22:
				case 29:
				case 33:
				case 25:
				case 21:
				case 31:
				case 35:
				case 27:
				case 23:
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceTile(new Tile(p2.getCoordFaceX(p2
								.getSize()), p2.getCoordFaceY(p2.getSize()), p2
								.getZ()));
						if (!player.getControllerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getPackets()
									.sendGameMessage(
											"You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2
									&& player.getAttackedByDelay() > Utilities
											.currentTimeMillis()) {
								player.getPackets()
										.sendGameMessage(
												"That "
														+ (player
																.getAttackedBy() instanceof Player ? "player"
																: "npc")
														+ " is already in combat.");
								return;
							}
							if (p2.getAttackedBy() != player
									&& p2.getAttackedByDelay() > Utilities
											.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player); // changes
									// enemy
									// to player,
									// player has
									// priority over
									// npc on single
									// areas
								} else {
									player.getPackets()
											.sendGameMessage(
													"That player is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(
								new PlayerCombat(p2));
					}
					break;
				}
			case 430:
				switch (componentId) {
				case 42:
					player.setNextFaceTile(new Tile(p2.getCoordFaceX(p2
							.getSize()), p2.getCoordFaceY(p2.getSize()), p2
							.getZ()));
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						if (p2 instanceof Player) {
							if (player.getSkills().getLevel(Skills.MAGIC) < 93) {
								player.getPackets()
										.sendGameMessage(
												"Your Magic level is not high enough for this spell.");
								return;
							} else if (player.getSkills().getLevel(
									Skills.DEFENCE) < 40) {
								player.getPackets()
										.sendGameMessage(
												"You need a Defence level of 40 for this spell");
								return;
							}
							Long lastVeng = (Long) p2.getTemporaryAttributtes()
									.get("LAST_VENG");
							if (lastVeng != null
									&& lastVeng + 30000 > Utilities
											.currentTimeMillis()) {
								player.getPackets().sendGameMessage(
										"That player already has vengeance.");
								return;
							}
							player.stopAll();
							player.setNextAnimation(new Animation(4411));
							p2.setNextGraphics(new Graphics(725, 0, 100));
							p2.setCastVeng(true);
							p2.getTemporaryAttributtes().put("LAST_VENG",
									Utilities.currentTimeMillis());
							p2.getPackets().sendGameMessage(
									"You recieve vengeance from "
											+ player.getDisplayName() + ".");
						}
					}
					break;
				}
			case 192:
				switch (componentId) {
				case 79: // Teletoher Lumbridge
					if (Magic.checkCombatSpell(player, componentId, 1, true)) {
						p2.getDialogueManager().startDialogue("Teleother", 0,
								player);
					}
					break;
				case 85: // Teleother Falador
					if (Magic.checkCombatSpell(player, componentId, 1, true)) {
						p2.getDialogueManager().startDialogue("Teleother", 1,
								player);
					}
					break;
				case 90: // Teletoher Camelot
					if (Magic.checkCombatSpell(player, componentId, 1, true)) {
						p2.getDialogueManager().startDialogue("Teleother", 2,
								player);
					}
					break;
				case 33: // bones to bananas
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						BonesToBananas.castBonesToBananas(player);
					}
					break;
				case 65: // bones to bananas
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						BonesToPeaches.castBonesToPeaches(player);
					}
					break;
				case 98: // wind rush
				case 25: // air strike
				case 28: // water strike
				case 30: // earth strike
				case 32: // fire strike
				case 34: // air bolt
				case 39: // water bolt
				case 42: // earth bolt
				case 45: // fire bolt
				case 49: // air blast
				case 52: // water blast
				case 58: // earth blast
				case 63: // fire blast
				case 70: // air wave
				case 73: // water wave
				case 77: // earth wave
				case 80: // fire wave
				case 86: // teleblock
				case 84: // air surge
				case 87: // water surge
				case 89: // earth surge
				case 91: // fire surge
				case 99: // storm of armadyl
				case 36: // bind
				case 66: // Sara Strike
				case 67: // Guthix Claws
				case 68: // Flame of Zammy
				case 55: // snare
				case 81: // entangle
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceTile(new Tile(p2.getCoordFaceX(p2
								.getSize()), p2.getCoordFaceY(p2.getSize()), p2
								.getZ()));
						if (!player.getControllerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getPackets()
									.sendGameMessage(
											"You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2
									&& player.getAttackedByDelay() > Utilities
											.currentTimeMillis()) {
								player.getPackets()
										.sendGameMessage(
												"That "
														+ (player
																.getAttackedBy() instanceof Player ? "player"
																: "npc")
														+ " is already in combat.");
								return;
							}
							if (p2.getAttackedBy() != player
									&& p2.getAttackedByDelay() > Utilities
											.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player); // changes
									// enemy
									// to player,
									// player has
									// priority over
									// npc on single
									// areas
								} else {
									player.getPackets()
											.sendGameMessage(
													"That player is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(
								new PlayerCombat(p2));
					}
					break;
				}
				break;
			}
			if (GameConstants.DEBUG)
				System.out.println("Spell:" + componentId);
			break;
		}
		case WORLD_MAP_CLICK: {
			int coordinateHash = stream.readInt();
			int x = coordinateHash >> 14;
			int y = coordinateHash & 0x3fff;
			int plane = coordinateHash >> 28;
			Integer hash = (Integer) player.getTemporaryAttributtes().get(
					"worldHash");
			if (hash == null || coordinateHash != hash) {
				player.getTemporaryAttributtes().put("worldHash",
						coordinateHash);
			} else {
				player.getTemporaryAttributtes().remove("worldHash");
				player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2,
						-1, true);
				player.getPackets().sendConfig(1159, coordinateHash);
			}
			break;
		}
			case INTERFACE_ON_NPC: {
				if (!player.isActive() || !player.clientHasLoadedMapRegion()
						|| player.isDead())
					return;
				if (player.getLockDelay() > Utilities.currentTimeMillis())
					return;
				int npcIndex = stream.readShort128();
				int itemId = stream.readShort128();
				int interfaceSlot = stream.readShort();
				boolean ctrlRun = stream.readByte128() == 1;
				int interfaceHash = stream.readInt();
				int interfaceId = interfaceHash >> 16;
				int componentId = interfaceHash - (interfaceId << 16);
				if (Utilities.getInterfaceDefinitionsSize() <= interfaceId)
					return;
				if (!player.getInterfaceManager().containsInterface(interfaceId))
					return;
				if (componentId == 65535)
					componentId = -1;
				if (componentId != -1
						&& Utilities
								.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
					return;
				NPC npc = Engine.getNPCs().get(npcIndex);
				if (npc == null || npc.isDead() || npc.hasFinished()
						|| !player.getMapRegionsIds().contains(npc.getRegionId()))
					return;
				player.stopAll(false);
				if (interfaceId != Inventory.INVENTORY_INTERFACE) {
					if (!npc.getDefinitions().hasAttackOption()) {
						player.getPackets().sendGameMessage(
								"You can't attack this npc.");
						return;
					}
				}
				switch (interfaceId) {
				case Inventory.INVENTORY_INTERFACE:
					Item item = player.getInventory().getItem(interfaceSlot);
					if (item == null
							|| !player.getControllerManager().processItemOnNPC(npc,
									item))
						return;
					InventoryOptionsHandler.handleItemOnNPC(player, npc, item);
					break;
				case 1165:
					Summoning.attackDreadnipTarget(npc, player);
					break;
				case 662:
				case 747:
					if (player.getFamiliar() == null)
						return;
					if (npc.getId() == 14301) {
						Glacor glacor = (Glacor) npc;
						if (glacor != null) {
							if (glacor.getTarget() == null) {
								if (player.getAttackedByDelay() > Utilities
										.currentTimeMillis()) {
									return;
								} else {
									glacor.setTarget(player);
									player.setAttackedByDelay(10000);
									return;
								}
							}
							if (!glacor.attackable(player)) {
								return;
							}

							player.setAttackedByDelay(10000);
						}
					}
					if (npc.getId() == 14302) {
						UnstableGlacyte unstable = (UnstableGlacyte) npc;
						if (unstable != null) {
							if (unstable.getTarget() == null)
								unstable.setTarget(player);
							if (!unstable.attackable(player)) {
								return;
							}
						}
					}
					if (npc.getId() == 14303) {
						SappingGlacyte sapping = (SappingGlacyte) npc;
						if (sapping != null) {
							if (sapping.getTarget() == null)
								sapping.setTarget(player);
							if (!sapping.attackable(player)) {
								return;
							}
						}
					}
					if (npc.getId() == 14304) {
						EnduringGlacyte enduring = (EnduringGlacyte) npc;
						if (enduring != null) {
							if (enduring.getTarget() == null)
								enduring.setTarget(player);
							if (!enduring.attackable(player)) {
								return;
							}
						}
					}
					player.resetWalkSteps();
					if ((interfaceId == 747 && componentId == 15)
							|| (interfaceId == 662 && componentId == 65)
							|| (interfaceId == 662 && componentId == 74)
							|| interfaceId == 747 && componentId == 18
							|| interfaceId == 747 && componentId == 24) {
						if ((interfaceId == 662 && componentId == 74 || interfaceId == 747
								&& componentId == 18)) {
							if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
								return;
						}
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getPackets().sendGameMessage(
										"You can't attack your own familiar.");
								return;
							}
							if (!player.getFamiliar()
									.canAttack(familiar.getOwner())) {
								player.getPackets()
										.sendGameMessage(
												"You can only attack players in a player-vs-player area.");
								return;
							}
						}
						if (!player.getFamiliar().canAttack(npc)) {
							player.getPackets()
									.sendGameMessage(
											"You can only use your familiar in a multi-zone area.");
							return;
						} else {
							player.getFamiliar().setSpecial(
									interfaceId == 662 && componentId == 74
											|| interfaceId == 747
											&& componentId == 18);
							player.getFamiliar().setTarget(npc);
						}
					}
					break;
				case 193:
					switch (componentId) {
					case 28:
					case 32:
					case 24:
					case 20:
					case 30:
					case 34:
					case 26:
					case 22:
					case 29:
					case 33:
					case 25:
					case 21:
					case 31:
					case 35:
					case 27:
					case 23:
						if (Magic.checkCombatSpell(player, componentId, 1, false)) {
							player.setNextFaceTile(new Tile(npc.getCoordFaceX(npc
									.getSize()), npc.getCoordFaceY(npc.getSize()),
									npc.getZ()));
							if (!player.getControllerManager().canAttack(npc))
								return;
							if (npc instanceof Familiar) {
								Familiar familiar = (Familiar) npc;
								if (familiar == player.getFamiliar()) {
									player.getPackets().sendGameMessage(
											"You can't attack your own familiar.");
									return;
								}
								if (!familiar.canAttack(player)) {
									player.getPackets().sendGameMessage(
											"You can't attack this npc.");
									return;
								}
							} else if (!npc.isForceMultiAttacked()) {
								if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
									if (player.getAttackedBy() != npc
											&& player.getAttackedByDelay() > Utilities
													.currentTimeMillis()) {
										player.getPackets().sendGameMessage(
												"You are already in combat.");
										return;
									}
									if (npc.getAttackedBy() != player
											&& npc.getAttackedByDelay() > Utilities
													.currentTimeMillis()) {
										player.getPackets().sendGameMessage(
												"This npc is already in combat.");
										return;
									}
								}
							}
							player.getActionManager().setAction(
									new PlayerCombat(npc));
						}
						break;
					}
				case 192:
					switch (componentId) {
					case 25: // air strike
					case 28: // water strike
					case 30: // earth strike
					case 32: // fire strike
					case 34: // air bolt
					case 39: // water bolt
					case 42: // earth bolt
					case 45: // fire bolt
					case 49: // air blast
					case 52: // water blast
					case 58: // earth blast
					case 63: // fire blast
					case 70: // air wave
					case 73: // water wave
					case 77: // earth wave
					case 80: // fire wave
					case 84: // air surge
					case 87: // water surge
					case 89: // earth surge
					case 66: // Sara Strike
					case 67: // Guthix Claws
					case 68: // Flame of Zammy
					case 93:
					case 91: // fire surge
					case 99: // storm of Armadyl
					case 36: // bind
					case 55: // snare
					case 81: // entangle
						if (Magic.checkCombatSpell(player, componentId, 1, false)) {
							player.setNextFaceTile(new Tile(npc.getCoordFaceX(npc
									.getSize()), npc.getCoordFaceY(npc.getSize()),
									npc.getZ()));
							if (!player.getControllerManager().canAttack(npc))
								return;
							if (npc instanceof Familiar) {
								Familiar familiar = (Familiar) npc;
								if (familiar == player.getFamiliar()) {
									player.getPackets().sendGameMessage(
											"You can't attack your own familiar.");
									return;
								}
								if (!familiar.canAttack(player)) {
									player.getPackets().sendGameMessage(
											"You can't attack this npc.");
									return;
								}
							} else if (!npc.isForceMultiAttacked()) {
								if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
									if (player.getAttackedBy() != npc
											&& player.getAttackedByDelay() > Utilities
													.currentTimeMillis()) {
										player.getPackets().sendGameMessage(
												"You are already in combat.");
										return;
									}
									if (npc.getAttackedBy() != player
											&& npc.getAttackedByDelay() > Utilities
													.currentTimeMillis()) {
										player.getPackets().sendGameMessage(
												"This npc is already in combat.");
										return;
									}
								}
							}
							player.getActionManager().setAction(
									new PlayerCombat(npc));
						}
						break;
					}
					break;
				}
				if (GameConstants.DEBUG)
					System.out.println("Spell:" + componentId);

				break;
			}
			case ATTACK_NPC: {
				if (!player.isActive() || !player.clientHasLoadedMapRegion()
						|| player.isDead()
						|| player.getLockDelay() > Utilities.currentTimeMillis()) {
					return;
				}
				int entityIndex = stream.readShort(); // i see
				boolean unknown = stream.readByte() == 1;
				if (unknown)
					player.setRun(unknown);
				NPC npc = Engine.getNPCs().get(entityIndex);
				if (npc == null || npc.isDead() || npc.hasFinished()
						|| !player.getMapRegionsIds().contains(npc.getRegionId())
						|| !npc.getDefinitions().hasAttackOption()) {
					return;
				}
				if (!player.getControllerManager().canAttack(npc)) {
					return;
				}
				if (npc instanceof Familiar) {
					Familiar familiar = (Familiar) npc;
					if (familiar == player.getFamiliar()) {
						player.getPackets().sendGameMessage(
								"You can't attack your own familiar.");
						return;
					}
					if (!familiar.canAttack(player)) {
						player.getPackets().sendGameMessage(
								"You can't attack this npc.");
						return;
					}
				} else if (!npc.isForceMultiAttacked()) {
					if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
						if (player.getAttackedBy() != npc
								&& player.getAttackedByDelay() > Utilities
										.currentTimeMillis()) {
							player.getPackets().sendGameMessage(
									"You are already in combat.");
							return;
						}
						if (npc.getAttackedBy() != player
								&& npc.getAttackedByDelay() > Utilities
										.currentTimeMillis()) {
							player.getPackets().sendGameMessage(
									"This npc is already in combat.");
							return;
						}
					}
				}
				player.stopAll(false);
				player.getActionManager().setAction(new PlayerCombat(npc));
				break;
			}
			case NPC_CLICK1_PACKET:
				NPCHandler.handleOption1(player, stream);
				break;
			case NPC_CLICK2_PACKET:
				NPCHandler.handleOption2(player, stream);
				break;
			case NPC_CLICK3_PACKET:
				NPCHandler.handleOption3(player, stream);
				break;
			case NPC_CLICK4_PACKET:
				NPCHandler.handleOption4(player, stream);
				break;
			case INTERFACE_ON_OBJECT: {
				int interfaceHash = stream.readIntLE();
				int objectId = stream.readInt();
				int itemId = stream.readShortLE();
				int y = stream.readShortLE128();
				boolean forceRun = stream.readByte128() == 1;
				int slot = stream.readShort();
				int x = stream.readShortLE();
				final int interfaceId = interfaceHash >> 16;
				if (!player.isActive()
						|| !player.clientHasLoadedMapRegion()
						|| player.isDead()
						|| Utilities.getInterfaceDefinitionsSize() <= interfaceId
						|| !player.getInterfaceManager().containsInterface(
								interfaceId) || player.isDead()
						|| player.isLocked()
						|| player.getEmotesManager().isDoingEmote())
					return;
				final Tile tile = new Tile(x, y, player.getZ());
				if (!player.getMapRegionsIds().contains(tile.getRegionId()))
					return;
				GameObject mapObject = Engine.getRegion(tile.getRegionId())
						.getObject(objectId, tile);
				if (mapObject == null || mapObject.getId() != objectId)
					return;
				final GameObject object = !player.isAtDynamicRegion() ? mapObject
						: new GameObject(objectId, mapObject.getType(),
								mapObject.getRotation(), x, y, player.getZ());
				final Item item = player.getInventory().getItem(slot);
				if (item == null || item.getId() != itemId)
					return;
				player.stopAll(false); // false
				if (forceRun)
					player.setRun(forceRun);
				switch (interfaceId) {
				case Inventory.INVENTORY_INTERFACE: // inventory
					ObjectHandler.handleItemOnObject(player, object, interfaceId,
							item);
					break;
				}
				break;
			}
			case PLAYER_OPTION_1_PACKET: {
				if (!player.isActive() || !player.clientHasLoadedMapRegion()
						|| player.isDead())
					return;
				boolean unknown = stream.readByte() == 1;
				int playerIndex = stream.readUnsignedShort128();
				Player p2 = Engine.getPlayers().get(playerIndex);
				if (p2 == null || p2.isDead() || p2.hasFinished()
						|| !player.getMapRegionsIds().contains(p2.getRegionId()))
					return;
				if (player.getLockDelay() > Utilities.currentTimeMillis()
						|| !player.getControllerManager().canPlayerOption1(p2))
					return;
				if (!player.isCanPvp())
					return;
				if (!player.getControllerManager().canAttack(p2))
					return;
				if (!player.isCanPvp() || !p2.isCanPvp()) {
					player.getPackets()
							.sendGameMessage(
									"You can only attack players in a player-vs-player area.");
					return;
				}
				if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
					if (player.getAttackedBy() != p2
							&& player.getAttackedByDelay() > Utilities
									.currentTimeMillis()) {
						player.getPackets().sendGameMessage(
								"You are already in combat.");
						return;
					}
					if (p2.getAttackedBy() != player
							&& p2.getAttackedByDelay() > Utilities
									.currentTimeMillis()) {
						if (p2.getAttackedBy() instanceof NPC) {
							p2.setAttackedBy(player); // changes enemy to player,
							// player has priority over
							// npc on single areas
						} else {
							player.getPackets().sendGameMessage(
									"That player is already in combat.");
							return;
						}
					}
				}
				player.stopAll(false);
				player.getActionManager().setAction(new PlayerCombat(p2));
				break;
			}
			case PLAYER_OPTION_2_PACKET: {
				if (!player.isActive() || !player.clientHasLoadedMapRegion()
						|| player.isDead())
					return;
				boolean unknown = stream.readByte() == 1;
				int playerIndex = stream.readUnsignedShort128();
				Player p2 = Engine.getPlayers().get(playerIndex);
				if (p2 == null || p2.isDead() || p2.hasFinished()
						|| !player.getMapRegionsIds().contains(p2.getRegionId()))
					return;
				if (player.getLockDelay() > Utilities.currentTimeMillis())
					return;
				if (player.getControllerManager().getController() instanceof DuelArena)
					return;
				player.stopAll(false);
				player.getActionManager().setAction(new PlayerFollow(p2));
				break;
			}
			case PLAYER_OPTION_4_PACKET: {
				boolean unknown = stream.readByte() == 1;
				int playerIndex = stream.readUnsignedShort128();
				Player p2 = Engine.getPlayers().get(playerIndex);
				if (p2 == null || p2.isDead() || p2.hasFinished()
						|| !player.getMapRegionsIds().contains(p2.getRegionId())
						|| player.getLockDelay() >= Utilities.currentTimeMillis()
						|| player == p2)
					return;
				player.stopAll(false);
				player.faceEntity(p2);
				if (player.checkTotalLevel(300) < 300) {
					player.sendMessage("You need atleast a total level of 300 to trade.");
					return;
				}
				if (p2.getInterfaceManager().containsScreenInter()) {
					player.getPackets()
							.sendGameMessage("The other player is busy.");
					return;
				}
				if (player.getX() == p2.getX() && player.getY() == p2.getY()) {
					if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
						if (!player.addWalkSteps(player.getX() + 1, player.getY(),
								1))
							if (!player.addWalkSteps(player.getX(),
									player.getY() + 1, 1))
								player.addWalkSteps(player.getX(),
										player.getY() - 1, 1);
				}
				if (player.getX() == p2.getX() && player.getY() == p2.getY()) {
					player.getPackets().sendGameMessage("Unable to trade here.");
					return;
				}
				if (!p2.withinDistance(player, 14)) {
					player.getPackets().sendGameMessage(
							"Unable to find target " + p2.getDisplayName());
					return;
				}
				if (p2.getTemporaryAttributtes().get("TradeTarget") == player) {
					p2.getTemporaryAttributtes().remove("TradeTarget");
					player.getTrade().openTrade(p2);
					p2.getTrade().openTrade(player);
					return;
				}
				player.getTemporaryAttributtes().put("TradeTarget", p2);
				player.getPackets().sendGameMessage(
						"Sending " + p2.getDisplayName() + " a request...");
				p2.getPackets().sendTradeRequestMessage(player);
				break;
			}
			case PLAYER_OPTION_9_PACKET: {
				boolean unknown = stream.readByte() == 1;
				int playerIndex = stream.readShort128();
				System.out.println(playerIndex);
				Player p2 = Engine.getPlayers().get(playerIndex);
				if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished()
						|| !player.getMapRegionsIds().contains(p2.getRegionId()))
					return;
				if (player.isLocked())
					return;
				if (unknown)
					player.setRun(unknown);
				player.stopAll();
				ClansManager.viewInvite(player, p2);
				break;
			}
			case ITEM_TAKE_PACKET: {
				if (!player.isActive() || !player.clientHasLoadedMapRegion()
						|| player.isDead())
					return;
				long currentTime = Utilities.currentTimeMillis();
				if (player.getLockDelay() > currentTime)
					return;
				boolean forceRun = stream.readByte() == 1;
				final int id = stream.readShort128();
				int y = stream.readShortLE();
				int x = stream.readShort128();
				final Tile tile = new Tile(x, y, player.getZ());
				final int regionId = tile.getRegionId();
				if (!player.getMapRegionsIds().contains(regionId))
					return;
				final FloorItem item = Engine.getRegion(regionId).getGroundItem(id,
						tile, player);
				if (item == null)
					return;
				player.stopAll(false);
				if (forceRun)
					player.setRun(forceRun);

				player.setRouteEvent(new RouteEvent(item, new Runnable() {
					@Override
					public void run() {
						final FloorItem item = Engine.getRegion(regionId)
								.getGroundItem(id, tile, player);
						if (item == null)
							return;
						player.setNextFaceTile(tile);
						player.addWalkSteps(tile.getX(), tile.getY(), 1);
						Engine.removeGroundItem(player, item);
					}
				}));
			}
				break;
		}
	}

	public static void loadPacketSizes() {
		PACKET_SIZES[0] = 3;
		PACKET_SIZES[1] = 1;
		PACKET_SIZES[2] = 1;
		PACKET_SIZES[3] = 9;
		PACKET_SIZES[4] = 8;
		PACKET_SIZES[5] = 2;
		PACKET_SIZES[6] = 11;
		PACKET_SIZES[7] = 16;
		PACKET_SIZES[8] = 4;
		PACKET_SIZES[9] = -1;
		PACKET_SIZES[10] = -2;
		PACKET_SIZES[11] = -2;
		PACKET_SIZES[12] = -1;
		PACKET_SIZES[13] = 8;
		PACKET_SIZES[14] = -2;
		PACKET_SIZES[15] = -1;
		PACKET_SIZES[16] = 9;
		PACKET_SIZES[17] = -1;
		PACKET_SIZES[18] = 2;
		PACKET_SIZES[19] = 4;
		PACKET_SIZES[20] = -1;
		PACKET_SIZES[21] = -1;
		PACKET_SIZES[22] = 8;
		PACKET_SIZES[23] = 7;
		PACKET_SIZES[24] = -2;
		PACKET_SIZES[25] = -1;
		PACKET_SIZES[26] = 16;
		PACKET_SIZES[27] = 7;
		PACKET_SIZES[28] = 8;
		PACKET_SIZES[29] = -2;
		PACKET_SIZES[30] = 0;
		PACKET_SIZES[31] = 6;
		PACKET_SIZES[32] = 3;
		PACKET_SIZES[33] = 4;
		PACKET_SIZES[34] = 7;
		PACKET_SIZES[35] = 3;
		PACKET_SIZES[36] = 3;
		PACKET_SIZES[37] = 8;
		PACKET_SIZES[38] = -1;
		PACKET_SIZES[39] = -1;
		PACKET_SIZES[40] = 9;
		PACKET_SIZES[41] = 18;
		PACKET_SIZES[42] = 7;
		PACKET_SIZES[43] = 3;
		PACKET_SIZES[44] = 3;
		PACKET_SIZES[45] = -1;
		PACKET_SIZES[46] = 3;
		PACKET_SIZES[47] = -1;
		PACKET_SIZES[48] = 4;
		PACKET_SIZES[49] = 9;
		PACKET_SIZES[50] = 3;
		PACKET_SIZES[51] = 7;
		PACKET_SIZES[52] = -1;
		PACKET_SIZES[53] = 3;
		PACKET_SIZES[54] = 3;
		PACKET_SIZES[55] = 3;
		PACKET_SIZES[56] = 4;
		PACKET_SIZES[57] = 0;
		PACKET_SIZES[58] = 3;
		PACKET_SIZES[59] = 8;
		PACKET_SIZES[60] = 4;
		PACKET_SIZES[61] = 7;
		PACKET_SIZES[62] = 7;
		PACKET_SIZES[63] = 2;
		PACKET_SIZES[64] = 0;
		PACKET_SIZES[65] = 4;
		PACKET_SIZES[66] = 4;
		PACKET_SIZES[67] = -1;
		PACKET_SIZES[68] = -1;
		PACKET_SIZES[69] = 8;
		PACKET_SIZES[70] = 1;
		PACKET_SIZES[71] = -1;
		PACKET_SIZES[72] = 3;
		PACKET_SIZES[73] = 3;
		PACKET_SIZES[74] = 5;
		PACKET_SIZES[75] = -1;
		PACKET_SIZES[76] = 8;
		PACKET_SIZES[77] = -1;
		PACKET_SIZES[78] = 4;
		PACKET_SIZES[79] = 3;
		PACKET_SIZES[80] = 8;
		PACKET_SIZES[81] = -1;
		PACKET_SIZES[82] = 9;
		PACKET_SIZES[83] = 6;
		PACKET_SIZES[84] = -1;
		PACKET_SIZES[85] = 9;
		PACKET_SIZES[86] = -1;
		PACKET_SIZES[87] = -1;
		PACKET_SIZES[88] = 3;
		PACKET_SIZES[89] = 15;
		PACKET_SIZES[90] = 6;
		PACKET_SIZES[91] = 9;
		PACKET_SIZES[92] = 1;
		PACKET_SIZES[93] = 4;
		PACKET_SIZES[94] = 1;
		PACKET_SIZES[95] = -2;
		PACKET_SIZES[96] = 15;
		PACKET_SIZES[97] = -2;
		PACKET_SIZES[98] = -1;
		PACKET_SIZES[99] = 12;
		PACKET_SIZES[100] = 0;
		PACKET_SIZES[101] = -1;
		PACKET_SIZES[102] = -2;
		PACKET_SIZES[103] = 8;
		PACKET_SIZES[104] = 3;
		PACKET_SIZES[105] = 2;
		PACKET_SIZES[106] = 11;
		PACKET_SIZES[107] = 17;
	}

	private Player player;

	public WorldPacketsDecoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	private static final short[] IGNORED = { 30, 33, 24, 61, 94, 67, 98, 90 };

	public static boolean isIgnored(int opcode) {
		for (short ignored : IGNORED) {
			if (ignored == opcode)
				return true;
		}
		return false;
	}

	@Override
	public void decode(Session session, InputStream in) {
		while (in.getRemaining() > 0
				&& player.getSession().getChannel().isConnected()
				&& !player.hasFinished()) {
			int opcode = in.readPacket(player);
			if (opcode >= PACKET_SIZES.length || opcode < 0) {
				break;
			}
			int length = PACKET_SIZES[opcode];
			if (length == -1)
				length = in.readUnsignedByte();
			else if (length == -2)
				length = in.readUnsignedShort();
			else if (length == -3)
				length = in.readInt();
			else if (length == -4) {
				length = in.getRemaining();
			}
			if (length > in.getRemaining()) {
				length = in.getRemaining();
			}
			int startOffset = in.getOffset();
			processPackets(opcode, length, in);
			in.setOffset(startOffset + length);
		}
	}

	private int chatType;
	
	
	/**
	 * 
	 * @param opcode
	 * @param length
	 * @param stream
	 * 
	 * All out-commented opcodes we're duplicated {@code -1}, get the real opcode and make them functional again.
	 */
	public void processPackets(final int opcode, final int length,
			InputStream stream) {
		player.setPacketsDecoderPing(Utilities.currentTimeMillis());
		switch (opcode) {
		case PING_PACKET:
			OutputStream packet = new OutputStream(0);
			packet.writeFixedPacket(player, 12);
			player.getSession().write(packet);
			break;
		case CLIENT_FOCUS_PACKET:
			boolean clientFocus = stream.readUnsignedByte() == 1;
			break;
		case DISPLAY_PACKET:
			int displayMode = stream.readByte();
			player.setScreenWidth(stream.readShort());
			player.setScreenHeight(stream.readShort());
			boolean switchScreenMode = stream.readByte() == 1;
			player.setDisplayMode(displayMode);
			player.getInterfaceManager().removeAll();
			player.getInterfaceManager().sendInterfaces();
			break;
		case ITEM_ON_ITEM_PACKET:
			InventoryOptionsHandler.handleItemOnItem(player, stream);
			break;
		case GROUND_ITEM_EXAMINE_PACKET:
			boolean forceRun = stream.readByte() == 1;
			int id = stream.readShort128();
			int y = stream.readShortLE();
			int x = stream.readShort128();
			Item item = new Item(id, 1);
			player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
			if (player.getRights() == 2) {
				player.getPackets()
						.sendGameMessage(
								"<img=1> X: " + x + " Y: " + y + " id: "
										+ item.getId() + " amnt: "
										+ item.getAmount() + " modelzoom: "
										+ item.getDefinitions().getModelZoom()
										+ " value: "
										+ item.getDefinitions().getValue());
			}
			break;
		case RECEIVE_PACKET_COUNT_PACKET:
			int packetcount = stream.readInt();
			break;
		case DIALOGUE_CONTINUE_PACKET: {
			int junk = stream.readShortLE();
			int interfaceHash = stream.readIntV1();
			int interfaceId = interfaceHash >> 16;
			int buttonId = (interfaceHash & 0xFF);
			if (Utilities.getInterfaceDefinitionsSize() <= interfaceId) {
				// hack, or server error or client error
				// player.getSession().getChannel().close();
				return;
			}
			if (!player.isRunning())
				return;
			if (GameConstants.DEBUG)
				Logger.log(this, "Dialogue: " + interfaceId + ", " + buttonId
						+ ", " + junk);
			int componentId = interfaceHash - (interfaceId << 16);
			player.getDialogueManager().continueDialogue(interfaceId,
					componentId);
			break;
		}
		case CLOSE_INTERFACE_PACKET:
			if (player.isActive() && !player.hasFinished()
					&& !player.isRunning()) {
				player.run();
				return;
			}
			player.stopAll();
			break;
		case SAVE_PLAYER_PACKET:
			SerializableFilesManager.savePlayer(player);
			if (player.getClanName() != null) {
				Clan clan = new Clan(player.getClanName(), player);
				SerializableFilesManager.saveClan(clan);
			}
			DisplayNames.save();
			IPBanL.save();
			PkRank.save();
			DTRank.save();
			break;
		case ENTER_INTEGER_PACKET: {
			if (!player.isRunning() || player.isDead())
				return;
			int value = stream.readInt();
			if (player.getInterfaceManager().containsInterface(762)
					&& player.getInterfaceManager().containsInterface(763)
					|| player.getInterfaceManager().containsInterface(11)) {
				if (value < 0)
					return;
				Integer bank_item_X_Slot = (Integer) player
						.getTemporaryAttributtes().remove("bank_item_X_Slot");
				if (bank_item_X_Slot == null)
					return;
				player.getBank().setLastX(value);
				player.getBank().refreshLastX();
				if (player.getTemporaryAttributtes().remove("bank_isWithdraw") != null) {
					player.getBank().withdrawItem(bank_item_X_Slot, value);
				} else {
					player.getBank()
							.depositItem(
									bank_item_X_Slot,
									value,
									player.getInterfaceManager()
											.containsInterface(11) ? false
											: true);
				}
			} else if (player.getInterfaceManager().containsInterface(206)
					&& player.getInterfaceManager().containsInterface(207)) {
				if (value < 0)
					return;
				Integer pc_item_X_Slot = (Integer) player
						.getTemporaryAttributtes().remove("pc_item_X_Slot");
				if (pc_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("pc_isRemove") != null) {
					player.getPriceCheckManager().removeItem(pc_item_X_Slot,
							value);
				} else {
					player.getPriceCheckManager()
							.addItem(pc_item_X_Slot, value);
				}
			} else if (player.getInterfaceManager().containsInterface(671)
					&& player.getInterfaceManager().containsInterface(665)) {
				if (player.getFamiliar() == null
						|| player.getFamiliar().getBob() == null)
					return;
				if (value < 0)
					return;
				Integer bob_item_X_Slot = (Integer) player
						.getTemporaryAttributtes().remove("bob_item_X_Slot");
				if (bob_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("bob_isRemove") != null) {
					player.getFamiliar().getBob()
							.removeItem(bob_item_X_Slot, value);
				} else {
					player.getFamiliar().getBob()
							.addItem(bob_item_X_Slot, value);
				}
			} else if (player.getInterfaceManager().containsInterface(335)
					&& player.getInterfaceManager().containsInterface(336)) {
				if (value < 0)
					return;
				Integer trade_item_X_Slot = (Integer) player
						.getTemporaryAttributtes().remove("trade_item_X_Slot");
				if (trade_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("trade_isRemove") != null) {
					player.getTrade().removeItem(trade_item_X_Slot, value);
				} else {
					player.getTrade().addItem(trade_item_X_Slot, value);
				}
			} else if (player.getTemporaryAttributtes().remove(
					"withdrawingPouch") == Boolean.TRUE) {
				player.getMoneyPouch().sendDynamicInteraction(value, true,
						MoneyPouch.TYPE_POUCH_INVENTORY);
			} else if (player.getTemporaryAttributtes().get("skillId") != null) {
				if (player.getEquipment().wearingArmour()) {
					player.getDialogueManager().finishDialogue();
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You cannot do this while having armour on!");
					return;
				}
				int skillId = (Integer) player.getTemporaryAttributtes()
						.remove("skillId");
				if (skillId == Skills.HITPOINTS && value <= 9) {
					value = 10;
				} else if (value < 1) {
					value = 1;
				} else if (value > 99) {
					value = 99;
				}
				player.getSkills().set(skillId, value);
				player.getSkills().setXp(skillId, Skills.getXPForLevel(value));
				player.getAppearence().generateAppearenceData();
				player.getDialogueManager().finishDialogue();
			} else if (player.getTemporaryAttributtes().get("kilnX") != null) {
				int index = (Integer) player.getTemporaryAttributtes().get(
						"scIndex");
				int componentId = (Integer) player.getTemporaryAttributtes()
						.get("scComponentId");
				int itemId = (Integer) player.getTemporaryAttributtes().get(
						"scItemId");
				player.getTemporaryAttributtes().remove("kilnX");
				if (StealingCreation.proccessKilnItems(player, componentId,
						index, itemId, value))
					return;
			}
			break;
		}
		case ENTER_NAME_PACKET: {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getInterfaceManager().containsInterface(1108))
				player.getFriendsIgnores().setChatPrefix(value);
			else if (player.getTemporaryAttributtes().remove("setclan") != null)
				ClansManager.createClan(player, value);
			else if (player.getTemporaryAttributtes().remove("joinguestclan") != null)
				ClansManager.connectToClan(player, value, true);
			else if (player.getTemporaryAttributtes().remove("banclanplayer") != null)
				ClansManager.banPlayer(player, value);
			else if (player.getTemporaryAttributtes().remove("unbanclanplayer") != null)
				ClansManager.unbanPlayer(player, value);
			else if (player.getTemporaryAttributtes().remove("setdisplay") != null) {
				if (Utilities.invalidAccountName(Utilities
						.formatPlayerNameForProtocol(value))) {
					player.getPackets().sendGameMessage("Invalid name!");
					return;
				}
				if (!DisplayNames.setDisplayName(player, value)) {
					player.getPackets().sendGameMessage("Name already in use!");
					return;
				}
				player.getPackets().sendGameMessage("Changed display name!");
			} else if (player.getTemporaryAttributtes()
					.remove("checkvoteinput") != null)
				player.getPackets().sendGameMessage("Unknown action.");
			break;
		}
		case ENTER_LONG_TEXT_PACKET: {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getTemporaryAttributtes().remove("entering_note") == Boolean.TRUE)
				player.getNotes().add(value);
			else if (player.getTemporaryAttributtes().remove("editing_note") == Boolean.TRUE)
				player.getNotes().edit(value);
			else if (player.getTemporaryAttributtes().remove("change_pass") == Boolean.TRUE) {
				if (value.length() < 5 || value.length() > 15) {
					player.getPackets().sendGameMessage(
							"Password length is limited to 5-15 characters.");
					return;
				}
				player.setPassword(Encrypt.encryptSHA1(value));
				player.getPackets().sendGameMessage(
						"You have changed your password! Your new password is \""
								+ value + "\".");
			} else if (player.getTemporaryAttributtes().remove(
					"change_troll_name") == Boolean.TRUE) {
				value = Utilities.formatPlayerNameForDisplay(value);
				if (value.length() < 3 || value.length() > 14) {
					player.getPackets()
							.sendGameMessage(
									"You can't use a name shorter than 3 or longer than 14 characters.");
					return;
				}
				if (value.equalsIgnoreCase("none")) {
					player.getPetManager().setTrollBabyName(null);
				} else {
					player.getPetManager().setTrollBabyName(value);
					if (player.getPet() != null
							&& player.getPet().getId() == Pets.TROLL_BABY
									.getBabyNpcId()) {
						player.getPet().setName(value);
					}
				}
			} else if (player.getTemporaryAttributtes().remove("setdisplay") == Boolean.TRUE) {
				if (Utilities.invalidAccountName(Utilities
						.formatPlayerNameForProtocol(value))) {
					player.getPackets()
							.sendGameMessage(
									"Name contains invalid characters or is too short/long.");
					return;
				}
				if (!DisplayNames.setDisplayName(player, value)) {
					player.getPackets().sendGameMessage(
							"This name is already in use.");
					return;
				}
				player.getPackets().sendGameMessage(
						"Your display name was successfully changed.");
			} else if (player.getInterfaceManager().containsInterface(1103))
				ClansManager.setClanMottoInterface(player, value);
			break;
		}
		case COLOR_ID_PACKET:
			if (!player.isActive())
				return;
			int colorId = stream.readUnsignedShort();
			if (player.getTemporaryAttributtes().get("SkillcapeCustomize") != null)
				SkillCapeCustomizer.handleSkillCapeCustomizerColor(player,
						colorId);
			else if (player.getTemporaryAttributtes().get("MottifCustomize") != null)
				ClansManager.setMottifColor(player, colorId);
			break;
		case SWITCH_INTERFACE_ITEM_PACKET:
			int fromSlot = stream.readShort();
			stream.readShortLE128();
			stream.readShort128();
			int toSlot = stream.readShortLE();
			int fromInterfaceHash = stream.readIntLE();
			int toInterfaceHash = stream.readInt();

			int toInterfaceId = toInterfaceHash >> 16;
			int toComponentId = toInterfaceHash & 0xFFF;
			int fromInterfaceId = fromInterfaceHash >> 16;
			int fromComponentId = fromInterfaceHash & 0xFFF;
			Logger.log(this, "ToSlot: " + toSlot + ", fromSlot: " + fromSlot
					+ " toInterfaceHash: " + toInterfaceHash
					+ ", toInterfaceID: " + toInterfaceId + ", toComponent: "
					+ toComponentId + ", frominterface: " + fromInterfaceId
					+ ", from componentId: " + fromComponentId);
			if (fromInterfaceId == 1265
					&& toInterfaceId == 1266
					&& player.getTemporaryAttributtes().get("shop_buying") != null) {
				if ((boolean) player.getTemporaryAttributtes().get(
						"shop_buying") == true) {
					Shop shop = (Shop) player.getTemporaryAttributtes().get(
							"Shop");
					if (shop == null)
						return;
					shop.buy(player, fromSlot, 1);
				}
			}
			if (Utilities.getInterfaceDefinitionsSize() <= fromInterfaceId
					|| Utilities.getInterfaceDefinitionsSize() <= toInterfaceId)
				return;
			if (!player.getInterfaceManager()
					.containsInterface(fromInterfaceId)
					|| !player.getInterfaceManager().containsInterface(
							toInterfaceId))
				return;
			if (fromComponentId != -1
					&& Utilities
							.getInterfaceDefinitionsComponentsSize(fromInterfaceId) <= fromComponentId)
				return;
			if (toComponentId != -1
					&& Utilities
							.getInterfaceDefinitionsComponentsSize(toInterfaceId) <= toComponentId)
				return;
			if (fromInterfaceId == Inventory.INVENTORY_INTERFACE
					&& fromComponentId == 0
					&& toInterfaceId == Inventory.INVENTORY_INTERFACE
					&& toComponentId == 0) {
				toSlot -= 28;
				if (toSlot < 0
						|| toSlot >= player.getInventory()
								.getItemsContainerSize()
						|| fromSlot >= player.getInventory()
								.getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 763 && fromComponentId == 0
					&& toInterfaceId == 763 && toComponentId == 0) {
				if (toSlot >= player.getInventory().getItemsContainerSize()
						|| fromSlot >= player.getInventory()
								.getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 762 && toInterfaceId == 762) {
				if (toSlot != 1)
					toSlot = 65535; // temp fix
				player.getBank().switchItem(fromSlot, toSlot, fromComponentId,
						toComponentId);
			}
			if (GameConstants.DEBUG)
				System.out.println("Switch item " + fromInterfaceId + ", "
						+ fromSlot + ", " + toSlot);
			break;
			case OBJECT_EXAMINE_PACKET:
				ObjectHandler.handleOption(player, stream, -1);
				break;
			case ADD_FRIEND_PACKET:
				if (!player.isActive())
					return;
				player.getFriendsIgnores().addFriend(stream.readString());
				break;
			case REMOVE_FRIEND_PACKET:
				if (!player.isActive())
					return;
				player.getFriendsIgnores().removeFriend(stream.readString());
				break;
			case ADD_IGNORE_PACKET:
				if (!player.isActive())
					return;
				player.getFriendsIgnores().addIgnore(stream.readString(),
						stream.readUnsignedByte() == 1);
				break;
			case REMOVE_IGNORE_PACKET:
				if (!player.isActive())
					return;
				player.getFriendsIgnores().removeIgnore(stream.readString());
				break;
			case SEND_FRIEND_MESSAGE_PACKET: {
				if (player.getLastPrivateMessage() > Utilities.currentTimeMillis())
	                return;
			if (!player.isActive()
					&& !Engine.containsLobbyPlayer(player.getUsername()) )
				return;
			if (player.getMuted() > Utilities.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"You are muted and cannot talk. You can apply this at forums.");
				return;
			}
			if (IPMute.isMuted(player.getSession().getIP())) {
				player.getPackets()
						.sendGameMessage(
								"You are IP-muted and cannot talk. You 'may' apply this at forums. However in must cases it will be declined.");
				return;
			}
			String username = stream.readString();
			String message = Huffman.readEncryptedMessage(150, stream);
			Player p2 = Engine.getPlayerByDisplayName(username);
			if (p2 == null) {
				player.getPackets().sendGameMessage(
						player.getDisplayName() + " is currently offline.");
				return;
			}
			player.getFriendsIgnores().sendMessage(p2,
					Utilities.fixChatMessage(message));
				break;
			}
			case SEND_FRIEND_QUICK_CHAT_PACKET: {
				if (!player.isActive() && !Engine.containsLobbyPlayer(player.getUsername()))
	                return;
	            String username = stream.readString();
	            int fileId = stream.readUnsignedShort();
	            if (!Utilities.isValidQuickChat(fileId))
					return;
	            byte[] data = null;
	            if (length > 3 + username.length()) {
	                data = new byte[length - (3 + username.length())];
	                stream.readBytes(data);
	            }
	            data = Utilities.completeQuickMessage(player, fileId, data);
	            Player p2 = Engine.getPlayerByDisplayName(username);
	            if (p2 == null) {
	                p2 = Engine.getLobbyPlayerByDisplayName(username); // getLobbyPlayerByDisplayName
	                if (p2 == null)
	                    return;
	            }
	            player.getFriendsIgnores().sendQuickChatMessage(p2, new QuickChatMessage(fileId, data));
				break;
			}
			case CHAT_TYPE_PACKET:
				chatType = stream.readUnsignedByte();
				break;
			case CHAT_PACKET:
				if (!player.isActive()
						|| player.getLastPublicMessage() > Utilities
								.currentTimeMillis())
					return;
				int colorEffect = stream.readByte();
				int moveEffect = stream.readByte();
				String message = Huffman.readEncryptedMessage(200, stream);
				if (message == null)
					return;
				if (message.startsWith("::") || message.startsWith(";;")) {
					if (!player.getControllerManager().processCommand(
							message.replace("::", "").replace(";;", ""), false,
							false))
						return;
					Commands.processCommand(player, message.replace("::", "")
							.replace(";;", ""), false, false);
					return;
				}
				if (player.getMuted() > Utilities.currentTimeMillis()) {
					player.getPackets()
							.sendGameMessage(
									"You are muted and cannot talk. You can apply this at forums.");
					return;
				}
				if (IPMute.isMuted(player.getSession().getIP())) {
					player.getPackets()
							.sendGameMessage(
									"You are IP-muted and cannot talk. You 'may' apply this at forums. However in must cases it will be declined.");
					return;
				}
				int effects = Utilities.fixChatEffects(player.getUsername(),
						colorEffect, moveEffect);
				if (chatType == 1) {
					player.sendFriendsChannelMessage(Utilities
							.fixChatMessage(message));
				} else if (chatType == 2) {
					player.sendClanChannelMessage(new ChatMessage(Utilities
							.fixChatMessage(message)));
				} else if (chatType == 3) {
					player.sendGuestClanChannelMessage(new ChatMessage(Utilities
							.fixChatMessage(message)));
				} else {
					player.sendPublicChatMessage(new PublicChatMessage(Utilities
							.fixChatMessage(message), effects));
				}
				player.setLastPublicMessage(Utilities.currentTimeMillis() + 300);
				if (GameConstants.DEBUG)
					Logger.log(this, "Chat type: " + chatType);
				break;
			case JOIN_FRIEND_CHAT_PACKET:
				if (!player.isActive()
						&& !Engine.containsLobbyPlayer(player.getUsername()) || player.getLastJoined() > Utilities
						.currentTimeMillis())
					return;
				FriendChatsManager.joinChat(stream.readString(), player);
				break;
			case KICK_FRIEND_CHAT_PACKET:
				if (!player.isActive())
					return;
				player.setLastPublicMessage(Utilities.currentTimeMillis() + 1000);
				player.kickPlayerFromFriendsChannel(stream.readString());
				break;
			case CHANGE_FRIEND_CHAT_PACKET:
				if (!player.isActive()
						|| !player.getInterfaceManager().containsInterface(1108))
					return;
				player.getFriendsIgnores().changeRank(stream.readString(),
						stream.readByte());
				break;
			/*case FORUM_THREAD_ID_PACKET: {
				String threadId = stream.readString();
				if (player.getInterfaceManager().containsInterface(1100))
					ClansManager.setThreadIdInterface(player, threadId);
				else if (GameConstants.DEBUG)
					Logger.log(this, "Called FORUM_THREAD_ID_PACKET: " + threadId);
				break;
			case OPEN_URL_PACKET:
				String type = stream.readString();
				String path = stream.readString();
				String unknown = stream.readString();
				int flag = stream.readUnsignedByte();
				break;*/
			case COMMANDS_PACKET:
				if (!player.isRunning() || player.getLastCommand() > Utilities
						.currentTimeMillis())
					return;
				boolean clientCommand = stream.readUnsignedByte() == 1;
				stream.readUnsignedByte();
				String command = stream.readString();
				if (!Commands.processCommand(player, command, true, clientCommand)
						&& GameConstants.DEBUG)
					Logger.log(this, "Command: " + command);
				break;
			case NPC_EXAMINE_PACKET:
				NPCHandler.handleExamine(player, stream);
				break;
			case PUBLIC_QUICK_CHAT_PACKET:
				if (!player.isActive())
					return;
				if (player.getLastPublicMessage() > Utilities.currentTimeMillis())
					return;
				player.setLastPublicMessage(Utilities.currentTimeMillis() + 300);
				boolean secondClientScript = stream.readByte() == 1;
				int fileId = stream.readUnsignedShort();
				if (!Utilities.isValidQuickChat(fileId))
					return;
				byte[] data = null;
				if (length > 3) {
					data = new byte[length - 3];
					stream.readBytes(data);
				}
				data = Utilities.completeQuickMessage(player, fileId, data);
				if (chatType == 0)
					player.sendPublicChatMessage(new QuickChatMessage(fileId, data));
				else if (chatType == 1)
					player.sendFriendsChannelQuickMessage(new QuickChatMessage(
							fileId, data));
				else if (chatType == 2)
					player.sendClanChannelQuickMessage(new QuickChatMessage(fileId,
							data));
				else if (chatType == 3)
					player.sendGuestClanChannelQuickMessage(new QuickChatMessage(
							fileId, data));
				else if (GameConstants.DEBUG)
					Logger.log(this, "Unknown chat type: " + chatType);
				break;
			case ACTION_BUTTON1_PACKET:
			case ACTION_BUTTON2_PACKET:
			case ACTION_BUTTON3_PACKET:
			case ACTION_BUTTON4_PACKET:
			case ACTION_BUTTON5_PACKET:
			case ACTION_BUTTON6_PACKET:
			//case ACTION_BUTTON7_PACKET:
			case ACTION_BUTTON8_PACKET:
			//case ACTION_BUTTON9_PACKET:
			case ACTION_BUTTON10_PACKET:
				ButtonHandler.handleButtons(player, stream, opcode);
				break;
			case DONE_LOADING_REGION_PACKET:
				break;
		}
		 if (opcode == WALKING_PACKET || opcode == MINI_WALKING_PACKET
				 || opcode == ITEM_TAKE_PACKET
				 || opcode == PLAYER_OPTION_2_PACKET
				 || opcode == PLAYER_OPTION_4_PACKET
				 || opcode == PLAYER_OPTION_6_PACKET
				 || opcode == PLAYER_OPTION_1_PACKET
				 || opcode == PLAYER_OPTION_9_PACKET || opcode == ATTACK_NPC
				 || opcode == INTERFACE_ON_PLAYER || opcode == INTERFACE_ON_NPC
				 || opcode == NPC_CLICK1_PACKET || opcode == NPC_CLICK2_PACKET
				 || opcode == NPC_CLICK3_PACKET || opcode == NPC_CLICK4_PACKET
				 || opcode == OBJECT_CLICK1_PACKET
				 || opcode == SWITCH_INTERFACE_ITEM_PACKET
				 || opcode == OBJECT_CLICK2_PACKET
				 || opcode == OBJECT_CLICK3_PACKET
				 || opcode == OBJECT_CLICK4_PACKET
				 || opcode == OBJECT_CLICK5_PACKET
				 || opcode == INTERFACE_ON_OBJECT) {
			 player.addLogicPacketToQueue(new LogicPacket(opcode, length, stream));
		}
	}
}