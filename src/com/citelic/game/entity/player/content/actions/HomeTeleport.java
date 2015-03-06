package com.citelic.game.entity.player.content.actions;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class HomeTeleport extends Action {

	private final int HOME_ANIMATION = 16385, HOME_GRAPHIC = 3017;
	private int currentTime;
	private Tile tile;

	public static final Tile LUMBRIDGE_LODE_STONE = new Tile(3233, 3221, 0),
			BURTHORPE_LODE_STONE = new Tile(2899, 3544, 0),
			LUNAR_ISLE_LODE_STONE = new Tile(2085, 3914, 0),
			BANDIT_CAMP_LODE_STONE = new Tile(3214, 2954, 0),
			TAVERLY_LODE_STONE = new Tile(2878, 3442, 0),
			ALKARID_LODE_STONE = new Tile(3297, 3184, 0),
			VARROCK_LODE_STONE = new Tile(3214, 3376, 0),
			EDGEVILLE_LODE_STONE = new Tile(3067, 3505, 0),
			FALADOR_LODE_STONE = new Tile(2967, 3403, 0),
			PORT_SARIM_LODE_STONE = new Tile(3011, 3215, 0),
			DRAYNOR_VILLAGE_LODE_STONE = new Tile(3105, 3298, 0),
			ARDOUGNE_LODE_STONE = new Tile(2634, 3348, 0),
			CATHERBAY_LODE_STONE = new Tile(2831, 3451, 0),
			YANILLE_LODE_STONE = new Tile(2529, 3094, 0),
			SEERS_VILLAGE_LODE_STONE = new Tile(2689, 3482, 0);

	public HomeTeleport(Tile tile) {
		this.tile = tile;
	}

	@Override
	public boolean process(Player player) {
		if (player.getAttackedByDelay() + 10000 > Utilities.currentTimeMillis()) {
			player.getPackets()
					.sendGameMessage(
							"You can't home teleport until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (currentTime++ == 0) {
			player.setNextAnimation(new Animation(HOME_ANIMATION));
			player.setNextGraphics(new Graphics(HOME_GRAPHIC));
		} else if (currentTime == 18) {
			player.lock();
			player.setNextTile(tile.transform(0, 1, 0));
			player.getControllerManager().magicTeleported(Magic.MAGIC_TELEPORT);
			if (player.getControllerManager().getController() == null)
				Magic.teleControlersCheck(player, tile);
			player.setNextFaceTile(new Tile(tile.getX(), tile.getY(), tile
					.getZ()));
			player.setDirection(6);
		} else if (currentTime == 19) {
			player.setNextGraphics(new Graphics(HOME_GRAPHIC + 1));
			player.setNextAnimation(new Animation(HOME_ANIMATION + 1));
		} else if (currentTime == 24) {
			player.setNextTile(tile);
			player.setNextAnimation(new Animation(16393));
			player.unlock();
		} else if (currentTime == 25)
			return -1;
		return 0;
	}

	@Override
	public boolean start(final Player player) {
		if (!player.getControllerManager().processMagicTeleport(tile))
			return false;
		return process(player);
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(-1));
		player.setNextGraphics(new Graphics(-1));
	}
}