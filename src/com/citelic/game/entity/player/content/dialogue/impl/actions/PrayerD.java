package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.entity.player.content.actions.skills.prayer.BonesOnAltar;
import com.citelic.game.entity.player.content.actions.skills.prayer.BonesOnAltar.Bones;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;
import com.citelic.game.map.objects.GameObject;

public class PrayerD extends Dialogue {

	private Bones bones;
	private GameObject object;

	@Override
	public void start() {
		this.bones = (Bones) parameters[0];
		this.object = (GameObject) parameters[1];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.OFFER,
				"How many would you like to offer?", player.getInventory()
						.getItems().getNumberOf(bones.getBone()),
				new int[] { bones.getBone().getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new BonesOnAltar(object, bones.getBone(), SkillsDialogue
						.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {

	}

}