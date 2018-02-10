package gvlfm78.plugin.OldCombatMechanics.utilities;

import java.util.HashMap;

import org.bukkit.Material;

/**
 * Created by Rayzr522 on 6/14/16.
 */
public class ArmourValues {

	private static HashMap<String, Double> values;


	public static void Initialise() {
		reload();
	}

	public static void reload() {

		values = new HashMap<>();

		values.put("LEATHER_HELMET", (double) 1);
		values.put("LEATHER_CHESTPLATE", (double) 3);
		values.put("LEATHER_LEGGINGS", (double) 2);
		values.put("LEATHER_BOOTS", (double) 1);
		
		values.put("CHAINMAIL_HELMET", (double) 2);
		values.put("CHAINMAIL_CHESTPLATE", (double) 5);
		values.put("CHAINMAIL_LEGGINGS", (double) 4);
		values.put("CHAINMAIL_BOOTS", (double) 1);
		
		values.put("GOLD_HELMET", (double) 2);
		values.put("GOLD_CHESTPLATE", (double) 5);
		values.put("GOLD_LEGGINGS", (double) 3);
		values.put("GOLD_BOOTS", (double) 1);
		
		values.put("IRON_HELMET", (double) 2);
		values.put("IRON_CHESTPLATE", (double) 6);
		values.put("IRON_LEGGINGS", (double) 5);
		values.put("IRON_BOOTS", (double) 2);
		
		values.put("DIAMOND_HELMET", (double) 3);
		values.put("DIAMOND_CHESTPLATE", (double) 8);
		values.put("DIAMOND_LEGGINGS", (double) 6);
		values.put("DIAMOND_BOOTS", (double) 3);
	}


	public static double getValue(Material mat) {

		if (!values.containsKey(mat.name()))
			return 0;

		return values.get(mat.name());
	}
}
