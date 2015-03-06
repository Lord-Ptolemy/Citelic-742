package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.map.tile.Tile;

public class FremennikShipmaster extends Dialogue {

	public static void sail(Player player, boolean backing) {
		player.useStairs(-1, backing ? new Tile(3254, 3171, 0) : new Tile(3511,
				3692, 0), 2, 3);
		if (backing)
			player.getControllerManager().forceStop();
		else
			player.getControllerManager().startController("Kalaboss");
	}

	int npcId;

	boolean backing;

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId) {
		// TODO Auto-generated method stub
		if (backing) {
			if (stage == -1) {
				stage = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.",
						"Not right now, thanks.", "You look happy.");
			} else if (stage == 0) {
				if (componentId == OPTION_1) {
					stage = 1;
					sendPlayerDialogue(9827, "Yes, please.");
				} else
					// not coded options
					end();
			} else if (stage == 1) {
				stage = 2;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
						NPCDefinitions.getNPCDefinitions(npcId).name,
						"All aboard, then." }, IS_NPC, npcId, 9827);
			} else if (stage == 2) {
				sail(player, backing);
				end();
			}
		} else {
			if (stage == -1) {
				stage = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.",
						"Not right now, thanks.", "Daemonheim?",
						"Why are you so grumpy?");
			} else if (stage == 0) {
				if (componentId == OPTION_1) {
					stage = 1;
					sendPlayerDialogue(9827, "Yes, please.");
				} else
					// not coded options
					end();
			} else if (stage == 1) {
				stage = 2;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
						NPCDefinitions.getNPCDefinitions(npcId).name,
						"Well, don't stand arround. Get on board." }, IS_NPC,
						npcId, 9827);
			} else if (stage == 2) {
				sail(player, backing);
				end();
			}
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		backing = (Boolean) parameters[1];
		if (backing) {
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Do you want a lift back to the south?" }, IS_NPC,
					npcId, 9827);
		} else {
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"You want passage to Daemonheim?" }, IS_NPC, npcId,
					9827);
		}

	}

}
