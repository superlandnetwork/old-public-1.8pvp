package gvlfm78.plugin.OldCombatMechanics.utilities;

import java.util.HashMap;

import org.bukkit.Material;

public class WeaponDamages {

	private static HashMap<String, Double> damages;


	public static void Initialise() {
		reload();
	}

	public static void reload() {

		damages = new HashMap<>();
		
		damages.put("DIAMOND_AXE", (double) 1.5);
		damages.put("IRON_AXE", (double) 1.8);
		damages.put("STONE_AXE", (double) 2.25);
		damages.put("WOOD_AXE", (double) 2.333333);
		damages.put("GOLD_AXE", (double) 2.333333);
		
		damages.put("DIAMOND_SPADE", (double) 1.375);
		damages.put("IRON_SPADE", (double) 1.5);
		damages.put("STONE_SPADE", (double) 1.75);
		damages.put("WOOD_SPADE", (double) 2.5);
		damages.put("GOLD_SPADE", (double) 2.5);
		
		damages.put("DIAMOND_SWORD", (double) 1);
		damages.put("IRON_SWORD", (double) 1);
		damages.put("STONE_SWORD", (double) 1);
		damages.put("WOOD_SWORD", (double) 1);
		damages.put("GOLD_SWORD", (double) 1);
		
		damages.put("DIAMOND_PICKAXE", (double) 1);
		damages.put("IRON_PICKAXE", (double) 1);
		damages.put("STONE_PICKAXE", (double) 1);
		damages.put("WOOD_PICKAXE", (double) 1);
		damages.put("GOLD_PICKAXE", (double) 1);
		
		damages.put("DIAMOND_HOE", (double) 1);
		damages.put("IRON_HOE", (double) 1);
		damages.put("STONE_HOE", (double) 1);
		damages.put("WOOD_HOE", (double) 1);
		damages.put("GOLD_HOE", (double) 1);
	}


    public static double getDamage(Material mat) {

        if (!damages.containsKey(mat.name()))
            return -1;

        return damages.get(mat.name());

    }
}
