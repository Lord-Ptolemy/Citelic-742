package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class Teleother extends Dialogue {

	final int TELEOTHER_INTERFACE = 326;

	public int TYPE_SELECTED = -1;

	public int ACCEPT_TELEPORT = 5;

	Player caster = null;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == ACCEPT_TELEPORT) {
			switch (TYPE_SELECTED) {
			case 0:
				player.sendTeleother(3222, 3219, 0);
				break;
			case 1:
				player.sendTeleother(2964, 3381, 0);
				break;
			case 2:
				player.sendTeleother(2757, 3479, 0);
				break;
			}
			player.closeInterfaces();
		} else
			end();
	}

	@Override
	public void start() {
		TYPE_SELECTED = (Integer) parameters[0];
		caster = (Player) parameters[1];
		if (caster == null)
			return;
		caster.setNextAnimation(new Animation(1818));
		caster.setNextGraphics(new Graphics(343));
		player.getInterfaceManager().sendInterface(TELEOTHER_INTERFACE);
		player.getPackets().sendIComponentText(TELEOTHER_INTERFACE, 1,
				caster.getDisplayName());
		switch (TYPE_SELECTED) {
		case 0:
			player.getPackets().sendIComponentText(TELEOTHER_INTERFACE, 3,
					"Lumbridge");
			break;
		case 1:
			player.getPackets().sendIComponentText(TELEOTHER_INTERFACE, 3,
					"Falador");
			break;
		case 2:
			player.getPackets().sendIComponentText(TELEOTHER_INTERFACE, 3,
					"Camelot");
			break;
		}
	}

}
