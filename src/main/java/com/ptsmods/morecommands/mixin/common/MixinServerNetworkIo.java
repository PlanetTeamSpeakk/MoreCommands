package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ServerNetworkIo.class)
public class MixinServerNetworkIo {

    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private List<ClientConnection> connections;

    @Overwrite
    public void tick() {
        synchronized (connections) {
            List<ClientConnection> connections = new ArrayList<>(this.connections);
            if (MoreCommands.<ServerNetworkIo>cast(this).getServer().getWorld(World.OVERWORLD).getGameRules().getBoolean(MoreCommands.randomOrderPlayerTickRule)) Collections.shuffle(connections);
            for (ClientConnection clientConnection : connections) {
                if (clientConnection.hasChannel()) continue;
                if (clientConnection.isOpen()) {
                    try {
                        clientConnection.tick();
                    } catch (Exception e) {
                        if (clientConnection.isLocal()) throw new CrashException(CrashReport.create(e, "Ticking memory connection"));
                        LOGGER.warn("Failed to handle packet for {}", clientConnection.getAddress(), e);
                        Text text = new LiteralText("Internal server error");
                        clientConnection.send(new DisconnectS2CPacket(text), (future) -> clientConnection.disconnect(text));
                        clientConnection.disableAutoRead();
                    }
                } else {
                    this.connections.remove(clientConnection);
                    clientConnection.handleDisconnection();
                }
            }
        }
    }

}
