package gvlfm78.plugin.OldCombatMechanics.module;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.codingforcookies.armourequip.ArmourEquipEvent;
import com.comphenix.example.Attributes;
import com.comphenix.example.Attributes.Attribute;
import com.comphenix.example.Attributes.AttributeType;

import gvlfm78.plugin.OldCombatMechanics.utilities.ArmourValues;
import gvlfm78.plugin.OldCombatMechanics.utilities.reflection.ItemData;

public class ModuleOldArmourStrength implements Listener {

	@EventHandler
	public void onArmourEquip(ArmourEquipEvent e) {
		//Equipping
		ItemStack newPiece = e.getNewArmourPiece();
		if (newPiece != null && newPiece.getType() != Material.AIR) {
			e.setNewArmourPiece(apply(newPiece, true));
		}

		//Unequipping
		ItemStack oldPiece = e.getOldArmourPiece();
		if (oldPiece != null && oldPiece.getType() != Material.AIR) {
			e.setOldArmourPiece(apply(oldPiece, false));
		}

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		setArmourAccordingly(player);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		final Player player = e.getPlayer();
		setArmourToDefault(player);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		final Player player = e.getPlayer();
		setArmourAccordingly(player);
	}

	private void setArmourToDefault(final Player player) {
		// Tells method that module is disabled in this world
		setArmourAccordingly(player, false);
	}

	private void setArmourAccordingly(final Player player){
		setArmourAccordingly(player, true);
	}

	public static void setArmourAccordingly(final Player player, boolean enabled) {
		final PlayerInventory inv = player.getInventory();
		ItemStack[] armours = inv.getContents();
		// Check the whole inventory for armour pieces

		for (int i = 0; i < armours.length; i++) {
			ItemStack piece = armours[i];

			if (piece != null && piece.getType() != Material.AIR) {
				//If this piece is one of the ones being worn right now
				if(ArrayUtils.contains(inv.getArmorContents(), armours[i]))
					armours[i] = apply(piece, enabled); //Apply/remove values according state of module in this world
				else armours[i] = apply(piece, false); //Otherwise set values back to default
			}
		}

		player.getInventory().setContents(armours);
	}

	private static ItemStack apply(ItemStack is, boolean enable) {
		String slot;
		String type = is.getType().toString().toLowerCase();

		if(type.contains("helmet"))
			slot = "head";
		else if(type.contains("chestplate"))
			slot = "chest";
		else if(type.contains("leggings"))
			slot = "legs";
		else if(type.contains("boots"))
			slot = "feet";
		else return is; //Not an armour piece

		double strength = ArmourValues.getValue(is.getType());

		Attributes attributes = new Attributes(is);

		double toughness = enable ? 9999 : getDefaultToughness(is.getType());

		boolean armourTagPresent = false, toughnessTagPresent = false;

		for(int i = 0; i < attributes.size(); i++){
			Attribute att = attributes.get(i);
			if(att == null) continue;

			AttributeType attType = att.getAttributeType();

			if(attType == AttributeType.GENERIC_ARMOR){ //Found a generic armour tag
				if(armourTagPresent) //If we've already found another tag
					attributes.remove(att); //Remove this one as it's a duplicate
				else{
					if(att.getAmount() != strength){ //If its value does not match what it should be, remove it
						attributes.remove(att);
						armourTagPresent = false; //Set armour value anew
					}
					else armourTagPresent = true;
				}
			}

			else if(attType == AttributeType.GENERIC_ARMOR_TOUGHNESS){ //Found a generic armour toughness tag
				if(toughnessTagPresent) //If we've already found another tag
					attributes.remove(att); //Remove this one as it's a duplicate
				else{
					if(att.getAmount() != toughness){ //If its value does not match what it should be, remove it
						attributes.remove(att);
						toughnessTagPresent = false; //Set armour value anew
					}
					else toughnessTagPresent = true;
				}
			}
		}

		//If there's no armour tag present add it
		if(!armourTagPresent){
			attributes.add(Attributes.Attribute.newBuilder().name("Armor").type(Attributes.AttributeType.GENERIC_ARMOR).amount(strength).slot(slot).build());
		}
		//If there's no toughness tag present add it
		if(!toughnessTagPresent){
			attributes.add(Attributes.Attribute.newBuilder().name("ArmorToughness").type(Attributes.AttributeType.GENERIC_ARMOR_TOUGHNESS).amount(toughness).slot(slot).build());
		}

		ItemData.mark(is, "ArmorModifier");

		return is;
	}

	public static int getDefaultToughness(Material mat){
		switch(mat){
		case DIAMOND_CHESTPLATE: case DIAMOND_HELMET: case DIAMOND_LEGGINGS: case DIAMOND_BOOTS:
			return 2;
		default: return 0;
		}
	}
}
