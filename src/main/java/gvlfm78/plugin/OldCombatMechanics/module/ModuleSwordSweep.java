package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.OCMSweepTask;
import gvlfm78.plugin.OldCombatMechanics.utilities.ToolDamage;

/**
 * Created by Rayzr522 on 25/06/16.
 */
public class ModuleSwordSweep implements Listener {

	private OCMMain plugin;
	
	public ModuleSwordSweep(OCMMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamaged(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;

		Player p = (Player) e.getDamager();
		ItemStack weapon = p.getInventory().getItemInMainHand();

		if (isHolding(weapon.getType(), "sword"))
			onSwordAttack(e, p, weapon);
	}

	private void onSwordAttack(EntityDamageByEntityEvent e, Player p, ItemStack weapon) {
		//Disable sword sweep
		int locHashCode = p.getLocation().hashCode(); // ATTACKER
		
		int level = 0;
		
		try{ //In a try catch for servers that haven't updated
		level = weapon.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
		}
		catch(NoSuchFieldError e1){ }

		float damage = ToolDamage.getDamage(weapon.getType()) * level / (level + 1) + 1;

		if (e.getDamage() == damage) {
			// Possibly a sword-sweep attack
			if (sweepTask().swordLocations.contains(locHashCode)){
				e.setCancelled(true);
			}
		} else
			sweepTask().swordLocations.add(locHashCode);

		ModuleOldToolDamage.onAttack(e);
	}

	private OCMSweepTask sweepTask() {
		return plugin.sweepTask();
	}

	private boolean isHolding(Material mat, String type) {
		return mat.toString().endsWith("_" + type.toUpperCase());
	}

}