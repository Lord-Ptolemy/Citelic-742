package com.citelic.game.entity.player.content.dialogue.impl.misc;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class CommunityUtils extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (!player.hasDisabledYell()) {
					player.setYellDisabled(true);
					player.getPackets()
							.sendGameMessage(
									"Yell messages are no longer appearing in your chatbox.");
				} else {
					player.setYellDisabled(false);
					player.getPackets().sendGameMessage(
							"Yell messages are now appearing in your chatbox.");
				}
				end();
				break;
			case OPTION_2:
				if (!player.isHidingWorldMessages())
					player.setHideWorldMessages(true);
				else
					player.setHideWorldMessages(false);
				player.getPackets().sendGameMessage(
						"World messages are now "
								+ (player.isHidingWorldMessages() ? "hidding."
										: "appearing."));
				end();
				break;
			case OPTION_3:
				player.setHideSofInterface((player.hideSofInterface() ? false
						: true));
				player.getSquealOfFortune().sendTab();
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Select an Option",
				player.hasDisabledYell() ? "Enable yell" : "Disable yell",
				player.isHidingWorldMessages() ? "Show World Announcements"
						: "Hide World Annoucements",
				player.hideSofInterface() ? "Enable SoF" : "Disable SoF");
	}
}