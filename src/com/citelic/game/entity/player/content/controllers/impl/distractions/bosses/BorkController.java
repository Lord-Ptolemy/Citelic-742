package com.citelic.game.entity.player.content.controllers.impl.distractions.bosses;

import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;

public class BorkController extends Controller {

	public static int borkStage;
	public NPC bork;

	int stage = 0;

	@Override
	public boolean canAttack(Entity target) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean canMove(int dir) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public void process() {
		if (borkStage == 0) {
			if (stage == 0) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new Tile(3114,
						5528, 0));
			}
			if (stage == 5) {
				sendInterfaces();
			}
			if (stage == 18) {
				player.getPackets().closeInterface(
						player.getInterfaceManager().hasRezizableScreen() ? 1
								: 11);
				player.getDialogueManager().startDialogue("DagonHai", 7137,
						player, -1);
				player.getPackets()
						.sendGameMessage(
								"The choas teleporter transports you to an unknown portal.");
				removeController();
			}
		} else if (borkStage == 1) {
			if (stage == 4) {
				sendInterfaces();
				bork.setCantInteract(true);
			} else if (stage == 14) {
				Engine.spawnNPC(7135, new Tile(bork, 1), -1, true, true);
				Engine.spawnNPC(7135, new Tile(bork, 1), -1, true, true);
				Engine.spawnNPC(7135, new Tile(bork, 1), -1, true, true);
				player.getPackets().closeInterface(
						player.getInterfaceManager().hasRezizableScreen() ? 1
								: 11);
				bork.setCantInteract(false);
				bork.setNextForceTalk(new ForceTalk(
						"Destroy the intruder, my Legions!"));
				removeController();
			}
		}
		stage++;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (borkStage == 0) {
			player.getInterfaceManager().sendTab(
					player.getInterfaceManager().hasRezizableScreen() ? 1 : 11,
					692);
		} else if (borkStage == 1) {
			for (Entity t : bork.getPossibleTargets()) {
				Player pl = (Player) t;
				pl.getInterfaceManager().sendTab(
						pl.getInterfaceManager().hasRezizableScreen() ? 1 : 11,
						691);
			}
		}
	}

	@Override
	public void start() {
		borkStage = (int) getArguments()[0];
		bork = (NPC) getArguments()[1];
		process();
	}

}
