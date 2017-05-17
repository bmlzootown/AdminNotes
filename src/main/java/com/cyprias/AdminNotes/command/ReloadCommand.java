package com.cyprias.AdminNotes.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;

public class ReloadCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.RELOAD))
			list.add("/%s reload - Reload the plugin.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) throws FileNotFoundException, IOException, InvalidConfigurationException {
		if (!Plugin.checkPermission(sender, Perm.RELOAD)) {
			return false;
		}

		Plugin instance = Plugin.getInstance();

		Plugin.reload();
		instance.getPluginLoader().disablePlugin(instance);
		instance.getPluginLoader().enablePlugin(instance);

		ChatUtils.send(sender, "Plugin reloaded.");

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.RELOAD, "/%s reload - Reload the plugin.", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}
