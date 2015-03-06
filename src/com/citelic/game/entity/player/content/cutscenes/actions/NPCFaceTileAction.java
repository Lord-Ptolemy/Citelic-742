package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.map.tile.Tile;

public class NPCFaceTileAction extends CutsceneAction {

	private int x, y;

	public NPCFaceTileAction(int cachedObjectIndex, int x, int y,
			int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextFaceTile(new Tile(scene.getBaseX() + x,
				scene.getBaseY() + y, npc.getZ()));
	}

}
