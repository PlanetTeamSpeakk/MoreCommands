package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.annotation.Nullable;

public class Permission {

	private String permission;
	private String modName;
	private Boolean reqPerms;
	private String description;
	public static final ArrayList<Permission> permissions = new ArrayList<>();

	/**
	 * Creates a new Permission.
	 *
	 * @param permission
	 *            A string, should be the name of the permission.
	 * @param modName
	 *            A string, should be the name of the mod, doing
	 *            Permission.toString() will return "modName.permission".
	 */
	public Permission(String modName, String permission, String description, Boolean reqPerms) {
		if (reqPerms) {
			this.modName = modName;
			this.permission = permission;
			try {
				for (Permission perm : permissions)
					if (perm.toString().equals(toString()) && !permission.equals("PERMISSION"))
						permissions.remove(perm);
			} catch (ConcurrentModificationException e) {}
			permissions.add(this);
		}
		this.description = description;
		this.reqPerms = reqPerms;
	}

	@Nullable
	public String getName() {
		return permission;
	}

	@Nullable
	public String getModName() {
		return modName;
	}

	@Override
	public String toString() {
		return modName.endsWith("*") ? modName : (modName == null ? "null" : modName) + "." + (permission == null ? "null" : permission);
	}

	public boolean reqPerms() {
		return reqPerms;
	}

	public String getDescription() {
		return description;
	}

	@Nullable
	public static Permission getPermissionByName(String name) {
		if (name.endsWith("*"))
			return new Permission(name, "", "All permissions available.", true);
		else {
			for (Permission perm : permissions)
				if (perm.toString().equals(name) || perm.getName().equals(name)) return perm;
			return null;
		}
	}

}
