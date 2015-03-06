package com.citelic.game.entity.player.content.actions.skills.dungeoneering;

import java.io.Serializable;

import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class Dungeoneering implements Serializable {

    public static final int FROZEN = 0;
    public static final int ABANDONED = 1;
    public static final int FURNISHED = 2;
    public static final int OCCULT = 3;
    public static final int WARPED = 4;
    private static final long serialVersionUID = -3866335496921765212L;
    public GameObject endLadder;
    public NPC monster1;
    public NPC monster2;
    public NPC boss;
    public NPC smuggler;
    public boolean openedStairs = false;
    public int dungType;
    public int deaths = 0;
    private Player player;
    private int mapChunks[];

    public Dungeoneering(Player player, int dungType) {
        this.player = player;
        this.dungType = dungType;
        bindChunksToEmptyMap();
        loadRooms(dungType);
        putPlayerAtStart();
        player.loadMapRegions();
        smuggler = new NPC(11226, getTileFromRegion(0, 0, 7, 8), 0, false, true);
        monster1 = new NPC(DungeoneeringConstants.MONSTER1[dungType], getTileFromRegion(0, 2, 8, 7), -1, true, true);
        monster2 = new NPC(DungeoneeringConstants.MONSTER2[dungType], getTileFromRegion(0, 4, 8, 7), -1, true, true);
        boss = new NPC(DungeoneeringConstants.BOSS[dungType], getTileFromRegion(0, 6, 6, 6), -1, true, true);
        player.getControllerManager().startController("DungeoneeringController", this);
        player.stopAll();
        player.reset();
    }

    public static boolean useStairs(final Player player, int objectId) {
        if (objectId == 50552) {
            player.setNextTile(new Tile(3454, 3725, 0));
            return true;
        }
        return false;
    }

    public void bindChunksToEmptyMap() {
        setMapChunks(MapBuilder.findEmptyChunkBound(14, 24));
    }

    public void end(boolean nextDung) {
        player.getPackets().sendGameMessage("Ending dungeon...");
        MapBuilder.destroyMap(mapChunks[0], mapChunks[1], 4, 8);
        MapBuilder.destroyMap(mapChunks[0], mapChunks[1] + 2, 4, 8);
        MapBuilder.destroyMap(mapChunks[0], mapChunks[1] + 4, 4, 8);
        MapBuilder.destroyMap(mapChunks[0], mapChunks[1] + 6, 4, 8);
        player.getControllerManager().removeControllerWithoutCheck();
        if (!nextDung) {
            player.setNextTile(DungeoneeringConstants.DAEMONHEIM_FLOOR);
        }
        if (!monster1.hasFinished()) {
            monster1.finish();
        }
        if (!monster2.hasFinished()) {
            monster2.finish();
        }
        if (!boss.hasFinished()) {
            boss.finish();
        }
        if (!smuggler.hasFinished()) {
            smuggler.finish();
        }
        if (endLadder != null) {
            Engine.removeObject(endLadder);
        }
        player.removeDungItems();
        player.setDungeon(null);
        player.setForceNextMapLoadRefresh(true);
        deaths = 0;
    }

    public int getDungType() {
        return dungType;
    }

    public void setDungType(int dungType) {
        this.dungType = dungType;
    }

    public int[] getMapChunks() {
        return mapChunks;
    }

    public void setMapChunks(int mapChunks[]) {
        this.mapChunks = mapChunks;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Tile getTileFromRegion(int offsetX, int offsetY, int tileOffsetX, int tileOffsetY) {
        return new Tile((mapChunks[0] << 3) + offsetX * 8 + tileOffsetX, (mapChunks[1] << 3) + offsetY * 8 + tileOffsetY, 0);
    }

    public int getXpForDungeon() {
        int initialXp = 100 + dungType * 15;
        initialXp *= dungType + 1;
        if (deaths > 0) {
            initialXp /= deaths;
        }
        return initialXp;
    }

    public void handleObjects(GameObject object) {
        ObjectDefinitions defs = ObjectDefinitions.getObjectDefinitions(object.getId());

        switch (defs.name.toLowerCase()) {
            case "dungeon exit":
                player.getDialogueManager().startDialogue("DungeonExitD", player);
                break;

            case "boss door":
                player.setNextTile(new Tile(player.getX(), player.getY() - 3, player.getZ()));
                break;

            case "ladder":
                if (boss.hasFinished() && monster1.hasFinished() && monster2.hasFinished()) {
                    player.getDialogueManager().startDialogue("DungeonCompleteD", this);
                } else {
                    player.getPackets().sendGameMessage("You didn't slay all the monsters, yet.");
                }
                break;

            case "door":
                if (player.getY() == getTileFromRegion(0, 2, 0, 0).getY() + 1) {
                    player.setNextTile(new Tile(player.getX(), player.getY() - 3, player.getZ()));
                }

                if (player.getY() == getTileFromRegion(0, 2, 0, 0).getY() - 2) {
                    player.setNextTile(new Tile(player.getX(), player.getY() + 3, player.getZ()));
                }

                if (player.getY() == getTileFromRegion(0, 4, 0, 0).getY() - 2) {
                    if (monster1.hasFinished()) {
                        player.setNextTile(new Tile(player.getX(), player.getY() + 3, player.getZ()));
                    } else {
                        player.getPackets().sendGameMessage("You must defeat all the monsters in this room to continue.");
                    }
                }

                if (player.getY() == getTileFromRegion(0, 6, 0, 0).getY() - 2) {
                    if (monster2.hasFinished()) {
                        player.setNextTile(new Tile(player.getX(), player.getY() + 3, player.getZ()));
                    } else {
                        player.getPackets().sendGameMessage("You must defeat all the monsters in this room to continue.");
                    }
                }

                if (player.getY() == getTileFromRegion(0, 2, 0, 0).getY() + 1) {
                    player.setNextTile(new Tile(player.getX(), player.getY() - 3, player.getZ()));
                }

                if (player.getY() == getTileFromRegion(0, 4, 0, 0).getY() + 1) {
                    player.setNextTile(new Tile(player.getX(), player.getY() - 3, player.getZ()));
                }
                break;
        }
    }

    public void loadRooms(int dungType) {
        switch (dungType) {
            case FROZEN:
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FROZEN_ROOMS[0][0], DungeoneeringConstants.FROZEN_ROOMS[0][1], mapChunks[0], mapChunks[1], 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FROZEN_ROOMS[1][0], DungeoneeringConstants.FROZEN_ROOMS[1][1], mapChunks[0], mapChunks[1] + 2, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FROZEN_ROOMS[1][0], DungeoneeringConstants.FROZEN_ROOMS[1][1], mapChunks[0], mapChunks[1] + 4, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FROZEN_ROOMS[2][0], DungeoneeringConstants.FROZEN_ROOMS[2][1], mapChunks[0], mapChunks[1] + 6, 0);
                player.getPackets().sendGameMessage("Welcome to Dungeoneering.");
                break;
            case ABANDONED:
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.ABANDONED_ROOMS[0][0], DungeoneeringConstants.ABANDONED_ROOMS[0][1], mapChunks[0], mapChunks[1], 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.ABANDONED_ROOMS[1][0], DungeoneeringConstants.ABANDONED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 2, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.ABANDONED_ROOMS[1][0], DungeoneeringConstants.ABANDONED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 4, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.ABANDONED_ROOMS[2][0], DungeoneeringConstants.ABANDONED_ROOMS[2][1], mapChunks[0], mapChunks[1] + 6, 0);
                player.getPackets().sendGameMessage("Welcome to Dungeoneering.");
                break;
            case FURNISHED:
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FURNISHED_ROOMS[0][0], DungeoneeringConstants.FURNISHED_ROOMS[0][1], mapChunks[0], mapChunks[1], 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FURNISHED_ROOMS[1][0], DungeoneeringConstants.FURNISHED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 2, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FURNISHED_ROOMS[1][0], DungeoneeringConstants.FURNISHED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 4, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.FURNISHED_ROOMS[2][0], DungeoneeringConstants.FURNISHED_ROOMS[2][1], mapChunks[0], mapChunks[1] + 6, 0);
                player.getPackets().sendGameMessage("Welcome to Dungeoneering.");
                break;
            case OCCULT:
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.OCCULT_ROOMS[0][0], DungeoneeringConstants.OCCULT_ROOMS[0][1], mapChunks[0], mapChunks[1], 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.OCCULT_ROOMS[1][0], DungeoneeringConstants.OCCULT_ROOMS[1][1], mapChunks[0], mapChunks[1] + 2, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.OCCULT_ROOMS[1][0], DungeoneeringConstants.OCCULT_ROOMS[1][1], mapChunks[0], mapChunks[1] + 4, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.OCCULT_ROOMS[2][0], DungeoneeringConstants.OCCULT_ROOMS[2][1], mapChunks[0], mapChunks[1] + 6, 0);
                player.getPackets().sendGameMessage("Welcome to Dungeoneering.");
                break;
            default:
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.WARPED_ROOMS[0][0], DungeoneeringConstants.WARPED_ROOMS[0][1], mapChunks[0], mapChunks[1], 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.WARPED_ROOMS[1][0], DungeoneeringConstants.WARPED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 2, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.WARPED_ROOMS[1][0], DungeoneeringConstants.WARPED_ROOMS[1][1], mapChunks[0], mapChunks[1] + 4, 2);
                MapBuilder.copy2RatioSquare(DungeoneeringConstants.WARPED_ROOMS[2][0], DungeoneeringConstants.WARPED_ROOMS[2][1], mapChunks[0], mapChunks[1] + 6, 0);
                player.getPackets().sendGameMessage("Welcome to Dungeoneering.");
                break;
        }
    }

    public void putPlayerAtStart() {
        player.setNextTile(getTileFromRegion(0, 0, 8, 8));
    }
}
