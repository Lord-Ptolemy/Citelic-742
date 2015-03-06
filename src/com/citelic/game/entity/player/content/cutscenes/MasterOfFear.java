package com.citelic.game.entity.player.content.cutscenes;

import java.util.ArrayList;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.actions.CutsceneAction;
import com.citelic.game.entity.player.content.cutscenes.actions.InterfaceAction;
import com.citelic.game.entity.player.content.cutscenes.actions.LookCameraAction;
import com.citelic.game.entity.player.content.cutscenes.actions.PosCameraAction;

public class MasterOfFear extends Cutscene {

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new InterfaceAction(115, 2));
		actionsList.add(new PosCameraAction(getX(player, player.getX() + 5),
				getY(player, player.getY() + 3), 1500, -1));
		actionsList.add(new LookCameraAction(getX(player, player.getX() - 2),
				getY(player, player.getY()), 1500, 5));
		return actionsList.toArray(new CutsceneAction[0]);
	}

	@Override
	public boolean hiddenMinimap() {
		return true;
	}
}
