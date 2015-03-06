package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class MutatedZygomites extends ConditionalDeath {

    boolean lvl74;

    public MutatedZygomites(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
	super(7421, null, false, id, tile, mapAreaNameHash, true);
	this.lvl74 = id == 3344;
    }

    @Override
    public void processNPC() {
	super.processNPC();
	if (!getCombat().process())
	    resetNPC();
    }

    @Override
    public void sendDeath(final Entity source) {
	super.sendDeath(source);
	if (getHitpoints() != 1)
	    resetNPC();
    }

    private void resetNPC() {
	setNPC(lvl74 ? 3344 : 3345);
	setNextNPCTransformation(lvl74 ? 3344 : 3345);
    }

    public static void transform(final Player player, final NPC npc) {
	player.setNextAnimation(new Animation(2988));
	npc.setNextNPCTransformation(npc.getId() + 2);
	npc.setNPC(npc.getId() + 2);
	npc.setNextAnimation(new Animation(2982));
	EngineTaskManager.schedule(new EngineTask() {

	    @Override
	    public void run() {
		npc.getCombat().setTarget(player);
		npc.setCantInteract(false);
	    }
	});
    }
}
