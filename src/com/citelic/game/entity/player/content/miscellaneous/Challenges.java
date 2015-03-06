package com.citelic.game.entity.player.content.miscellaneous;

import com.citelic.game.entity.player.Player;

public class Challenges {

	public static void sendChallengeComplete(Player player, int spriteId,
			String taskName, String taskSet) {
		player.getInterfaceManager().sendOverlay(1055, true);
		player.getPackets().sendHideIComponent(1055, 0, false);
		player.getPackets().sendIComponentSprite(1055, 13, spriteId);
		// player.getPackets().sendItemOnIComponent(1055, 13, 14484, 1);
		player.getPackets().sendRunScript(3970);
		player.getPackets().sendRunScript(3969);
		player.getPackets().sendRunScript(3968);
		player.getPackets().sendRunScript(3967);
		player.getPackets().sendRunScript(3971);
		player.getPackets().sendIComponentText(1055, 15, taskName);
	}

}
