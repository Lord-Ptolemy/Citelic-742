package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.CompletionistStand;

public class CapeStand extends Dialogue {

	private Boolean trimmed;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				CompletionistStand.openCapeStandInterface(player, trimmed);
				end();
				break;
			case OPTION_2:
				sendOptionsDialogue("Select an Option",
						trimmed ? "Buy a trimmed Completionist Cape"
								: "Buy a Completionist Cape", "Nevermind");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (CompletionistStand.completionistCapeRequierment(player,
						trimmed)) {
					if (player.getInventory().containsItem(995,
							trimmed ? 150000000 : 75000000)) {
						player.getInventory().deleteItem(995,
								trimmed ? 150000000 : 75000000);
						player.getInventory().addItem(trimmed ? 20771 : 20769,
								1);
						player.getInventory().addItem(20770, 1);
						sendDialogue(trimmed ? "You've bought a trimmed Completionist Cape."
								: "You've bought a Completionist Cape.");
						stage = 2;
					} else {
						sendDialogue("You don't have enough coins to buy a cape.");
						stage = 2;
					}
				} else if (!CompletionistStand.completionistCapeRequierment(
						player, trimmed)) {
					sendDialogue(trimmed ? "You don't have the reqs to buy a trimmed Completionist Cape."
							: "You don't have the reqs to buy a Completionist Cape.");
					stage = 2;
				}
				break;
			case 2:
				end();
				break;
			}
			break;
		case 2:
			end();
			break;
		}
	}

	@Override
	public void start() {
		trimmed = (Boolean) parameters[0];
		sendOptionsDialogue("Select an Option",
				trimmed ? "Check requirements for trimmed Completionist Cape"
						: "Check requirements for Completionist Cape",
				trimmed ? "Buy trimmed Completionist Cape"
						: "Buy Completionist Cape");
	}

}