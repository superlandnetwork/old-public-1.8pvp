package gvlfm78.plugin.OldCombatMechanics;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.codingforcookies.armourequip.ArmourListener;

import gvlfm78.plugin.OldCombatMechanics.module.ModuleAttackCooldown;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleDisableCrafting;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleDisableEnderpearlCooldown;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleDisableOffHand;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleFishingKnockback;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleGoldenApple;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleNoLapisEnchantments;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleOldArmourStrength;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleOldBrewingStand;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleOldToolDamage;
import gvlfm78.plugin.OldCombatMechanics.module.ModulePlayerRegen;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleProjectileKnockback;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleSwordBlocking;
import gvlfm78.plugin.OldCombatMechanics.module.ModuleSwordSweep;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;

public class OCMMain extends JavaPlugin {

	Logger logger = getLogger();
	//private OCMTask task = null;
	private OCMSweepTask sweepTask = null;

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();

		// Register every event class (as well as our command handler)
		registerAllEvents();

		// Initialise Config utility
		Config.Initialise(this);

		// Initialise the team if it doesn't already exist
		createTeam();

		// Disabling player collision
		/*if (Config.moduleEnabled("disable-player-collision"))
			// Even though it says "restart", it works for just starting it too
			restartTask();*/

		//Remove scoreboard
		String name = "ocmInternal";
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		scoreboard.getTeam(name).unregister();

		//Start up anti sword sweep attack task
		restartSweepTask();

		// Logging to console the enabling of OCM
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been enabled");

	}

	@Override
	public void onDisable() {

		PluginDescriptionFile pdfFile = this.getDescription();

		//if (task != null) task.cancel();

		// Logging to console the disabling of OCM
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled");
	}

	private void registerAllEvents() {

		// Module listeners
		Bukkit.getPluginManager().registerEvents(new ArmourListener(), this);
		Bukkit.getPluginManager().registerEvents(new ModuleAttackCooldown(), this);

		//Apparently listeners registered after get priority
		Bukkit.getPluginManager().registerEvents(new ModuleOldToolDamage(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleSwordSweep(this), this);

		Bukkit.getPluginManager().registerEvents(new ModuleGoldenApple(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleFishingKnockback(), this);
		Bukkit.getPluginManager().registerEvents(new ModulePlayerRegen(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleSwordBlocking(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleOldArmourStrength(), this);
		Bukkit.getPluginManager().registerEvents(new ModuleDisableCrafting(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleDisableOffHand(this), this);
		Bukkit.getPluginManager().registerEvents(new ModuleOldBrewingStand(), this);
		Bukkit.getPluginManager().registerEvents(new ModuleProjectileKnockback(), this);
		Bukkit.getPluginManager().registerEvents(new ModuleNoLapisEnchantments(), this);
		Bukkit.getPluginManager().registerEvents(new ModuleDisableEnderpearlCooldown(), this);
	}

	private void createTeam() {
		String name = "ocmInternal";
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team team = null;

		for (Team t : scoreboard.getTeams()) {
			if (t.getName().equals(name)) {
				team = t;
				break;
			}
		}

		if (team == null)
			team = scoreboard.registerNewTeam(name);

		team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		team.setAllowFriendlyFire(true);
	}

	/*public void restartTask() {

		if (task == null)
			task = new OCMTask(this);
		else {
			task.cancel();
			task = new OCMTask(this);
		}

		double minutes = getConfig().getDouble("disable-player-collision.collision-check-frequency");

		if (minutes > 0)
			task.runTaskTimerAsynchronously(this, 0, (long) minutes * 60 * 20);
		else
			task.runTaskTimerAsynchronously(this, 0, 60 * 20);

	}*/

	public void restartSweepTask() {
		if (sweepTask == null)
			sweepTask = new OCMSweepTask();
		else {
			sweepTask.cancel();
			sweepTask = new OCMSweepTask();
		}
		sweepTask.runTaskTimer(this, 0, 1);
	}

	public OCMSweepTask sweepTask() {
		return sweepTask;
	}
}