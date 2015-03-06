package com.citelic.game.entity.player.content.dialogue.impl.distractions.clanwars;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

/**
 * Handles the clan wars viewing dialogue.
 * 
 * @author Emperor
 * 
 */
public final class ClanWarsViewing extends Dialogue {

	@Override
	public void start() {
		// TITLE: "Your clan does not appear to be in a war."
		sendOptionsDialogue("Select an option",
				"I want to watch a friend's clan war.",
				"Show me a battle - any battle.", "Oh, forget it.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		player.getPackets().sendGameMessage(
				"There are no clan wars going on currently.");
	}

	@Override
	public void finish() {
	}

}