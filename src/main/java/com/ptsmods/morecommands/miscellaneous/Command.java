package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static com.ptsmods.morecommands.MoreCommands.*;

public abstract class Command {

    public static Formatting DF = MoreCommands.DF;
    public static Formatting SF = MoreCommands.SF;
    public static Style DS = MoreCommands.DS;
    public static Style SS = MoreCommands.SS;
    public static final Logger log = MoreCommands.log;
    public static final Predicate<ServerCommandSource> IS_OP = source -> source.hasPermissionLevel(source.getMinecraftServer().getOpPermissionLevel());

    public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher);

    public boolean forDedicated() {
        return false;
    }

    public static int sendMsg(CommandContext<ServerCommandSource> ctx, String msg) {
        return sendMsg(ctx, new LiteralText(fixResets(msg)).setStyle(DS));
    }

    public static int sendMsg(CommandContext<ServerCommandSource> ctx, Text msg) {
        ctx.getSource().sendFeedback(msg.shallowCopy().setStyle(msg.getStyle() == null ? DS : msg.getStyle().getColor() == null ? msg.getStyle().withFormatting(DF) : msg.getStyle()), true);
        return 1;
    }

    static String fixResets(String s) {
        return s.replaceAll(Formatting.RESET.toString(), Formatting.RESET.toString() + DF).replaceAll("\n", "\n" + DF);
    }

    public static int sendMsg(Entity entity, String msg) {
        return sendMsg(entity, new LiteralText(msg).setStyle(DS));
    }

    public static int sendMsg(Entity entity, Text msg) {
        msg = msg.shallowCopy().setStyle(msg.getStyle() == null ? DS : msg.getStyle());
        if (entity instanceof PlayerEntity) ((PlayerEntity) entity).sendMessage(msg, false);
        else entity.sendSystemMessage(msg, entity.getUuid());
        return 1;
    }

    public static void broadcast(MinecraftServer server, String msg) {
        broadcast(server, new LiteralText(msg).setStyle(DS));
    }

    public static void broadcast(MinecraftServer server, Text msg) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList())
            sendMsg(player, msg);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
        return CommandManager.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        return CommandManager.argument(name, type);
    }

    public static String translateFormats(String s) {
        for (Formatting f : Formatting.values())
            s = s.replaceAll("&" + getChar(f), f.toString());
        return s;
    }

    public static String joinNicely(Collection<String> strings) {
        return joinNicely(strings, SF, DF);
    }

    public static String joinNicely(Collection<String> strings, Formatting colour, Formatting commaColour) {
        List<String> l = new ArrayList<>(strings);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < l.size(); i++)
            s.append(colour == null ? "" : colour).append(l.get(i)).append(commaColour == null ? "" : commaColour).append(i == l.size()-2 ? " and" : i == l.size()-1 ? "" : ",").append(i == l.size()-1 ? "" : " ");
        return s.toString();
    }

    public static Formatting formatFromBool(boolean b) {
        return b ? Formatting.GREEN : Formatting.RED;
    }

    public static String formatFromBool(boolean b, String yes, String no) {
        return formatFromBool(b) + (b ? yes : no);
    }

    public static String formatFromValue(float v, float max, float yellow, float green) {
        float percent = v/max;
        return "" + (percent >= green ? Formatting.GREEN : percent >= yellow ? Formatting.YELLOW : Formatting.RED) + DF + "/" + Formatting.GREEN + max;
    }

    public static boolean isOp(CommandContext<ServerCommandSource> ctx) {
        return ctx.getSource().hasPermissionLevel(ctx.getSource().getMinecraftServer().getOpPermissionLevel());
    }

    public static UUID getServerUuid(MinecraftServer server) {
        return UUID.nameUUIDFromBytes(server.getCommandSource().getName().getBytes(StandardCharsets.UTF_8));
    }

}
