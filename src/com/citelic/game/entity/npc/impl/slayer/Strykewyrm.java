package com.citelic.game.entity.npc.impl.slayer;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class Strykewyrm extends NPC {

    private int stompId;

    public Strykewyrm(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
        super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
        stompId = id;
    }

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
        switch (npc.getId()) {
            case 9462:
                if (player.getSkills().getLevel(18) < 93) {
                    player.getPackets().sendGameMessage("You need at least a slayer level of 93 to fight this.");
                    return;
                }
                break;
            case 9464:
                if (player.getSkills().getLevel(18) < 77) {
                    player.getPackets().sendGameMessage("You need at least a slayer level of 77 to fight this.");
                    return;
                }
                break;
            case 9466:
                if (player.getSkills().getLevel(18) < 73) {
                    player.getPackets().sendGameMessage("You need at least a slayer level of 73 to fight this.");
                    return;
                }
                break;
            default:
                return;
        }
        player.setNextAnimation(new Animation(4278));
        player.lock(2);
        npc.setCantInteract(true);
        EngineTaskManager.schedule(new EngineTask() {
            @Override
            public void run() {
                npc.setNextAnimation(new Animation(12795));
                npc.setNextNPCTransformation(((Strykewyrm) npc).stompId + 1);
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

    @Override
    public void processNPC() {
        super.processNPC();
        if (isDead())
            return;
        if (getId() != stompId && !isCantInteract() && !isUnderCombat()) {
            setNextAnimation(new Animation(12796));
            setCantInteract(true);
            EngineTaskManager.schedule(new EngineTask() {
                @Override
                public void run() {
                    Strykewyrm.this.setNextNPCTransformation(stompId);
                    EngineTaskManager.schedule(new EngineTask() {
                        @Override
                        public void run() {
                            Strykewyrm.this.setCantInteract(false);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void reset() {
        setNPC(stompId);
        super.reset();
    }

}