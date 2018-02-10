package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.MobDamage;
import gvlfm78.plugin.OldCombatMechanics.utilities.WeaponDamages;

/**
 * Created by Rayzr522 on 6/25/16.
 */
public class ModuleOldToolDamage implements Listener {

    private String[] weapons = {"sword", "axe", "pickaxe", "spade", "hoe"};

    private static ModuleOldToolDamage INSTANCE;

    public ModuleOldToolDamage(OCMMain plugin) {
        INSTANCE = this;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamaged(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();

        if (!(damager instanceof Player)) return;

        Player p = (Player) damager;
        if (p.getInventory().getItemInMainHand() == null) return;

        Material mat = p.getInventory().getItemInMainHand().getType();

        if (isHolding(mat, weapons))
            onAttack(e, p, mat);
    }

    private void onAttack(EntityDamageByEntityEvent e, Player p, Material mat) {
        ItemStack item = p.getInventory().getItemInMainHand();
        EntityType entity = e.getEntityType();

        double baseDamage = e.getDamage();
        double enchantmentDamage = (MobDamage.applyEntityBasedDamage(entity, item, baseDamage)
                + getSharpnessDamage(item.getEnchantmentLevel(Enchantment.DAMAGE_ALL))) - baseDamage;

        double divider = WeaponDamages.getDamage(mat);
        if(divider <= 0) divider = 1;
        double newDamage = (baseDamage - enchantmentDamage) / divider;
        newDamage += enchantmentDamage;//Re-add damage from enchantments
        if (newDamage < 0) newDamage = 0;
        e.setDamage(newDamage);
    }

    public static void onAttack(EntityDamageByEntityEvent e) {
        INSTANCE.onEntityDamaged(e);
    }

    private double getSharpnessDamage(int level) {
        return level >= 1 ? level * 1.25 : 0;
    }

    private boolean isHolding(Material mat, String type) {
        return mat.toString().endsWith("_" + type.toUpperCase() );
    }

    private boolean isHolding(Material mat, String[] types) {
        boolean hasAny = false;
        for (String type : types)
            if (isHolding(mat, type))
                hasAny = true;
        return hasAny;
    }

}
