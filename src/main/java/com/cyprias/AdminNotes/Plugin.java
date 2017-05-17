package com.cyprias.AdminNotes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import com.cyprias.AdminNotes.command.AppendCommand;
import com.cyprias.AdminNotes.command.CommandManager;
import com.cyprias.AdminNotes.command.CreateCommand;
import com.cyprias.AdminNotes.command.InfoCommand;
import com.cyprias.AdminNotes.command.ListCommand;
import com.cyprias.AdminNotes.command.ModifyCommand;
import com.cyprias.AdminNotes.command.NotifyCommand;
import com.cyprias.AdminNotes.command.ReloadCommand;
import com.cyprias.AdminNotes.command.RemoveCommand;
import com.cyprias.AdminNotes.command.SearchCommand;
import com.cyprias.AdminNotes.configuration.Config;
import com.cyprias.AdminNotes.configuration.YML;
import com.cyprias.AdminNotes.database.Database;
import com.cyprias.AdminNotes.database.MySQL;
import com.cyprias.AdminNotes.database.SQLite;
import com.cyprias.AdminNotes.listeners.PlayerListener;

public class Plugin extends JavaPlugin {
	private static Plugin instance = null;

	//public void onLoad() {}

	public static Database database;

	public void onEnable() {
		instance = this;

		getConfig().options().copyDefaults(true);
		saveConfig();

		if (Config.getString("properties.db-type").equalsIgnoreCase("mysql")) {
			database = new MySQL();
		} else if (Config.getString("properties.db-type").equalsIgnoreCase("sqlite")) {
			database = new SQLite();
		} else {
			Logger.severe("No database selected (" + Config.getString("properties.db-type") + "), unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}

		if (!database.init()) {
			Logger.severe("Failed to initilize database, unloading plugin...");
			instance.getPluginLoader().disablePlugin(instance);
			return;
		}

		loadPermissions();

		registerListeners(new PlayerListener());

		CommandManager cm = new CommandManager().registerCommand("create", new CreateCommand()).registerCommand("info", new InfoCommand())
			.registerCommand("list", new ListCommand()).registerCommand("notify", new NotifyCommand()).registerCommand("search", new SearchCommand())
			.registerCommand("remove", new RemoveCommand()).registerCommand("reload", new ReloadCommand()).registerCommand("append", new AppendCommand()).registerCommand("modify", new ModifyCommand());

		this.getCommand("notes").setExecutor(cm);

		if (Config.getBoolean("properties.use-metrics"))
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
			}


		try {
			loadAutoNotes();
		} catch (FileNotFoundException | InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Logger.info("enabled.");
	}

	private void loadPermissions() {
		PluginManager pm = Bukkit.getPluginManager();
		for (Perm permission : Perm.values()) {
			permission.loadPermission(pm);
		}
	}

	
	//anPermissions
	private static void autoNotePermission(String title){
		Permission perm = new Permission("adminnotes.autonote."+title, PermissionDefault.getByName(Config.getString("properties.permission-default")));// PermissionDefault.getByName(Config.getString("properties.auto-note-permission"))
		perm.addParent(Perm.AUTO_NOTE.getPermission(), true);
		Bukkit.getPluginManager().addPermission(perm);
	}
	
	
	public static List<anCommand> anCommands = new ArrayList<anCommand>();
	
	public static class anCommand {
		public String[]  regex;
		public String player;
		public String note;
		public String title;
		public Boolean notify;
		public anCommand(String title, String[] regex, String player, String note, Boolean notify) {
			this.title = title;
			this.regex = regex;
			this.player = player;
			this.note = note;
			this.notify = notify;
			//for (int i=0; i<regex.length; i++){
			//	Logger.info(title + " regex " + i + ": " + regex[i]);
			//}
			
			if (Config.getBoolean("properties.auto-note-permission"))
				autoNotePermission(title);
		}
	}
	
	private static void loadAutoNotes() throws FileNotFoundException, IOException, InvalidConfigurationException{
		anCommands.clear();
		
		YML loadAutoNotes = new YML(instance.getResource("auto-notes.yml"),instance.getDataFolder(), "auto-notes.yml");
		ConfigurationSection commands = loadAutoNotes.getConfigurationSection("commands");
	
		String player, note;
		String[] regex;
		anCommand autonote;
		Boolean notify;
		for(String title : commands.getKeys(false)) {
		//	Logger.info("title: " + title);
			
			
			
			 regex = commands.getConfigurationSection(title).contains("regex") ? commands.getConfigurationSection(title).getString("regex").split(Config.getString("properties.line-separator")) : null;
			 player = commands.getConfigurationSection(title).contains("player") ?  commands.getConfigurationSection(title).getString("player") : null;
			 note = commands.getConfigurationSection(title).contains("note") ? commands.getConfigurationSection(title).getString("note") : null;
			 notify = (commands.getConfigurationSection(title).contains("notify")) ? commands.getConfigurationSection(title).getBoolean("create"): Config.getBoolean("properties.notify-by-default");
			 
			 autonote = new anCommand(title, regex, player, note, notify);
			 
			 anCommands.add(autonote);
		}
	}
	
	
	
	private void registerListeners(Listener... listeners) {
		PluginManager manager = getServer().getPluginManager();

		for (Listener listener : listeners) {
			manager.registerEvents(listener, this);
		}
	}

	public static void removeANPermissions(){
		for (int i=0;i<anCommands.size();i++){
			Bukkit.getPluginManager().removePermission("adminnotes.autonote."+anCommands.get(i).title);
		}
	}
	
	public void onDisable() {
		removeANPermissions();
		
		PluginManager pm = Bukkit.getPluginManager();
		for (Perm permission : Perm.values()) {
			// permission.loadPermission(pm);
			permission.unloadPermission(pm);
		}

		CommandManager.unregisterCommands();
		this.getCommand("notes").setExecutor(null);
		
		
		instance.getServer().getScheduler().cancelAllTasks();
		
		PlayerListener.unregisterEvents(instance);

		instance = null;
		Logger.info("disabled.");
	}

	public static void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
		removeANPermissions();
		instance.reloadConfig();
		loadAutoNotes();
	}

	public static void disable() {
		instance.getServer().getPluginManager().disablePlugin(instance);
	}

	static public boolean hasPermission(CommandSender sender, Perm permission) {
		if (sender != null) {
			if (sender instanceof ConsoleCommandSender)
				return true;
			
			if (sender.hasPermission(permission.getPermission())) {
				return true;
			} else {
				Perm parent = permission.getParent();
				return (parent != null) && hasPermission(sender, parent);
			}
		}
		return false;
	}

	public static boolean checkPermission(CommandSender sender, Perm permission) {
		if (!hasPermission(sender, permission)) {
			String mess = permission.getErrorMessage();
			if (mess == null)
				mess = Perm.DEFAULT_ERROR_MESSAGE;
			ChatUtils.error(sender, mess);
			return false;
		}
		return true;
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

	public static <T> T[] concat(T[] first, T[]... rest) {

		// Read rest
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}

		// Concat with arraycopy
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;

	}
	public static Boolean hasPlayedBefore(String playerName){
		Player player = instance.getServer().getPlayer(playerName);
		if (player != null && player.hasPlayedBefore())
			return true;

		if (instance.getServer().getOfflinePlayer(playerName).hasPlayedBefore())
			return true;

		return false;
	}
	
}
