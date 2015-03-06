package com.citelic.game.entity.player.content.dialogue.impl.items;

import com.citelic.game.entity.player.content.actions.skills.dungeoneering.DungeoneeringRewards;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.DungeoneeringRewards.DungeonReward;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class DungRewardConfirm extends Dialogue {

    DungeonReward item;

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
        player.getPackets().sendGameMessage("COMPONENTID: " + componentId);
        if (componentId == 9) {
            if (player.getInventory().getFreeSlots() >= 1) {
                player.dungTokens -= item.getCost();
                player.getInventory().addItem(item.getId(), 1);
                DungeoneeringRewards.refresh(player);
            } else {
                player.getPackets().sendGameMessage("You need more inventory space.");
            }
        }
        end();
    }

    @Override
    public void start() {
        item = (DungeonReward) parameters[0];
        player.getInterfaceManager().sendChatBoxInterface(1183);
        player.getPackets().sendIComponentText(1183, 12, "It will cost " + item.getCost() + " for " + item.getName() + ".");
        player.getPackets().sendItemOnIComponent(1183, 13, item.getId(), 1);
        player.getPackets().sendIComponentText(1183, 7, "Are you sure you want to buy this?");
        player.getPackets().sendIComponentText(1183, 22, "Confirm Purchase");
    }

}
