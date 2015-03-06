package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class Jadinko extends NPC {

    public Jadinko(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
    }

    @Override
    public void sendDeath(Entity source) {
	super.sendDeath(source);
	if (source instanceof Player) {
	    Player player = (Player) source;
	    player.setFavorPoints((getId() == 13820 ? 3 : getId() == 13821 ? 7 : 10) + player.getFavorPoints());
	}
    }
}
