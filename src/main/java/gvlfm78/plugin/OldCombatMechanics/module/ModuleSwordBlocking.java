package gvlfm78.plugin.OldCombatMechanics.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import gvlfm78.plugin.OldCombatMechanics.BlockingTask;
import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;
import gvlfm78.plugin.OldCombatMechanics.utilities.ItemUtils;

/**
 * Created by Rayzr522 on 7/4/16.
 */
@SuppressWarnings("deprecation")
public class ModuleSwordBlocking implements Listener {

	public static ModuleSwordBlocking INSTANCE;

	private static final ItemStack SHIELD = ItemUtils.makeItem("shield");

	private int restoreDelay;
	private String blockingDamageReduction;
	private boolean blacklist;

	private final Map<UUID, ItemStack> storedOffhandItems = new HashMap<>();
	private final Map<UUID, BukkitRunnable> correspondingTasks = new HashMap<>();
	private final List<Material> noBlockingItems = new ArrayList<>();

	private OCMMain plugin;
	
	public ModuleSwordBlocking(OCMMain plugin) {
		this.plugin = plugin;
		INSTANCE = this;
		reload();
	}

	public void reload(){
		restoreDelay = 40;
		blockingDamageReduction = "50%";
		blockingDamageReduction = blockingDamageReduction.replaceAll(" ", "");
		reloadNoBlockingItems();
		blacklist = true;
	}
	public void reloadNoBlockingItems(){
		noBlockingItems.clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() == null) return;

		Action action = e.getAction();

		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

		if (action == Action.RIGHT_CLICK_BLOCK && Config.getInteractiveBlocks().contains(e.getClickedBlock().getType())) return;

		Player p = e.getPlayer();

		UUID id = p.getUniqueId();

		/*if (isBlocking(id)) return;

		ItemStack item = e.getItem();

		if (!isHolding(item.getType(), "sword") || hasShield(p)) return;

		PlayerInventory inv = p.getInventory();

		storedOffhandItems.put(id, inv.getItemInOffHand());

		inv.setItemInOffHand(SHIELD);

		scheduleRestore(p);*/

		if (p.isBlocking()){
			BukkitRunnable task = correspondingTasks.get(id);
			if(task != null) task.cancel();
			correspondingTasks.remove(id);
		} else {
			ItemStack item = e.getItem();

			if (!isHolding(item.getType(), "sword") || hasShield(p)) return;

			PlayerInventory inv = p.getInventory();

			boolean isANoBlockingItem = noBlockingItems.contains(inv.getItemInOffHand().getType());

			if(blacklist && isANoBlockingItem || !blacklist && !isANoBlockingItem) return;

			storedOffhandItems.put(id, inv.getItemInOffHand());

			inv.setItemInOffHand(SHIELD);
		}

		correspondingTasks.put(id, scheduleRestore(p));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent e){
		Entity ent = e.getEntity();

		if(ent == null || !(ent instanceof Player)) return;

		Player p = (Player) ent;

		if(isBlocking(p.getUniqueId())){
			//If it's a player blocking
			//Instead of reducing damage to 33% apply config reduction

			double damageReduction = e.getDamage(); //Reducing by this would mean blocking all damage

			if(blockingDamageReduction.matches("\\d{1,3}%")){
				//Reduce damage by percentage
				int percentage = Integer.parseInt(blockingDamageReduction.replace("%", ""));
				damageReduction = damageReduction * percentage / 100;
			}
			else if (blockingDamageReduction.matches("\\d+")){
				//Reduce by specified amount of half-hearts
				damageReduction = Integer.parseInt(blockingDamageReduction);
			}
			else damageReduction = 0;

			if(damageReduction < 0) damageReduction = 0;

			//Only reduce damage if they were hit head on, i.e. the shield blocked some of the damage
			if(e.getDamage(DamageModifier.BLOCKING) >= 0) return;

			//Also make sure reducing the damage doesn't result in negative damage
			e.setDamage(DamageModifier.BLOCKING, 0);

			if(e.getFinalDamage() >= damageReduction)
				e.setDamage(DamageModifier.BLOCKING, damageReduction * -1);

			//Make maximum reduction possible be up to amount specified in config

			double minDamage = 1.0;
			if(e.getFinalDamage() < minDamage)
				e.setDamage(minDamage);

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent e) {
		restore(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogout(PlayerQuitEvent e) {
		restore(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!isBlocking(e.getEntity().getUniqueId())) return;

		Player p = e.getEntity();
		UUID id = p.getUniqueId();

		e.getDrops().replaceAll(item -> {

			if (item.getType().equals(Material.SHIELD))
				item = storedOffhandItems.get(id);

			return item;
		});

		storedOffhandItems.remove(id);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e){
		Player p = e.getPlayer();
		if (isBlocking(p.getUniqueId()))
			e.setCancelled(true);

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e){

		if(e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();

			if (isBlocking(p.getUniqueId())){
				ItemStack cursor = e.getCursor();
				ItemStack current = e.getCurrentItem();
				if(cursor != null && cursor.getType() == Material.SHIELD ||
						current != null && current.getType() == Material.SHIELD){
					e.setCancelled(true);
					restore(p);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent e){
		Item is = e.getItemDrop();

		Player p = e.getPlayer();

		if (isBlocking(p.getUniqueId()) && is.getItemStack().getType() == Material.SHIELD){
			e.setCancelled(true);
			restore(p);
		}
	}

	private BukkitRunnable scheduleRestore(final Player p) {
		BukkitRunnable run = new BlockingTask(p);
		run.runTaskLater(plugin, restoreDelay);

		return run;
	}

	public void restore(Player p) {
		UUID id = p.getUniqueId();

		if (!isBlocking(id)) return;

		if(p.isBlocking()) //They are still blocking with the shield so postpone restoring
			postponeRestoring(p);
		else {
			p.getInventory().setItemInOffHand(storedOffhandItems.get(id));
			storedOffhandItems.remove(id);
		}
	}

	private void postponeRestoring(Player p){
		UUID id = p.getUniqueId();
		correspondingTasks.get(id).cancel();
		correspondingTasks.remove(id);
		correspondingTasks.put(id, scheduleRestore(p));
	}

	public static void RestoreAll() {
		INSTANCE.restoreAll();
	}

	public void restoreAll() {
		for (Map.Entry<UUID, ItemStack> entry : storedOffhandItems.entrySet()) {

			UUID id = entry.getKey();
			Player p = Bukkit.getPlayer(id);

			p.getInventory().setItemInOffHand(storedOffhandItems.get(id));

			storedOffhandItems.remove(id);
		}
	}

	private boolean isBlocking(UUID uuid){
		return storedOffhandItems.containsKey(uuid);
	}

	private boolean hasShield(Player p) {
		return p.getInventory().getItemInOffHand().getType() == Material.SHIELD;
	}

	private boolean isHolding(Material mat, String type) {
		return mat.toString().endsWith("_" + type.toUpperCase());
	}
}
