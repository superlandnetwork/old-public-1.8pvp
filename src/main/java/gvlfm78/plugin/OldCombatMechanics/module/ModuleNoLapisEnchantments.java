package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class ModuleNoLapisEnchantments implements Listener {

	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		EnchantingInventory ei = (EnchantingInventory) e.getInventory(); //Not checking here because how else would event be fired?
		ei.setSecondary(getLapis());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!e.getInventory().getType().equals(InventoryType.ENCHANTING)) return;

		ItemStack item = e.getCurrentItem();
		if(item!=null && ( 
				(item.getType() == Material.INK_SACK && e.getRawSlot() == 1) ||
				(e.getCursor() != null && e.getCursor().getType() == Material.INK_SACK && e.getClick() == ClickType.DOUBLE_CLICK) ) )
			e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if(inv == null || inv.getType() != InventoryType.ENCHANTING) return;
			EnchantingInventory ei = (EnchantingInventory) inv;
			ei.setSecondary(new ItemStack(Material.AIR));
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Inventory inv = e.getInventory();
		if(inv == null || inv.getType() != InventoryType.ENCHANTING) return;
			( (EnchantingInventory) inv).setSecondary(getLapis());
	}

	private ItemStack getLapis(){
		Dye dye = new Dye();
		dye.setColor(DyeColor.BLUE);
		return dye.toItemStack(64);
	}

}
