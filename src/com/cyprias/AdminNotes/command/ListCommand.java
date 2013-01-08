package com.cyprias.AdminNotes.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;

public class ListCommand implements Command {

	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.LIST))
			list.add("/%s list - List all notes.");
	}

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.hasPermission(sender, Perm.LIST)) {
			return false;
		}

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.LIST, "/%s list - List all notes.", cmd);
	}

	public boolean hasValues() {
		// TODO Auto-generated method stub
		return false;
	}

}
