package com.cyprias.AdminNotes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class SearchParser {
	public CommandSender player = null;
	public List<String> players = new ArrayList<String>();
	public List<String> writers = new ArrayList<String>();
	public List<String> keywords = new ArrayList<String>();

	public String searchString;
	public SearchParser(CommandSender player, String[] args) throws IllegalArgumentException {
		this.player = player;

		String lastParam = "";
		boolean paramSet = false;

		searchString = Plugin.getFinalArg(args, 0);
		
		String arg;
		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if (arg.isEmpty())
				continue;

			if (!paramSet) {
				if (arg.length() < 2)
					throw new IllegalArgumentException("Invalid argument format: &7" + arg);
				if (!arg.substring(1, 2).equals(":")) {
					if (arg.contains(":"))
						throw new IllegalArgumentException("Invalid argument format: &7" + arg);

					// No arg specified, treat as player
					players.add(arg);
					continue;
				}

				lastParam = arg.substring(0, 1).toLowerCase();
				paramSet = true;

				if (arg.length() == 2) {
					if (i == (args.length - 1)) // No values specified
						throw new IllegalArgumentException("Invalid argument format: &7" + arg);
					else
						// User put a space between the colon and value
						continue;
				}

				// Get values out of argument
				arg = arg.substring(2);
			}

			if (paramSet) {
				if (arg.isEmpty()) {
					throw new IllegalArgumentException("Invalid argument format: &7" + lastParam + ":");
				}

				String[] values = arg.split(",");

				// Players
				if (lastParam.equals("p"))
					for (String p : values)
						players.add(p);

				// Writer
				if (lastParam.equals("w"))
					for (String p : values)
						writers.add(p);

				// Keyword
				if (lastParam.equals("k"))
					for (String p : values)
						keywords.add(p);

			} else
				throw new IllegalArgumentException("Invalid parameter supplied: &7" + lastParam);

			paramSet = false;
		}

	}

}
