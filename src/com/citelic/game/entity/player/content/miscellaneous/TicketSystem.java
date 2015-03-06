package com.citelic.game.entity.player.content.miscellaneous;

import java.util.ArrayList;
import java.util.Iterator;

import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class TicketSystem {

	public static class TicketEntry {
		private Player player;
		private Tile tile;

		public TicketEntry(Player player) {
			this.player = player;
			this.tile = player;
		}

		public Player getPlayer() {
			return player;
		}

		public Tile getTile() {
			return tile;
		}
	}

	public static final ArrayList<TicketEntry> tickets = new ArrayList<TicketEntry>();

	public static void answerTicket(Player player) {
		removeTicket(player);
		filterTickets();
		if (tickets.isEmpty()) {
			player.getPackets().sendGameMessage(
					"There are no tickets open, congratulations!.");
			if (player.getUsername().equalsIgnoreCase("kevin")
					&& Utilities.random(5) == 0) {
				player.getPackets().sendOpenURL("http://420yolo.com");
			}
			return;
		} else if (player.getTemporaryAttributtes().get("ticketTarget") != null) {
			removeTicket(player);
		}
		while (tickets.size() > 0) {
			TicketEntry ticket = tickets.get(0);// next in line
			Player target = ticket.player;
			if (target == null)
				continue; // shouldn't happen but k
			if (target.getInterfaceManager().containsChatBoxInter()
					|| target.getControllerManager().getController() != null
					|| target.getInterfaceManager().containsInventoryInter()
					|| target.getInterfaceManager().containsScreenInter()) {
				tickets.remove(0);
				continue;
			}
			player.getTemporaryAttributtes().put("ticketTarget", ticket);
			target.setNextTile(player);
			tickets.remove(ticket);
			player.setNextForceTalk(new ForceTalk(
					"Hello, how may I help you today?"));
			break;
		}
	}

	public static boolean canSubmitTicket() {
		filterTickets();
		return true;
	}

	public static void filterTickets() {
		for (Iterator<TicketEntry> it = tickets.iterator(); it.hasNext();) {
			TicketEntry entry = it.next();
			if (entry.player.hasFinished())
				it.remove();
		}
	}

	public static void removeTicket(Player player) {
		Object att = player.getTemporaryAttributtes().get("ticketTarget");
		if (att == null)
			return;
		TicketEntry ticket = (TicketEntry) att;
		Player target = ticket.getPlayer();
		target.setNextTile(ticket.getTile());
		target.getTemporaryAttributtes().remove("ticketRequest");
		player.getTemporaryAttributtes().remove("ticketTarget");
	}

	public static void requestTicket(Player player) {
		if (player.getInterfaceManager().containsChatBoxInter()
				|| player.getInterfaceManager().containsInventoryInter()
				|| player.getInterfaceManager().containsScreenInter()) {
			player.getPackets()
					.sendGameMessage(
							"Please finish what you're doing before requesting a ticket.");
			return;
		}
		if (!canSubmitTicket()
				|| player.getTemporaryAttributtes().get("ticketRequest") != null
				|| player.getControllerManager().getController() != null) {
			player.getPackets()
					.sendGameMessage("You cannot send a ticket yet!");
			return;
		}
		player.getTemporaryAttributtes().put("ticketRequest", true);
		tickets.add(new TicketEntry(player));
		for (Player mod : Engine.getPlayers()) {
			if (mod == null || mod.hasFinished() || !mod.isActive()
					|| (mod.getRights() < 1 && !mod.isSupporter()))
				continue;
			mod.getPackets().sendGameMessage(
					"A ticket has been submitted by " + player.getDisplayName()
							+ "! ::answerticket to solve it!");
			mod.getPackets()
					.sendGameMessage(
							"There is currently " + tickets.size()
									+ " tickets active.");
		}
	}
}
