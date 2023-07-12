package com.ptsmods.morecommands.client.commands;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.client.mixin.accessor.MixinWindowAccessor;
import dev.architectury.event.events.client.ClientTickEvent;
import lombok.Data;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotCommand extends ClientCommand {
    private ScreenshotTask queue = null;
    private ScreenshotTask task = null;

    public void preinit() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (queue != null) {
                task = queue;
                queue = null;
            }
        });

        RenderTickEvent.POST.register(tick -> {
            if (task != null) {
                if (task.tries++ > 2) {
                    task = null;
                    return;
                }

                long startTake = System.currentTimeMillis();
                GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                ByteBuffer buf = ByteBuffer.allocateDirect(task.getWidth() * task.getHeight() * 3);
                RenderTarget fb = Minecraft.getInstance().getMainRenderTarget();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.getColorTextureId());
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
                ReflectionHelper.<MixinWindowAccessor>cast(Minecraft.getInstance().getWindow()).callOnFramebufferResize(Minecraft.getInstance().getWindow().getWindow(),
                        task.getOriginalWidth(), task.getOriginalHeight());

                ScreenshotTask task = this.task;
                this.task = null;
                long takeTime = System.currentTimeMillis() - startTake;

                MoreCommands.execute(() -> {
                    sendMsg("Saving the screenshot, this may take a while depending on its dimensions...");
                    long start = System.currentTimeMillis();
                    try {
                        BufferedImage img = new BufferedImage(task.getWidth(), task.getHeight(), BufferedImage.TYPE_INT_RGB);
                        for (int x = 0; x < task.getWidth(); x++)
                            for (int y = task.getHeight() - 1; y >= 0; y--) { // Image is flipped because OpenGL starts drawing at the bottom left instead of the top left for some reason.
                                int i = (x + task.getWidth() * y) * 3;
                                img.setRGB(x, task.getHeight() - y - 1, shiftRGB(buf.get(i) & 0xFF, buf.get(i + 1) & 0xFF, buf.get(i + 2) & 0xFF));
                            }

                        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss_" + task.getWidth() + "'x'" + task.getHeight() + ".'png'").format(new Date());
                        File out = Paths.get(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "screenshots", fileName).toFile();
                        if (!out.getParentFile().exists()) out.getParentFile().mkdirs();

                        ImageIO.write(img, "png", out);
                        long saveTime = System.currentTimeMillis() - start;
                        sendMsg(literalText("Saved screenshot as ", DS)
                                .append(literalText(fileName)
                                        .withStyle(SS
                                                .applyFormat(ChatFormatting.UNDERLINE)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, out.getCanonicalPath()))))
                                .append(literalText(", took " + (saveTime + takeTime) / 1000 + " seconds (" + takeTime / 1000 +
                                        " seconds to take the screenshot and " + saveTime / 1000 + " seconds to save it).", DS)));
                    } catch (IOException e) {
                        log.catching(e);
                        sendMsg(ChatFormatting.RED + "An unknown error occurred while saving the image: " + SF + e.getMessage() + DF + ".");
                    } catch (OutOfMemoryError e) { // I know you shouldn't catch these, but there is no way to know beforehand if the screenshot is too big or not.
                        sendMsg(ChatFormatting.RED + "The screenshot was too big and could thus not be saved due to a lack of memory.");
                    }
                });
            }
        });
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        // Calculation of max values was done as follows:
        // x * y = 2,147,483,647 / 3 = 715,827,882 (max value of array)
        // x / y = 16 / 9 (aspect ratio)
        // x = 16 / 9 * y
        // 16 / 9 * y * y = 715,827,882
        // y = sqrt(715,827,882 / 16 / 9) = 20066.22
        // x = 715,827,882 / y = 35673.28
        // Went with these values, however, as OpenGL does not support dimensions larger than 32768 as of right now.
        dispatcher.register(cLiteral("screenshot")
                .executes(ctx -> execute(ctx, -1, -1))
                .then(cArgument("width", IntegerArgumentType.integer(1, 32768))
                        .then(cArgument("height", IntegerArgumentType.integer(1, 18432))
                                .executes(ctx -> execute(ctx, ctx.getArgument("width", Integer.class), ctx.getArgument("height", Integer.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/screenshot";
    }

    private int execute(CommandContext<ClientSuggestionProvider> ctx, int width, int height) {
        width = width == -1 ? Minecraft.getInstance().getWindow().getScreenWidth() : width;
        height = height == -1 ? Minecraft.getInstance().getWindow().getScreenHeight() : height;
        int ogWidth = Minecraft.getInstance().getWindow().getScreenWidth();
        int ogHeight = Minecraft.getInstance().getWindow().getScreenHeight();
        if (width * height * 3 < 0) sendMsg(ChatFormatting.RED + "The given dimensions are too big. " +
                "The product of the width and height may at most be " + Integer.MAX_VALUE / 3 +
                " (product was " + (long) width * (long) height + ").");
        else {
            queue = new ScreenshotTask(ctx, ogWidth, ogHeight, width, height);
            ReflectionHelper.<MixinWindowAccessor>cast(Minecraft.getInstance().getWindow()).callOnFramebufferResize(Minecraft.getInstance().getWindow().getWindow(), width, height);
        }
        return 1;
    }

    private int shiftRGB(int r, int g, int b) {
        return (r & 0xFF) << 16 | // Red
                (g & 0xFF) << 8 | // Green
                (b & 0xFF); // Blue
    }

    @Data
    private static class ScreenshotTask {
        public final CommandContext<ClientSuggestionProvider> ctx;
        public final int originalWidth, originalHeight;
        public final int width, height;
        public int tries = 0;
    }
}
