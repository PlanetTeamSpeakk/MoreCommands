package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import com.ptsmods.morecommands.miscellaneous.Command;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Hand;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PowerToolCommand extends Command {

    @Override
    public void preinit(boolean serverOnly) {
        if (Platform.getEnv() == EnvType.CLIENT) MouseEvent.EVENT.register((button, action, mods) -> checkPowerToolClient(action));
        else if (serverOnly) {
            InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> checkPowerToolServer(player, hand));
            InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> checkPowerToolServer(player, hand).isTrue() ?
                    CompoundEventResult.interruptTrue(player.getStackInHand(hand)) : CompoundEventResult.pass());
        }
    }

    @Environment(EnvType.CLIENT)
    private boolean checkPowerToolClient(int action) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && action == 1 && MinecraftClient.getInstance().currentScreen == null) {
            String cmd = Optional.ofNullable(getPowerTool(player, Hand.MAIN_HAND)).orElse(getPowerTool(player, Hand.OFF_HAND));
            if (cmd != null) {
                MinecraftClient.getInstance().player.sendChatMessage("/" + cmd);
                return true;
            }
        }
        return false;
    }

    @Environment(EnvType.SERVER)
    private EventResult checkPowerToolServer(PlayerEntity player, Hand hand) {
        if (player != null) {
            String cmd = getPowerTool(player, hand);
            if (cmd != null) {
                Objects.requireNonNull(player.getServer()).getCommandManager().execute(player.getCommandSource(), cmd);
                return EventResult.interruptTrue();
            }
        }
        return EventResult.pass();
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("pt", dispatcher.register(literalReq("powertool").executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            ItemStack stack = getPowerToolStack(player);
            if (stack == null) sendError(ctx, "The item you're holding is not a powertool.");
            else if (!getPowerToolOwner(stack).equals(player.getUuid())) sendError(ctx, "This is not your powertool.");
            else {
                setPowerTool(player, stack, null);
                sendMsg(ctx, "The item you're holding is no longer a powertool.");
                return 1;
            }
            return 0;
        }).then(argument("cmd", StringArgumentType.greedyString()).executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            ItemStack stack = player.getMainHandStack();
            if (isPowerTool(stack) && !getPowerToolOwner(stack).equals(player.getUuid())) sendError(ctx, "This is not your powertool.");
            else {
                setPowerTool(player, stack, ctx.getArgument("cmd", String.class));
                sendMsg(ctx, "Your item is now a powertool!");
                return 1;
            }
            return 0;
        })))));
    }

    public static String getPowerTool(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        return isPowerTool(stack) && getPowerToolOwner(stack).equals(player.getUuid()) ? Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool").getString("Command") : null;
    }

    public static void setPowerTool(PlayerEntity owner, ItemStack stack, String command) {
        NbtCompound tag = stack.getOrCreateNbt();
        if (command == null && tag.contains("PowerTool", 10)) tag.remove("PowerTool");
        else if (command != null) {
            if (command.startsWith("/")) command = command.substring(1);
            if (!tag.contains("PowerTool", 10)) tag.put("PowerTool", new NbtCompound());
            tag.getCompound("PowerTool").putUuid("Owner", owner.getUuid());
            tag.getCompound("PowerTool").putString("Command", command);
        }
    }

    public static UUID getPowerToolOwner(ItemStack stack) {
        return stack.getOrCreateNbt().getCompound("PowerTool").getUuid("Owner");
    }

    public static ItemStack getPowerToolStack(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getNbt() == null || !stack.getNbt().contains("PowerTool", 10)) stack = player.getOffHandStack();
        return stack.getNbt() == null || !stack.getNbt().contains("PowerTool", 10) ? null : stack;
    }

    public static boolean isPowerTool(ItemStack stack) {
        return stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains("PowerTool", 10);
    }
}
