package com.citelic.game.entity.player.content.controllers.impl.skills.dungeoneering;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.Dungeoneering;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.DungeoneeringConstants;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class DungeoneeringController extends Controller {

    Dungeoneering dungeon;

    public static void removeDungeoneeringSettings(Player p) {
        p.getInterfaceManager().closeOverlay(false);
        // p.getCombatDefinitions().removeDungeoneeringBook();
        p.setForceMultiArea(false);
    }

    public static void stopController(Player p) {
        p.getControllerManager().getController();
        p.getControllerManager().removeControllerWithoutCheck();
    }

    @Override
    public void forceClose() {
        player.removeDungItems();
        player.setNextTile(DungeoneeringConstants.DAEMONHEIM_LOBBY);
        if (dungeon != null) {
            dungeon.end(false);
        }
    }

    @Override
    public boolean login() {
        player.removeDungItems();
        player.setNextTile(DungeoneeringConstants.DAEMONHEIM_LOBBY);
        if (dungeon != null) {
            dungeon.end(false);
        }
        return false;
    }

    @Override
    public boolean logout() {
        player.removeDungItems();
        player.setNextTile(DungeoneeringConstants.DAEMONHEIM_LOBBY);
        if (dungeon != null) {
            dungeon.end(false);
        }
        return false;
    }

    public void openStairs() {
        Tile pos;

        switch (dungeon.getDungType()) {
            case 0:
                pos = dungeon.getTileFromRegion(0, 8, 7, -1);
                dungeon.endLadder = new GameObject(3784, 10, 3, pos.getX(), pos.getY(), 0);
                break;
            case 1:
                pos = dungeon.getTileFromRegion(0, 8, 7, -5);
                dungeon.endLadder = new GameObject(3786, 10, 3, pos.getX(), pos.getY(), 0);
                break;
            case 2:
                pos = dungeon.getTileFromRegion(0, 8, 7, -1);
                dungeon.endLadder = new GameObject(49700, 10, 3, pos.getX(), pos.getY(), 0);
                break;
            case 3:
                pos = dungeon.getTileFromRegion(0, 8, 7, -1);
                dungeon.endLadder = new GameObject(3808, 10, 3, pos.getX(), pos.getY(), 0);
                break;
            case 4:
                pos = dungeon.getTileFromRegion(0, 8, 7, -1);
                dungeon.endLadder = new GameObject(55484, 10, 3, pos.getX(), pos.getY(), 0);
                break;
            default:
                pos = dungeon.getTileFromRegion(0, 8, 8, 0);
                dungeon.endLadder = new GameObject(55484, 10, 3, pos.getX(), pos.getY(), 0);
                break;
        }

        Engine.spawnObject(dungeon.endLadder);
        dungeon.openedStairs = true;
    }

    @Override
    public void process() {
        if (dungeon != null) {
            if (dungeon.boss.hasFinished() && !dungeon.openedStairs) {
                openStairs();
            }
        } else {
            player.setDungeon(null);
            player.setNextTile(DungeoneeringConstants.DAEMONHEIM_LOBBY);
            removeController();
        }
    }

    @Override
    public boolean processItemTeleport(Tile toTile) {
        player.getPackets().sendGameMessage("You may not teleport in a dungeon. You may leave at any time from the home room.");
        return false;
    }

    @Override
    public boolean processMagicTeleport(Tile toTile) {
        player.getPackets().sendGameMessage("You may not teleport in a dungeon. You may leave at any time from the home room.");
        return false;
    }

    @Override
    public boolean processObjectClick1(GameObject object) {
        if (dungeon != null) {
            dungeon.handleObjects(object);
        }
        return false;
    }

    @Override
    public boolean sendDeath() {
        player.lock(7);
        player.stopAll();
        EngineTaskManager.schedule(new EngineTask() {
            int loop;

            @Override
            public void run() {
                if (loop == 0) {
                    player.setNextAnimation(new Animation(836));
                } else if (loop == 1) {
                    player.getPackets().sendGameMessage("Oh dear, you have died.");
                } else if (loop == 3) {
                    player.reset();
                    dungeon.putPlayerAtStart();
                    player.setNextAnimation(new Animation(-1));
                    dungeon.deaths++;
                    sendDeathVarp();
                    stop();
                }
                loop++;
            }
        }, 0, 1);
        return false;
    }

    private void sendDeathVarp() {
        player.getPackets().sendConfigByFile(7554, dungeon.deaths);
    }

    public void setDungeoneeringSettings(Player p) {
        p.getInterfaceManager().sendOverlay(945, false);
        // p.getCombatDefinitions().setSpellBook(3);
        p.setForceMultiArea(true);
        sendDeathVarp();
    }

	/*
     * @Override public boolean processNPCClick1(NPC npc) { switch(npc.getId())
	 * { case 11226: ShopsHandler.openShop(player, 195); return true; default:
	 * player
	 * .getPackets().sendGameMessage("NPC not added to dungeoneering controller."
	 * ); return true; } }
	 * 
	 * @Override public boolean processNPCClick2(NPC npc) { switch(npc.getId())
	 * { case 11226: ShopsHandler.openShop(player, 195); return true; default:
	 * player
	 * .getPackets().sendGameMessage("NPC not added to dungeoneering controller."
	 * ); return true; } }
	 */

    @Override
    public void start() {
        dungeon = (Dungeoneering) getArguments()[0];
        setArguments(null);
        setDungeoneeringSettings(player);
        player.getInventory().addItem(18201, 2500 * (dungeon.getDungType() + 1));
        if (player.getDungBinds() != null) {
            if (player.getDungBinds()[0] != null) {
                player.getInventory().addItem(player.getDungBinds()[0]);
            }
            if (player.getDungBinds()[1] != null) {
                player.getInventory().addItem(player.getDungBinds()[1]);
            }
            if (player.getDungBinds()[2] != null) {
                player.getInventory().addItem(player.getDungBinds()[2]);
            }
        }
    }

}