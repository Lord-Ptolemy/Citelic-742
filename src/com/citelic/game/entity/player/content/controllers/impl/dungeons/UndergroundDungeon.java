package com.citelic.game.entity.player.content.controllers.impl.dungeons;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.miscellaneous.LightSource;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class UndergroundDungeon extends Controller {

    transient int ticks;
    boolean hasStench, requiresLightSource;
    transient boolean initial;

    private void checkRequriments() {
        boolean lastInitial = initial;
        if (hasStench) {
            if (!Slayer.hasNosepeg(player) && !Slayer.hasMask(player)) {
                if (initial) {
                    player.getPackets().sendGameMessage("The stench of the monsters begins to reach your nose..");
                }
                initial = false;
            } else {
                initial = true;
            }
        }
        if (requiresLightSource) {
            if (!LightSource.hasLightSource(player)) {
                if (initial) {
                    player.getPackets().sendGameMessage("You hear tiny insects skittering over the ground...");
                }
                initial = false;
            } else {
                initial = true;
            }
        }
        if (lastInitial != initial) {
            sendInterfaces();
        }
    }

    @Override
    public void forceClose() {
        player.getPackets().sendMiniMapStatus(0);
        player.getInterfaceManager().closeOverlay(true);
    }

    private void init() {
        if (getArguments() != null) {
            hasStench = (boolean) getArguments()[0];
            requiresLightSource = (boolean) getArguments()[1];
        }
        ticks = 0;
        initial = true;
        sendInterfaces();
    }

    @Override
    public boolean login() {
        init();
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public void magicTeleported(int type) {
        player.getControllerManager().forceStop();
    }

    @Override
    public void process() {
        checkRequriments();
        if (initial)
            return;
        ticks++;
        if (hasStench) {
            if (ticks % 12 == 0) {
                player.getPackets().sendGameMessage("The strench of the monsters burns your innards.");
                player.applyHit(new Hit(player, 200, HitLook.REGULAR_DAMAGE));
            }
        }
        if (requiresLightSource) {
            if (ticks % 2 == 0) {
                if (!LightSource.hasLightSource(player)) {
                    if (!player.isLocked()) {
                        player.applyHit(new Hit(player, Utilities.random(10, 100), HitLook.REGULAR_DAMAGE));
                    }
                }
            }
        }
    }

    @Override
    public boolean processObjectClick1(final GameObject object) {
        if (object.getId() == 31316) {
            player.useStairs(-1, new Tile(3360, 2971, 0), 1, 2);
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 5946) {
            player.useStairs(828, new Tile(3168, 3171, 0), 1, 2);
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 32944) {
            player.useStairs(-1, new Tile(3219, 9532, 2), 1, 2);
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 31435)
            return false;
        else if (object.getId() == 15811) {
            player.useStairs(-1, new Tile(3749, 2973, 0), 1, 2);
            return false;
        } else if (object.getId() == 15790) {
            if (object.getX() == 3829) {
                player.useStairs(-1, new Tile(3831, 3062, 0), 1, 2);
            }
            if (object.getX() == 3814) {
                player.useStairs(-1, new Tile(3816, 3062, 0), 1, 2);
            }
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 15812) {
            player.useStairs(-1, new Tile(3749, 2973, 0), 1, 2);
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 6912) {
            player.setNextAnimation(new Animation(10578));
            player.useStairs(-1, object, 1, 2);
            player.useStairs(10579, new Tile(player.getX(), player.getY() == 9601 ? player.getY() + 2 : player.getY() - 2, 0), 1, 2);
            return false;
        } else if (object.getId() == 6899) {
            player.setNextAnimation(new Animation(10578));
            player.useStairs(-1, object, 1, 2);
            player.useStairs(10579, new Tile(3219, 9618, 0), 1, 2);
            player.getControllerManager().forceStop();
            player.getPackets().sendGameMessage("You squeeze through the hole.");
            return false;
        } else if (object.getId() == 6439) {
            player.useStairs(828, new Tile(3310, 2961, 0), 1, 2);
            player.getControllerManager().forceStop();
            return false;
        } else if (object.getId() == 31390) {
            player.useStairs(-1, new Tile(3318, 9355, 0), 1, 2, "You tumble into the darkness, arriving on a different cave level.");
            return false;
        } else if (object.getId() == 31367) {
            player.useStairs(-1, new Tile(3338, 9350, 0), 1, 2, "You tumble into the darkness, arriving on a different cave level.");
            return false;
        }
        return true;
    }

    @Override
    public void sendInterfaces() {
        if (requiresLightSource) {
            boolean hasLight = LightSource.hasLightSource(player);
            player.getInterfaceManager().sendOverlay(hasLight ? LightSource.hasExplosiveSource(player) ? 98 : 97 : 96, true);
            if (!hasLight) {
                player.getPackets().sendMiniMapStatus(2);
            } else {
                player.getPackets().sendMiniMapStatus(0);
            }
        }
    }

    @Override
    public void start() {
        init();
        setArguments(null);
    }
}
