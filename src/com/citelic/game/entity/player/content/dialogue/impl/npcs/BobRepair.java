package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.RepairItems;

public class BobRepair extends Dialogue {

	private int itemId;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		int amount = player.getInventory().getItems().getNumberOf(itemId);
		if (stage == -1) {
			if (componentId == OPTION_1) {
				if (amount == 1) {
					RepairItems.Repair(player, itemId, 1);
					return;
				}
				sendOptionsDialogue("What would you like to do?", "Repair (1)",
						"Repair (X)", "Repair (ALL).");
				stage = 2;
			} else if (componentId == OPTION_2) {
				// RepairItems.CheckPrice(player,itemId,amount);
				end();
			}
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				RepairItems.Repair(player, itemId, 1);
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributtes().put("Repair",
						Integer.valueOf(0));
				player.getTemporaryAttributtes().put("Ritem", itemId);
				player.getPackets()
						.sendRunScript(
								108,
								new Object[] { "You currently have "
										+ amount
										+ " of that item, How many of them would you like to repair?" });
				end();
			} else {
				RepairItems.Repair(player, itemId, amount);
			}
		}
	}

	@Override
	public void start() {
		itemId = (Integer) parameters[1];
		sendOptionsDialogue("What would you like to do?", "Repair.",
				"Nevermind.");
	}
}