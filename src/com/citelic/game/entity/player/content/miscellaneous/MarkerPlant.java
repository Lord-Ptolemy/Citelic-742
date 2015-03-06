package com.citelic.game.entity.player.content.miscellaneous;

import java.io.Serializable;

import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;

public final class MarkerPlant implements Serializable {

	private static final long serialVersionUID = 1758770164752076710L;

	private final Player owner;
	private final NPC planted;

	public final int[] markerPlants = { 9150, 9151, 9152, 9153, 9154, 9155,
			9156, 9157, 9158, };

	public MarkerPlant(Player owner, NPC planted) {
		this.owner = owner;
		this.planted = planted;
	}

	public boolean throwPlant() {
		if (planted == null)
			return false;
		if (owner.getMarkerPlant() != null
				&& planted.equals(owner.getMarkerPlant().getMarkPlant())) {
			owner.getPackets().sendGameMessage(
					"You pull up the plant and throw it away.");
			owner.setMarkerPlant(null);
			planted.setMarkerPlantOwner(null);
			planted.finish();
			return false;
		}
		if (planted.getMarkerPlantOwner() != null) {
			owner.getPackets()
					.sendGameMessage("This is not your Marker Plant.");
		}
		return false;
	}

	public void logged() {
		owner.setMarkerPlant(null);
		planted.setMarkerPlantOwner(null);
		planted.finish();
		return;
	}

	public boolean plantPlant() {
		if (owner.getMarkerPlant() == null) {
			owner.setMarkerPlant(this);
			planted.setMarkerPlantOwner(owner);
			return false;
		} else {
			owner.getPackets().sendGameMessage("You cannot do that yet.");
			return false;
		}
	}

	public boolean isMarkerPlant(int npcId) {
		for (int markerPlant : markerPlants) {
			if (npcId == markerPlant) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public NPC getMarkPlant() {
		return planted;
	}

}