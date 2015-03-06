package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class Werewolf extends NPC {

    private int realId;

    public Werewolf(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
        super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        realId = id;
    }

    public boolean hasWolfbane(Entity target) {
        if (target instanceof NPC)
            return false;
        return ((Player) target).getEquipment().getWeaponId() == 2952;
    }

    @Override
    public void processNPC() {
        if (isDead() || isCantInteract())
            return;
        if (isUnderCombat() && getId() == realId && Utilities.random(5) == 0) {
            final Entity target = getCombat().getTarget();
            if (!hasWolfbane(target)) {
                setNextAnimation(new Animation(6554));
                setCantInteract(true);
                EngineTaskManager.schedule(new EngineTask() {
                    @Override
                    public void run() {
                        Werewolf.this.setNextNPCTransformation(realId - 20);
                        Werewolf.this.setNextAnimation(new Animation(-1));
                        Werewolf.this.setCantInteract(false);
                        Werewolf.this.setTarget(target);
                    }
                }, 1);
                return;
            }
        }
        super.processNPC();
    }

    @Override
    public void reset() {
        setNPC(realId);
        super.reset();
    }

}
