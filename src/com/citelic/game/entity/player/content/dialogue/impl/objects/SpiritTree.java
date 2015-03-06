package com.citelic.game.entity.player.content.dialogue.impl.objects;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class SpiritTree extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				end();
				player.getDialogueManager().startDialogue("MinigameTeleports");
			}
			if (componentId == OPTION_2) {
				end();
				player.getDialogueManager().startDialogue("SkillingTeleports");
			}
			if (componentId == OPTION_3) {
				end();
				player.getDialogueManager().startDialogue("TrainingTeleports");
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose your destination",
						"Pking Teleports", "Slayer Teleports",
						"Boss Teleports", "First Page");
				stage = 1;
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				end();
				player.getDialogueManager().startDialogue("PkingTeleports");
			} else if (componentId == OPTION_2) {
				end();
				player.getDialogueManager().startDialogue("SlayerTeleports");
			} else if (componentId == OPTION_3) {
				end();
				player.getDialogueManager().startDialogue("BossTeleports");
			} else if (componentId == OPTION_4) {
				stage = -1;
				sendOptionsDialogue("Choose your destination",
						"Minigame Teleports", "Skilling Teleports",
						"Training Teleports", "More Options");
			}
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Choose your destination", "Minigame Teleports",
				"Skilling Teleports", "Training Teleports", "More Options");
	}

}