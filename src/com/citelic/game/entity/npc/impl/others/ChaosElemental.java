package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class ChaosElemental extends NPC {

    public ChaosElemental(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
        super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
    }

    @Override
    public void processNPC() {
        super.processNPC();
        Entity target = getCombat().getTarget();
        if (target != null && Utilities.isOnRange(target.getX(), target.getY(), target.getSize(), getX(), getY(), getSize(), 4) && Utilities.random(15) == 0) {
            sendTeleport(target);
        }
    }

    private void sendTeleport(Entity entity) {
        int entitySize = entity.getSize();
        for (int c = 0; c < 10; c++) {
            int dir = Utilities.random(Utilities.DIRECTION_DELTA_X.length);
            if (Engine.checkWalkStep(entity.getZ(), entity.getX(), entity.getY(), dir, entitySize)) {
                entity.setNextTile(new Tile(getX() + Utilities.DIRECTION_DELTA_X[dir], getY() + Utilities.DIRECTION_DELTA_Y[dir], getZ()));
                break;
            }
        }
    }
}
