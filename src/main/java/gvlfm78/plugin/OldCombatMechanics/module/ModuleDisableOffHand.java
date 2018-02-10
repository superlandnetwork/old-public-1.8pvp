package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;

public class ModuleDisableOffHand implements Listener {

	public static ModuleDisableOffHand INSTANCE;

	public ModuleDisableOffHand(OCMMain plugin) {
		INSTANCE = this;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onSwapHandItems(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e){
		if(e.getInventory().getType() != InventoryType.CRAFTING) return; //Making sure it's a survival player's inventory

		if(e.getSlot() != 40) return;
		// If they didn't click into the offhand slot, return

		e.setResult(Event.Result.DENY);
		e.setCancelled(true);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onInventoryDrag(InventoryDragEvent e){
		if(e.getInventory().getType() != InventoryType.CRAFTING ||
				!e.getInventorySlots().contains(40)) return;

		
		e.setResult(Event.Result.DENY);
		e.setCancelled(true);
	}

}