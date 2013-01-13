package com.cyprias.AdminNotes;

import java.sql.SQLException;

import org.bukkit.ChatColor;

import com.cyprias.AdminNotes.configuration.Config;

public class Note {

	public Note(int int1, int int2, boolean boolean1, String string, String string2, String string3) {
		this.id = int1;
		this.time = int2;
		this.notify = boolean1;
		this.writer = string;
		this.player = string2;
		this.text = string3;
	}
	private int id, time;
	private Boolean notify;
	private String writer, player, text;
	
	public int getId(){
		return this.id;
	}

	public String getColouredId(){
		return (this.notify) ? ChatColor.valueOf(Config.getString("properties.notify-colour")) + String.valueOf(this.id)+ChatColor.RESET : ChatColor.WHITE + String.valueOf(this.id)+ChatColor.RESET;
	}
	
	public int getTime(){
		return this.time;
	}
	
	public Boolean getNotify(){
		return this.notify;
	}

	public String getWriter(){
		return this.writer;
	}
	
	public String getPlayer(){
		return this.player;
	}
	
	public String getColouredPlayer(){
		return ChatColor.valueOf(Config.getString("properties.player-colour")) + this.player + ChatColor.RESET;
	}
	
	public String getText(){
		return this.text;
	}
	
	public Boolean remove() throws SQLException{
		return Plugin.database.remove(id);
	}
	
}
