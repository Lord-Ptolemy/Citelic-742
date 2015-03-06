package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class HoleInTheWall extends NPC {

    private transient boolean hasGrabbed;

    public HoleInTheWall(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	setCantFollowUnderCombat(true);
	setCantInteract(true);
	setForceAgressive(true);
    }

    @Override
    public void processNPC() {
	super.processNPC();
	if (getId() == 2058) {
	    if (!hasGrabbed) {
		for (Entity entity : getPossibleTargets()) {
		    if (entity == null || entity.isDead() || !withinDistance(entity, 1))
			continue;
		    if (entity instanceof Player) {
			final Player player = (Player) entity;
			player.resetWalkSteps();
			hasGrabbed = true;
			if (Slayer.hasSpinyHelmet(player)) {
			    setNextNPCTransformation(7823);
			    setNextAnimation(new Animation(1805));
			    setCantInteract(false);
			    player.getPackets().sendGameMessage("The spines on your helmet repell the beast's hand.");
			    return;
			}
			setNextAnimation(new Animation(1802));
			player.lock(4);
			player.setNextAnimation(new Animation(425));
			player.getPackets().sendGameMessage("A giant hand appears and grabs your head.");
			EngineTaskManager.schedule(new EngineTask() {

			    @Override
			    public void run() {
				player.applyHit(new Hit(player, Utilities.getRandom(44), HitLook.REGULAR_DAMAGE));
				setNextAnimation(new Animation(-1));
				EngineTaskManager.schedule(new EngineTask() {

				    @Override
				    public void run() {
					hasGrabbed = false;
				    }
				}, 20);
			    }
			}, 5);
		    }
		}
	    }
	} else {
	    if (!getCombat().process()) {
		setCantInteract(true);
		setNextNPCTransformation(2058);
	    }
	}
    }

    @Override
    public void sendDeath(Entity source) {
	final NPCCombatDefinitions defs = getCombatDefinitions();
	resetWalkSteps();
	getCombat().removeTarget();
	setNextAnimation(null);
	EngineTaskManager.schedule(new EngineTask() {
	    int loop;

	    @Override
	    public void run() {
		if (loop == 0) {
		    setNextAnimation(new Animation(defs.getDeathEmote()));
		} else if (loop >= defs.getDeathDelay()) {
		    setNPC(2058);
		    drop();
		    reset();
		    setLocation(getRespawnTile());
		    finish();
		    EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
			    hasGrabbed = false;
			}
		    }, 8);
		    spawn();
		    stop();
		}
		loop++;
	    }
	}, 0, 1);
    }
}
