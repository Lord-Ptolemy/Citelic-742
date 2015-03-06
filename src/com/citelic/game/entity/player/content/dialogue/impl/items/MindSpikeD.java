package com.citelic.game.entity.player.content.dialogue.impl.items;

import java.util.LinkedList;
import java.util.List;

import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class MindSpikeD extends Dialogue {

	private static final String[] NAMES = { "Air", "Water", "Earth", "Fire" };

	private int slot;
	private List<String> options;

	@Override
	public void finish() {

	}

	private int getTransformationForId(int index) {
		String selected = options.get(index);
		for (int i = 0; i < MindSpikeD.NAMES.length; i++) {
			if (selected == MindSpikeD.NAMES[i])
				return 23044 + i;
		}
		return -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId != Dialogue.OPTION_4) {
			player.getInventory().replaceItem(
					getTransformationForId(componentId == Dialogue.OPTION_1 ? 0
							: componentId == Dialogue.OPTION_2 ? 1 : 2), 1,
					slot);
			player.getPackets().sendGameMessage(
					"You alter your staff's elemental alignment.");
		}
		end();
	}

	@Override
	public void start() {
		int index = (int) parameters[0] - 23044;
		slot = (int) parameters[1];
		options = new LinkedList<String>();
		for (int i = 0; i < MindSpikeD.NAMES.length; i++) {
			if (i == index) {
				continue;
			}
			options.add(MindSpikeD.NAMES[i]);
		}
		options.add("None");
		sendOptionsDialogue("What would you like to bind?",
				options.toArray(new String[3]));
	}
}
