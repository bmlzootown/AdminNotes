package com.cyprias.AdminNotes.database;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;

public interface Database {
	
	//boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	void list(CommandSender sender);
	
	Boolean init();
	
	void create(CommandSender sender, Boolean notify, String player, String text) throws SQLException;
	
}
