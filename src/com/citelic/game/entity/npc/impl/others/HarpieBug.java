package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.Hit;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class HarpieBug extends NPC {

    public HarpieBug(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
    }

    @Override
    public void handleIngoingHit(Hit hit) {
	if (hit.getSource() instanceof Player) {
	    Player player = (Player) hit.getSource();
	    if (player.getEquipment().getShieldId() != 7053)
		hit.setDamage(0);
	}
	super.handleIngoingHit(hit);
    }

}
