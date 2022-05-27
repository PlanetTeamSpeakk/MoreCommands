package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableMap;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

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
    private final GameRules.Key<GameRules.BooleanRule> doMeltRule = createBooleanRule("doMelt", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.IntRule> maxHomesRule = createIntRule("maxHomes", GameRules.Category.PLAYER, 3);
    private final GameRules.Key<GameRules.BooleanRule> doSilkSpawnersRule = createBooleanRule("doSilkSpawners", GameRules.Category.DROPS, false, true);
    private final GameRules.Key<GameRules.BooleanRule> randomOrderPlayerTickRule = createBooleanRule("randomOrderPlayerTick", GameRules.Category.PLAYER, true, false);
    private final GameRules.Key<GameRules.IntRule> hopperTransferCooldownRule = createIntRule("hopperTransferCooldown", GameRules.Category.UPDATES, 8);
    private final GameRules.Key<GameRules.IntRule> hopperTransferRateRule = createIntRule("hopperTransferRate", GameRules.Category.UPDATES, 1);
    private final GameRules.Key<GameRules.BooleanRule> doFarmlandTrampleRule = createBooleanRule("doFarmlandTrample", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doJoinMessageRule = createBooleanRule("doJoinMessage", GameRules.Category.PLAYER, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doExplosionsRule = createBooleanRule("doExplosions", GameRules.Category.MISC, true, false);
    private final GameRules.Key<GameRules.IntRule> wildLimitRule = createIntRule("wildLimit", GameRules.Category.PLAYER, 5000);
    private final GameRules.Key<GameRules.IntRule> tpaTimeoutRule = createIntRule("tpaTimeout", GameRules.Category.PLAYER, 2400);
    private final GameRules.Key<GameRules.BooleanRule> fluidsInfiniteRule = createBooleanRule("fluidsInfinite", GameRules.Category.UPDATES, false, false);
    private final GameRules.Key<GameRules.BooleanRule> doLiquidFlowRule = createBooleanRule("doLiquidFlow", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.IntRule> vaultRowsRule = createIntRule("vaultRows", GameRules.Category.PLAYER, 6);
    private final GameRules.Key<GameRules.IntRule> vaultsRule = createIntRule("vaults", GameRules.Category.PLAYER, 3);
    private final GameRules.Key<GameRules.IntRule> nicknameLimitRule = createIntRule("nicknameLimit", GameRules.Category.PLAYER, 16);
    private final GameRules.Key<GameRules.BooleanRule> doSignColoursRule = createBooleanRule("doSignColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doBookColoursRule = createBooleanRule("doBookColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doChatColoursRule = createBooleanRule("doChatColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doItemColoursRule = createBooleanRule("doItemColours", GameRules.Category.CHAT, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doEnchantLevelLimitRule = createBooleanRule("doEnchantLevelLimit", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doPriorWorkPenaltyRule = createBooleanRule("doPriorWorkPenalty", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doItemsFireDamageRule = createBooleanRule("doItemsFireDamage", GameRules.Category.DROPS, true, true);
    private final GameRules.Key<GameRules.BooleanRule> doPathFindingRule = createBooleanRule("doPathFinding", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.BooleanRule> doGoalsRule = createBooleanRule("doGoals", GameRules.Category.UPDATES, true, false);
    private final GameRules.Key<GameRules.BooleanRule> doStacktraceRule = createBooleanRule("doStacktrace", GameRules.Category.MISC, true, false);
    private final GameRules.Key<GameRules.BooleanRule> doChairsRule = createBooleanRule("doChairs", GameRules.Category.MISC, true, true);
    private final GameRules.Key<GameRules.BooleanRule> sendCommandFeedbackToOpsRule = createBooleanRule("sendCommandFeedbackToOps", GameRules.Category.PLAYER, true, true);

    private MoreGameRules() {}

    private GameRules.Key<GameRules.BooleanRule> createBooleanRule(String name, GameRules.Category category, boolean defaultValue, boolean doPermCheck) {
        GameRules.Key<GameRules.BooleanRule> key = GameRules.register(name, category, GameRules.BooleanRule.create(defaultValue));
        if (doPermCheck) {
            pendingPermChecks.add(key); // TODO forge perms
            MoreCommands.registerPermission("morecommands.gamerule." + key.getName(), true);
        }

        allRules.put(name, key);
        return key;
    }

    private GameRules.Key<GameRules.IntRule> createIntRule(String name, GameRules.Category category, int defaultValue) {
        GameRules.Key<GameRules.IntRule> key = GameRules.register(name, category, GameRules.IntRule.create(defaultValue));
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

    public boolean checkBooleanWithPerm(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> key, Entity entity) {
        // Returns inversed value if the player does not have the required permission.
        // Returns set value is entity is null, LuckPerms (or compatible) isn't installed or permission isn't set for entity.
        return entity == null ? gameRules.getBoolean(key) :
                MoreCommandsArch.checkPermission(entity.getCommandSource(), "morecommands.gamerule." + key.getName(), true) == gameRules.getBoolean(key);
    }

    public void checkPerms(MinecraftServer server) {
        CommandSource commandSource = server.getCommandSource();
        // Idk how LuckPerms knows what perms there are, but I presume it just stores every perm that's requested.
        // So we request all the gamerule perms here.
        if (Command.isPermissionsLoaded()) pendingPermChecks.forEach(key -> MoreCommandsArch.checkPermission(commandSource, "morecommands.gamerule." + key.getName()));
    }

    public static void init() {
        Holder.setMoreGameRules(INSTANCE);
    }

    public static MoreGameRules get() {
        return INSTANCE;
    }
}
