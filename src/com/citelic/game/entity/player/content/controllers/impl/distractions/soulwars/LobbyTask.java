package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.Teams;

public class LobbyTask extends TimerTask {

	/**
	 * The arraylist holding the players.
	 */
	private ArrayList<Player> players = new ArrayList<Player>(500);

	@Override
	public void run() {
		try {
			for (Iterator<Player> it = players.iterator(); it.hasNext();) {
				Player player = it.next();
				if (player != null
						&& player.getControllerManager().getController() instanceof LobbyController)
					player.getControllerManager().sendInterfaces();
				else
					it.remove();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
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
			if (player != null) {
				final int cape = player.getEquipment().getCapeId()
						- SoulWarsManager.TEAM_CAPE_INDEX;
				if (cape < 0 || cape > 1)
					continue;
				if (Teams.values()[cape].equals(team))
					members.add(player);
			}
		}
		return members;
	}

	/**
	 * Get the players in the lobby.
	 * 
	 * @return The {@link players}.
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
}