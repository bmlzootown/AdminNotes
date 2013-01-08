package com.cyprias.AdminNotes;

import org.bukkit.plugin.PluginManager;

public enum Perm {

	LIST("egonotes.list", "You do not have permission list notes."), 
	
	PARENT_ADMIN("lottery.admin", LIST);

	private Perm(String value, Perm... childrenArray) {
		this(value, DEFAULT_ERROR_MESSAGE, childrenArray);
	}

	private Perm(String perm, String errorMess) {
		this.permission = perm;
		this.errorMessage = errorMess;
		this.bukkitPerm = new org.bukkit.permissions.Permission(permission);
	}

	private Perm(String value, String errorMess, Perm... childrenArray) {
		this(value, DEFAULT_ERROR_MESSAGE);
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

	public static final String DEFAULT_ERROR_MESSAGE = "You do not have permission";
	private final org.bukkit.permissions.Permission bukkitPerm;
	private Perm parent;
	private final String permission;
	private final String errorMessage;
}
