package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SkullCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("skull").then(argument("player", StringArgumentType.word()).executes(ctx -> execute(ctx, 1)).then(argument("amount", IntegerArgumentType.integer(1)).executes(ctx -> execute(ctx, ctx.getArgument("amount", Integer.class))))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, int amount) throws CommandSyntaxException {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD, amount);
        String playername = ctx.getArgument("player", String.class);
        CompoundTag tag = new CompoundTag();
        try {
            UUID id = UUIDTypeAdapter.fromString(playername);
            try {
                playername = new Gson().fromJson(MoreCommands.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(id)), Map.class).get("name").toString();
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                sendMsg(ctx, Formatting.RED + "An error occurred while getting the playername attached to the given UUID.");
                return 0;
            }
        } catch (IllegalArgumentException ignored) {} // Given playername is not a UUID.
        tag.putString("SkullOwner", playername);
        stack.setTag(tag);
        if (ctx.getSource().getEntity() instanceof PlayerEntity) ctx.getSource().getPlayer().inventory.insertStack(stack);
        else ctx.getSource().getEntityOrThrow().dropStack(stack, ctx.getSource().getEntityOrThrow().getEyeHeight(ctx.getSource().getEntityOrThrow().getPose()));
        ctx.getSource().getWorld().playSound(null, ctx.getSource().getPosition().x, ctx.getSource().getPosition().y, ctx.getSource().getPosition().z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, ((ctx.getSource().getWorld().random.nextFloat() - ctx.getSource().getWorld().random.nextFloat()) * 0.7F + 1.0F) * 2.0F, 1F);
        sendMsg(ctx, "Your skull" + (stack.getCount() == 1 ? "" : "s") + " ha" + (stack.getCount() == 1 ? "s" : "ve") + " arrived.");
        return 1;
    }
}
