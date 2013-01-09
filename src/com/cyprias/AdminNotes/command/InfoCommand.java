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
				
				ChatColor G = ChatColor.GRAY;
				ChatColor W = ChatColor.WHITE;
				
				
				
				
				
				
				ChatUtils.send(sender, String.format((G+"[%s] %s: %s"), W+String.valueOf(note.getId())+G, W+note.getPlayer()+G, W+note.getText()+G));
				ChatUtils.send(sender, String.format(G+"Writer %s @ %s, Notify: %s", W+note.getWriter()+G, W+date.toString()+G, W+String.valueOf(note.getNotify())+G));

				
				
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
