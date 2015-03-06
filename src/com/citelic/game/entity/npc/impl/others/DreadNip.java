package com.citelic.game.entity.npc.impl.others;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class DreadNip extends NPC {

	public static final String[] DREADNIP_MESSAGES = {
			"Your dreadnip couldn't attack so it left.",
			"The dreadnip gave up as you were too far away.",
			"Your dreadnip served its purpose and fled." };

	private Player owner;
	private int ticks;

	public DreadNip(Player owner, int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.owner = owner;
	}

	private void finish(int index) {
		if (index != -1) {
			owner.getPackets().sendGameMessage(DREADNIP_MESSAGES[index]);
			owner.getTemporaryAttributtes().remove("hasDN");
		}
		this.finish();
	}

	public Player getOwner() {
		return owner;
	}

	public int getTicks() {
		return ticks;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (owner == null || owner.hasFinished()) {
			finish(-1);
			return;
		} else if (getCombat().getTarget() == null
				|| getCombat().getTarget().isDead()) {
			finish(0);
			return;
		} else if (Utilities.getDistance(owner, this) >= 10) {
			finish(1);
			return;
		} else if (ticks++ == 33) {
			finish(2);
			return;
		}
	}
}
