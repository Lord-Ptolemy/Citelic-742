package com.citelic.utility.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.citelic.cache.Cache;
import com.citelic.cache.impl.item.ItemDefinitions;

/**
 * A tool that dumps the item weight from runescape.wikia.com * Format: itemId,
 * equipped kg, inventory kg - no support for half/whole pie pieces.
 * 
 * @author Displee
 */
public class WeightDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("weights.txt"));
		for (int i = 1; i <= 26000; i++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
			if (def != null) {
				String weight = getItemWeight(def.getName());
				writer.write(i + ", " + weight);
				writer.flush();
				writer.newLine();
				System.out.println("Dumped: " + i + ", " + weight);
			}
		}
		writer.close();
	}

	public static String getItemWeight(String name) throws IOException {
		try {
			String formattedName = name.replace(" ", "_");
			URL url = new URL("http://runescape.wikia.com/wiki/"
					+ formattedName);
			String line;
			URLConnection urlConnection = url.openConnection();
			urlConnection.setReadTimeout(5000);
			BufferedReader stream = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			while ((line = stream.readLine()) != null) {
				if (line.startsWith("<th style=\"white-space: nowrap;\"><a href=\"/wiki/Weight\" title=\"Weight\">Weight</a>")) {
					String nextLine = stream.readLine();
					if (nextLine.contains("<b>In inventory</b>:")) {
						String inv = nextLine.substring(
								nextLine.indexOf("<b>In inventory</b>: "),
								nextLine.indexOf("#"));
						String equip = nextLine.substring(
								nextLine.indexOf("<br /><b>Equipped</b>: "),
								nextLine.indexOf("#160;kg"));
						String invWeight = inv.substring(21, inv.indexOf("&"));
						String equipWeight = equip.substring(23,
								equip.indexOf("&"));
						nextLine = nextLine.replace(inv, equipWeight + ", ")
								.replace(equip, invWeight);
					}
					String weight = nextLine.replace("</th><td> ", "")
							.replace("*", "").replace("#160;", "")
							.replace("<", "").replace("k", "").replace("g", "")
							.replace(">", "");
					if (weight.endsWith("</small>")) {
						String toRemove = weight.substring(
								weight.indexOf("<small>"),
								weight.indexOf("</small>") + 8);
						return weight.replace(toRemove, "");
					}
					return weight;
				}
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown";
	}

}