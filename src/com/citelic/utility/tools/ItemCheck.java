package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.utility.Utilities;

public class ItemCheck {

	public static final void main(String[] args) throws IOException {
		Cache.init();
		int total = 0;
		for (int itemId = 0; itemId < Utilities.getItemDefinitionsSize(); itemId++) {
			if (ItemDefinitions.getItemDefinitions(itemId).isWearItem(true)
					&& !ItemDefinitions.getItemDefinitions(itemId).isNoted()) {
				File file = new File("bonuses/" + itemId + ".txt");
				if (!file.exists()) {
					System.out.println(file.getName());
					total++;
				}
			}
		}
		System.out.println("Total " + total);
	}
}
