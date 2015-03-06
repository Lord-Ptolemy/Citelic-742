package com.citelic.game.entity.player.content.dialogue.impl.objects.dungeoneering;

import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.Dungeoneering;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class FloorSelection extends Dialogue {

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
        /*
         * if (!(player.getInventory().getFreeSlots() == 28) ||
		 * player.getEquipment().wearingArmour() || player.hasFamiliar()) {
		 * end(); return; }
		 */
        if (stage == 1) {
            sendOptionsDialogue("Floor type", "Frozen (1-22 dung)", "Abandoned (23-34 dung)", "Furnished (35-70 dung)", "Occult (71-94 dung)", "Warped (95-120 dung)");
            stage = 2;
        } else if (stage == 2) {
            switch (componentId) {
                case 11:
                    player.setDungeon(new Dungeoneering(player, 0));
                    end();
                    break;
                case 13:
                    if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) > 22) {
                        player.setDungeon(new Dungeoneering(player, 1));

                    } else {
                        player.getPackets().sendGameMessage("You need at least 23 dungeoneering to access the abandoned floors.");
                    }
                    end();
                    break;
                case 14:
                    if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) > 34) {
                        player.setDungeon(new Dungeoneering(player, 2));
                    } else {
                        player.getPackets().sendGameMessage("You need at least 35 dungeoneering to access the furnished floors.");
                    }
                    end();
                    break;
                case 15:
                    if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) > 70) {
                        player.setDungeon(new Dungeoneering(player, 3));
                    } else {
                        player.getPackets().sendGameMessage("You need at least 71 dungeoneering to access the occult floors.");
                    }
                    end();
                    break;
                case 16:
                    if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) > 94) {
                        player.setDungeon(new Dungeoneering(player, 4));
                    } else {
                        player.getPackets().sendGameMessage("You need at least 95 dungeoneering to access the warped floors.");
                    }
                    end();
                    break;
                default:
                    end();
            }
        }
    }

    @Override
    public void start() {
        sendDialogue("Please choose a floor type.");
        stage = 1;
    }
}