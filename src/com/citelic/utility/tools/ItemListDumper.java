package com.citelic.utility.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.utility.Utilities;

public class ItemListDumper {

	public static int convertInt(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static void main(String[] args) {
		try {
			new ItemListDumper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ItemListDumper() throws IOException {
		Cache.init();
		File file = new File("itemList.txt"); // = new
		// File("information/itemlist.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// writer.append("//Version = 709\n");
		writer.append("//Version = 718\n");
		writer.flush();
		for (int id = 0; id < Utilities.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
			if (def.getName().equals("null"))
				continue;
			writer.append(id + " - " + def.getName());
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

}
