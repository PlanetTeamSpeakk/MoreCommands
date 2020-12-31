package com.ptsmods.morecommands.mixin.client;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {

    @Shadow @Final private static Ordering<PlayerListEntry> ENTRY_ORDERING;
    @Shadow @Final private Text footer;
    @Shadow @Final private Text header;
    private int mc_i = 0;

    @Inject(at = @At("HEAD"), method = "renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V", cancellable = true)
    protected void renderLatencyIcon(MatrixStack matrixStack, int i, int j, int k, PlayerListEntry playerListEntry, CallbackInfo cbi) {
        if (ClientOptions.Rendering.showExactLatency) {
            cbi.cancel();
            PlayerListHud thiz = ReflectionHelper.cast(this);
            thiz.setZOffset(thiz.getZOffset() + 100);
            int latency = playerListEntry.getLatency();
            float p = latency < 0 ? 100f : Math.min(100f / 900f * Math.max(latency-100, 0), 100f);
            if (p > 0) p = p / 100f;
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, "" + latency, j + i - 11, k, new Color((int) (p*255), (int) ((1f-p)*255), 0).getRGB());
            thiz.setZOffset(thiz.getZOffset() - 100);
        }
    }

    @Overwrite
    public void render(MatrixStack matrixStack, int i, Scoreboard scoreboard, ScoreboardObjective scoreboardObjective) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerListHud thiz = ReflectionHelper.cast(this);
        ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
        List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        int j = 0;
        int k = 0;
        int o;
        for (PlayerListEntry playerListEntry : list) {
            o = client.textRenderer.getWidth(thiz.getPlayerName(playerListEntry));
            j = Math.max(j, o);
            if (scoreboardObjective != null && scoreboardObjective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
                o = client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), scoreboardObjective).getScore());
                k = Math.max(k, o);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int m = list.size();
        int n = m;

        for(o = 1; n > 20; n = (m + o - 1) / o) {
            ++o;
        }

        boolean bl = client.isInSingleplayer() || client.getNetworkHandler().getConnection().isEncrypted();
        int r;
        if (scoreboardObjective != null) {
            if (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
                r = 90;
            } else {
                r = k;
            }
        } else {
            r = 0;
        }

        int s = Math.min(o * ((bl ? 9 : 0) + j + r + 13), i - 50) / o + (ClientOptions.Rendering.showExactLatency ? 15 : 0);
        int t = i / 2 - (s * o + (o - 1) * 5) / 2;
        int u = 10;
        int v = s * o + (o - 1) * 5;
        List<StringRenderable> list2 = null;
        if (header != null) {
            list2 = client.textRenderer.wrapLines(header, i - 50);

            StringRenderable stringRenderable;
            for(Iterator<StringRenderable> var19 = list2.iterator(); var19.hasNext(); v = Math.max(v, client.textRenderer.getWidth(stringRenderable))) {
                stringRenderable = var19.next();
            }
        }

        List<StringRenderable> list3 = null;
        StringRenderable stringRenderable3;
        Iterator<StringRenderable> var37;
        if (footer != null) {
            list3 = client.textRenderer.wrapLines(footer, i - 50);

            for(var37 = list3.iterator(); var37.hasNext(); v = Math.max(v, client.textRenderer.getWidth(stringRenderable3))) {
                stringRenderable3 = var37.next();
            }
        }

        int var10001;
        int var10002;
        int var10003;
        int var10005;
        int z;
        if (list2 != null) {
            var10001 = i / 2 - v / 2 - 1;
            var10002 = u - 1;
            var10003 = i / 2 + v / 2 + 1;
            var10005 = list2.size();
            client.textRenderer.getClass();
            fill(matrixStack, var10001, var10002, var10003, u + var10005 * 9, -2147483648);

            for(var37 = list2.iterator(); var37.hasNext(); u += 9) {
                stringRenderable3 = var37.next();
                z = client.textRenderer.getWidth(stringRenderable3);
                client.textRenderer.drawWithShadow(matrixStack, stringRenderable3, (float)(i / 2 - z / 2), (float)u, -1);
                client.textRenderer.getClass();
            }

            ++u;
        }

        fill(matrixStack, i / 2 - v / 2 - 1, u - 1, i / 2 + v / 2 + (ClientOptions.Rendering.showExactLatency ? 2 : 1), u + n * 9, -2147483648);
        int x = client.options.getTextBackgroundColor(553648127);

        int aj;
        for(int y = 0; y < m; ++y) {
            z = y / n;
            aj = y % n;
            int ab = t + z * s + z * 5;
            int ac = u + aj * 9;
            fill(matrixStack, ab, ac, ab + s, ac + 8, x);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (y < list.size()) {
                PlayerListEntry playerListEntry2 = list.get(y);
                GameProfile gameProfile = playerListEntry2.getProfile();
                if (bl) {
                    PlayerEntity playerEntity = client.world.getPlayerByUuid(gameProfile.getId());
                    boolean bl2 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                    client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                    int ad = 8 + (bl2 ? 8 : 0);
                    int ae = 8 * (bl2 ? -1 : 1);
                    DrawableHelper.drawTexture(matrixStack, ab, ac, 8, 8, 8.0F, (float)ad, 8, ae, 64, 64);
                    if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                        int af = 8 + (bl2 ? 8 : 0);
                        int ag = 8 * (bl2 ? -1 : 1);
                        DrawableHelper.drawTexture(matrixStack, ab, ac, 8, 8, 40.0F, (float)af, 8, ag, 64, 64);
                    }
                    ab += 9;
                }

                client.textRenderer.drawWithShadow(matrixStack, thiz.getPlayerName(playerListEntry2), (float)ab, (float)ac, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
                if (scoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
                    int ah = ab + j + 1;
                    int ai = ah + r;
                    if (ai - ah > 5) {
                        renderScoreboardObjective(scoreboardObjective, ac, gameProfile.getName(), ah, ai, playerListEntry2, matrixStack);
                    }
                }

                renderLatencyIcon(matrixStack, s - (ClientOptions.Rendering.showExactLatency ? 13 : 0), ab - (bl ? 9 : 0), ac, playerListEntry2);
            }
        }

        if (list3 != null) {
            u += n * 9 + 1;
            var10001 = i / 2 - v / 2 - 1;
            var10002 = u - 1;
            var10003 = i / 2 + v / 2 + 1;
            var10005 = list3.size();
            client.textRenderer.getClass();
            fill(matrixStack, var10001, var10002, var10003, u + var10005 * 9, -2147483648);

            for(Iterator<StringRenderable> var40 = list3.iterator(); var40.hasNext(); u += 9) {
                StringRenderable stringRenderable4 = var40.next();
                aj = client.textRenderer.getWidth(stringRenderable4);
                client.textRenderer.drawWithShadow(matrixStack, stringRenderable4, (float)(i / 2 - aj / 2), (float)u, -1);
                client.textRenderer.getClass();
            }
        }
    }

    @Shadow
    abstract void renderScoreboardObjective(ScoreboardObjective scoreboardObjective, int i, String string, int j, int k, PlayerListEntry playerListEntry, MatrixStack matrixStack);

    @Shadow
    public abstract void renderLatencyIcon(MatrixStack matrixStack, int i, int j, int k, PlayerListEntry playerListEntry);

}
