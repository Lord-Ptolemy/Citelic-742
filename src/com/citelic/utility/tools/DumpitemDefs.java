package com.citelic.utility.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.utility.Utilities;

public class DumpitemDefs {

	public static final void main(String[] args) throws IOException {
		Cache.init();
		String location = "ItemList.txt";
		System.out.println("Starting dumping ItemDefinitons at " + location);
		for (int itemId = 0; itemId < Utilities.getItemDefinitionsSize(); itemId++) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						location, true));
				writer.write(ItemDefinitions.getItemDefinitions(itemId).getId()
						+ " - " + ""
						+ ItemDefinitions.getItemDefinitions(itemId).getName()
						+ "");
				writer.newLine();
				writer.flush();
				writer.close();
				if (ItemDefinitions.getItemDefinitions(itemId).getId() == Utilities
						.getItemDefinitionsSize() - 1)
					System.out.println("Finished "
							+ Utilities.getItemDefinitionsSize() + " items.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
