package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.addons.CachedContainerBlockEntity;
import com.ptsmods.morecommands.api.callbacks.PacketReceiveEvent;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class SearchItemCommand extends ClientCommand {
    public static Map<BlockPos, SearchItemResultType> RESULTS = new HashMap<>();
    @Getter
    private static Predicate<ItemStack> currentPredicate;

    @Override
    public void preinit() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            BlockEntity be = player.level.getBlockEntity(pos);
            if (be instanceof BaseContainerBlockEntity) CachedContainerBlockEntity.WAITING.setPlain(pos);

            return EventResult.pass();
        });

        // FIXME this doesn't seem to work.
        // (At least not on singleplayer, but maybe it does on multiplayer)
        PacketReceiveEvent.POST.register((packet, listener) -> {
            if (!(packet instanceof ClientboundContainerSetContentPacket scp) || CachedContainerBlockEntity.WAITING.get() == null)
                return;

            BlockEntity be = Objects.requireNonNull(Minecraft.getInstance().level)
                    .getBlockEntity(CachedContainerBlockEntity.WAITING.get());
            if (!(be instanceof CachedContainerBlockEntity)) return;
            ((CachedContainerBlockEntity) be).setCache(scp.getItems());
        });
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        dispatcher.register(cLiteral("searchitem")
                .then(cLiteral("clear").executes(ctx -> {
                    RESULTS.clear();
                    currentPredicate = null;

                    return sendMsg("Results cleared.");
                }))
                .then(cArgument("item", Compat.get().createItemPredicateArgument())
                        .executes(ctx -> {
                            RESULTS.clear();

                            Predicate<ItemStack> item = currentPredicate = ctx.getArgument("item", ItemPredicateArgument.Result.class);
                            BlockPos ppos = getPlayer().blockPosition();
                            final int range = 8;
                            BlockPos min = ppos.offset(-range, -range, -range);
                            BlockPos max = ppos.offset(range, range, range);

                            int present = 0;
                            int total = 0;
                            ClientLevel level = getWorld();
                            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                                BlockEntity be = level.getBlockEntity(pos);
                                if (!(be instanceof CachedContainerBlockEntity containerCache)) continue;

                                int result = containerCache.contains(item);
                                RESULTS.put(pos.immutable(), SearchItemResultType.values()[result]);

                                if (result == 2) present++;
                                total++;
                            }

                            if (total == 0) sendMsg("There are no containers near you to search through.");
                            else sendMsg("That item was found in %s%d %scontainer%s.", SF, present, DF, present == 1 ? "" : "s");
                            return present;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/search-item";
    }

    public enum SearchItemResultType {
        UNKNOWN(1, 0, 0, true),
        ABSENT(0, 0, 0, false),
        PRESENT(0, 1, 0, false);

        public final float r, g, b;
        @Getter
        private final boolean unknown;

        SearchItemResultType(float r, float g, float b, boolean unknown) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.unknown = unknown;
        }
    }
}
