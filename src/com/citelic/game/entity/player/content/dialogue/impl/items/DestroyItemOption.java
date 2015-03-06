package com.citelic.game.entity.player.content.dialogue.impl.items;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.item.Item;

public class DestroyItemOption extends Dialogue {

	int slotId;
	Item item;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1183 && componentId == 14) {
			player.getInventory().deleteItem(slotId, item);
			player.getCharges().degradeCompletly(item);
			player.getPackets().sendSound(4500, 0, 1);
		}
		end();
	}

	@Override
	public void start() {
		slotId = (Integer) parameters[0];
		item = (Item) parameters[1];
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().sendIComponentText(1183, 10,
				"Are you sure you want to destroy this object?");
		player.getPackets().sendIComponentText(1183, 29,
				item.getDefinitions().getDestroyMessage());
		player.getPackets().sendIComponentText(1183, 13, item.getName());
		player.getPackets().sendItemOnIComponent(1183, 30, item.getId(), 1);
	}

}
