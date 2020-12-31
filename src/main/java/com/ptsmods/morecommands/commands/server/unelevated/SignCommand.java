package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;

public class SignCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("sign").executes(ctx -> {
            BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getPlayer(), ctx.getSource().getWorld(), 160, true, true);
            BlockEntity be = ctx.getSource().getWorld().getBlockEntity(result.getBlockPos());
            if (BlockTags.SIGNS.contains(ctx.getSource().getWorld().getBlockState(result.getBlockPos()).getBlock()) && be instanceof SignBlockEntity) {
                SignBlockEntity sbe = (SignBlockEntity) be;
                ReflectionHelper.setYarnFieldValue(SignBlockEntity.class, "editable", "field_12048", sbe, true);
                sbe.setEditor(ctx.getSource().getPlayer());
                ctx.getSource().getPlayer().openEditSignScreen(sbe); // Copying content onto edit screen is handled in MixinSignEditScreen.
                return 1;
            }
            return 0;
        }));
    }
}
