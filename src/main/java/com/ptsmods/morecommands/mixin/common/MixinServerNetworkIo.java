package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerNetworkIo.class)
public class MixinServerNetworkIo {
	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", remap = false), method = "tick")
	private Iterator<ClientConnection> tick_newArrayList(List<ClientConnection> connections) {
		connections = new ArrayList<>(connections);
		if (Objects.requireNonNull(ReflectionHelper.<ServerNetworkIo>cast(this).getServer().getWorld(World.OVERWORLD)).getGameRules().getBoolean(MoreCommands.randomOrderPlayerTickRule)) Collections.shuffle(connections);
		return connections.iterator();
	}
}
