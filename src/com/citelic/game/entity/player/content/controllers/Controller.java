package com.citelic.game.entity.player.content.controllers;

import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.consumables.Foods.Food;
import com.citelic.game.entity.player.content.actions.consumables.Potions.Pot;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public abstract class Controller {

	// private static final long serialVersionUID = 8384350746724116339L;

	protected Player player;

	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you start trying
	 * to attack
	 */
	public boolean canAttack(Entity target) {
		return true;
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean canEat(Food food) {
		return true;
	}

	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * return can move that step
	 */
	public boolean canMove(int dir) {
		return true;
	}

	public boolean canPlayerOption1(Player target) {
		return true;
	}

	public boolean canPot(Pot pot) {
		return true;
	}

	public boolean canSummonFamiliar() {
		return true;
	}

	/**
	 * check if you can use commands in the controller
	 */
	public boolean processCommand(String s, boolean b, boolean c) {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	public boolean canWalk() {
		return true;
	}

	/**
	 * return can set that step
	 */
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	public void forceClose() {
	}

	public final Object[] getArguments() {
		return player.getControllerManager().getLastControllerArguments();
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	public boolean keepCombating(Entity target) {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean login() {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean logout() {
		return true;
	}

	/**
	 * called once teleport is performed
	 */
	public void magicTeleported(int type) {

	}

	public void moved() {

	}

	/**
	 * processes every game ticket, usualy not used
	 */
	public void process() {

	}

	/**
	 * return process normaly
	 */
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		return true;
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	public boolean processItemOnPlayer(Player player, int itemId) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processItemTeleport(Tile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	public boolean processMoneyPouch() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick1(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick2(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick3(GameObject object) {
		return true;
	}

	public boolean processObjectClick5(GameObject object) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processObjectTeleport(Tile toTile) {
		return true;
	}

	public final void removeController() {
		player.getControllerManager().removeControllerWithoutCheck();
	}

	/**
	 * return let default death
	 */
	public boolean sendDeath() {
		return true;
	}

	public void sendInterfaces() {

	}

	public final void setArguments(Object[] objects) {
		player.getControllerManager().setLastControllerArguments(objects);
	}

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public void trackXP(int skillId, int addedXp) {

	}

	/**
	 * return can use script
	 */
	public boolean useDialogueScript(Object key) {
		return true;
	}

	public boolean handleItemOnObject(GameObject object, Item item) {
		return true;
	}

	public boolean processObjectClick4(GameObject object) {
		return true;
	}

	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		// TODO Auto-generated method stub
		return false;
	}

}
