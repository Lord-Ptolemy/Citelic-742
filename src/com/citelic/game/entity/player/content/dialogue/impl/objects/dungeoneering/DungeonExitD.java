package com.citelic.game.entity.player.content.dialogue.impl.objects.dungeoneering;

import com.citelic.game.entity.player.content.controllers.impl.skills.dungeoneering.DungeoneeringController;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class DungeonExitD extends Dialogue {

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
        if (stage == 1) {
            sendOptionsDialogue("Are you sure you want to leave?", "Yes", "No");
            stage = 2;
        } else if (stage == 2) {
            if (componentId == 11) {
                if (player.getDungeon() != null) {
                    player.getDungeon().end(false);
                }
                DungeoneeringController.removeDungeoneeringSettings(player);
            }
            end();
        }
    }

    @Override
    public void start() {
        sendDialogue("You will recieve no experience for leaving the dungeon early.");
        stage = 1;
    }
}