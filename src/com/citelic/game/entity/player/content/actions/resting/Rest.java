package com.citelic.game.entity.player.content.actions.resting;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.utility.Utilities;

public class Rest extends Action {

	private static int[][] REST_DEFS = { { 5713, 1549, 5748 },
			{ 11786, 1550, 11788 }, { 5713, 1551, 2921 } };

	private int index;

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.getPackets().sendGameMessage(
					"You can't rest while you're poisoned.");
			return false;
		}
		if (player.getAttackedByDelay() + 10000 > Utilities.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"You can't rest until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (player.getRunEnergy() > 90 && player.getRunEnergy() == 100) {
			player.setRunEnergy(100);
		} else if (player.getRunEnergy() < 90) {
			player.setRunEnergy(player.getRunEnergy() + Utilities.random(10));
		}
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		index = Utilities.random(REST_DEFS.length);
		player.setResting(true);
		player.setNextAnimation(new Animation(REST_DEFS[index][0]));
		player.getPlayerAppearance().setRenderEmote(REST_DEFS[index][1]);
		return true;
	}

	@Override
	public void stop(Player player) {
		player.setResting(false);
		player.getPlayerAppearance().setRenderEmote(-1);
		player.getEmotesManager().setNextEmoteEnd();
		player.setNextAnimation(new Animation(REST_DEFS[index][2]));
	}
}
