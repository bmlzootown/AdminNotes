package com.cyprias.AdminNotes.listeners;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Perm;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.configuration.Config;

public class PlayerListener implements Listener {

	
	private void loginNotifyQuery(Player player) throws SQLException{
		List<Note> notes = Plugin.database.getPlayerNotifications(player.getName());
		Note note;
		for (int i=0; i< notes.size(); i++){
			note = notes.get(i);
			ChatUtils.broadcast(Perm.LOGIN_NOTIFIED, String.format((ChatColor.GRAY+"["+ChatColor.WHITE+"%s"+ChatColor.GRAY+"] "+ChatColor.WHITE+"%s"+ChatColor.GRAY+": "+ChatColor.WHITE+"%s"), note.getId(), note.getPlayer(), note.getText()));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoinEvent(PlayerJoinEvent event) throws SQLException {
		final Player player = event.getPlayer();
		
		
		if (Config.getBoolean("properties.async-db-queries")){
			Plugin instance = Plugin.getInstance();
			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					try {
						loginNotifyQuery(player);
					} catch (SQLException e) {e.printStackTrace();}
				}
			});
		} else {
			loginNotifyQuery(player);
		}
		
	}
	
}
