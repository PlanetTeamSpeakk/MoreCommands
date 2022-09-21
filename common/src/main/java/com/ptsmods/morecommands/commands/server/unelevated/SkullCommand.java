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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SkullCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("skull")
                .then(argument("player", StringArgumentType.word())
                        .executes(ctx -> execute(ctx, 1))
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> execute(ctx, ctx.getArgument("amount", Integer.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/skull";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD, amount);
        String playername = ctx.getArgument("player", String.class);
        CompoundTag tag = new CompoundTag();
        try {
            UUID id = UUIDTypeAdapter.fromString(playername);
            try {
                playername = new Gson().fromJson(MoreCommands.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(id)), Map.class).get("name").toString();
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                sendError(ctx, "An error occurred while getting the playername attached to the given UUID.");
                return 0;
            }
        } catch (IllegalArgumentException ignored) {} // Given playername is not a UUID.
        tag.putString("SkullOwner", playername);
        stack.setTag(tag);
        if (ctx.getSource().getEntity() instanceof Player) ctx.getSource().getPlayerOrException().getInventory().add(stack);
        else ctx.getSource().getEntityOrException().spawnAtLocation(stack, ctx.getSource().getEntityOrException().getEyeHeight(ctx.getSource().getEntityOrException().getPose()));
        ctx.getSource().getLevel().playSound(null, ctx.getSource().getPosition().x, ctx.getSource().getPosition().y, ctx.getSource().getPosition().z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, ((ctx.getSource().getLevel().random.nextFloat() - ctx.getSource().getLevel().random.nextFloat()) * 0.7F + 1.0F) * 2.0F, 1F);
        sendMsg(ctx, "Your skull" + (stack.getCount() == 1 ? "" : "s") + " ha" + (stack.getCount() == 1 ? "s" : "ve") + " arrived.");
        return 1;
    }
}
