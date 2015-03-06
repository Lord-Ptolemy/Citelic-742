package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.impl.soulwars.Avatar;
import com.citelic.game.entity.npc.impl.soulwars.Barricade;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.PlayerType;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.Teams;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class GameTask extends TimerTask {

	/**
	 * The areas in the game that can be winned by a team.
	 */
	public static final Tile[][] AREAS = {
			{ new Tile(1837, 3213, 0), new Tile(1847, 3223, 0) },
			{ new Tile(1872, 3221, 0), new Tile(1901, 3242, 0) },
			{ new Tile(1928, 3240, 0), new Tile(1938, 3250, 0) }, };

	/**
	 * The array that holds the locations of the anti-barricade 'spots'.
	 */
	private static final Tile[] ANTI_CARRICADES = { new Tile(1959, 3239, 0),
			new Tile(1933, 3243, 0), new Tile(1842, 3220, 0),
			new Tile(1815, 3225, 0) };

	/**
	 * The names of the objects.
	 */
	private static final String[] OBJECT_NAMES = { "western graveyard",
			"soul obelisk", "eastern graveyard" };

	/**
	 * The areas in the game.
	 */
	private Teams[] areas = new Teams[3];

	/**
	 * The status of calling out the ghosts from their safezone.
	 */
	private int callOutGhosts = -1;

	/**
	 * The area values of the game.
	 */
	private int[] areaValues = new int[3];

	/**
	 * The arraylist holding the players.
	 */
	private ArrayList<Player> players = new ArrayList<Player>(500);

	/**
	 * The array holding the amount of times the avatars died.
	 */
	private int[] kills = new int[2],

	/**
	 * The array holding the amount of levels of the avatars.
	 */
	levels = { 100, 100 };

	/**
	 * The red barricades in the game.
	 */
	private ArrayList<Barricade> redBarricades = new ArrayList<Barricade>(5);

	/**
	 * The blue barricades in the game.
	 */
	private ArrayList<Barricade> blueBarricades = new ArrayList<Barricade>(5);

	/**
	 * The avatars of the two teams.
	 */
	private Avatar[] avatars = new Avatar[2];

	/**
	 * The ticks proceeded.
	 */
	private int ticks = 1;

	@Override
	public void run() {
		try {
			if (SoulWarsManager.MINUTES_BEFORE_NEXT_GAME.get() < 4)
				return;
			handleAreas();
			boolean showInterfaces = ++ticks % 3 == 0;
			if (showInterfaces)
				ticks = 1;
			boolean ghostTick = callOutGhosts != -1;
			for (Iterator<Player> it = players.iterator(); it.hasNext();) {
				Player player = it.next();
				if (player != null
						&& player.getTemporaryAttributtes().remove(
								"soulwars_kicked") != null)
					it.remove();
				else if (player != null
						&& player.getControllerManager().getController() instanceof GameController) {
					player.getControllerManager().sendInterfaces();
					if (showInterfaces)
						((GameController) player.getControllerManager()
								.getController()).decreaseActivity();
					if (ghostTick) {
						final Integer respawnIndex = (Integer) player
								.getTemporaryAttributtes().remove(
										"soul_wars_last_respawn_index");
						final Tile respawnTile = (Tile) player
								.getTemporaryAttributtes().remove(
										"soul_wars_last_respawn_loc");
						if (respawnIndex != null
								&& respawnIndex == callOutGhosts
								&& respawnTile != null
								&& respawnIndex != 1
								&& player.getAppearence().getTransformedNpcId() != -1) {
							final Tile base = new Tile(
									AREAS[respawnIndex][0].getX()
											+ ((AREAS[respawnIndex][1].getX() - AREAS[respawnIndex][0]
													.getX()) / 2),
									AREAS[respawnIndex][0].getY()
											+ ((AREAS[respawnIndex][1].getY() - AREAS[respawnIndex][0]
													.getY()) / 2), 0);
							player.setNextTile(base.transform(0,
									respawnIndex == 0 ? 2 : -2, 0));
							player.getAppearence().transformIntoNPC(-1);
							player.getPackets()
									.sendGameMessage(
											"The graveyard has became neutral again and will no longer give you immunity.");
						}
					}
				}
			}
			callOutGhosts = -1;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle the areas in the game.
	 */
	private void handleAreas() {
		int[][] playerAmounts = new int[3][2];
		for (Player player : players) {
			for (int index = 0; index < playerAmounts.length; index++) {
				if (player != null && player.getX() >= AREAS[index][0].getX()
						&& player.getY() >= AREAS[index][0].getY()
						&& player.getX() <= AREAS[index][1].getX()
						&& player.getY() <= AREAS[index][1].getY()
						&& player.getAppearence().getTransformedNpcId() == -1) {
					final Teams team = Teams.values()[player.getEquipment()
							.getCapeId() - SoulWarsManager.TEAM_CAPE_INDEX];
					playerAmounts[index][team.ordinal()]++;
					if (player.getControllerManager().getController() instanceof GameController)
						((GameController) player.getControllerManager()
								.getController()).setAreaIndex(index);
					break;
				} else if (player != null)
					if (player.getControllerManager().getController() instanceof GameController)
						((GameController) player.getControllerManager()
								.getController()).setAreaIndex(-1);
			}
		}
		for (int index = 0; index < playerAmounts.length; index++) {
			final Teams generalTeam = playerAmounts[index][0] > playerAmounts[index][1] ? Teams.RED
					: playerAmounts[index][0] < playerAmounts[index][1] ? Teams.BLUE
							: null;
			if (generalTeam != null) {
				if (areas[index] != null && areas[index].equals(generalTeam)) {
					if (areaValues[index] < 10) {
						areaValues[index]++;
						if (areaValues[index] == 10)
							sendGlobalMessage(index, true);
					}
				} else if (areas[index] != null
						&& areas[index]
								.equals(generalTeam.equals(Teams.RED) ? Teams.BLUE
										: Teams.RED)) {
					if (areaValues[index] == 10)
						sendGlobalMessage(index, false);
					if (--areaValues[index] <= 0)
						areas[index] = null;
				} else if (areaValues[index] < 10) {
					areas[index] = generalTeam;
					areaValues[index]++;
				}
			} else if ((playerAmounts[index][0] > 0 && playerAmounts[index][1] > 0)
					|| (areaValues[index] < 10) && areas[index] != null) {
				if (--areaValues[index] <= 0)
					areas[index] = null;
			}
		}
	}

	/**
	 * Send a global message to the players about object activitys.
	 * 
	 * @param index
	 *            The index.
	 * @param taken
	 *            If it has been taken or lost.
	 */
	private void sendGlobalMessage(int index, boolean taken) {
		if ((index == 0 || index == 2) && !taken)
			callOutGhosts = index;
		for (Iterator<Player> it = players.iterator(); it.hasNext();) {
			Player player = it.next();
			if (player != null)
				player.getPackets()
						.sendGameMessage(
								"The "
										+ (areas[index].equals(Teams.BLUE) ? "<col=337FB5>blue</col>"
												: "<col=F00004>red</col>")
										+ " team has "
										+ (taken ? "taken" : "lost")
										+ " control of the "
										+ OBJECT_NAMES[index] + ".");
			else
				it.remove();
		}
	}

	/**
	 * Start the game.
	 */
	public void start() {
		reset(); // Security
		for (int i = 0; i < 2; i++)
			(avatars[i] = new Avatar(SoulWarsManager.AVATAR_INDEX + i,
					i == 0 ? new Tile(1968, 3251, 0) : new Tile(1807, 3210, 0),
					-1, true)).setRandomWalk(true);
	}

	/**
	 * Reset the game.
	 */
	public void reset() {
		for (int i = 0; i < 2; i++)
			if (avatars[i] != null)
				avatars[i].finish();
		// avatars = new Avatar[2];
		ticks = 1;
		areas = new Teams[3];
		areaValues = new int[3];
		kills = new int[2];
		levels = new int[] { 100, 100 };
		for (Barricade barricade : redBarricades) {
			if (barricade != null)
				barricade.finish();
		}
		redBarricades.clear();
		for (Barricade barricade : blueBarricades) {
			if (barricade != null)
				barricade.finish();
		}
		blueBarricades.clear();
		players.clear();
		callOutGhosts = -1;
	}

	/**
	 * Get the players from the specific given team.
	 * 
	 * @param team
	 *            The given team.
	 * @return The players of the team.
	 */
	public ArrayList<Player> getPlayers(Teams team) {
		ArrayList<Player> members = new ArrayList<Player>(players.size());
		for (Player player : players) {
			if (player != null
					&& player.getEquipment().getCapeId() != -1
					&& Teams.values()[player.getEquipment().getCapeId()
							- SoulWarsManager.TEAM_CAPE_INDEX].equals(team))
				members.add(player);
		}
		return members;
	}

	/**
	 * Check if a player can move to the specific location.
	 * 
	 * @param tile
	 *            The location.
	 * @return {@code true} If so.
	 */
	public boolean containsBarricade(Tile tile) {
		for (Barricade barricade : redBarricades)
			if (barricade != null && barricade.matches(tile))
				return false;
		for (Barricade barricade : blueBarricades)
			if (barricade != null && barricade.matches(tile))
				return false;
		return true;
	}

	/**
	 * Get the players of this game.
	 * 
	 * @return The {@link players}.
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * Get the amount of time the team avatar died.
	 * 
	 * @param team
	 *            The team of the avatar.
	 * @return The died amount.
	 */
	public int getAvatarDies(Teams team) {
		return kills[team.ordinal()];
	}

	/**
	 * Increases the amount of time the team avatar died.
	 * 
	 * @param team
	 *            The team of the avatar.
	 */
	public void increaseAvatarDies(Teams team) {
		kills[team.ordinal()]++;
	}

	/**
	 * Get the amount of time the team avatar died.
	 * 
	 * @param team
	 *            The team of the avatar.
	 * @return The died amount.
	 */
	public int getAvatarSlayerLevel(Teams team) {
		return levels[team.ordinal()];
	}

	/**
	 * Increases the required slayer level of the avatar.
	 * 
	 * @param team
	 *            The team of the avatar.
	 * @param amount
	 *            The amount of increasement.
	 */
	public void increaseAvatarLevel(Teams team, int amount) {
		levels[team.ordinal()] = levels[team.ordinal()] + amount > 100 ? 100
				: levels[team.ordinal()] + amount < 0 ? 0 : levels[team
						.ordinal()] + amount;
	}

	/**
	 * Get the respawn location for the player.
	 * 
	 * @param player
	 *            The player.
	 * @param team
	 *            The team of the player.
	 * @return The respawn location.
	 */
	public Tile getRespawnLocation(Player player, int team) {
		Tile respawnLocation = Engine.soulWars.calculateRandomLocation(
				Teams.values()[team], PlayerType.IN_GAME);
		Integer respawnIndex = new Integer(-1);
		for (int index = 0; index < 3; index++) {
			if (index == 1 || areas[index] == null
					|| !areas[index].equals(Teams.values()[team])
					|| areaValues[index] < 10)
				continue;
			final Tile base = new Tile(
					AREAS[index][0].getX()
							+ ((AREAS[index][1].getX() - AREAS[index][0].getX()) / 2),
					AREAS[index][0].getY()
							+ ((AREAS[index][1].getY() - AREAS[index][0].getY()) / 2),
					0);
			if (Utilities.getDistance(player, respawnLocation) > Utilities
					.getDistance(player, base)) {
				respawnLocation = new Tile(base, 1);
				respawnIndex = index;
			}
		}
		player.getTemporaryAttributtes().put("soul_wars_last_respawn_index",
				respawnIndex);
		player.getTemporaryAttributtes().put("soul_wars_last_respawn_loc",
				respawnLocation);
		return respawnLocation;
	}

	/**
	 * Get the areas that can be controlled by teams.
	 * 
	 * @return The {@link areas}.
	 */
	public Teams[] getTeamAreas() {
		return areas;
	}

	/**
	 * Get the area values of the teams that are controlling the areas.
	 * 
	 * @return The {@link areaValues}.
	 */
	public int[] getTeamAreaValues() {
		return areaValues;
	}

	/**
	 * Increases an avatar kill.
	 * 
	 * @param id
	 *            The id of the avatar.
	 */
	public void increaseKill(int id) {
		kills[id - SoulWarsManager.AVATAR_INDEX]++;
		levels[id - SoulWarsManager.AVATAR_INDEX] = 100;
	}

	/**
	 * Get the avatars.
	 * 
	 * @return The {@link avatars}.
	 */
	public Avatar[] getAvatars() {
		return avatars;
	}

	/**
	 * Remove a barricade.
	 * 
	 * @param barricade
	 *            The barricade.
	 */
	public void removeBarricade(Barricade barricade, int team) {
		if (team == 0)
			redBarricades.remove(barricade);
		else
			blueBarricades.remove(barricade);
	}

	/**
	 * Place a barricade.
	 * 
	 * @param player
	 *            The player.
	 * @param team
	 *            The team.
	 * @return If the barricade is placed correctly.
	 */
	public boolean placeBarricade(Player player, int team) {
		if (team == 0 ? redBarricades.size() >= 10
				: blueBarricades.size() >= 10) {
			player.getPackets().sendGameMessage(
					"Your team has placed enough barricades already.");
			return false;
		}
		for (final Tile t1 : ANTI_CARRICADES) {
			if (player.matches(t1)) {
				player.getPackets().sendGameMessage(
						"You cannot place a barricade here.");
				return false;
			}
		}
		player.getInventory().deleteItem(4053, 1);
		if (team == 0)
			redBarricades.add(new Barricade(player, team));
		else
			blueBarricades.add(new Barricade(player, team));
		return true;
	}
}