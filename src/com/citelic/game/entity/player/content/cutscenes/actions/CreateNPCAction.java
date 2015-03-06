package com.citelic.game.entity.player.content.cutscenes.actions;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.map.tile.Tile;

public class CreateNPCAction extends CutsceneAction {

	private int id, x, y, plane;

	public CreateNPCAction(int cachedObjectIndex, int id, int x, int y,
			int plane, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.id = id;
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		if (cache[getCachedObjectIndex()] != null)
			scene.destroyCache(cache[getCachedObjectIndex()]);
		NPC npc = (NPC) (cache[getCachedObjectIndex()] = Engine.spawnNPC(id,
				new Tile(scene.getBaseX() + x, scene.getBaseY() + y, plane),
				-1, true, true));
		npc.setRandomWalk(false);
	}

}
