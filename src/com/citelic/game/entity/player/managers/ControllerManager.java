package com.citelic.game.entity.player.managers;

import java.io.Serializable;

import com.citelic.GameConstants;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.consumables.Foods.Food;
import com.citelic.game.entity.player.content.actions.consumables.Potions.Pot;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.ControllerHandler;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public final class ControllerManager implements Serializable {

	private static final long serialVersionUID = 2084691334731830796L;

	private transient Player player;
	private transient Controller controller;
	private transient boolean inited;
	private Object[] lastControllerArguments;

	private String lastController;

	public ControllerManager() {
		lastController = /* Settings.HOSTED ? */GameConstants.START_CONTROLLER;// :
		// "TutorialIsland";
		// // se
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canAddInventoryItem(itemId, amount);
	}

	public boolean canAttack(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canAttack(entity);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canDropItem(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.canDropItem(item);
	}

	public boolean canEat(Food food) {
		if (controller == null || !inited)
			return true;
		return controller.canEat(food);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.canEquip(slotId, itemId);
	}

	public boolean canHit(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canHit(entity);
	}

	public boolean canMove(int dir) {
		if (controller == null || !inited)
			return true;
		return controller.canMove(dir);
	}

	public boolean canPlayerOption1(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption1(target);
	}

	public boolean canPot(Pot pot) {
		if (controller == null || !inited)
			return true;
		return controller.canPot(pot);
	}

	public boolean canSummonFamiliar() {
		if (controller == null || !inited)
			return true;
		return controller.canSummonFamiliar();
	}

	public boolean processCommand(String s, boolean b, boolean c) {
		if (controller == null || !inited)
			return true;
		return controller.processCommand(s, b, c);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controller == null || !inited)
			return true;
		return controller.canUseItemOnItem(itemUsed, usedWith);
	}

	public boolean canWalk() {
		if (controller == null || !inited)
			return true;
		return controller.canWalk();
	}

	public void forceStop() {
		if (controller != null) {
			controller.forceClose();
			controller = null;
		}
		lastControllerArguments = null;
		lastController = null;
		inited = false;
	}

	public Controller getController() {
		return controller;
	}

	public Object[] getLastControllerArguments() {
		return lastControllerArguments;
	}

	public boolean keepCombating(Entity target) {
		if (controller == null || !inited)
			return true;
		return controller.keepCombating(target);
	}

	public void login() {
		if (lastController == null)
			return;
		controller = ControllerHandler.getController(lastController);
		if (controller == null) {
			forceStop();
			return;
		}
		controller.setPlayer(player);
		if (controller.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controller == null)
			return;
		if (controller.logout())
			forceStop();
	}

	public void magicTeleported(int type) {
		if (controller == null || !inited)
			return;
		controller.magicTeleported(type);
	}

	public void moved() {
		if (controller == null || !inited)
			return;
		controller.moved();
	}

	public void process() {
		if (controller == null || !inited)
			return;
		controller.process();
	}

	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (controller == null || !inited)
			return true;
		return controller.processButtonClick(interfaceId, componentId, slotId,
				packetId);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnNPC(npc, item);
	}

	public boolean processItemOnPlayer(Player player, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnPlayer(player, itemId);
	}

	public boolean processItemTeleport(Tile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processItemTeleport(toTile);
	}

	public boolean processMagicTeleport(Tile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processMagicTeleport(toTile);
	}

	public boolean processMoneyPouch() {
		if (controller == null || !inited)
			return true;
		return controller.processMoneyPouch();
	}

	public boolean processNPCClick1(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick1(npc);
	}

	public boolean processNPCClick2(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick3(npc);
	}

	public boolean processObjectClick1(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick1(object);
	}

	public boolean processObjectClick2(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick2(object);
	}

	public boolean processObjectClick3(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick3(object);
	}

	public boolean processObjectClick4(GameObject object) {
		return true; // unused atm
	}

	public boolean processObjectClick5(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick5(object);
	}

	public boolean processObjectTeleport(Tile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectTeleport(toTile);
	}

	public void removeControllerWithoutCheck() {
		controller = null;
		lastControllerArguments = null;
		lastController = null;
		inited = false;
	}

	public boolean sendDeath() {
		if (controller == null || !inited)
			return true;
		return controller.sendDeath();
	}

	public void sendInterfaces() {
		if (controller == null || !inited)
			return;
		controller.sendInterfaces();
	}

	public void setLastControllerArguments(Object[] lastControllerArguments) {
		this.lastControllerArguments = lastControllerArguments;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void startController(Object key, Object... parameters) {
		if (controller != null)
			forceStop();
		controller = (Controller) (key instanceof Controller ? key
				: ControllerHandler.getController(key));
		if (controller == null)
			return;
		controller.setPlayer(player);
		lastControllerArguments = parameters;
		lastController = (String) key;
		controller.start();
		inited = true;
	}

	public void trackXP(int skillId, int addedXp) {
		if (controller == null || !inited)
			return;
		controller.trackXP(skillId, addedXp);
	}

	public boolean useDialogueScript(Object key) {
		if (controller == null || !inited)
			return true;
		return controller.useDialogueScript(key);
	}

	public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controller == null || !inited)
			return true;
		return controller.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean handleItemOnObject(GameObject object, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.handleItemOnObject(object, item);
	}

}
