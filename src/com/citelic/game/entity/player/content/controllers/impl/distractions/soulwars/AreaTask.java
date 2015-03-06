package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;

public class AreaTask extends TimerTask {

	/**
	 * The arraylist holding the players.
	 */
	private ArrayList<Player> players = new ArrayList<Player>(500);

	@Override
	public void run() {
		try {
			if (Engine.soulWars.decrementMinute()) {
				for (Iterator<Player> it = players.iterator(); it.hasNext();) {
					Player player = it.next();
					if (player != null
							&& !player.hasFinished()
							&& player.getControllerManager().getController() instanceof AreaController)
						player.getControllerManager().sendInterfaces();
					else
						it.remove();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the arraylist holding the players.
	 * 
	 * @return The {@link players}.
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
}