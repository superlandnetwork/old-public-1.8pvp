package gvlfm78.plugin.OldCombatMechanics.module;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;

/**
 * Created by Rayzr522 on 25/6/16.
 */
public class ModuleGoldenApple implements Listener {

	private List<PotionEffect> enchantedGoldenAppleEffects, goldenAppleEffects;
	private ItemStack napple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
	private ShapedRecipe r;

	public static ModuleGoldenApple INSTANCE;
	private OCMMain plugin;

	public ModuleGoldenApple(OCMMain plugin) {
		this.plugin = plugin;
		INSTANCE = this;
		reloadRecipes();
	}

	@SuppressWarnings("deprecation")
	public void reloadRecipes(){
		enchantedGoldenAppleEffects = getPotionEffects("napple");
		goldenAppleEffects = getPotionEffects("gapple");

		try{
			r = new ShapedRecipe(new NamespacedKey(plugin, "MINECRAFT"), napple);
		}
		catch(NoClassDefFoundError e) {
			r = new ShapedRecipe(napple);
		}
		r.shape("ggg", "gag", "ggg").setIngredient('g', Material.GOLD_BLOCK).setIngredient('a', Material.APPLE);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onItemConsume(PlayerItemConsumeEvent e) {

		if (e.getItem() == null || e.getItem().getType() != Material.GOLDEN_APPLE) return;

		e.setCancelled(true);

		ItemStack originalItem = e.getItem();

		ItemStack item = e.getItem();

		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();

		int foodLevel = p.getFoodLevel();
		foodLevel = foodLevel + 4 > 20 ? 20 : foodLevel + 4;

		item.setAmount(item.getAmount() - 1);

		p.setFoodLevel(foodLevel);

		if (item.getDurability() == (short) 1) {

			for (PotionEffect effect : enchantedGoldenAppleEffects)
				e.getPlayer().removePotionEffect(effect.getType());

			e.getPlayer().addPotionEffects(enchantedGoldenAppleEffects);

		} else {

			for (PotionEffect effect : goldenAppleEffects)
				e.getPlayer().removePotionEffect(effect.getType());

			e.getPlayer().addPotionEffects(goldenAppleEffects);
		}
		if (item.getAmount() <= 0)
			item = null;

		ItemStack mainHand = inv.getItemInMainHand();
		ItemStack offHand = inv.getItemInOffHand();

		if(mainHand.equals(originalItem))
			inv.setItemInMainHand(item);

		else if(offHand.equals(originalItem))
			inv.setItemInOffHand(item);

		else if(mainHand.getType() == Material.GOLDEN_APPLE)
			inv.setItemInMainHand(item);
		//The bug occurs here, so we must check which hand has the apples
		// A player can't eat food in the offhand if there is any in the main hand
		// On this principle if there are gapples in the mainhand it must be that one, else it's the offhand
	}
	public List<PotionEffect> getPotionEffects(String apple){
		List<PotionEffect> appleEffects = new ArrayList<>();

		if(apple == "gapple") {
			PotionEffect fx = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);
			appleEffects.add(fx);
			PotionEffect fx2 = new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0);
			appleEffects.add(fx2);
		}
		if(apple == "napple") {
			PotionEffect fx = new PotionEffect(PotionEffectType.REGENERATION, 600, 4);
			appleEffects.add(fx);
			PotionEffect fx2 = new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0);
			appleEffects.add(fx2);
			PotionEffect fx3 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0);
			appleEffects.add(fx3);
			PotionEffect fx4 = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0);
			appleEffects.add(fx4);
		}
		
		return appleEffects;
	}

	public void registerCrafting(){
		if(Bukkit.getRecipesFor(napple).size() > 0) return;
		Bukkit.addRecipe(r);
	}
}
