package com.citelic.game.entity.player.content.miscellaneous.distractions;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class WildyWyrmManager {

	/**
	 * The {@link WildyWyrmManager} instance.
	 */
	public static final WildyWyrmManager INSTANCE = new WildyWyrmManager();

	/**
	 * The wildy wyrm reference.
	 */
	public NPC WILDY_WYRM;

	/**
	 * The spawn locations.
	 */
	private static final Tile[] LOCATIONS = { new Tile(3296, 3887, 0),
			new Tile(3197, 3690, 0), new Tile(3238, 3623, 0),
			new Tile(3237, 3765, 0), new Tile(3305, 3927, 0),
			new Tile(3206, 3774, 0) };

	/**
	 * Initializes this {@link WildyWyrmManager}.
	 */
	public final void init() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (WILDY_WYRM == null || WILDY_WYRM.hasFinished())
					spawnWildyWyrm();
			}
		}, 0, 30, TimeUnit.MINUTES);
	}

	/**
	 * Spawn a wildywyrm.
	 */
	private final void spawnWildyWyrm() {
		if (WILDY_WYRM != null)
			WILDY_WYRM.finish();
		WILDY_WYRM = new NPC(2417,
				LOCATIONS[Utilities.random(LOCATIONS.length)], -1, true, true);
		Engine.sendWorldMessage(
				"<img=7>News: "
						+ "<col=ff0000>"
						+ "A new Wildywyrm has spawned in a random location in the Wilderness!"
						+ "</col>", false);
	}
}