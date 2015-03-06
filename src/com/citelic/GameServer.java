package com.citelic;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import knol.marko.voting.ConnectorManager;

import com.alex.store.Index;
import com.citelic.cache.Cache;
import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.cache.impl.item.ItemsEquipIds;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.combat.CombatScriptsHandler;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.fishing.FishingSpotsHandler;
import com.citelic.game.entity.player.content.controllers.ControllerHandler;
import com.citelic.game.entity.player.content.cutscenes.CutscenesHandler;
import com.citelic.game.entity.player.content.dialogue.DialogueHandler;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;
import com.citelic.game.entity.player.content.socialization.clans.ClansManager;
import com.citelic.game.lobby.WorldList;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.Region;
import com.citelic.networking.ServerChannelHandler;
import com.citelic.utility.AutoBackup;
import com.citelic.utility.DTRank;
import com.citelic.utility.DisplayNames;
import com.citelic.utility.IPBanL;
import com.citelic.utility.IPMute;
import com.citelic.utility.Logger;
import com.citelic.utility.MACBan;
import com.citelic.utility.MusicHints;
import com.citelic.utility.PkRank;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.ShopsHandler;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.Huffman;
import com.citelic.utility.item.ItemBonuses;
import com.citelic.utility.item.ItemExamines;
import com.citelic.utility.item.ItemWeights;
import com.citelic.utility.map.MapArchiveKeys;
import com.citelic.utility.map.MapAreas;
import com.citelic.utility.npc.NPCBonuses;
import com.citelic.utility.npc.NPCCombatDefinitionsL;
import com.citelic.utility.npc.NPCDrops;
import com.citelic.utility.npc.NPCExamines;
import com.citelic.utility.npc.NPCSpawns;
import com.citelic.utility.objects.ObjectExamines;
import com.citelic.utility.objects.ObjectSpawns;

public final class GameServer {

	private static void addAccountsSavingTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					saveFiles();
				} catch (final Throwable e) {
					Logger.handle(e);
				}
			}
		}, 10, 10, TimeUnit.MINUTES);
	}

	private static void addCleanMemoryTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < GameConstants.MINIMUM_RAM_ALLOCATED);
				} catch (final Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			for (final Region region : Engine.getRegions().values()) {
				region.removeMapFromMemory();
			}
		}
		for (final Index index : Cache.STORE.getIndexes()) {
			index.resetCachedFiles();
		}
		CoresManager.fastExecutor.purge();
		System.gc();
	}

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}

	public static void init() throws IOException {
		final long launchTime = Utilities.currentTimeMillis();
		new ConnectorManager("IP", "DATABASE", "USER", "PASSWORD");
		AutoBackup.init();
		Cache.init();
		ItemsEquipIds.init();
		Huffman.init();
		DisplayNames.init();
		ObjectExamines.initiate();
		IPBanL.init();
		MACBan.init();
		IPMute.init();
		PkRank.init();
		DTRank.init();
		CoresManager.init();
		Engine.init();
		MapArchiveKeys.init();
		MapAreas.init();
		ObjectSpawns.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		NPCExamines.init();
		CombatScriptsHandler.init();
		FishingSpotsHandler.init();
		ItemExamines.init();
		ItemWeights.init();
		ItemBonuses.init();
		MusicHints.init();
		ShopsHandler.init();
		WorldList.init();
		ClansManager.init();
		DialogueHandler.init();
		ControllerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();
		MapBuilder.init();
		ServerChannelHandler.init();
		addAccountsSavingTask();
		addCleanMemoryTask();
		Logger.log("Citelic launched in "
				+ (Utilities.currentTimeMillis() - launchTime) + " ms.");
	}

	public static void main(String[] args) throws Exception {
		init();
	}

	public static void saveFiles() throws Exception {
		for (final Player player : Engine.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished()) {
				continue;
			}
			SerializableFilesManager.savePlayer(player);
		}
		DisplayNames.save();
		IPBanL.save();
		IPMute.save();
		MACBan.save();
		PkRank.save();
		DTRank.save();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}

	private GameServer() {

	}
}