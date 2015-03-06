package com.citelic.game.entity.player.content.actions.skills.construction;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class SitChair extends Action {

	private int chair;
	private Tile originalTile;
	private Tile chairTile;
	private boolean tped;

	public SitChair(Player player, int chair, GameObject object) {
		this.chair = chair;
		originalTile = new Tile(player);
		chairTile = object;
		Tile face = new Tile(player);
		if (object.getType() == 10) {
			if (object.getRotation() == 0) {
				face.moveLocation(0, -1, 0);
			} else if (object.getRotation() == 2) {
				face.moveLocation(0, 1, 0);
			}
		} else if (object.getType() == 11) {
			if (object.getRotation() == 1) {
				face.moveLocation(-1, 1, 0);
			} else if (object.getRotation() == 2) {
				face.moveLocation(1, 1, 0);
			}
		}
		player.setNextFaceTile(face);
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!tped) {
			player.setNextTile(chairTile);
			tped = true;
		}
		player.setNextAnimation(new Animation(
				HouseConstants.CHAIR_EMOTES[chair]));
		return 0;
	}

	@Override
	public boolean start(Player player) {
		setActionDelay(player, 1);
		return true;
	}

	@Override
	public void stop(final Player player) {
		player.lock(1);
		player.setNextTile(originalTile);
		player.setNextAnimation(new Animation(-1));
	}
}
