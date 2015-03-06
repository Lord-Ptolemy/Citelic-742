package com.citelic.game.entity.npc.impl.others;

import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class Bork extends NPC {

	public static long deadTime;

	public static boolean atBork(Tile tile) {
		if ((tile.getX() >= 3083 && tile.getX() <= 3120)
				&& (tile.getY() >= 5522 && tile.getY() <= 5550))
			return true;
		return false;
	}

	public static String convertToTime() {
		String time = "You have to wait "
				+ (getTime() == 0 ? "few more seconds" : getTime() + " mins")
				+ " to kill bork again.";
		return time;
	}

	public static int getTime() {
		return (int) (deadTime - System.currentTimeMillis() / 60000);
	}

	public Bork(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		deadTime = System.currentTimeMillis() + (1000 * 60 * 60);
		resetWalkSteps();
		for (Entity e : getPossibleTargets()) {
			if (e instanceof Player) {
				final Player player = (Player) e;
				player.getInterfaceManager().sendInterface(693);
				player.getDialogueManager().startDialogue("DagonHai", 7137,
						player, 1);
				player.setKilledBork(true);
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						player.stopAll();
					}
				}, 8);
			}
		}
		getCombat().removeTarget();
		setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				if (!isSpawned())
					setRespawnTask();
				stop();
			}

		}, 4);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 45, TimeUnit.MINUTES);
	}
}