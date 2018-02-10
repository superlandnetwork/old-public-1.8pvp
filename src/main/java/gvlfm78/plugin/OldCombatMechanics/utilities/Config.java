package gvlfm78.plugin.OldCombatMechanics.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleDisableCrafting;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleGoldenApple;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleOldArmourStrength;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleSwordBlocking;

/**
 * Created by Rayzr522 on 6/14/16.
 */

public class Config {

	private static OCMMain plugin;
	private static List<Material> interactive = new ArrayList<>();

	public static void Initialise(OCMMain plugin) {

		Config.plugin = plugin;
		reload();
	}

	public static void reload() {

		//plugin.restartTask(); //Restart no-collision check
		plugin.restartSweepTask(); //Restart sword sweep check

		WeaponDamages.Initialise(); //Reload weapon damages from config

		ArmourValues.Initialise(); //Reload armour values from config

		//Setting correct attack speed and armour values for online players
		for(World world : Bukkit.getWorlds()){

			List<Player> players = world.getPlayers();

			double GAS = 24;

			boolean isArmourEnabled = true;

			for(Player player : players){
				//Setting attack speed
				AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
				double baseValue = attribute.getBaseValue();

				if(baseValue != GAS){
					attribute.setBaseValue(GAS);
					player.saveData();
				}

				//Setting armour values
				ModuleOldArmourStrength.setArmourAccordingly(player, isArmourEnabled);
			}
		}

		ModuleGoldenApple.INSTANCE.reloadRecipes();
		ModuleGoldenApple.INSTANCE.registerCrafting();
		
		reloadInteractiveBlocks();
		ModuleSwordBlocking.INSTANCE.reload();
		ModuleDisableCrafting.INSTANCE.reload();
	}

	public static void reloadInteractiveBlocks(){

		interactive.add(Material.ENCHANTMENT_TABLE);
		interactive.add(Material.BREWING_STAND);
		interactive.add(Material.ANVIL);
		interactive.add(Material.TRAPPED_CHEST);
		interactive.add(Material.CHEST);
		interactive.add(Material.BED);
		interactive.add(Material.BOAT);
		interactive.add(Material.FENCE_GATE);
		interactive.add(Material.DISPENSER);
		interactive.add(Material.DROPPER);
		interactive.add(Material.FURNACE);
		interactive.add(Material.JUKEBOX);
		interactive.add(Material.ENDER_CHEST);
		interactive.add(Material.STONE_BUTTON);
		interactive.add(Material.WOOD_BUTTON);
		interactive.add(Material.BEACON);
		interactive.add(Material.TRIPWIRE_HOOK);
		interactive.add(Material.HOPPER);
		interactive.add(Material.DAYLIGHT_DETECTOR);
		interactive.add(Material.DAYLIGHT_DETECTOR_INVERTED);
		interactive.add(Material.ITEM_FRAME);
		interactive.add(Material.DIODE);
		interactive.add(Material.DIODE_BLOCK_OFF);
		interactive.add(Material.DIODE_BLOCK_ON);
		interactive.add(Material.REDSTONE_COMPARATOR);
		interactive.add(Material.REDSTONE_COMPARATOR_OFF);
		interactive.add(Material.REDSTONE_COMPARATOR_ON);
		interactive.add(Material.ACACIA_DOOR);
		interactive.add(Material.BIRCH_DOOR);
		interactive.add(Material.DARK_OAK_DOOR);
		interactive.add(Material.JUNGLE_DOOR);
		interactive.add(Material.SPRUCE_DOOR);
		interactive.add(Material.WOOD_DOOR);
		interactive.add(Material.WORKBENCH);
		interactive.add(Material.BED_BLOCK);
		interactive.add(Material.LEVER);
		interactive.add(Material.TRAP_DOOR);
		interactive.add(Material.BURNING_FURNACE);
		interactive.add(Material.SPRUCE_FENCE_GATE);
		interactive.add(Material.BIRCH_FENCE_GATE);
		interactive.add(Material.JUNGLE_FENCE_GATE);
		interactive.add(Material.DARK_OAK_FENCE_GATE);
		interactive.add(Material.ACACIA_FENCE_GATE);
		interactive.add(Material.WHITE_SHULKER_BOX);
		interactive.add(Material.LIGHT_BLUE_SHULKER_BOX);
		interactive.add(Material.YELLOW_SHULKER_BOX);
		interactive.add(Material.MAGENTA_SHULKER_BOX);
		interactive.add(Material.ORANGE_SHULKER_BOX);
		interactive.add(Material.LIME_SHULKER_BOX);
		interactive.add(Material.PINK_SHULKER_BOX);
		interactive.add(Material.GRAY_SHULKER_BOX);
		interactive.add(Material.SILVER_SHULKER_BOX);
		interactive.add(Material.CYAN_SHULKER_BOX);
		interactive.add(Material.BLUE_SHULKER_BOX);
		interactive.add(Material.PURPLE_SHULKER_BOX);
		interactive.add(Material.BROWN_SHULKER_BOX);
		interactive.add(Material.GREEN_SHULKER_BOX);
		interactive.add(Material.RED_SHULKER_BOX);
		interactive.add(Material.BLACK_SHULKER_BOX);
	}

	public static List<Material> getInteractiveBlocks(){
		return interactive;
	}

}
