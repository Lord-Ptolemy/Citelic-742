package com.citelic.utility.tools;

import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.utility.Utilities;

public class ObjectCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int i = 0; i < Utilities.getObjectDefinitionsSize(); i++) {
			ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(i);
			if (def.containsOption("Steal-from")) {
				System.out.println(def.id + " - " + def.name);
			}
		}
	}

}
