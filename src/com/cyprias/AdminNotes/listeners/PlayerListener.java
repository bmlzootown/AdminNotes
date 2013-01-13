package com.cyprias.AdminNotes.listeners;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Logger;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.Plugin.anCommand;
import com.cyprias.AdminNotes.command.Command;
import com.cyprias.AdminNotes.configuration.Config;

public class PlayerListener implements Listener {

	static public void unregisterEvents(JavaPlugin instance) {
		PlayerCommandPreprocessEvent.getHandlerList().unregister(instance);
	}

	private void loginNotifyQuery(Player player) throws SQLException {
		List<Note> notes = Plugin.database.getPlayerNotifications(player.getName());
		Note note;
		for (int i = 0; i < notes.size(); i++) {
			note = notes.get(i);
			ChatUtils.broadcast(
				Perm.LOGIN_NOTIFIED,
				String.format((ChatColor.GRAY + "[%s" + ChatColor.GRAY + "] " + ChatColor.WHITE + "%s" + ChatColor.GRAY + ": " + ChatColor.WHITE + "%s"),
					note.getColouredId(), note.getColouredPlayer(), note.getText()));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoinEvent(PlayerJoinEvent event) throws SQLException {
		final Player player = event.getPlayer();

		if (Config.getBoolean("properties.async-db-queries")) {
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					try {
						loginNotifyQuery(player);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			loginNotifyQuery(player);
		}

	}

	public class AutoNote {
		private CommandSender sender;
		String player, msg, text, location;

		public AutoNote(CommandSender sender, String msg, String player, String text, String location) {
			this.sender = sender;
			this.player = player;
			this.text = text;
			this.location = location;
		}
	}

	private final static Map<CommandSender, AutoNote> lastAutoNote = new HashMap<CommandSender, AutoNote>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws SQLException {
		String msg = event.getMessage();
		// String command = msg.split(" ")[0].replace("/", "");

		CommandSender sender = (CommandSender) event.getPlayer();

		anCommand command;
		String player, text, regex, location;
		for (int i = 0; i < Plugin.anCommands.size(); i++) {
			command = Plugin.anCommands.get(i);

			for (int r = 0; r < command.regex.length; r++) {
				regex = command.regex[r];

				if (msg.matches(regex)) {
					if (Config.getBoolean("properties.debug-messages"))
						Logger.severe(msg + " matches " + regex);
					
					if (!Config.getBoolean("properties.auto-note-permission") || sender.hasPermission("adminnotes.autonote." + command.title)) {// ||
						
						try{
							player = lastAutoNote.containsKey(sender) ? lastAutoNote.get(sender).player : msg.replaceFirst(regex, command.player);
						} catch (IndexOutOfBoundsException e) {
							if (Config.getBoolean("properties.debug-messages"))
								Logger.warning("Error getting player: " + e.getMessage());
							continue;
						}
						if (Config.getBoolean("properties.confirm-player-joined"))
							if (Plugin.getInstance().getServer().getOfflinePlayer(player).hasPlayedBefore() == false){
								if (Config.getBoolean("properties.debug-messages"))
									Logger.warning("Player has never been on the server: " + player);
								continue;
							}
						if (lastAutoNote.containsKey(sender)) {
							text = lastAutoNote.get(sender).text;
							location = lastAutoNote.get(sender).location;
						} else {
							text = msg.replaceAll(regex, command.note);
							location = event.getPlayer().getWorld().getName() + "|" + event.getPlayer().getLocation().getBlockX() + " "
								+ event.getPlayer().getLocation().getBlockY();
							text = text.replace("<location>", location);
							text = text.replace("<commander>", sender.getName());
						}
						
						if (r == (command.regex.length-1)) {
							Plugin.database.create(sender, Config.getBoolean("properties.notify-by-default"), player, text);
							Logger.info("[AutoNote] " + sender.getName() + " on " + player + ": " + text);

							if (lastAutoNote.containsKey(sender))
								lastAutoNote.remove(sender);
						} else {
							lastAutoNote.put(sender, new AutoNote(sender, msg, player, text, location));
							
							if (Config.getBoolean("properties.debug-messages"))
								Logger.info("Saving autonote for later: " + text);
						}

						return;
					}

				}
			}

		}

	}

}
