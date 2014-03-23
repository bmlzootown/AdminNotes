package com.cyprias.AdminNotes.command;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

public interface Command extends Listable {

	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) throws FileNotFoundException, IOException, InvalidConfigurationException;

	CommandAccess getAccess();

	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);

	// Temprary work around for commands that run with 0 args
	boolean hasValues();

}
