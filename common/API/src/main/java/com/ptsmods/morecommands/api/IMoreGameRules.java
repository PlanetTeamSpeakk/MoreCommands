package com.ptsmods.morecommands.api;

import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;

public interface IMoreGameRules {

    static IMoreGameRules get() {
        return Holder.getMoreGameRules();
    }

    Map<String, GameRules.Key<?>> allRules();

    // SFrule and DFrule are not API because EnumRule isn't either.
    GameRules.Key<GameRules.BooleanValue> doMeltRule();
    GameRules.Key<GameRules.IntegerValue> maxHomesRule();
    GameRules.Key<GameRules.BooleanValue> doSilkSpawnersRule();
    GameRules.Key<GameRules.BooleanValue> randomOrderPlayerTickRule();
    GameRules.Key<GameRules.IntegerValue> hopperTransferCooldownRule();
    GameRules.Key<GameRules.IntegerValue> hopperTransferRateRule();
    GameRules.Key<GameRules.BooleanValue> doFarmlandTrampleRule();
    GameRules.Key<GameRules.BooleanValue> doJoinMessageRule();
    GameRules.Key<GameRules.BooleanValue> doExplosionsRule();
    GameRules.Key<GameRules.IntegerValue> wildLimitRule();
    GameRules.Key<GameRules.IntegerValue> tpaTimeoutRule();
    GameRules.Key<GameRules.BooleanValue> fluidsInfiniteRule();
    GameRules.Key<GameRules.BooleanValue> doLiquidFlowRule();
    GameRules.Key<GameRules.IntegerValue> vaultRowsRule();
    GameRules.Key<GameRules.IntegerValue> vaultsRule();
    GameRules.Key<GameRules.IntegerValue> nicknameLimitRule();
    GameRules.Key<GameRules.BooleanValue> doSignColoursRule();
    GameRules.Key<GameRules.BooleanValue> doBookColoursRule();
    GameRules.Key<GameRules.BooleanValue> doChatColoursRule();
    GameRules.Key<GameRules.BooleanValue> doItemColoursRule();
    GameRules.Key<GameRules.BooleanValue> doEnchantLevelLimitRule();
    GameRules.Key<GameRules.BooleanValue> doPriorWorkPenaltyRule();
    GameRules.Key<GameRules.BooleanValue> doItemsFireDamageRule();
    GameRules.Key<GameRules.BooleanValue> doPathFindingRule();
    GameRules.Key<GameRules.BooleanValue> doGoalsRule();
    GameRules.Key<GameRules.BooleanValue> doStacktraceRule();
    GameRules.Key<GameRules.BooleanValue> doChairsRule();
    GameRules.Key<GameRules.BooleanValue> sendCommandFeedbackToOpsRule();

    boolean checkBooleanWithPerm(GameRules gameRules, GameRules.Key<GameRules.BooleanValue> key, Entity entity);
}
