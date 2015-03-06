package com.citelic.game.entity.npc.impl.fightkiln;

import java.util.ArrayList;
import java.util.List;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightKiln;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class HarAken extends NPC {

	private long time;
	private long spawnTentacleTime;
	private boolean underLava;
	private List<HarAkenTentacle> tentacles;

	private FightKiln controler;

	public HarAken(int id, Tile tile, FightKiln controler) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		this.controler = controler;
		tentacles = new ArrayList<HarAkenTentacle>();
	}

	public void process() {
		if (isDead())
			return;
		if (time != 0) {
			if (time < Utilities.currentTimeMillis()) {
				if (underLava) {
					controler.showHarAken();
					resetTimer();
				} else
					controler.hideHarAken();
			}
			if (spawnTentacleTime < Utilities.currentTimeMillis())
				spawnTentacle();

		}
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
	}

	public void removeTentacle(HarAkenTentacle tentacle) {
		tentacles.remove(tentacle);

	}

	public void removeTentacles() {
		for (HarAkenTentacle t : tentacles)
			t.finish();
		tentacles.clear();
	}

	public void resetTimer() {
		underLava = !underLava;
		if (time == 0)
			spawnTentacleTime = Utilities.currentTimeMillis() + 9000;
		time = Utilities.currentTimeMillis() + (underLava ? 45000 : 30000);
	}

	@Override
	public void sendDeath(Entity source) {
		setNextGraphics(new Graphics(2924 + getSize()));
		if (time != 0) {
			removeTentacles();
			controler.removeNPC();
			time = 0;
		}
		super.sendDeath(source);
	}

	public void spawnTentacle() {
		tentacles.add(new HarAkenTentacle(Utilities.random(2) == 0 ? 15209
				: 15210, controler.getTentacleTile(), this));
		spawnTentacleTime = Utilities.currentTimeMillis()
				+ Utilities.random(15000, 25000);
	}

}
