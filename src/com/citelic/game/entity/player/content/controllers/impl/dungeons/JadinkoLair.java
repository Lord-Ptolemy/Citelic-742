package com.citelic.game.entity.player.content.controllers.impl.dungeons;

import java.util.TimerTask;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class JadinkoLair extends Controller { // 10, 13, 50

    private static final int[] JADE_ROOTS = {12290, 12291, 12272, 12274};
    private static final Tile[] JADE_WORLDTILE = {new Tile(3053, 9239, 0), new Tile(3055, 9246, 0), new Tile(3056, 9250, 0), new Tile(3061, 9240, 0), new Tile(3048, 9233, 0), new Tile(3044, 9237, 0), new Tile(3036, 9241, 0), new Tile(3026, 9237, 0), new Tile(3023, 9232, 0), new Tile(3035, 9233, 0), new Tile(3035, 9233, 0), new Tile(3035, 9233, 0), new Tile(3033, 9231, 0), new Tile(3033, 9231, 0), new Tile(3023, 9229, 0), new Tile(3020, 9243, 0), new Tile(3015, 9252, 0), new Tile(3024, 9252, 0), new Tile(3023, 9257, 0), new Tile(3040, 9262, 0), new Tile(3041, 9268, 0), new Tile(3046, 9268, 0), new Tile(3046, 9268, 0), new Tile(3046, 9268, 0), new Tile(3053, 9249, 0), new Tile(3058, 9251, 0), new Tile(3058, 9251, 0), new Tile(3064, 9238, 0), new Tile(3059, 9241, 0), new Tile(3056, 9237, 0), new Tile(3060, 9243, 0), new Tile(3026, 9234, 0), new Tile(3038, 9237, 0), new Tile(3041, 9263, 0), new Tile(3040, 9269, 0), new Tile(3047, 9265, 0), new Tile(3046, 9273, 0), new Tile(3045, 9270, 0), new Tile(3044, 9263, 0), new Tile(3043, 9259, 0), new Tile(3037, 9265, 0), new Tile(3040, 9260, 0), new Tile(3043, 9264, 0), new Tile(3021, 9257, 0), new Tile(3017, 9259, 0), new Tile(3015, 9263, 0), new Tile(3011, 9261, 0), new Tile(3017, 9251, 0), new Tile(3020, 9252, 0), new Tile(3020, 9256, 0), new Tile(3021, 9260, 0), new Tile(3024, 9257, 0), new Tile(3021, 9252, 0), new Tile(3022, 9238, 0), new Tile(3021, 9234, 0), new Tile(3020, 9229, 0), new Tile(3028, 9232, 0), new Tile(3026, 9236, 0), new Tile(3022, 9236, 0), new Tile(3026, 9236, 0), new Tile(3027, 9233, 0), new Tile(3029, 9231, 0), new Tile(3028, 9237, 0), new Tile(3030, 9236, 0), new Tile(3033, 9239, 0), new Tile(3034, 9235, 0), new Tile(3035, 9237, 0), new Tile(3036, 9232, 0), new Tile(3036, 9237, 0), new Tile(3036, 9241, 0), new Tile(3041, 9236, 0), new Tile(3040, 9241, 0), new Tile(3043, 9229, 0), new Tile(3045, 9234, 0), new Tile(3041, 9238, 0), new Tile(3047, 9234, 0), new Tile(3042, 9243, 0), new Tile(3054, 9238, 0), new Tile(3055, 9244, 0), new Tile(3054, 9249, 0), new Tile(3058, 9249, 0), new Tile(3059, 9245, 0), new Tile(3063, 9241, 0), new Tile(3062, 9237, 0), new Tile(3059, 9240, 0), new Tile(3064, 9242, 0), new Tile(3062, 9246, 0), new Tile(3063, 9236, 0), new Tile(3041, 9265, 0), new Tile(3042, 9261, 0), new Tile(3045, 9262, 0), new Tile(3048, 9266, 0), new Tile(3042, 9271, 0)};
    public static int playersCount;

    private static void createJadeRoot(int id, Tile tile, boolean permenant) {
        if (Engine.getObject(tile) != null)
            return;
        GameObject o = new GameObject(id, 10, 0, tile);
        if (permenant) {
            Engine.spawnObject(o);
        } else {
            Engine.spawnObjectTemporary(o, Utilities.random(10000, 30000));
        }
    }

    public static void init() {
        CoresManager.fastExecutor.schedule(new JadinkoTimer(), 600, 600);
        for (Tile tile : JadinkoLair.JADE_WORLDTILE) {
            if (Utilities.random(2) == 0) {
                continue;
            }
            JadinkoLair.createJadeRoot(JadinkoLair.JADE_ROOTS[Utilities.random(JadinkoLair.JADE_ROOTS.length)], tile, true);
        }
    }

    @Override
    public void forceClose() {
        JadinkoLair.playersCount--;
        player.getInterfaceManager().closeOverlay(true);
    }

    @Override
    public boolean login() {
        start();
        return false;
    }

    @Override
    public boolean logout() {
        JadinkoLair.playersCount--;
        return false;
    }

    @Override
    public void magicTeleported(int teleType) {
        player.getControllerManager().forceStop();
    }

    @Override
    public boolean processObjectClick1(final GameObject object) {
        if (object.getId() == 12284) {
            if (!player.getInventory().containsItemToolBelt(590) || !player.getInventory().containsItem(21350, 1)) {
                player.getPackets().sendGameMessage("You do not have the required items to light this.");
                return false;
            } else if (player.getSkills().getLevel(Skills.FIREMAKING) < 83) {
                player.getPackets().sendGameMessage("You do not have the required level to light this.");
                return false;
            }
            player.lock(5);
            player.setNextAnimation(new Animation(827));
            EngineTaskManager.schedule(new EngineTask() {

                byte ticks = 0;

                @Override
                public void run() {
                    ticks++;
                    if (ticks == 2) {
                        JadinkoLair.this.player.getInventory().deleteItem(21350, 1);
                        Engine.spawnObjectTemporary(new GameObject(object.getId() + 1, object.getType(), object.getRotation(), object), 2000);
                    } else if (ticks == 3) {
                        JadinkoLair.this.player.setNextAnimation(new Animation(16700));
                    } else if (ticks == 4) {
                        Engine.spawnObjectTemporary(new GameObject(object.getId() + 2, object.getType(), object.getRotation(), object), 30000);
                        JadinkoLair.this.player.getSkills().addXp(Skills.FIREMAKING, 378.7);
                        JadinkoLair.this.player.setFavorPoints(3 + JadinkoLair.this.player.getFavorPoints());
                        JadinkoLair.this.player.refreshFavorPoints();
                        stop();
                        return;
                    }
                }
            }, 0, 0);
            return false;
        } else if (object.getId() == 12327) {
            player.getControllerManager().forceStop();
            player.useStairs(-1, new Tile(2948, 2955, 0), 1, 2);
            return false;
        } else if (object.getId() == 12321) {
            player.useStairs(3414, new Tile(2946, 2886, 0), 2, 3);
            EngineTaskManager.schedule(new EngineTask() {

                @Override
                public void run() {
                    JadinkoLair.this.player.setNextFaceTile(new Tile(2946, 2887, 0));
                    JadinkoLair.this.player.setNextAnimation(new Animation(11043));
                    JadinkoLair.this.player.getControllerManager().forceStop();
                }
            }, 3);
            return false;
        }
        return true;
    }

    @Override
    public void sendInterfaces() {
        player.getInterfaceManager().sendOverlay(715, true);
        player.refreshFavorPoints();
    }

    @Override
    public void start() {
        JadinkoLair.playersCount++;
        sendInterfaces();
    }

    static class JadinkoTimer extends TimerTask {

        int ticks;

        @Override
        public void run() {
            ticks++;
            if (ticks % 10 == 0) {
                if (JadinkoLair.playersCount != 0) {
                    for (int i = 0; i < Utilities.random(Utilities.random(JadinkoLair.JADE_ROOTS.length)); i++) {
                        JadinkoLair.createJadeRoot(JadinkoLair.JADE_ROOTS[Utilities.random(JadinkoLair.JADE_ROOTS.length)], JadinkoLair.JADE_WORLDTILE[Utilities.random(JadinkoLair.JADE_WORLDTILE.length)], false);
                    }
                }
            }
        }
    }
}
