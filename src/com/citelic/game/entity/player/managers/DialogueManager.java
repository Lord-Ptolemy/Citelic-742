package com.citelic.game.entity.player.managers;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.DialogueHandler;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void startDialogue(Object key, Object... parameters) {
		if (!player.getControllerManager().useDialogueScript(key))
			return;
		if (lastDialogue != null)
			lastDialogue.finish();
		lastDialogue = DialogueHandler.getDialogue(key);
		if (lastDialogue == null)
			return;
		lastDialogue.parameters = parameters;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
	}

	public void pauseDialogue() {
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
	}

	public void continueDialogue(int interfaceId, int componentId) {
		if (interfaceId == 13 && componentId == 6 || interfaceId == 13
				&& componentId == 7 || interfaceId == 13 && componentId == 8
				|| interfaceId == 13 && componentId == 9 || interfaceId == 13
				&& componentId == 10 || interfaceId == 13 && componentId == 11
				|| interfaceId == 13 && componentId == 12 || interfaceId == 13
				&& componentId == 13 || interfaceId == 13 && componentId == 14
				|| interfaceId == 13 && componentId == 15) {
			player.getBankPin().handleButtons(interfaceId, componentId);
			return;
		}
		if (interfaceId == 14 && componentId == 18 || interfaceId == 14
				&& componentId == 33 || interfaceId == 14 && componentId == 35
				|| interfaceId == 14 && componentId == 19) {
			player.getBankPin().handleButtons(interfaceId, componentId);
			return;
		}
		if (lastDialogue == null)
			return;
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		if (lastDialogue == null)
			return;
		lastDialogue.finish();
		lastDialogue = null;
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
	}

}
