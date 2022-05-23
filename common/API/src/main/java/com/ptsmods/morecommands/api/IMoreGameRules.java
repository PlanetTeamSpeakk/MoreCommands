package com.ptsmods.morecommands.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.GameRules;

public interface IMoreGameRules {

    static IMoreGameRules get() {
        return Holder.getMoreGameRules();
    }

    // SFrule and DFrule are not API because EnumRule isn't either.
    GameRules.Key<GameRules.BooleanRule> doMeltRule();
    GameRules.Key<GameRules.IntRule> maxHomesRule();
    GameRules.Key<GameRules.BooleanRule> doSilkSpawnersRule();
    GameRules.Key<GameRules.BooleanRule> randomOrderPlayerTickRule();
    GameRules.Key<GameRules.IntRule> hopperTransferCooldownRule();
    GameRules.Key<GameRules.IntRule> hopperTransferRateRule();
    GameRules.Key<GameRules.BooleanRule> doFarmlandTrampleRule();
    GameRules.Key<GameRules.BooleanRule> doJoinMessageRule();
    GameRules.Key<GameRules.BooleanRule> doExplosionsRule();
    GameRules.Key<GameRules.IntRule> wildLimitRule();
    GameRules.Key<GameRules.IntRule> tpaTimeoutRule();
    GameRules.Key<GameRules.BooleanRule> fluidsInfiniteRule();
    GameRules.Key<GameRules.BooleanRule> doLiquidFlowRule();
    GameRules.Key<GameRules.IntRule> vaultRowsRule();
    GameRules.Key<GameRules.IntRule> vaultsRule();
    GameRules.Key<GameRules.IntRule> nicknameLimitRule();
    GameRules.Key<GameRules.BooleanRule> doSignColoursRule();
    GameRules.Key<GameRules.BooleanRule> doBookColoursRule();
    GameRules.Key<GameRules.BooleanRule> doChatColoursRule();
    GameRules.Key<GameRules.BooleanRule> doItemColoursRule();
    GameRules.Key<GameRules.BooleanRule> doEnchantLevelLimitRule();
    GameRules.Key<GameRules.BooleanRule> doPriorWorkPenaltyRule();
    GameRules.Key<GameRules.BooleanRule> doItemsFireDamageRule();
    GameRules.Key<GameRules.BooleanRule> doPathFindingRule();
    GameRules.Key<GameRules.BooleanRule> doGoalsRule();
    GameRules.Key<GameRules.BooleanRule> doStacktraceRule();
    GameRules.Key<GameRules.BooleanRule> doChairsRule();
    GameRules.Key<GameRules.BooleanRule> sendCommandFeedbackToOpsRule();

    boolean checkBooleanWithPerm(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> key, Entity entity);
}
