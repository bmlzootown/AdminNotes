package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.configuration.Config;

public class CreateCommand implements Command {
	
	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.CREATE))
			list.add("/%s create - Create a note.");
	}

	private void DBQuery(CommandSender sender, String player, String text){
		try {
			Plugin.database.create(sender, Config.getBoolean("properties.notify-by-default"), player, text);
		} catch (SQLException e) {
			e.printStackTrace();
			ChatUtils.error(sender, "An error has occured: " + e.getLocalizedMessage());
			
			return;
		}
		
		ChatUtils.send(sender, "Created note.");
		String senderName = (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();

		ChatUtils.broadcast(Perm.CREATE_NOTIFIED, String.format((ChatColor.WHITE+"%s "+ChatColor.GRAY+"created note on "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"),senderName, player, text));
		
	}
	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.CREATE)) {
			return false;
		}
		if (args.length == 0){
			getCommands(sender, cmd);
			return true;
		}
		
		if (args.length < 2){
			ChatUtils.sendCommandHelp(sender, Perm.CREATE, "/%s create <player> <note>", cmd);
			return true;
		}
		final String player = args[0];
		final String text = ChatColor.stripColor(Plugin.getFinalArg(args, 1));

		if (Config.getBoolean("properties.async-db-queries")){
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					DBQuery(sender, player, text);
				}
			});
		} else {
			DBQuery(sender, player, text);
		}
		

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.CREATE, "/%s create <player> <note>", cmd);
	}

	public boolean hasValues() {
		return false;
	}

}
