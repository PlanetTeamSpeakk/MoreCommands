package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Permission {

	private String							permission;
	private String							modName;
	private boolean							reqPerms;
	private String							description;
	private ICommand						command;
	public static final List<Permission>	permissions	= new CopyOnWriteArrayList();
	private static Object					LOCK		= new Object();

	public Permission(String modName, String permission, String description, boolean reqPerms) {
		this(modName, permission, description, reqPerms, null);
	}

	/**
	 * Creates a new Permission.
	 *
	 * @param permission  A string, should be the name of the permission.
	 * @param modName     A string, should be the name of the mod, doing
	 *                        Permission.toString() will return
	 *                        "modName.permission".
	 * @param description A string, can be whatever you want, what the command
	 *                        assigned to this permission does, for instance.
	 * @param reqPerms
	 */
	public Permission(String modName, String permission, String description, boolean reqPerms, ICommand command) {
		Permission found = null;
		if (reqPerms && permission != null && modName != null && !permission.isEmpty() && !modName.isEmpty() && !permission.equals("PERMISSION")) {
			this.modName = modName;
			this.permission = permission;
			for (Permission perm : new ArrayList<>(permissions))
				if (perm.toString().equals(toString())) found = perm;
			if (found == null) permissions.add(this);
		}
		this.description = description;
		this.reqPerms = reqPerms;
		this.command = found == null ? command : found.getCommand();
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

	public ICommand getCommand() {
		return command;
	}

	public void setCommand() {
		for (ICommand c : FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().values())
			if (c instanceof CommandBase) {
				Permission perm = ((CommandBase) c).getPermission();
				if (perm.getName() != null && !perm.getName().equals("PERMISSION") && !perm.getName().isEmpty() && perm.getModName() != null && !perm.getModName().isEmpty() && perm.getName().equals(permission) && perm.getModName().equals(modName)) {
					command = c;
					break;
				}
			}
	}

	public static void setCommands() {
		for (Permission perm : new ArrayList<>(permissions))
			if (perm.getCommand() == null) perm.setCommand();
	}

	@Nullable
	public static Permission getPermissionByName(String name) {
		if (name.endsWith("*")) return new Permission(name, "", "All permissions available.", true);
		else {
			for (Permission perm : permissions)
				if (perm.toString().equals(name) || perm.getName().equals(name)) return perm;
			return null;
		}
	}

	@Nullable
	public static Permission getPermissionFromCommand(ICommand command) {
		for (Permission perm : permissions)
			if (perm.getCommand() != null && perm.getCommand().getName().equals(command.getName())) return perm;
		return null;
	}

}
