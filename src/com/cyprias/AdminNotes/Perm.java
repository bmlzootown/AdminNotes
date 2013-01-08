package com.cyprias.AdminNotes;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.cyprias.AdminNotes.configuration.Config;

public enum Perm {

	LIST("adminnotes.list", "You do not have permission list notes."), 
	CREATE("adminnotes.create", "You do not have permission to create notes."),
	CREATE_NOTIFIED("adminnotes.create-notified"),
	
	PARENT_MOD("lottery.mod", LIST, CREATE_NOTIFIED),
	PARENT_ADMIN("lottery.admin", PARENT_MOD, CREATE);

	private Perm(String value, Perm... childrenArray) {
		this(value, String.format(DEFAULT_ERROR_MESSAGE, value), childrenArray);
	}

	private Perm(String perm, String errorMess) {
		this.permission = perm;
		this.errorMessage = errorMess;
		this.bukkitPerm = new org.bukkit.permissions.Permission(permission);
		this.bukkitPerm.setDefault(PermissionDefault.getByName(Config.getString("properties.permission-default")));
	}

	private Perm(String value, String errorMess, Perm... childrenArray) {
		this(value, String.format(DEFAULT_ERROR_MESSAGE, value));
		for (Perm child : childrenArray) {
			child.setParent(this);
		}
	}

	public void loadPermission(PluginManager pm) {
		pm.addPermission(bukkitPerm);
	}

	private void setParent(Perm parentValue) {
		if (this.parent != null)
			return;
		this.parent = parentValue;
	}

	public Perm getParent() {
		return parent;
	}

	public org.bukkit.permissions.Permission getBukkitPerm() {
		return bukkitPerm;
	}

	public String getPermission() {
		return permission;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static final String DEFAULT_ERROR_MESSAGE = "You do not have access to %s";
	private final org.bukkit.permissions.Permission bukkitPerm;
	private Perm parent;
	private final String permission;
	private final String errorMessage;
}
