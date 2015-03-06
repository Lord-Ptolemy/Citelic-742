package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.Hit;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.combat.PlayerCombat;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class Kurask extends NPC {

    public Kurask(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
    }

    @Override
    public void handleIngoingHit(Hit hit) {
	if (hit.getSource() instanceof Player) {
	    Player player = (Player) hit.getSource();
	    if (!(player.getEquipment().getWeaponId() == 13290 || player.getEquipment().getWeaponId() == 4158) && !(PlayerCombat.isRanging(player) == 2 && (player.getEquipment().getAmmoId() == 13280 || player.getEquipment().getAmmoId() == 4160)))
		hit.setDamage(0);
	}
	super.handleIngoingHit(hit);
    }
}
