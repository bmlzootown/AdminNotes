package com.cyprias.AdminNotes.database;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.Note;

public interface Database {
	
	//boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	List<Note> list(CommandSender sender, int page) throws SQLException;
	
	Boolean init();
	
	Boolean create(CommandSender sender, Boolean notify, String player, String text) throws SQLException;
	
	Note info(int id) throws SQLException;
	
}
