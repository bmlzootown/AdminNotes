package com.cyprias.AdminNotes;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyprias.AdminNotes.command.CommandManager;
import com.cyprias.AdminNotes.command.CreateCommand;
import com.cyprias.AdminNotes.command.InfoCommand;
import com.cyprias.AdminNotes.command.ListCommand;
import com.cyprias.AdminNotes.configuration.Config;
import com.cyprias.AdminNotes.database.Database;
import com.cyprias.AdminNotes.database.MySQL;

public class Plugin extends JavaPlugin {
	//static PluginDescriptionFile description;
	private static Plugin instance = null;
	private File configFile;
	public void onLoad() {
	//	description = getDescription();
	}

	public static Database database;

	public void onEnable() {
		instance = this;
		
		File dataFolder = getDataFolder();
		configFile = new File(dataFolder, "config.yml");
		getConfig().options().copyDefaults(true);
		saveConfig();

		if (Config.getString("properties.db-type").equalsIgnoreCase("mysql")){
			database = new MySQL();
		}else{
			Logger.severe("No database selected ("+Config.getString("properties.db-type")+"), unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}

		if (!database.init()){
			Logger.severe("Failed to initilize database, unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}
		
		CommandManager cm = new CommandManager()
			.registerCommand("create", new CreateCommand())
			.registerCommand("info", new InfoCommand())
			.registerCommand("list", new ListCommand());
		
		this.getCommand("notes").setExecutor(cm);
		
		Logger.info("enabled.");
	}

	public void onDisable() {
		Logger.info("disabled.");
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

	public static double getUnixTime() {
		return (System.currentTimeMillis() / 1000D);
	}
	
	public static String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}
	
	public static boolean isInt(final String sInt) {
		try {
			Integer.parseInt(sInt);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isDouble(final String sDouble) {
		try {
			Double.parseDouble(sDouble);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
