package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;


public class WildyWyrm {

    private int stompId;
    
	public static void handleStomping(final Player player, final NPC npc) {
        if (npc.isCantInteract())
            return;
        if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
            if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utilities.currentTimeMillis()) {
                player.getPackets().sendGameMessage("You are already in combat.");
                return;
            }
            if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utilities.currentTimeMillis()) {
                if (npc.getAttackedBy() instanceof NPC) {
                    npc.setAttackedBy(player); // changes enemy to player,
                    // player has priority over
                    // npc on single areas
                } else {
                    player.getPackets().sendGameMessage("That npc is already in combat.");
                    return;
                }
            }
        }
        player.setNextAnimation(new Animation(4278));
        player.lock(2);
        npc.setCantInteract(true);
        EngineTaskManager.schedule(new EngineTask() {
            @Override
            public void run() {
                npc.setNextAnimation(new Animation(12795));
                npc.setNextNPCTransformation(3334);
                stop();
                EngineTaskManager.schedule(new EngineTask() {
                    @Override
                    public void run() {
                        npc.setTarget(player);
                        npc.setAttackedBy(player);
                        npc.setCantInteract(false);
                    }
                });
            }

        }, 1);
    }

    public int getStompId() {
        return stompId;
    }
}
