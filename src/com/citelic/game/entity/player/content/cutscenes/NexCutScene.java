package com.citelic.game.entity.player.content.cutscenes;

import java.util.ArrayList;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.actions.CutsceneAction;
import com.citelic.game.entity.player.content.cutscenes.actions.LookCameraAction;
import com.citelic.game.entity.player.content.cutscenes.actions.PosCameraAction;
import com.citelic.game.map.tile.Tile;

public class NexCutScene extends Cutscene {

	private Tile dir;
	private int selected;

	public NexCutScene(Tile dir, int selected) {
		this.dir = dir;
		this.selected = selected;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		int xExtra = 0;
		int yExtra = 0;
		if (selected == 0)
			yExtra -= 7;
		else if (selected == 2)
			yExtra += 7;
		else if (selected == 1)
			xExtra -= 7;
		else
			xExtra += 7;
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new PosCameraAction(getX(player, 2925 + xExtra), getY(
				player, 5203 + yExtra), 2500, -1));
		actionsList.add(new LookCameraAction(getX(player, dir.getX()), getY(
				player, dir.getY()), 2500, 3));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

}
