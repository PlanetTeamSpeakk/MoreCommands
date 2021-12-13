package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MoreGameRules {
    public static final CustomGameRuleCategory grc = new CustomGameRuleCategory(new Identifier("morecommands:main"),
            new LiteralText("MoreCommands").setStyle(Style.EMPTY.withFormatting(ClientOptions.Tweaks.defColour.getValue().asFormatting()).withBold(true)));

    private static final List<GameRules.Key<?>> pendingPermChecks = new ArrayList<>();
    public static final GameRules.Key<EnumRule<FormattingColour>> DFrule = createEnumRule("defaultFormatting", FormattingColour.GOLD, (server, value) -> MoreCommands.updateFormatting(server, 0, value.get()));
    public static final GameRules.Key<EnumRule<FormattingColour>> SFrule = createEnumRule("secondaryFormatting", FormattingColour.YELLOW, (server, value) -> MoreCommands.updateFormatting(server, 1, value.get()));
    public static final GameRules.Key<GameRules.BooleanRule> doMeltRule = createBooleanRule("doMelt", true, false);
    public static final GameRules.Key<GameRules.IntRule> maxHomesRule = createIntRule("maxHomes", 3, -1);
    public static final GameRules.Key<GameRules.BooleanRule> doSilkSpawnersRule = createBooleanRule("doSilkSpawners", false, true);
    public static final GameRules.Key<GameRules.BooleanRule> randomOrderPlayerTickRule = createBooleanRule("randomOrderPlayerTick", true, false);
    public static final GameRules.Key<GameRules.IntRule> hopperTransferCooldownRule = createIntRule("hopperTransferCooldown", 8, 0);
    public static final GameRules.Key<GameRules.IntRule> hopperTransferRateRule = createIntRule("hopperTransferRate", 1, 1, 64);
    public static final GameRules.Key<GameRules.BooleanRule> doFarmlandTrampleRule = createBooleanRule("doFarmlandTrample", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doJoinMessageRule = createBooleanRule("doJoinMessage", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doExplosionsRule = createBooleanRule("doExplosions", true, false);
    public static final GameRules.Key<GameRules.IntRule> wildLimitRule = createIntRule("wildLimit", 5000, 0, (int) WorldBorder.DEFAULT_BORDER.getSize()/2);
    public static final GameRules.Key<GameRules.IntRule> tpaTimeoutRule = createIntRule("tpaTimeout", 2400, 600);
    public static final GameRules.Key<GameRules.BooleanRule> fluidsInfiniteRule = createBooleanRule("fluidsInfinite", false, false);
    public static final GameRules.Key<GameRules.BooleanRule> doLiquidFlowRule = createBooleanRule("doLiquidFlow", true, false);
    public static final GameRules.Key<GameRules.IntRule> vaultRowsRule = createIntRule("vaultRows", 6, 1, 6);
    public static final GameRules.Key<GameRules.IntRule> vaultsRule = createIntRule("vaults",3, 0);
    public static final GameRules.Key<GameRules.IntRule> nicknameLimitRule = createIntRule("nicknameLimit", 16, 0);
    public static final GameRules.Key<GameRules.BooleanRule> doSignColoursRule = createBooleanRule("doSignColours", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doBookColoursRule = createBooleanRule("doBookColours", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doChatColoursRule = createBooleanRule("doChatColours", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doItemColoursRule = createBooleanRule("doItemColours", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doEnchantLevelLimitRule = createBooleanRule("doEnchantLevelLimit", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doPriorWorkPenaltyRule = createBooleanRule("doPriorWorkPenalty", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doItemsFireDamageRule = createBooleanRule("doItemsFireDamage", true, true);
    public static final GameRules.Key<GameRules.BooleanRule> doPathFindingRule = createBooleanRule("doPathFinding", true, false);
    public static final GameRules.Key<GameRules.BooleanRule> doGoalsRule = createBooleanRule("doGoals", true, false);
    public static final GameRules.Key<GameRules.BooleanRule> doStacktraceRule = createBooleanRule("doStacktrace", true, false);
    public static final GameRules.Key<GameRules.BooleanRule> sendCommandFeedbackToOpsRule = createBooleanRule("sendCommandFeedbackToOps", true, true);

    private static GameRules.Key<GameRules.BooleanRule> createBooleanRule(String name, boolean defaultValue, boolean doPermCheck) {
        GameRules.Key<GameRules.BooleanRule> key = GameRuleRegistry.register(name, grc, GameRuleFactory.createBooleanRule(defaultValue));
        if (doPermCheck && Command.isPermissionsLoaded()) pendingPermChecks.add(key);
        return key;
    }

    private static GameRules.Key<GameRules.IntRule> createIntRule(String name, int defaultValue, int min) {
        return GameRuleRegistry.register(name, grc, GameRuleFactory.createIntRule(defaultValue, min));
    }

    private static GameRules.Key<GameRules.IntRule> createIntRule(String name, int defaultValue, int min, int max) {
        return GameRuleRegistry.register(name, grc, GameRuleFactory.createIntRule(defaultValue, min, max));
    }

    private static <E extends Enum<E>> GameRules.Key<EnumRule<E>> createEnumRule(String name, E defaultValue, BiConsumer<MinecraftServer, EnumRule<E>> changeListener) {
        return GameRuleRegistry.register(name, grc, GameRuleFactory.createEnumRule(defaultValue, changeListener));
    }

    public static boolean checkBooleanWithPerm(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> key, Entity entity) {
        // Returns inversed value if the player does not have the required permission.
        return entity == null ? gameRules.getBoolean(key) : !Command.isPermissionsLoaded() ? gameRules.getBoolean(key) :
                Permissions.check(entity, "morecommands.gamerule." + key.getName()) == gameRules.getBoolean(key);
    }

    public static void checkPerms(MinecraftServer server) {
        CommandSource commandSource = server.getCommandSource();
        // Idk how LuckPerms knows what perms there are, but I presume it just stores every perm that's requested.
        // So we request all the gamerule perms here.
        if (Command.isPermissionsLoaded()) pendingPermChecks.forEach(key -> Permissions.check(commandSource, "morecommands.gamerule." + key.getName()));
    }

    // Empty method, but calling this initialises the class.
    public static void init() {}
}
