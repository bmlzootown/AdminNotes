package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Logger;
import com.cyprias.AdminNotes.Note;
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

		int page = -1;
		if (args.length > 0) {// && args[1].equalsIgnoreCase("compact"))
			if (Plugin.isInt(args[0])) {
				page = Integer.parseInt(args[0]);
				if (page>0)
					page-=1;
			} else {
				ChatUtils.send(sender, "Invalid page: " +  args[0]);
				return true;
			}
		}
		
		try {
			List<Note> notes = Plugin.database.list(sender, page);
			
			Note note;
			for (int i=0; i<notes.size(); i++){
				note = notes.get(i);

				ChatUtils.send(sender, String.format((ChatColor.GRAY+"["+ChatColor.WHITE+"%s"+ChatColor.GRAY+"] "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"), note.getId(), note.getPlayer(), note.getText()));
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ChatUtils.send(sender, "An error has occured: " + e.getLocalizedMessage());
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
		return false;
	}

}
