package gvlfm78.plugin.OldCombatMechanics.module;

import java.util.Collection;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

public class ModuleFishingKnockback implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRodLand(ProjectileHitEvent e) {

		Entity hookEntity = e.getEntity();
		World world = hookEntity.getWorld();

		if (e.getEntityType() != EntityType.FISHING_HOOK)
			return;


		Entity hitent = null;

		try{
			hitent = e.getHitEntity();
		}
		catch(NoSuchMethodError e1){ //For older version that don't have such method
			Collection<Entity> entities = world.getNearbyEntities(hookEntity.getLocation(), 0.25, 0.25, 0.25);

			for (Entity entity : entities) {
				if (entity instanceof Player)
					hitent = entity;
				break;

			}
		}

		if(hitent == null) return;
		if(!(hitent instanceof Player)) return;

		FishHook hook = (FishHook) hookEntity;
		Player rodder = (Player) hook.getShooter();
		Player player = (Player) hitent;

		if (player.getUniqueId().equals(rodder.getUniqueId()))
			return;

		if(player.getGameMode() == GameMode.CREATIVE) return;

		double damage = 0.2;

		EntityDamageEvent event = makeEvent(rodder, player, damage);
		Bukkit.getPluginManager().callEvent(event);

		player.damage(damage);

		Location loc = player.getLocation().add(0, 0.5, 0);
		player.teleport(loc);
		player.setVelocity(loc.subtract(rodder.getLocation()).toVector().normalize().multiply(0.4));
	}

	/**
	 * This is to cancel dragging the player closer when you reel in
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onReelIn(PlayerFishEvent e){
		if(e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
		e.getHook().remove(); //Nuke the bobber and don't do anything else
		e.setCancelled(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private EntityDamageEvent makeEvent(Player rodder, Player player, double damage) {

		return new EntityDamageByEntityEvent(rodder, player,
				EntityDamageEvent.DamageCause.PROJECTILE,
				new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
				new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damage))));
	}
}