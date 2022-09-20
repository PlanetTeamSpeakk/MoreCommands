package com.ptsmods.morecommands.forge.compat;

import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.ForgeCompatAdapater;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.HashMap;
import java.util.Map;

public class ForgeCompat182 extends ForgeCompatAdapater {
    private final Map<String, PermissionNode<?>> permissions = new HashMap<>();

    @Override
    public boolean shouldRegisterListener() {
        return Version.getCurrent().isNewerThanOrEqual(Version.V1_18_2);
    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(new Listener()::onPermissionGather);
    }

    @Override
    public void registerPermission(String permission, int defaultLevel, String desc) {
        boolean defaultValue = defaultLevel < 2;
        PermissionNode<Boolean> permissionNode = new PermissionNode<>(new ResourceLocation("morecommands:" +
                (permission.startsWith("morecommands.") ? permission.substring("morecommands.".length()) : permission)),
                PermissionTypes.BOOLEAN, (player, uuid, permissionDynamicContexts) -> defaultValue);
        permissions.put(permission, permissionNode);
    }

    @Override
    public boolean checkPermission(ServerPlayer player, String permission) {
        return !permissions.containsKey(permission) || (boolean) PermissionAPI.getPermission(player, permissions.get(permission));
    }

    private class Listener {
        private void onPermissionGather(PermissionGatherEvent.Nodes event) {
            event.addNodes(permissions.values());
        }
    }
}
