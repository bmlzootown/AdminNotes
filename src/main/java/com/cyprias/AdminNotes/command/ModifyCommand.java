package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;

public class ModifyCommand implements Command {
	
	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.MODIFY))
			list.add("/%s modify - Modify a message to your note.");
	}


	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.MODIFY)) {
			return false;
		}
		if (args.length < 2){
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
		
		try {
			Note note = Plugin.database.info(id);
			
			if (!note.getWriter().equalsIgnoreCase(sender.getName())){
				ChatUtils.send(sender, ChatColor.GRAY + "You cannot append " + ChatColor.WHITE + note.getWriter() + ChatColor.GRAY+"'s note.");
				return true;
			}
			
			String text = ChatColor.stripColor(Plugin.getFinalArg(args, 1));
			
			note.setText(text);
			ChatUtils.send(sender, ChatColor.GRAY + "#" +  ChatColor.WHITE+id +  ChatColor.GRAY+" set to " + ChatColor.WHITE+ text);
			
		} catch (SQLException e) {e.printStackTrace();
			e.printStackTrace();
			ChatUtils.error(sender, e.getLocalizedMessage());
			return true;
		}
		
		
	

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.MODIFY, "/%s modify <id> <message>", cmd);
	}

	public boolean hasValues() {
		return false;
	}

}