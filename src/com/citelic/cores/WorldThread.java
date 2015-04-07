package com.citelic.cores;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	@Override
	public final void run() {
		while (!CoresManager.shutdown) {
			WORLD_CYCLE++;
			long currentTime = Utilities.currentTimeMillis();
			Engine.currentTime = currentTime - Utilities.currentTimeMillis();
			try {
				EngineTaskManager.processTasks();
				for (Player player : Engine.getPlayers()) {
					if (player == null || !player.isActive()
							|| player.hasFinished())
						continue;
					if (currentTime - player.getPacketsDecoderPing() > GameConstants.MAX_PACKETS_DECODER_PING_DELAY
							&& player.getSession().getChannel().isOpen())
						player.getSession().getChannel().close();
					player.processEntity();
				}
				for (NPC npc : Engine.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				for (Player player : Engine.getPlayers()) {
					if (player == null || !player.isActive()
							|| player.hasFinished())
						continue;
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
				}
				for (Player player : Engine.getPlayers()) {
					if (player == null || !player.isActive()
							|| player.hasFinished())
						continue;
					player.resetMasks();
				}
				for (NPC npc : Engine.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.resetMasks();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
			long sleepTime = GameConstants.WORLD_CYCLE_TIME + currentTime
					- Utilities.currentTimeMillis();
			if (sleepTime <= 0)
				continue;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Logger.handle(e);
			}
		}
	}

}
