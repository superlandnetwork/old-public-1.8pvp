package gvlfm78.plugin.OldCombatMechanics.module;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;

public class ModuleDisableCrafting implements Listener {

	public static ModuleDisableCrafting INSTANCE;
	private List<Material> denied = new ArrayList<>();

	public ModuleDisableCrafting(OCMMain plugin) {
		INSTANCE = this;
		reload();
	}

	public void reload(){
		denied.add(Material.SHIELD);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPrepareItemCraft (PrepareItemCraftEvent e) {
		if (e.getViewers().size() < 1) return;

		CraftingInventory inv = e.getInventory();
		ItemStack result = inv.getResult();
		
		if(result != null && denied.contains(result.getType()))
			inv.setResult(null);
	}
}