package com.citelic.game.entity.player.content.controllers.impl.distractions.godwars;

import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightCaves;

public class ZGDController extends Controller {

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public boolean login() {
		ZarosGodwars.addPlayer(player);
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		ZarosGodwars.removePlayer(player);
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeController();
	}

	public void remove() {
		ZarosGodwars.removePlayer(player);
		player.getPackets().sendGlobalConfig(1435, 255);
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 34 : 8);
	}

	@Override
	public boolean sendDeath() {
		remove();
		removeController();
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(601, true);
		player.getPackets().sendRunScript(1171);
	}

	@Override
	public void start() {
		ZarosGodwars.addPlayer(player);
		sendInterfaces();
	}
}
