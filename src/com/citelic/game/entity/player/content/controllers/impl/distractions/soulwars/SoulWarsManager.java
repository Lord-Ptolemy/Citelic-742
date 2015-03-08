package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.citelic.GameConstants;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.consumables.Foods;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public final class SoulWarsManager {

	public enum Teams {
		RED(), BLUE();
	}

	public enum PlayerType {

		/**
		 * The outside part of the lobby type.
		 */
		OUTSIDE_LOBBY(new Tile(1884, 3166, 0), new Tile(1888, 3174, 0),
				new Tile(1892, 3166, 0), new Tile(1896, 3174, 0)),

		/**
		 * The lobby type.
		 */
		INSIDE_LOBBY(new Tile(1870, 3158, 0), new Tile(1879, 3166, 0),
				new Tile(1900, 3157, 0), new Tile(1909, 3166, 0)),

		/**
		 * The in-game type.
		 */
		IN_GAME(new Tile(1816, 3220, 0), new Tile(1823, 3230, 0), new Tile(
				1951, 3234, 0), new Tile(1958, 3244, 0));

		/**
		 * The a-location.
		 */
		private final Tile LOCATION_A;

		/**
		 * The b-location.
		 */
		private final Tile LOCATION_B;

		/**
		 * The c-location.
		 */
		private final Tile LOCATION_C;

		/**
		 * The d-location.
		 */
		private final Tile LOCATION_D;

		/**
		 * Constructs a new {@code SoulWarsManager}.
		 * 
		 * @param a
		 *            The a-location.
		 * @param b
		 *            The b-location.
		 * @param c
		 *            The c-location.
		 * @param d
		 *            The d-location.
		 */
		private PlayerType(Tile a, Tile b, Tile c, Tile d) {
			this.LOCATION_A = a;
			this.LOCATION_B = b;
			this.LOCATION_C = c;
			this.LOCATION_D = d;
		}

		/**
		 * Get the a-location of this {@link PlayerType}.
		 * 
		 * @return The {@link LOCATION_A}.
		 */
		public final Tile getLocationA() {
			return LOCATION_A;
		}

		/**
		 * Get the b-location of this {@link PlayerType}.
		 * 
		 * @return The {@link LOCATION_B}.
		 */
		public final Tile getLocationB() {
			return LOCATION_B;
		}

		/**
		 * Get the c-location of this {@link PlayerType}.
		 * 
		 * @return The {@link LOCATION_C}.
		 */
		public final Tile getLocationC() {
			return LOCATION_C;
		}

		/**
		 * Get the d-location of this {@link PlayerType}.
		 * 
		 * @return The {@link LOCATION_D}.
		 */
		public final Tile getLocationD() {
			return LOCATION_D;
		}
	}

	/**
	 * The minutes before the next game.
	 */
	public static AtomicInteger MINUTES_BEFORE_NEXT_GAME = new AtomicInteger(3);

	/**
	 * The zeal modifier.
	 */
	public static int ZEAL_MODIFIER = 1;

	/**
	 * The bandage id.
	 */
	public final static int BANDAGE_ID = 14648,

	/**
	 * The barricade id.
	 */
	BARRICADE_ID = 14649,

	/**
	 * The explosive potion id.
	 */
	EXPLOSIVE_POTION_ID = 14650,

	/**
	 * The teamcape index.
	 */
	TEAM_CAPE_INDEX = 14641,

	/**
	 * The soul fragment id.
	 */
	SOUL_FRAGMENT = 14646,

	/**
	 * The bones id.
	 */

	BONES = 3187,

	/**
	 * The required amount of members for each team before the game can start.
	 */
	REQUIRED_TEAM_MEMBERS = 5,

	/**
	 * The id of the jelly slayer monster.
	 */
	JELLY = 8599,

	/**
	 * The id of the pyrefriend slayer monster.
	 */
	PYREFRIEND = 8598,

	/**
	 * The id of the ghost the player is transformed into.
	 */
	GHOST = 8623,

	/**
	 * The avatar index.
	 */
	AVATAR_INDEX = 8596;

	/**
	 * The map that holds the timertasks of each player in the area.
	 */
	private final HashMap<PlayerType, TimerTask> tasks = new HashMap<PlayerType, TimerTask>(
			PlayerType.values().length);

	/**
	 * Start (initializes) this {@link SoulWarsManager}.
	 */
	public void start() {
		startTask(PlayerType.OUTSIDE_LOBBY, new AreaTask());
		startTask(PlayerType.INSIDE_LOBBY, new LobbyTask());
		startTask(PlayerType.IN_GAME, new GameTask());
	}

	/**
	 * Pass a barrier in the game.
	 * 
	 * @param currentType
	 *            The barrier type.
	 * @param player
	 *            The player that passes the barrier.
	 * @param object
	 *            The barrier.
	 */
	public void passBarrier(PlayerType currentType, Player player,
			GameObject object) {
		if (currentType.equals(PlayerType.OUTSIDE_LOBBY)
				&& !canEnterLobby(player))
			return;
		switch (object.getId()) {
		case 42015:
		case 42018:
			int id = player.getEquipment().getCapeId();
			id -= TEAM_CAPE_INDEX;
			if (id < 0 || id > 1)
				return;
			final Teams team = Teams.values()[id];
			if ((team.equals(Teams.RED) && object.getId() == 42015)
					|| (team.equals(Teams.BLUE) && object.getId() == 42018)
					|| player.getPlayerAppearances().getTransformedNpcId() != -1)
				return;
			int x = player.getX() + object.getX() - player.getX();
			if (object.getId() == 42015 ? (object.getX() >= player.getX())
					: (object.getX() <= player.getX()))
				x = object.getX() + (object.getId() == 42015 ? 1 : -1);
			player.addWalkSteps(x, object.getY(), -1, false);
			player.lock(1);
			break;
		case 42019:
		case 42020:
			if (player.getPlayerAppearances().getTransformedNpcId() != -1
					|| (object.getId() == 42020 && object.getY() <= player
							.getY())
					|| (object.getId() == 42019 && object.getY() >= player
							.getY()))
				return;
			player.addWalkSteps(object.getX(), object.getY(), -1, false);
			player.lock(1);
			break;
		case 42029:
		case 42030:
			x = player.getX() + object.getX() - player.getX();
			if (object.getId() == 42029 ? (object.getX() >= player.getX())
					: (object.getX() <= player.getX()))
				x = object.getX() + (object.getId() == 42029 ? 1 : -1);
			player.addWalkSteps(x, object.getY(), -1, false);
			player.lock(1);
			break;
		case 42031:
			final Teams nextTeam = nextJoiningTeam();
			player.setNextTile(calculateRandomLocation(nextTeam,
					PlayerType.INSIDE_LOBBY));
			enterLobby(player, nextTeam);
			return;
		}
		if (player.getControllerManager().getController() != null
				&& player.getControllerManager().getController() instanceof GameController) {
			Boolean bool = (Boolean) player.getTemporaryAttributtes().get(
					"sw_safe_zone");
			if (bool != null)
				player.getTemporaryAttributtes().put("sw_safe_zone", !bool);
		}
		switch (currentType) {
		case OUTSIDE_LOBBY:
			enterLobby(player, object.getId() == 42029 ? Teams.BLUE : Teams.RED);
			break;
		case INSIDE_LOBBY:
			((LobbyTask) tasks.get(PlayerType.INSIDE_LOBBY)).getPlayers()
					.remove(player);
			resetPlayer(player, PlayerType.OUTSIDE_LOBBY, false);
			break;
		default:
			break;
		}
	}

	/**
	 * Check if the player can enter the lobby.
	 * 
	 * @param player
	 *            The player.
	 * @return {@code true} If entering is allowed.
	 */
	private boolean canEnterLobby(Player player) {
		if (player.getEquipment().getCapeId() != -1) {
			player.getPackets()
					.sendGameMessage(
							"You cannot join the waiting lobby, remove your cape from your equipment.");
			return false;
		}
		if (player.getFamiliar() != null) {
			player.getPackets().sendGameMessage(
					"You're not allowed to enter with that familiar.");
			return false;
		}
		for (final Pouches pouch : Pouches.values()) {
			if (pouch == null)
				continue;
			if (player.getInventory().containsItem(pouch.getPouchId(), 1)) {
				player.getPackets()
						.sendGameMessage(
								"You cannot enter with having pouches in your inventory!");
				return false;
			}
		}
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			if (Foods.forId(item.getId()) != null) {
				player.getPackets().sendGameMessage(
						"You cannot bring food into this arena.");
				return false;
			}
			final ItemDefinitions defs = ItemDefinitions
					.getItemDefinitions(item.getId());
			if (defs != null) {
				String name = defs.getName();
				if (name != null) {
					for (final String disabled : GameConstants.FORBIDDEN_SOUL_WARS_ITEMS) {
						if (name.toLowerCase().contains(disabled.toLowerCase())) {
							player.getPackets().sendGameMessage(
									"You cannot bring your "
											+ name.toLowerCase()
											+ " into this area, bank it.");
							return false;
						}
					}
				}
			}
		}
		for (final Item item : player.getEquipment().getItems().getItems()) {
			if (item != null) {
				final ItemDefinitions defs = ItemDefinitions
						.getItemDefinitions(item.getId());
				if (defs != null) {
					String name = ItemDefinitions.getItemDefinitions(
							item.getId()).getName();
					if (name != null) {
						for (final String disabled : GameConstants.FORBIDDEN_SOUL_WARS_ITEMS) {
							if (name.toLowerCase().contains(
									disabled.toLowerCase())) {
								player.getPackets()
										.sendGameMessage(
												"You cannot bring your "
														+ name.toLowerCase()
														+ " into this area, take it off your equipment and bank it.");
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param player
	 * @param team
	 */
	private void enterLobby(Player player, Teams team) {
		player.getControllerManager().startController("LobbyController");
		player.getEquipment()
				.getItems()
				.set(Equipment.SLOT_CAPE,
						new Item(TEAM_CAPE_INDEX + team.ordinal()));
		player.getEquipment().refresh(Equipment.SLOT_CAPE);
		player.getAppearence().generateAppearenceData();
		player.getPackets().sendGameMessage(
				"You join the " + team.toString().toLowerCase() + " team.");
	}

	/**
	 * Get the next team to join.
	 * 
	 * @return The team with the most players will not be returned.
	 */
	private Teams nextJoiningTeam() {
		final int minutes = MINUTES_BEFORE_NEXT_GAME.get();
		final GameTask game = (GameTask) tasks.get(PlayerType.IN_GAME);
		final LobbyTask lobby = (LobbyTask) tasks.get(PlayerType.INSIDE_LOBBY);
		if (minutes < 4) {
			if (lobby.getPlayers(Teams.RED).size() > lobby.getPlayers(
					Teams.BLUE).size())
				return Teams.BLUE;
			else if (lobby.getPlayers(Teams.RED).size() < lobby.getPlayers(
					Teams.BLUE).size())
				return Teams.RED;
			else
				return Teams.values()[Utilities.getRandom(1)];
		} else {
			int[] totalSizes = new int[2];
			for (int index = 0; index < 2; index++)
				totalSizes[index] = game.getPlayers(Teams.values()[index])
						.size()
						+ lobby.getPlayers(Teams.values()[index]).size();
			if (totalSizes[Teams.RED.ordinal()] > totalSizes[Teams.BLUE
					.ordinal()])
				return Teams.BLUE;
			else if (totalSizes[Teams.RED.ordinal()] < totalSizes[Teams.BLUE
					.ordinal()])
				return Teams.RED;
			else
				return Teams.values()[Utilities.getRandom(1)];
		}
	}

	/**
	 * Reset a player.
	 * 
	 * @param player
	 *            The player.
	 * @param type
	 *            The type.
	 * @param logout
	 *            If a player has logged out.
	 */
	public void resetPlayer(Player player, PlayerType type, boolean logout) {
		int id = player.getEquipment().getCapeId();
		id -= TEAM_CAPE_INDEX;
		if (id < 0 || id > 1) // Safety reasons.
			return;
		Teams team = Teams.values()[id];
		if (!logout) {
			player.getControllerManager().startController("AreaController");
		}
		player.getEquipment().deleteItem(TEAM_CAPE_INDEX + id, 1);
		player.getEquipment().refresh(Equipment.SLOT_CAPE);
		player.getAppearence().generateAppearenceData();
		player.getInventory().deleteItem(SOUL_FRAGMENT, Integer.MAX_VALUE);
		player.getInventory().deleteItem(BANDAGE_ID, 28);
		player.getInventory().deleteItem(BONES, 28);
		player.getInventory().deleteItem(EXPLOSIVE_POTION_ID, 28);
		player.getInventory().deleteItem(4053, 28);
		player.getInventory().deleteItem(14644, 28);
		player.getInventory().deleteItem(BARRICADE_ID, 28);
		player.getAppearence().transformIntoNPC(-1);
		if (type.equals(PlayerType.IN_GAME)) {
			player.setCanPvp(false);
			player.getPrayer().reset();
			player.getPoison().reset();
			player.resetReceivedDamage();
			player.setHitpoints(player.getMaxHitpoints());
			player.setRunEnergy(100);
			player.unlock(); // safety reasons
		}
		if (!type.equals(PlayerType.OUTSIDE_LOBBY)) {
			Tile random = calculateRandomLocation(team,
					PlayerType.OUTSIDE_LOBBY);
			if (!logout)
				player.setNextTile(random);
			else
				player.setLocation(random);
		}
	}

	/**
	 * Calculate and returns a random location.
	 * 
	 * @param team
	 *            The team of the player.
	 * @param type
	 *            The type, required to know the area the location is within.
	 * @return The calculated random location.
	 */
	public Tile calculateRandomLocation(Teams team, PlayerType type) {
		final Tile A = team.equals(Teams.BLUE) ? type.getLocationA() : type
				.getLocationC();
		final Tile B = team.equals(Teams.BLUE) ? type.getLocationB() : type
				.getLocationD();
		ArrayList<Tile> possibleLocations = new ArrayList<Tile>();
		for (int x = A.getX(); x <= B.getX(); x++) {
			for (int y = A.getY(); y <= B.getY(); y++) {
				if (Engine.canMoveNPC(0, x, y, 1)) {
					possibleLocations.add(new Tile(x, y, 0));
				}
			}
		}
		return possibleLocations
				.get(Utilities.random(possibleLocations.size()));
	}

	/**
	 * Decrements a minute.
	 * 
	 * @return If the minute has been succesfully decremented.
	 */
	public boolean decrementMinute() {
		if (tasks.size() < 3
				|| (MINUTES_BEFORE_NEXT_GAME.get() <= 3 && (((LobbyTask) tasks
						.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.BLUE)
						.size() < REQUIRED_TEAM_MEMBERS || ((LobbyTask) tasks
						.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.RED)
						.size() < REQUIRED_TEAM_MEMBERS)))
			return false;
		if (MINUTES_BEFORE_NEXT_GAME.get() > 3
				&& (((GameTask) tasks.get(PlayerType.IN_GAME)).getPlayers(
						Teams.BLUE).size() < 1 || ((GameTask) tasks
						.get(PlayerType.IN_GAME)).getPlayers(Teams.RED).size() < 1)) {
			endGame();
			MINUTES_BEFORE_NEXT_GAME.set(3);
			return false;
		}
		int decrement = MINUTES_BEFORE_NEXT_GAME.decrementAndGet();
		if (decrement >= 3 + 10)
			sendPlayers(false);
		if (decrement == 3)
			endGame();
		if (decrement == 0) {
			sendPlayers(true);
			MINUTES_BEFORE_NEXT_GAME.set(23);
		}
		return true;
	}

	/**
	 * Ends the current game.
	 */
	private void endGame() {
		((GameTask) tasks.get(PlayerType.IN_GAME)).getAvatars()[0]
				.resetReceivedDamage();
		((GameTask) tasks.get(PlayerType.IN_GAME)).getAvatars()[1]
				.resetReceivedDamage();
		final int blue = ((GameTask) tasks.get(PlayerType.IN_GAME))
				.getAvatarDies(Teams.BLUE), red = ((GameTask) tasks
				.get(PlayerType.IN_GAME)).getAvatarDies(Teams.RED);
		final Teams winningTeam = red > blue ? Teams.BLUE
				: blue > red ? Teams.RED : null;
		final String name = winningTeam == null ? "nigger" : winningTeam
				.equals(Teams.BLUE) ? "<col=337FB5>blue</col>"
				: "<col=F00004>red</col>";
		for (Iterator<Player> it = ((GameTask) tasks.get(PlayerType.IN_GAME))
				.getPlayers().iterator(); it.hasNext();) {
			Player player = it.next();
			it.remove();
			if (player == null || player.hasFinished())
				continue;
			int id = player.getEquipment().getCapeId();
			id -= TEAM_CAPE_INDEX;
			if (id < 0 || id > 1) // Safety reasons.
				return;
			final Teams team = Teams.values()[id];
			resetPlayer(player, PlayerType.IN_GAME, false);
			final int zeals = (winningTeam == null ? 3 : winningTeam
					.equals(team) ? 4 : 2) * ZEAL_MODIFIER;
			final String message = winningTeam == null ? "The game was a draw, you received "
					+ zeals + " zeals for parcitipating."
					: "The "
							+ name
							+ " team was victorious! You received "
							+ (winningTeam.equals(team) ? zeals
									+ " zeals for winning!" : zeals
									+ " zeals for losing,");
			player.increaseZeals(zeals);
			player.getDialogueManager().startDialogue("SimpleNPCMessage",
					team.equals(Teams.RED) ? 8528 : 8526, message);
			player.getPackets().sendGameMessage(message);
			;
		}
		((GameTask) tasks.get(PlayerType.IN_GAME)).reset();
	}

	/**
	 * Creates the game with the players in the lobby.
	 * 
	 * @param create
	 *            If the game is created.
	 */
	private void sendPlayers(boolean create) {
		if (create)
			((GameTask) tasks.get(PlayerType.IN_GAME)).start();
		final ArrayList<Player> blue = ((LobbyTask) tasks
				.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.BLUE), red = ((LobbyTask) tasks
				.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.RED);
		if (GameConstants.DEBUG) {
			for (Player player : blue) {
				addPlayerToGame(player, Teams.BLUE);
			}
			blue.clear();
			for (Player player : red) {
				addPlayerToGame(player, Teams.RED);
			}
			red.clear();
		}
		int size = 0;
		if (create) {
			size = blue.size() > red.size() ? red.size() : blue.size();
			if (size < 1)
				return;
			for (int i = 0; i < 2; i++) {
				int index = 0;
				for (Iterator<Player> it = (i == 0 ? red.iterator() : blue
						.iterator()); it.hasNext();) {
					if (index++ >= size) {
						break;
					}
					Player player = it.next();
					if (player != null && !player.hasFinished()
							&& !player.isLocked())
						addPlayerToGame(player, Teams.values()[i]);
					it.remove();
				}
			}
		} else {
			final ArrayList<Player> gameBlue = ((GameTask) tasks
					.get(PlayerType.IN_GAME)).getPlayers(Teams.BLUE), gameRed = ((GameTask) tasks
					.get(PlayerType.IN_GAME)).getPlayers(Teams.RED), lobbyBlue = ((LobbyTask) tasks
					.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.BLUE), lobbyRed = ((LobbyTask) tasks
					.get(PlayerType.INSIDE_LOBBY)).getPlayers(Teams.RED);
			size = gameBlue.size() > gameRed.size() ? gameBlue.size()
					- gameRed.size() : gameRed.size() - gameBlue.size();
			final int lobbySize = lobbyBlue.size() > lobbyRed.size() ? lobbyRed
					.size() : lobbyBlue.size();
			final int[] takeOutEachTeam = new int[2];
			takeOutEachTeam[0] = lobbySize
					+ (gameBlue.size() > gameRed.size() ? size + lobbySize > lobbyRed
							.size() ? lobbyRed.size() : size + lobbySize : 0);
			takeOutEachTeam[1] = lobbySize
					+ (gameRed.size() > gameBlue.size() ? size + lobbySize > lobbyBlue
							.size() ? lobbyBlue.size() : size + lobbySize : 0);
			if (takeOutEachTeam[0] == 0 && takeOutEachTeam[1] == 0
					&& gameRed.size() > 0)
				takeOutEachTeam[0] = gameRed.size();
			if (takeOutEachTeam[1] == 0 && takeOutEachTeam[0] == 0
					&& gameBlue.size() > 0)
				takeOutEachTeam[1] = gameBlue.size();
			for (int index = 0; index < 2; index++) {
				final ArrayList<Player> players = index == 0 ? lobbyRed
						: lobbyBlue;
				int playerIndex = 0;
				for (Iterator<Player> it = players.iterator(); it.hasNext();) {
					if (playerIndex++ == takeOutEachTeam[index])
						break;
					Player player = it.next();
					if (player != null)
						addPlayerToGame(player, Teams.values()[index]);
					it.remove();
				}
			}
		}
		for (Iterator<Player> it = ((LobbyTask) tasks
				.get(PlayerType.INSIDE_LOBBY)).getPlayers().iterator(); it
				.hasNext();) {
			Player player = it.next();
			if (player != null)
				player.getPackets()
						.sendGameMessage(
								"You now have a higher priority to enter a game of Soul Wars.");
			else
				it.remove();
		}
	}

	/**
	 * Add a player to the actual game.
	 * 
	 * @param player
	 *            The player to add.
	 * @param team
	 *            The team of the player.
	 */
	private void addPlayerToGame(Player player, Teams team) {
		GameTask task = (GameTask) tasks.get(PlayerType.IN_GAME);
		if (task.getPlayers().contains(player)
				|| player.getControllerManager().getController() instanceof GameController)
			return;
		player.getControllerManager().startController("GameController",
				team.ordinal());
		task.getPlayers().add(player);
	}

	/**
	 * Start a task.
	 * 
	 * @param type
	 *            The type of the task.
	 * @param task
	 *            The task to start.
	 */
	private void startTask(PlayerType type, TimerTask task) {
		tasks.put(type, task);
		CoresManager.fastExecutor.schedule(
				task,
				0,
				1000 * (type.equals(PlayerType.OUTSIDE_LOBBY) ? 60 : type
						.equals(PlayerType.IN_GAME) ? 2 : 5));
	}

	/**
	 * Get the tasks running.
	 * 
	 * @return The {@link tasks}.
	 */
	public HashMap<PlayerType, TimerTask> getTasks() {
		return tasks;
	}
}