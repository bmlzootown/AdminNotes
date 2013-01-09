package com.cyprias.AdminNotes.command;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Logger;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;

public class InfoCommand implements Command {

	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.INFO))
			list.add("/%s info - Show info on a note.");
	}

	@Override
	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.hasPermission(sender, Perm.INFO)) 
			return false;
		
		int id = 0; //Default to last page.
		if (args.length > 0) {// && args[1].equalsIgnoreCase("compact"))
			if (Plugin.isInt(args[0])) {
				id = Integer.parseInt(args[0]);
			} else {
				ChatUtils.send(sender, "Invalid id: " +  args[0]);
				return true;
			}
		}
		
		
		try {
			Note note = Plugin.database.info(id);
			
			if (note != null){

				
				//expiry.setTime( (long) note.getTime() * 1000);
				
			
				SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy h:mm a");
				String date = f.format((long) note.getTime() * 1000); 
				
				
				
				
				ChatUtils.send(sender, String.format((ChatColor.GRAY+"["+ChatColor.WHITE+"%s"+ChatColor.GRAY+"] "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"), note.getId(), note.getPlayer(), note.getText()));
				ChatUtils.send(sender, "Writer: " + note.getWriter() + ", Date: " + date.toString() + ", Notify: " + note.getNotify());

				
				
			}else{
				ChatUtils.send(sender, "Could not find info on id #" + id);
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			ChatUtils.send(sender, "Error: " + e.getLocalizedMessage());
			return true;
		}
		
		
		
		return false;
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.INFO, "/%s info <id> - Show info on a note.", cmd);
	}

	@Override
	public boolean hasValues() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
}
