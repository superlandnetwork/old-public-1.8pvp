package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ModuleProjectileKnockback implements Listener {

	@EventHandler(priority=EventPriority.NORMAL)
	public void onEntityHit(EntityDamageByEntityEvent e){

		EntityType type = e.getDamager().getType();
		
		switch(type){
		case SNOWBALL: case EGG: case ENDER_PEARL:
			e.setDamage(0.1);
		default:
			break;
		}

	}
}