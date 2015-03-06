package com.citelic.utility.economy;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;

public final class EconomyPrices {

	public static int getPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.isNoted())
			itemId = defs.getCertId();
		else if (defs.isLended())
			itemId = defs.getLendId();
		if (!ItemConstants.isTradeable(new Item(itemId, 1)))
			return 0;
		if (itemId == 995) // TODO after here
			return 1;
		return defs.getValue() * 3; // TODO get price from real item from saved
		// prices from ge
	}

	private EconomyPrices() {

	}

	public static int getAlchPrice(Item item) {
		switch (item.getId()) {
		case 23659:
			item.getDefinitions().setValue(50000000);
			break;
		case 6570:
			item.getDefinitions().setValue(20000000);
			break;
		case 4151:
			item.getDefinitions().setValue(10000000);
			break;
		case 11694:
			item.getDefinitions().setValue(1500000000);
			break;
		case 11730:
			item.getDefinitions().setValue(40000000);
			break;
		case 18353:
			item.getDefinitions().setValue(285000000);
			break;
		case 6585:
			item.getDefinitions().setValue(28500000);
			break;
		case 14484:
			item.getDefinitions().setValue(1000000000);
			break;
		case 13740:
			item.getDefinitions().setValue(2000000000);
			break;
		case 13738:
			item.getDefinitions().setValue(1700000000);
			break;
		case 13744:
			item.getDefinitions().setValue(1500000000);
			break;
		case 13742:
			item.getDefinitions().setValue(1900000000);
			break;
		case 22494:
			item.getDefinitions().setValue(35000000);
			break;
		case 21787:
		case 21793:
		case 21790:
			item.getDefinitions().setValue(75000000);
			break;
		case 18357:
		case 18349:
		case 16425:
		case 16423:
		case 18351:
			item.getDefinitions().setValue(1000000000);
			break;
		case 6737:
		case 6731:
		case 6733:
		case 6734:
			item.getDefinitions().setValue(80000000);
			break;
		case 1038:
		case 1040:
		case 1042:
		case 1044:
		case 1046:
		case 1048:
			item.getDefinitions().setValue(2100000000);
			break;
		case 1050:
			item.getDefinitions().setValue(1);
			break;
		case 11724:
			item.getDefinitions().setValue(170000000);
			break;
		case 19784:
			item.getDefinitions().setValue(900000000);
			break;
		case 11726:
			item.getDefinitions().setValue(150000000);
			break;
		case 21371:
			item.getDefinitions().setValue(50000000);
			break;
		case 4722:
		case 4720:
		case 4718:
		case 4716:
			item.getDefinitions().setValue(30000000);
			break;
		}
		return item.getDefinitions().getValue();
	}
}
