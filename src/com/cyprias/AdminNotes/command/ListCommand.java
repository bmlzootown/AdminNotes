package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.configuration.Config;

public class ListCommand implements Command {

	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.LIST))
			list.add("/%s list - List all notes.");
	}

	private void DBQuery(CommandSender sender, int page){
		try {
			List<Note> notes = Plugin.database.list(sender, page);
			
			Note note;
			for (int i=0; i<notes.size(); i++){
				note = notes.get(i);

				ChatUtils.send(sender, String.format((ChatColor.GRAY+"[%s"+ChatColor.GRAY+"] "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"), note.getColouredId(), note.getColouredPlayer(), note.getText()));
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			ChatUtils.error(sender, e.getLocalizedMessage());
			return;
		}
	}
	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.LIST)) 
			return false;
		
		int page = -1; //Default to last page.
		if (args.length > 0) {// && args[1].equalsIgnoreCase("compact"))
			if (Plugin.isInt(args[0])) {
				page = Integer.parseInt(args[0]);
				if (page>0)
					page-=1;
			} else {
				ChatUtils.error(sender, "Invalid page: " +  args[0]);
				return true;
			}
		}
		
		
		
		if (Config.getBoolean("properties.async-db-queries")){
			final int fpage = page;
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					DBQuery(sender, fpage);
				}
			});
		} else {
			DBQuery(sender, page);
		}
		
		
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.LIST, "/%s list [page] - List all notes.", cmd);
	}

	public boolean hasValues() {
		return false;
	}

}
