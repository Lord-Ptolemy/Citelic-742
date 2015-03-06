package com.citelic.game.entity.player.content.cutscenes;

import java.util.ArrayList;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.actions.CutsceneAction;
import com.citelic.game.entity.player.content.cutscenes.actions.LookCameraAction;
import com.citelic.game.entity.player.content.cutscenes.actions.PosCameraAction;

public class EdgeWilderness extends Cutscene {

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();

		actionsList.add(new PosCameraAction(80, 75, 5000, 6, 6, -1));
		actionsList.add(new LookCameraAction(30, 75, 1000, 6, 6, 10));
		actionsList.add(new PosCameraAction(30, 75, 5000, 3, 3, 10));

		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

}
