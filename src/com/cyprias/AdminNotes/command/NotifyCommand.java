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

public class NotifyCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.NOTIFY))
			list.add("/%s notify - Toggle notify on login flag");
	}

	private void DBQuery(CommandSender sender, int id){
		try {
			Boolean success = Plugin.database.notify(id);
			if (success){
				Note note = Plugin.database.info(id);

				ChatColor G = ChatColor.GRAY;
				ChatColor W = ChatColor.WHITE;
				ChatUtils.send(sender, G+"Notify on #" + W+id + G+ " set to " + W+String.valueOf(note.getNotify()));
				
			}else{
				ChatUtils.error(sender, "Failed to toggle notify on #" +  id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ChatUtils.error(sender,  e.getLocalizedMessage());
		}
	}
	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.NOTIFY)) {
			return false;
		}
		if (args.length == 0){
			getCommands(sender, cmd);
			return true;
		}
		
		int id = 0; //Default to last page.
		if (args.length > 0) {// && args[1].equalsIgnoreCase("compact"))
			if (Plugin.isInt(args[0])) {
				id = Integer.parseInt(args[0]);
			} else {
				ChatUtils.error(sender, "Invalid id: " +  args[0]);
				return true;
			}
		}
		
		
		
		if (Config.getBoolean("properties.async-db-queries")){
			final int fid = id;
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					DBQuery(sender, fid);
				}
			});
		} else {
			DBQuery(sender, id);
		}


		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.NOTIFY, "/%s notify <id>", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}
