package com.citelic.game.entity.npc.impl.others;

import java.util.ArrayList;

import com.citelic.game.ForceTalk;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.godwars.saradomin.GodwarsSaradominFaction;
import com.citelic.game.entity.npc.impl.godwars.zamorak.GodwarsZamorakFaction;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class BanditCampBandits extends NPC {

    public BanditCampBandits(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
        super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        setForceAgressive(true); // to ignore combat lvl
    }

    @Override
    public ArrayList<Entity> getPossibleTargets() {
        ArrayList<Entity> targets = super.getPossibleTargets();
        ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
        for (Entity t : targets) {
            if (!(t instanceof Player) || !GodwarsZamorakFaction.hasGodItem((Player) t) && !GodwarsSaradominFaction.hasGodItem((Player) t)) {
                continue;
            }
            targetsCleaned.add(t);
        }
        return targetsCleaned;
    }

    @Override
    public void setTarget(Entity entity) {
        if (entity instanceof Player && (GodwarsZamorakFaction.hasGodItem((Player) entity) || GodwarsSaradominFaction.hasGodItem((Player) entity))) {
            setNextForceTalk(new ForceTalk(GodwarsZamorakFaction.hasGodItem((Player) entity) ? "Time to die, Saradominist filth!" : "Prepare to suffer, Zamorakian scum!"));
        }
        super.setTarget(entity);
    }

}
