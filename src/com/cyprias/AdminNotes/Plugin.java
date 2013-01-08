package com.cyprias.AdminNotes;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyprias.AdminNotes.configuration.Config;
import com.cyprias.AdminNotes.database.Database;
import com.cyprias.AdminNotes.database.MySQL;

public class Plugin extends JavaPlugin {
	static PluginDescriptionFile description;
	private static Plugin instance = null;
	private File configFile;
	public void onLoad() {
		description = getDescription();

		File dataFolder = getDataFolder();
		configFile = new File(dataFolder, "config.yml");

		if (!configFile.exists()) {
			Logger.info("Config file not found. Writing defaults.");
			saveDefaultConfig();
		}
	}

	public static Database database;
	

	public void onEnable() {
		instance = this;
		

		if (Config.getString("properties.db-type") == "mysql"){
			database = new MySQL();
		}else{
			Logger.severe("No database selected, unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}

		if (!database.init()){
			Logger.severe("Failed to initilize database, unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}
		
	}

	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		instance = null;
	}

	public static void reload() {
		instance.reloadConfig();
	}

	public static void disable() {
		instance.getServer().getPluginManager().disablePlugin(instance);
	}

	static public boolean hasPermission(CommandSender sender, Perm permission) {
		if (sender != null) {
			if (sender.hasPermission(permission.getPermission())) {
				return true;
			} else {
				Perm parent = permission.getParent();
				return (parent != null) ? hasPermission(sender, parent) : false;
			}
		}
		return false;
	}

	public static final Plugin getInstance() {
		return instance;
	}

}
