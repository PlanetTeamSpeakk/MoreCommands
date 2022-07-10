package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LockLookCommand extends ClientCommand {
    private Entity target = null;
    private boolean smooth = false;

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        RenderTickEvent.PRE.register(tick -> {
            if (target != null) {
                if (Compat.get().isRemoved(target)) target = null;
                else {
                    Vec3 ctr = getPlayer().position();
                    Vec3 pos = target.position();
                    double dx = pos.x() - ctr.x();
                    double dz = pos.z() - ctr.z();
                    getPlayer().turn(
                            (clampAngle(((MixinEntityAccessor) getPlayer()).getYRot_(), (float) Math.toDegrees(-Math.atan2(pos.x() - ctr.x(), pos.z() - ctr.z())), false) - ((MixinEntityAccessor) getPlayer()).getYRot_()) / 0.15,
                            (clampAngle(((MixinEntityAccessor) getPlayer()).getXRot_(), (float) -Math.toDegrees(Math.atan2(target.getEyeY() - getPlayer().getEyeY(), Math.sqrt(dx * dx + dz * dz))), true) - ((MixinEntityAccessor) getPlayer()).getXRot_()) / 0.15);
                }
            }
        });

        dispatcher.register(cLiteral("locklook")
                .executes(ctx -> {
                    HitResult hit = MoreCommands.getRayTraceTarget(getPlayer(), 160, false, true);
                    if (hit instanceof EntityHitResult && target != ((EntityHitResult) hit).getEntity()) {
                        target = ((EntityHitResult) hit).getEntity();
                        sendMsg("Your eyes have now been locked onto " + IMoreCommands.get().textToString(target.getDisplayName(), SS, true) + DF + ".");
                        return 1;
                    } else if (target == null) sendMsg(ChatFormatting.RED + "You're not looking at an entity.");
                    else {
                        target = null;
                        sendMsg("Your eyes are now unlocked.");
                    }
                    return 0;
                })
                .then(cLiteral("smooth")
                        .executes(ctx -> {
                            sendMsg("Looking will " + Util.formatFromBool(smooth = !smooth, ChatFormatting.GREEN + "now", ChatFormatting.RED + "no longer") + DF + " be done smoothly.");
                            return smooth ? 2 : 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/lock-look";
    }

    private float clampAngle(float from, float to, boolean isPitch) {
        return smooth ? from + Mth.clamp(Mth.degreesDifference(from, to), isPitch ? -40 : -10, isPitch ? 40 : 10) : to;
    }
}
