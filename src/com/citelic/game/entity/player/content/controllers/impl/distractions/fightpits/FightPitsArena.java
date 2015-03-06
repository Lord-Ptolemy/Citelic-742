package com.citelic.game.entity.player.content.controllers.impl.distractions.fightpits;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightPits;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class FightPitsArena extends Controller {

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			if (canHit(target))
				return true;
			player.getPackets().sendGameMessage(
					"You're not allowed to attack yet!");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		return FightPits.canFight();
	}

	// fuck it dont dare touching here again or dragonkk(me) kills u irl :D btw
	// nice code it keeps nulling, fixed

	@Override
	public void forceClose() {
		FightPits.leaveArena(player, 3);
	}

	@Override
	public boolean login() { // shouldnt happen
		removeController();
		FightPits.leaveArena(player, 2);
		return false;
	}

	@Override
	public boolean logout() {
		FightPits.leaveArena(player, 0);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		FightPits.leaveArena(player, 3); // teled out somehow, impossible usualy
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 68222) {
			FightPits.leaveArena(player, 1);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					FightPits.leaveArena(player, 2);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager()
				.sendTab(
						player.getInterfaceManager().hasRezizableScreen() ? 34
								: 0, 373);
		if (FightPits.currentChampion != null)
			player.getPackets().sendIComponentText(373, 10,
					"Current Champion: JaLYt-Ket-" + FightPits.currentChampion);
	}

	@Override
	public void start() {
		sendInterfaces();
	}
}
