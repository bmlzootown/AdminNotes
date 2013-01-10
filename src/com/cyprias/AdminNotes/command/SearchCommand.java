package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.SearchParser;
import com.cyprias.AdminNotes.configuration.Config;

public class SearchCommand implements Command {

	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.SEARCH))
			list.add("/%s search - Search notes.");
	}

	private void DBQuery(CommandSender sender, SearchParser parser){
		try {
			List<Note> notes = Plugin.database.search(parser);
			Note note;
			for (int i=0; i<notes.size(); i++){
				note = notes.get(i);
				ChatUtils.send(sender, String.format((ChatColor.GRAY+"[%s"+ChatColor.GRAY+"] "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"), note.getColouredId(), note.getPlayer(), note.getText()));
			}
		} catch (SQLException e) {
			ChatUtils.error(sender, e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.SEARCH))
			return false;

		if (args.length == 0){
			getCommands(sender, cmd);
			return true;
		}
		
		// Parse arguments
		SearchParser parser = null;
		try {
			parser = new SearchParser(sender, args);
		} catch (IllegalArgumentException e) {
			ChatUtils.error(sender, e.getMessage());
			return true;
		}
		//new SearchQuery(new SearchCallback(session), parser, SearchDir.DESC);
		
		if (Config.getBoolean("properties.async-db-queries")){
			final SearchParser fparser = parser;
			
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					DBQuery(sender, fparser);
				}
			});
		} else {
			DBQuery(sender, parser);
		}
		
		return false;
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.SEARCH, "/%s serach <filters> - Search notes.", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.SEARCH, "Player "+ChatColor.WHITE+"p:"+ChatColor.GRAY+" - Search by Player", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.SEARCH, "Writer "+ChatColor.WHITE+"w:"+ChatColor.GRAY+" - Search by writer", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.SEARCH, "Keyword "+ChatColor.WHITE+"k:"+ChatColor.GRAY+" - Search by note keyword", cmd);

		
		
	}

	@Override
	public boolean hasValues() {
		return false;
	}
}
