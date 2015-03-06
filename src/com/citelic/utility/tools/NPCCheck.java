package com.citelic.utility.tools;

import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.utility.Utilities;

public class NPCCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int id = 0; id < Utilities.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			if (def.name.contains("Elemental")) {
				System.out.println(id + " - " + def.name);
			}
		}
	}

}
