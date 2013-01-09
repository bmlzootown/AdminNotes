package com.cyprias.AdminNotes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.cyprias.AdminNotes.ChatUtils;
import com.cyprias.AdminNotes.Logger;
import com.cyprias.AdminNotes.Note;
import com.cyprias.AdminNotes.Plugin;
import com.cyprias.AdminNotes.configuration.Config;

public class MySQL implements Database {




	static String prefix;
	static String notes_table;
	public Boolean init() {
		if (!canConnect()){
			Logger.info("Failed to connect to MySQL!");
			return false;
		}
		prefix = Config.getString("mysql.prefix");
		notes_table = prefix+ "Notes";
		
		
		try {
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public List<Note> list(CommandSender sender, int page) throws SQLException {
		
		int rows = getResultCount("SELECT COUNT(*) FROM " + notes_table);

		//Logger.info("rows: " + rows);
		
		int perPage = Config.getInt("properties.notes-per-page");
		
		//Logger.info("page1: " + page);
		int max = (rows / 10) + 1;
		if (rows % perPage == 0)
			max--;
		
		if (page < 0){
			page = max - (Math.abs(page) - 1);
		}else{
			if (page > max)
				page = max;
			
		}

		ChatUtils.send(sender, "Page: " + (page+1) + "/" + (max+1));
		List<Note> notes = new ArrayList<Note>();
		
		
		
		queryReturn results = executeQuery("SELECT * FROM `"+notes_table+"` LIMIT "+(perPage * page)+" , " + perPage);
		ResultSet r = results.result;

		while (r.next()) {
		//	Logger.info("id: " + r.getInt(1));
			notes.add(new Note(r.getInt(1), r.getInt(2), r.getBoolean(3), r.getString(4), r.getString(5), r.getString(6)));
			
		}
		
		
		
		return notes;
	}
	
	public void createTables() throws SQLException{
		Connection con = getConnection();
		
		if (tableExists(notes_table) == false) {
			Logger.info("Creating Notes table.");
			con.prepareStatement("CREATE TABLE " + notes_table+ " (`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `time` BIGINT NOT NULL, `notify` BOOLEAN NOT NULL DEFAULT '0', `writer` VARCHAR(32) NOT NULL, `player` VARCHAR(32) NOT NULL, `text` TEXT NOT NULL) ENGINE = InnoDB").executeUpdate();
		}
		
	}
	
	public static class queryReturn {
		Connection con;
		PreparedStatement statement;
		public ResultSet result;

		public queryReturn(Connection con, PreparedStatement statement, ResultSet result) {
			this.con = con;
			this.statement = statement;
			this.result = result;
		}

		public void close() throws SQLException {
			this.result.close();
			this.statement.close();
			this.con.close();
		}

	}
	
	public static queryReturn executeQuery(String query, Object... args) throws SQLException {
		Connection con = getConnection();

		queryReturn myreturn = null;// = new queryReturn();

		PreparedStatement statement = con.prepareStatement(query);

		int i = 0;
		for (Object a : args) {
			i += 1;
			// plugin.info("executeQuery "+i+": " + a);
			statement.setObject(i, a);
		}
		ResultSet result = statement.executeQuery();
		myreturn = new queryReturn(con, statement, result);

		return myreturn;
	}
	
	public static int getResultCount(String query, Object... args) throws SQLException {
		queryReturn qReturn = executeQuery(query, args);
		qReturn.result.first();
		int rows = qReturn.result.getInt(1);
		qReturn.close();
		return rows;
	}
	
	public static boolean tableExists(String tableName) throws SQLException {
		boolean exists = false;
		Connection con = getConnection();
		ResultSet result = con.prepareStatement("show tables like '" + tableName + "'").executeQuery();
		result.last();
		if (result.getRow() != 0) 
			exists = true;
		con.close();
		return exists;
	}

	
	private static String getURL(){
		return "jdbc:mysql://" + Config.getString("mysql.hostname") + ":" + Config.getInt("mysql.port") + "/" + Config.getString("mysql.database");
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getURL(), Config.getString("mysql.username"), Config.getString("mysql.password"));
	}
	
	private Boolean canConnect(){
		try {
			@SuppressWarnings("unused")
			Connection con = getConnection();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public Boolean create(CommandSender sender, Boolean notify, String player, String text) throws SQLException {
		int succsess = executeUpdate("INSERT INTO `"+notes_table+"` (`time` ,`notify` ,`writer` ,`player` ,`text`) VALUES (?, ?, ?, ?, ?);", (int) Plugin.getUnixTime(), notify, sender.getName(), player, text);
		return (succsess > 0) ? true : false;
	}
	
	public static int executeUpdate(String query, Object... args) throws SQLException {
		Connection con = getConnection();
		int sucessful = 0;

		PreparedStatement statement = con.prepareStatement(query);
		int i = 0;
		for (Object a : args) {
			i += 1;
			statement.setObject(i, a);
		}
		sucessful = statement.executeUpdate();
		con.close();
		return sucessful;
	}
	
	
}
