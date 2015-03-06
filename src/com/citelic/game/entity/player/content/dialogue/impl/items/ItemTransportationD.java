package com.citelic.game.entity.player.content.dialogue.impl.items;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.transportation.ItemTransportation;

public class ItemTransportationD extends Dialogue {

    @Override
    public void start() {
	String[] locations = (String[]) parameters[0];
	sendOptionsDialogue("Where would you like to teleport to", locations);
    }

    @Override
    public void run(int interfaceId, int componentId) {
	String[] locations = (String[]) parameters[0];
	ItemTransportation.sendTeleport(player, player.getInventory().getItems().lookup((Integer) parameters[1]), componentId == OPTION_1 ? 0 : componentId - 12, false, locations.length - 1);
	end();
    }

    @Override
    public void finish() {
    }
}
