package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.ptsmods.morecommands.MoreCommandsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ClientCommand extends Command {

    public static final Logger log = MoreCommandsClient.log;

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        throw new RuntimeException(new IllegalAccessException("Client commands can only be registered via the cRegister method."));
    }

    public abstract void cRegister(CommandDispatcher<ClientCommandSource> dispatcher);

    public LiteralArgumentBuilder<ClientCommandSource> cLiteral(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public <T> RequiredArgumentBuilder<ClientCommandSource, T> cArgument(String name, ArgumentType<T> argument) {
        return RequiredArgumentBuilder.argument(name, argument);
    }

    public static void sendMsg(String s) {
        sendMsg(new LiteralText(fixResets(s)).setStyle(DS));
    }

    public static void sendMsg(Text t) {
        MinecraftClient.getInstance().player.sendMessage(t, false);
    }

    public static ClientPlayerEntity getPlayer() {
        return getClient().player;
    }

    public static ClientWorld getWorld() {
        return getClient().world;
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static ServerCommandSource getServerCommandSource() {
        return new ServerCommandSource(CommandOutput.DUMMY, getPlayer().getPos(), getPlayer().getRotationClient(), null, 0, getPlayer().getEntityName(), getPlayer().getDisplayName(), null, getPlayer());
    }

}
