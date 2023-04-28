package com.ptsmods.morecommands.commands.unelevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VaultCommand extends Command {
    public static final List<MenuType<ChestMenu>> types = ImmutableList.of(MenuType.GENERIC_9x1, MenuType.GENERIC_9x2,
            MenuType.GENERIC_9x3, MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6);

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("vault")
                .then(argument("vault", IntegerArgumentType.integer(1))
                        .executes(ctx -> execute(ctx, ctx.getSource().getPlayerOrException()))
                        .then(argument("player", EntityArgument.player())
                                .requires(hasPermissionOrOp("morecommands.vault.others"))
                                .executes(ctx -> execute(ctx, EntityArgument.getPlayer(ctx, "player"))))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/vault";
    }

    public int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer owner) throws CommandSyntaxException {
        int vault = ctx.getArgument("vault", Integer.class);
        int maxVaults = getCountFromPerms(ctx.getSource(), "morecommands.vault.max.", ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().vaultsRule()));
        if (maxVaults == 0) sendError(ctx, "Vaults are disabled on this server.");
        else if (vault > maxVaults) sendError(ctx, (owner == ctx.getSource().getPlayerOrException() ? "You" : IMoreCommands.get().textToString(owner.getDisplayName(), Style.EMPTY.withColor(ChatFormatting.RED), true)) +
                " may only have " + ChatFormatting.DARK_RED + ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().vaultsRule()) + ChatFormatting.RED + " vaults.");
        else {
            int rows = getCountFromPerms(ctx.getSource(), "morecommands.vault.rows.", ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().vaultRowsRule()));
            ctx.getSource().getPlayerOrException().openMenu(new SimpleMenuProvider((syncId, inv, player) -> new ChestMenu(types.get(rows - 1), syncId, inv,
                    Objects.requireNonNull(getVault(vault, owner)), rows), literalText(String.valueOf(DF) + ChatFormatting.BOLD + "Vault " + SF + ChatFormatting.BOLD + vault).build()));
            return 1;
        }
        return 0;
    }

    public static Container getVault(int id, Player player) {
        int maxVaults = player.getCommandSenderWorld().getGameRules().getInt(MoreGameRules.get().vaultsRule());
        if (id > maxVaults) return null;
        int rows = player.getCommandSenderWorld().getGameRules().getInt(MoreGameRules.get().vaultRowsRule());
        ListTag list = player.getEntityData().get(IDataTrackerHelper.get().vaults()).getList("Vaults", 9);
        if (list.size() < maxVaults)
            for (int i = 0; i < maxVaults - list.size() + 1; i++)
                list.add(new ListTag());
        SimpleContainer inv = new SimpleContainer(rows * 9);
        ListTag vault = list.getList(id-1);
        for (int i = 0; i < vault.size(); i++)
            inv.setItem(i, ItemStack.of(vault.getCompound(i)));
        inv.addListener(inventory -> {
            list.remove(id-1);
            ListTag stacks = new ListTag();
            for (int i = 0; i < inv.getContainerSize(); i++)
                stacks.add(inv.getItem(i).save(new CompoundTag()));
            list.add(id-1, stacks);
            player.getEntityData().set(IDataTrackerHelper.get().vaults(), MoreCommands.wrapTag("Vaults", list));
        });
        return inv;
    }

    @Override
    public Map<String, Boolean> getExtraPermissions() {
        return Stream.concat(IntStream.range(1, 100)
                        .mapToObj(i -> "morecommands.vault.max." + i),
                        IntStream.range(1, 6)
                                .mapToObj(i -> "morecommands.vault.rows." + i))
                .collect(Collectors.toMap(s -> s, s -> false));
    }
}
