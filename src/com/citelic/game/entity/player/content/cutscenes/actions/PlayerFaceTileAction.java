package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.map.tile.Tile;

public class PlayerFaceTileAction extends CutsceneAction {

	private int x, y;

	public PlayerFaceTileAction(int x, int y, int actionDelay) {
		super(-1, actionDelay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		player.setNextFaceTile(new Tile(scene.getBaseX() + x, scene.getBaseY()
				+ y, player.getZ()));
	}

}
