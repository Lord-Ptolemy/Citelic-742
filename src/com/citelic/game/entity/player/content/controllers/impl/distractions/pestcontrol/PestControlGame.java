package com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class PestControlGame extends Controller {

	private PestControl control;
	private double points;

	@Override
	public boolean canMove(int dir) {
		Tile toTile = new Tile(
				player.getX() + Utilities.DIRECTION_DELTA_X[dir], player.getY()
						+ Utilities.DIRECTION_DELTA_Y[dir], player.getZ());
		return !control.isBrawlerAt(toTile);
	}

	@Override
	public boolean canSummonFamiliar() {
		player.getPackets()
				.sendGameMessage(
						"You feel it's best to keep your Familiar away during this game.");
		return false;
	}

	@Override
	public void forceClose() {
		if (control != null) {
			if (control.getPortalCount() != 0) {
				if (control.getPlayers().contains(player))
					control.getPlayers().remove(player);
			}
			player.useStairs(-1, Lander.getLanders()[control.getPestData()
					.ordinal()].getLanderRequierment().getExitTile(), 1, 2);
		} else
			player.useStairs(-1, new Tile(2657, 2639, 0), 1, 2);
		player.setForceMultiArea(false);
		player.getInterfaceManager().closeOverlay(false);
		player.reset();
	}

	public double getPoints() {
		return points;
	}

	@Override
	public boolean login() {
		return true;
	}

	@Override
	public boolean logout() {
		if (control != null)
			control.getPlayers().remove(player);
		return false;
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean sendDeath() {
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.reset();
					player.setNextTile(control.getWorldTile(
							35 - Utilities.random(4),
							54 - (Utilities.random(3))));
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
		updatePestPoints();
		player.getInterfaceManager().sendOverlay(408, false);
	}

	public void setPoints(double points) {
		this.points = points;
	}

	@Override
	public void start() {
		control = (PestControl) getArguments()[0];
		setArguments(null);
		setPoints(0.0D);
		sendInterfaces();
		player.setForceMultiArea(true);
	}

	@Override
	public void trackXP(int skillId, int addedXp) {
		if (skillId == 3) // hp
			setPoints(getPoints() + ((addedXp) * 2.5));
		updatePestPoints();
	}

	private void updatePestPoints() {
		boolean isGreen = getPoints() > 750;
		player.getPackets().sendIComponentText(408, 11,
				(isGreen ? "<col=75AE49>" : "") + (int) getPoints() + "</col>");
	}
}
