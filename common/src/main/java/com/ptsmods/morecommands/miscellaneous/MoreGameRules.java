package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableMap;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Accessors(fluent = true)
@Getter
public class MoreGameRules implements IMoreGameRules {
    private static final List<GameRules.Key<?>> pendingPermChecks = new ArrayList<>();
    private static final MoreGameRules INSTANCE = new MoreGameRules();
    private final Map<String, GameRules.Key<?>> allRules = new LinkedHashMap<>();
    private final GameRules.Key<EnumRule<FormattingColour>> DFrule = createEnumRule("defaultFormatting", FormattingColour.class, FormattingColour.GOLD,
            (server, value) -> MoreCommands.updateFormatting(server, 0, value.get()));
    private final GameRules.Key<EnumRule<FormattingColour>> SFrule = createEnumRule("secondaryFormatting", FormattingColour.class, FormattingColour.YELLOW,
            (server, value) -> MoreCommands.updateFormatting(server, 1, value.get()));
    private final GameRules.Key<GameRules.BooleanValue> doMeltRule = createBooleanRule("doMelt", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.IntegerValue> maxHomesRule = createIntRule("maxHomes", GameRules.Category.PLAYER, 3);
    private final GameRules.Key<GameRules.BooleanValue> doSilkSpawnersRule = createBooleanRule("doSilkSpawners", GameRules.Category.DROPS, false, true);
    private final GameRules.Key<GameRules.BooleanValue> randomOrderPlayerTickRule = createBooleanRule("randomOrderPlayerTick", GameRules.Category.PLAYER, true, false);
    private final GameRules.Key<GameRules.IntegerValue> hopperTransferCooldownRule = createIntRule("hopperTransferCooldown", GameRules.Category.UPDATES, 8);
    private final GameRules.Key<GameRules.IntegerValue> hopperTransferRateRule = createIntRule("hopperTransferRate", GameRules.Category.UPDATES, 1);
    private final GameRules.Key<GameRules.BooleanValue> doFarmlandTrampleRule = createBooleanRule("doFarmlandTrample", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doJoinMessageRule = createBooleanRule("doJoinMessage", GameRules.Category.PLAYER, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doExplosionsRule = createBooleanRule("doExplosions", GameRules.Category.MISC, true, false);
    private final GameRules.Key<GameRules.IntegerValue> wildLimitRule = createIntRule("wildLimit", GameRules.Category.PLAYER, 5000);
    private final GameRules.Key<GameRules.IntegerValue> tpaTimeoutRule = createIntRule("tpaTimeout", GameRules.Category.PLAYER, 2400);
    private final GameRules.Key<GameRules.BooleanValue> fluidsInfiniteRule = createBooleanRule("fluidsInfinite", GameRules.Category.UPDATES, false, false);
    private final GameRules.Key<GameRules.BooleanValue> doLiquidFlowRule = createBooleanRule("doLiquidFlow", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.IntegerValue> vaultRowsRule = createIntRule("vaultRows", GameRules.Category.PLAYER, 6);
    private final GameRules.Key<GameRules.IntegerValue> vaultsRule = createIntRule("vaults", GameRules.Category.PLAYER, 3);
    private final GameRules.Key<GameRules.IntegerValue> nicknameLimitRule = createIntRule("nicknameLimit", GameRules.Category.PLAYER, 16);
    private final GameRules.Key<GameRules.BooleanValue> doSignColoursRule = createBooleanRule("doSignColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doBookColoursRule = createBooleanRule("doBookColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doChatColoursRule = createBooleanRule("doChatColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doItemColoursRule = createBooleanRule("doItemColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doEnchantLevelLimitRule = createBooleanRule("doEnchantLevelLimit", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doPriorWorkPenaltyRule = createBooleanRule("doPriorWorkPenalty", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doItemsFireDamageRule = createBooleanRule("doItemsFireDamage", GameRules.Category.DROPS, true, true);
    private final GameRules.Key<GameRules.BooleanValue> doPathFindingRule = createBooleanRule("doPathFinding", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.BooleanValue> doGoalsRule = createBooleanRule("doGoals", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.BooleanValue> doStacktraceRule = createBooleanRule("doStacktrace", GameRules.Category.MISC, true, false);
    private final GameRules.Key<GameRules.BooleanValue> doChairsRule = createBooleanRule("doChairs", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanValue> sendCommandFeedbackToOpsRule = createBooleanRule("sendCommandFeedbackToOps", GameRules.Category.PLAYER, true, true);

    private MoreGameRules() {}

    private GameRules.Key<GameRules.BooleanValue> createBooleanRule(String name, GameRules.Category category, boolean defaultValue, boolean doPermCheck) {
        GameRules.Key<GameRules.BooleanValue> key = GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
        if (doPermCheck) {
            pendingPermChecks.add(key);
            MoreCommands.registerPermission("morecommands.gamerule." + key.getId(), true);
        }

        allRules.put(name, key);
        return key;
    }

    private GameRules.Key<GameRules.IntegerValue> createIntRule(String name, GameRules.Category category, int defaultValue) {
        GameRules.Key<GameRules.IntegerValue> key = GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
        allRules.put(name, key);
        return key;
    }

    private <E extends Enum<E>> GameRules.Key<EnumRule<E>> createEnumRule(String name, Class<E> clazz, E defaultValue, BiConsumer<MinecraftServer, EnumRule<E>> changeListener) {
        GameRules.Key<EnumRule<E>> key = GameRules.register(name, GameRules.Category.MISC, EnumRule.createEnumRule(clazz, defaultValue, changeListener));
        allRules.put(name, key);
        return key;
    }

    @Override
    public Map<String, GameRules.Key<?>> allRules() {
        return ImmutableMap.copyOf(allRules);
    }

    public boolean checkBooleanWithPerm(GameRules gameRules, GameRules.Key<GameRules.BooleanValue> key, Entity entity) {
        // Returns inversed value if the player does not have the required permission.
        // Returns set value is entity is null, LuckPerms (or compatible) isn't installed or permission isn't set for entity.
        return entity == null ? gameRules.getBoolean(key) :
                MoreCommandsArch.checkPermission(entity.createCommandSourceStack(), "morecommands.gamerule." + key.getId(), true) == gameRules.getBoolean(key);
    }

    public void checkPerms(MinecraftServer server) {
        SharedSuggestionProvider commandSource = server.createCommandSourceStack();
        // Idk how LuckPerms knows what perms there are, but I presume it just stores every perm that's requested.
        // So we request all the gamerule perms here.
        if (Command.isPermissionsLoaded()) pendingPermChecks.forEach(key -> MoreCommandsArch.checkPermission(commandSource, "morecommands.gamerule." + key.getId()));
    }

    public static void init() {
        Holder.setMoreGameRules(INSTANCE);
    }

    public static MoreGameRules get() {
        return INSTANCE;
    }
}
