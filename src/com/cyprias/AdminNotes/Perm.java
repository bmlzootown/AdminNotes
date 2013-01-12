package com.cyprias.AdminNotes;

import java.util.HashMap;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.cyprias.AdminNotes.configuration.Config;

public enum Perm {

	LIST("adminnotes.list", "You do not have permission list notes."), 
	CREATE("adminnotes.create", "You do not have permission to create notes."),
	CREATE_NOTIFIED("adminnotes.create-notified"),
	INFO("adminnotes.info", "You do not have permissiont to see note info."), 
	LOGIN_NOTIFIED("adminnotes.login-notified"), 
	NOTIFY("adminnotes.notify", "You do not have permissiont to toggle notify."),
	SEARCH("adminnotes.search", "You do not have permission to search notes."),
	REMOVE("adminnotes.remove", "You do not have permission remove notes."),
	PARENT_MOD("adminnotes.mod", LIST, CREATE_NOTIFIED, INFO, LOGIN_NOTIFIED, SEARCH),
	PARENT_ADMIN("adminnotes.admin", PARENT_MOD, CREATE, REMOVE, NOTIFY);

	private Perm(String value, Perm... childrenArray) {
		this(value, String.format(DEFAULT_ERROR_MESSAGE, value), childrenArray);
	}

	private Perm(String perm, String errorMess) {
		this.permission = perm;
		this.errorMessage = errorMess;
		this.bukkitPerm = new Permission(permission, PermissionDefault.getByName(Config.getString("properties.permission-default")));
	}

	
	private Perm(String value, String errorMess, Perm... childrenArray) {
		this(value, String.format(DEFAULT_ERROR_MESSAGE, value));
		for (Perm child : childrenArray) {
			child.setParent(this);
		}
	}
	public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	
	private final String permission;
	public static final String DEFAULT_ERROR_MESSAGE = "You do not have access to %s";
	public Perm getParent() {
		return parent;
	}

	private final Permission bukkitPerm;
	private Perm parent;
	private final String errorMessage;
	
	
	private void setParent(Perm parentValue) {
		if (this.parent != null)
			return;
		this.parent = parentValue;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public void loadPermission(PluginManager pm) {
		pm.addPermission(bukkitPerm);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/*
	public Permission getBukkitPerm() {
		return bukkitPerm;
	}
	*/
}
