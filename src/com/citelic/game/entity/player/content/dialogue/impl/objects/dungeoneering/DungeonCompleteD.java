package com.citelic.game.entity.player.content.dialogue.impl.objects.dungeoneering;

import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.Dungeoneering;
import com.citelic.game.entity.player.content.controllers.impl.skills.dungeoneering.DungeoneeringController;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class DungeonCompleteD extends Dialogue {

    Dungeoneering dungeon;

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
        if (stage == 1) {
            sendOptionsDialogue("Move on to the next dungeon?", "Yes", "No", "Leave");
            stage = 2;
        } else if (stage == 2) {
            if (componentId == Dialogue.OPTION_1) {
                if (player.getDungeon() != null) {
                    int dungType = dungeon.getDungType();
                    player.getSkills().addXp(Skills.DUNGEONEERING, dungeon.getXpForDungeon());
                    player.dungTokens += (dungeon.getXpForDungeon() / 10 * 110);
                    player.getDungeon().end(true);
                    player.reset();
                    player.setDungeon(new Dungeoneering(player, dungType));
                }
            }
            if (componentId == Dialogue.OPTION_3) {
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
        dungeon = (Dungeoneering) parameters[0];

        if (dungeon != null) {
            sendDialogue("You have completed the dungeon with " + dungeon.deaths + " deaths.", "You will be rewarded " + dungeon.getXpForDungeon() * 200 + " xp , and " + dungeon.getXpForDungeon() / 10 * 110 + " tokens for completing the dungeon.");
        }
        stage = 1;
    }
}