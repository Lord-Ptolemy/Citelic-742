package com.citelic.game.entity.player.content.actions.resting;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.utility.Utilities;

public class Listen extends Action {

	private static int[][] LISTEN_DEFS = { { 5713, 1549, 5748 },
			{ 11786, 1550, 11788 }, { 5713, 1551, 2921 } // TODO
	// First
	// emote
	};

	private int index;

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.getPackets()
					.sendGameMessage(
							"You don't feel like listening to a musician while you're poisoned.");
			return false;
		}
		if (player.getAttackedByDelay() + 10000 > Utilities.currentTimeMillis()) {
			player.getPackets()
					.sendGameMessage(
							"You can't listen to a musician until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		index = Utilities.random(Listen.LISTEN_DEFS.length);
		player.setListening(true);
		player.setNextAnimation(new Animation(Listen.LISTEN_DEFS[index][0]));
		player.getGlobalPlayerUpdater().setRenderEmote(
				Listen.LISTEN_DEFS[index][1]);
		return true;
	}

	@Override
	public void stop(Player player) {
		player.setListening(false);
		player.setNextAnimation(new Animation(Listen.LISTEN_DEFS[index][2]));
		player.getEmotesManager().setNextEmoteEnd();
		player.getGlobalPlayerUpdater().setRenderEmote(-1);
	}
}