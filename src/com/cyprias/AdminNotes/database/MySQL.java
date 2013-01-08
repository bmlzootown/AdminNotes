package com.cyprias.AdminNotes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Logger;
import com.cyprias.AdminNotes.configuration.Config;

public class MySQL implements Database {


	public void list(CommandSender sender) {
		
	}

	public Boolean init() {
		if (canConnect()){
			Logger.info("Connected to MySQL!");
			
			
			
		}else{
			Logger.info("Failed to connect to MySQL!");
		}
		
		
		return false;
	}

	private static String getURL(){
		return "jdbc:mysql://" + Config.getString("mysql.hostname") + ":" + Config.getInt("mysql.port") + "/" + Config.getString("mysql.database");
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getURL(), Config.getString("mysql.username"), Config.getString("mysql.password"));
	}
	
	private Boolean canConnect(){
		try {
			Connection con = getConnection();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
}
