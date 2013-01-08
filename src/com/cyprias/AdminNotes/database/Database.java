package com.cyprias.AdminNotes.database;

import org.bukkit.command.CommandSender;

public interface Database {
	
	//boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	void list(CommandSender sender);
	
	Boolean init();
	
}
