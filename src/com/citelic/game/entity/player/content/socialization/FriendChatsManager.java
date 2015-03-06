package com.citelic.game.entity.player.content.socialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.chat.QuickChatMessage;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.ClanWars;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;

public class FriendChatsManager {

	private static HashMap<String, FriendChatsManager> cachedFriendChats;
	private String owner;
	private String ownerDisplayName;
	private FriendsIgnores settings;
	private CopyOnWriteArrayList<Player> players;
	private ConcurrentHashMap<String, Long> bannedPlayers;
	private byte[] dataBlock;
	/**
	 * The clan wars instance (if the clan is in a war).
	 */
	private ClanWars clanWars;

	private FriendChatsManager(Player player) {
		owner = player.getUsername();
		ownerDisplayName = player.getDisplayName();
		settings = player.getFriendsIgnores();
		players = new CopyOnWriteArrayList<Player>();
		bannedPlayers = new ConcurrentHashMap<String, Long>();
	}

	public static void destroyChat(Player player) {
		synchronized (FriendChatsManager.cachedFriendChats) {
			FriendChatsManager chat = FriendChatsManager.cachedFriendChats
					.get(player.getUsername());
			if (chat == null)
				return;
			chat.destroyChat();
			player.getPackets().sendGameMessage(
					"Your friends chat channel has now been disabled!");
		}
	}

	public static List<Player> getLootSharingPeople(Player player) {
		if (!player.isToggleLootShare())
			return null;
		FriendChatsManager chat = player.getCurrentFriendChat();
		if (chat == null)
			return null;
		List<Player> players = new ArrayList<Player>();
		for (Player p2 : player.getCurrentFriendChat().getPlayers()) {
			if (p2.isToggleLootShare() && p2.withinDistance(player)) {
				players.add(p2);
			}
		}
		return players;

	}

	public static void init() {
		FriendChatsManager.cachedFriendChats = new HashMap<String, FriendChatsManager>();
	}

	public static void joinChat(String ownerName, Player player) {
		synchronized (FriendChatsManager.cachedFriendChats) {
			if (player.getCurrentFriendChat() != null)
				return;
			player.getPackets()
					.sendGameMessage("Attempting to join channel...");
			String formatedName = Utilities
					.formatPlayerNameForProtocol(ownerName);
			FriendChatsManager chat = FriendChatsManager.cachedFriendChats
					.get(formatedName);
			if (chat == null) {
				Player owner = Engine.getPlayerByDisplayName(ownerName);
				if (owner == null) {
					owner = Engine.getLobbyPlayerByDisplayName(ownerName);
				}
				if (owner == null) {
					if (!SerializableFilesManager.containsPlayer(formatedName)) {
						player.getPackets()
								.sendGameMessage(
										"The channel you tried to join does not exist.");
						return;
					}
					owner = SerializableFilesManager.loadPlayer(formatedName);
					if (owner == null) {
						player.getPackets()
								.sendGameMessage(
										"The channel you tried to join does not exist.");
						return;
					}
					owner.setUsername(formatedName);
				}
				FriendsIgnores settings = owner.getFriendsIgnores();
				if (!settings.hasFriendChat()) {
					player.getPackets().sendGameMessage(
							"The channel you tried to join does not exist.");
					return;
				}
				if (!player.getUsername().equals(ownerName)
						&& !settings.hasRankToJoin(player.getUsername())
						&& player.getRights() < 2) {
					player.getPackets()
							.sendGameMessage(
									"You do not have a enough rank to join this friends chat channel.");
					return;
				}
				chat = new FriendChatsManager(owner);
				FriendChatsManager.cachedFriendChats.put(ownerName, chat);
				chat.joinChatNoCheck(player);
			} else {
				chat.joinChat(player);
			}
		}

	}

	public static void linkSettings(Player player) {
		synchronized (FriendChatsManager.cachedFriendChats) {
			FriendChatsManager chat = FriendChatsManager.cachedFriendChats
					.get(player.getUsername());
			if (chat == null)
				return;
			chat.settings = player.getFriendsIgnores();
		}
	}

	public static void refreshChat(Player player) {
		synchronized (FriendChatsManager.cachedFriendChats) {
			FriendChatsManager chat = FriendChatsManager.cachedFriendChats
					.get(player.getUsername());
			if (chat == null)
				return;
			chat.refreshChannel();
		}
	}

	public static void toggleLootShare(Player player) {
		if (player.getCurrentFriendChat() == null) {
			player.getPackets()
					.sendGameMessage(
							"You need to be in a Friends Chat channel to activate LootShare.");
			player.refreshToggleLootShare();
			return;
		}
		if (!player.getUsername().equals(
				player.getCurrentFriendChat().getOwnerName())
				&& !player.getCurrentFriendChat().settings
						.hasRankToLootShare(player.getUsername())) {
			player.getPackets()
					.sendGameMessage(
							"You must be on channel owner's Friend List ot use LootShare on this channel.");
			player.refreshToggleLootShare();
			return;
		}
		player.toggleLootShare();
		if (player.isToggleLootShare()) {
			player.getPackets().sendGameMessage("LootShare is now active.");
		}
	}

	public void destroyChat() {
		synchronized (this) {
			for (Player player : players) {
				player.setCurrentFriendChat(null);
				player.setCurrentFriendChatOwner(null);
				player.disableLootShare();
				player.getPackets().sendFriendsChatChannel();
				player.getPackets().sendGameMessage(
						"You have been removed from this channel!");
			}
		}
		synchronized (FriendChatsManager.cachedFriendChats) {
			FriendChatsManager.cachedFriendChats.remove(owner);
		}

	}

	public String getChannelName() {
		return settings.getChatName().replaceAll("<img=", "");
	}

	/**
	 * Gets the clanWars.
	 *
	 * @return The clanWars.
	 */
	public ClanWars getClanWars() {
		return clanWars;
	}

	/**
	 * Sets the clanWars.
	 *
	 * @param clanWars
	 *            The clanWars to set.
	 */
	public void setClanWars(ClanWars clanWars) {
		this.clanWars = clanWars;
	}

	public byte[] getDataBlock() {
		return dataBlock;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public String getOwnerName() {
		return owner;
	}

	public Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utilities
				.formatPlayerNameForProtocol(username);
		for (Player player : players) {
			if (player.getUsername().equals(formatedUsername)
					|| player.getDisplayName().equals(username))
				return player;
		}
		return null;
	}

	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}

	public int getRank(int rights, String username) {
		if (rights == 2)
			return 127;
		if (username.equals(owner))
			return 7;
		return settings.getRank(username);
	}

	public int getWhoCanKickOnChat() {
		return settings.getWhoCanKickOnChat();
	}

	private void joinChat(Player player) {
		synchronized (this) {
			if (!player.getUsername().equals(owner)
					&& !settings.hasRankToJoin(player.getUsername())
					&& player.getRights() < 2) {
				player.getPackets()
						.sendGameMessage(
								"You do not have a enough rank to join this friends chat channel.");
				return;
			}
			if (players.size() >= 100) {
				player.getPackets().sendGameMessage("This chat is full.");
				return;
			}
			Long bannedSince = bannedPlayers.get(player.getUsername());
			if (bannedSince != null) {
				if (bannedSince + 3600000 > Utilities.currentTimeMillis()) {
					player.getPackets().sendGameMessage(
							"You have been banned from this channel.");
					return;
				}
				bannedPlayers.remove(player.getUsername());
			}
			joinChatNoCheck(player);
		}
	}

	private void joinChatNoCheck(Player player) {
		synchronized (this) {
			players.add(player);
			player.setCurrentFriendChat(this);
			player.setCurrentFriendChatOwner(owner);
			player.getPackets().sendGameMessage(
					"You are now talking in the friends chat channel "
							+ settings.getChatName());
			refreshChannel();
		}
	}

	public void kickPlayerFromChat(Player player, String username) {
		String name = "";
		for (char character : username.toCharArray()) {
			name += Utilities.containsInvalidCharacter(character) ? " "
					: character;
		}
		synchronized (this) {
			int rank = getRank(player.getRights(), player.getUsername());
			if (rank < getWhoCanKickOnChat())
				return;
			Player kicked = getPlayerByDisplayName(name);
			if (kicked == null) {
				player.getPackets().sendGameMessage(
						"This player is not this channel.");
				return;
			}
			if (rank <= getRank(kicked.getRights(), kicked.getUsername()))
				return;
			kicked.setCurrentFriendChat(null);
			kicked.setCurrentFriendChatOwner(null);
			player.disableLootShare();
			players.remove(kicked);
			bannedPlayers.put(kicked.getUsername(),
					Utilities.currentTimeMillis());
			kicked.getPackets().sendFriendsChatChannel();
			kicked.getPackets().sendGameMessage(
					"You have been kicked from the friends chat channel.");
			player.getPackets().sendGameMessage(
					"You have kicked " + kicked.getUsername()
							+ " from friends chat channel.");
			refreshChannel();

		}
	}

	public void leaveChat(Player player, boolean logout) {
		synchronized (this) {
			player.setCurrentFriendChat(null);
			players.remove(player);
			if (players.size() == 0) {
				synchronized (FriendChatsManager.cachedFriendChats) {
					FriendChatsManager.cachedFriendChats.remove(owner);
				}
			} else {
				refreshChannel();
			}
			if (!logout) {
				player.setCurrentFriendChatOwner(null);
				player.getPackets().sendGameMessage(
						"You have left the channel.");
				player.getPackets().sendFriendsChatChannel();
				player.disableLootShare();
			}
			if (clanWars != null) {
				clanWars.leave(player, false);
			}
		}
	}

	private void refreshChannel() {
		synchronized (this) {
			OutputStream stream = new OutputStream();
			stream.writeString(ownerDisplayName);
			String ownerName = Utilities.formatPlayerNameForDisplay(owner);
			stream.writeByte(getOwnerDisplayName().equals(ownerName) ? 0 : 1);
			if (!getOwnerDisplayName().equals(ownerName)) {
				stream.writeString(ownerName);
			}
			stream.writeLong(Utilities.stringToLong(getChannelName()));
			int kickOffset = stream.getOffset();
			stream.writeByte(0);
			stream.writeByte(getPlayers().size());
			for (Player player : getPlayers()) {
				String displayName = player.getDisplayName();
				String name = Utilities.formatPlayerNameForDisplay(player
						.getUsername());
				stream.writeString(displayName);
				stream.writeByte(displayName.equals(name) ? 0 : 1);
				if (!displayName.equals(name)) {
					stream.writeString(name);
				}
				stream.writeShort(1);
				int rank = getRank(player.getRights(), player.getUsername());
				stream.writeByte(rank);
				stream.writeString(GameConstants.SERVER_NAME);
			}
			dataBlock = new byte[stream.getOffset()];
			stream.setOffset(0);
			stream.getBytes(dataBlock, 0, dataBlock.length);
			for (Player player : players) {
				dataBlock[kickOffset] = (byte) (player.getUsername().equals(
						owner) ? 0 : getWhoCanKickOnChat());
				player.getPackets().sendFriendsChatChannel();
			}
		}
	}

	public void sendDiceMessage(Player player, String message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner)
					&& !settings.canTalk(player) && player.getRights() < 2) {
				player.getPackets()
						.sendGameMessage(
								"You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			for (Player p2 : players) {
				p2.getPackets().sendGameMessage(message);
			}
		}
	}

	public void sendMessage(Player player, String message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner)
					&& !settings.canTalk(player) && player.getRights() < 2) {
				player.getPackets()
						.sendGameMessage(
								"You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			String formatedName = Utilities.formatPlayerNameForDisplay(player
					.getUsername());
			String displayName = player.getDisplayName();
			int rights = player.getMessageIcon();
			for (Player p2 : players) {
				p2.getPackets().receiveFriendChatMessage(formatedName,
						displayName, rights, settings.getChatName(), message);
			}
		}
	}

	public void sendQuickMessage(Player player, QuickChatMessage message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner)
					&& !settings.canTalk(player) && player.getRights() < 2) {
				player.getPackets()
						.sendGameMessage(
								"You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			String formatedName = Utilities.formatPlayerNameForDisplay(player
					.getUsername());
			String displayName = player.getDisplayName();
			int rights = player.getMessageIcon();
			for (Player p2 : players) {
				p2.getPackets().receiveFriendChatQuickMessage(formatedName,
						displayName, rights, settings.getChatName(), message);
			}
		}
	}
}