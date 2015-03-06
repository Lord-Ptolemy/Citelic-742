package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.PartyRoom;

public class PartyPete extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void start() {
		sendEntityDialogue(
				SEND_3_TEXT_CHAT,
				new String[] {
						NPCDefinitions.getNPCDefinitions(659).name,
						"The items in the party chest are worth "
								+ PartyRoom.getTotalCoins() + "",
						"coins! Hang around until they drop and you might get",
						"something valueable!" }, IS_NPC, 659, 9843);
	}

}
