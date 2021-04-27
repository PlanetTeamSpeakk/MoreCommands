package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.callbacks.RenderTickCallback;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import com.ptsmods.morecommands.mixin.client.accessor.MixinWindowAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScreenshotCommand extends ClientCommand {

	private Map<String, Object> queue = null;
	private Map<String, Object> task = null;

	public void preinit() {
		registerCallback(ClientTickEvents.END_CLIENT_TICK, client -> {
			if (queue != null) {
				task = queue;
				queue = null;
			}
		});
		registerCallback(RenderTickCallback.POST, tick -> {
			if (task != null) {
				long startTake = System.currentTimeMillis();
				task.put("tries", (int) task.get("tries") + 1);
				if ((int) task.get("tries") > 2) {
					task = null;
					return;
				}
				int width = (int) task.get("width");
				int height = (int) task.get("height");
				GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 3);
				Framebuffer fb = MinecraftClient.getInstance().getFramebuffer();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.getColorAttachment());
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
				ReflectionHelper.<MixinWindowAccessor>cast(MinecraftClient.getInstance().getWindow()).callOnFramebufferSizeChanged(MinecraftClient.getInstance().getWindow().getHandle(), (int) task.get("ogWidth"), (int) task.get("ogHeight"));
				Map<String, Object> task = this.task;
				this.task = null;
				long takeTime = System.currentTimeMillis() - startTake;
				MoreCommands.execute(() -> {
					sendMsg("Saving the screenshot, this may take a while depending on its dimensions...");
					long start = System.currentTimeMillis();
					try {
						BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						for (int x = 0; x < width; x++)
							for (int y = height - 1; y >= 0; y--) { // Image is flipped because this game starts drawing at the bottom left instead of the top left for some reason.
								int i = (x + width * y) * 3;
								img.setRGB(x, height - y - 1, shiftRGB(buf.get(i) & 0xFF, buf.get(i + 1) & 0xFF, buf.get(i + 2) & 0xFF, 0xFF));
							}
						String fileName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss_" + task.get("width") + "'x'" + task.get("height") + ".'png'").format(new Date());
						File out = Paths.get(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "screenshots", fileName).toFile();
						ImageIO.write(img, "png", out);
						long saveTime = System.currentTimeMillis() - start;
						sendMsg(new LiteralText("Saved screenshot as ").setStyle(DS).append(new LiteralText(fileName).setStyle(SS.withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, out.getCanonicalPath())))).append(new LiteralText(", took " + (saveTime / 1000 + takeTime / 1000) + " seconds (" + takeTime / 1000 + " seconds to take the screenshot and " + saveTime / 1000 + " seconds to save it).").setStyle(DS)));
					} catch (IOException e) {
						log.catching(e);
						sendMsg(Formatting.RED + "An unknown error occurred while saving the image: " + SF + e.getMessage() + DF + ".");
					} catch (OutOfMemoryError e) { // I know you shouldn't catch these, but there is no way to know beforehand if the screenshot is too big or not.
						sendMsg(Formatting.RED + "The screenshot was too big and could thus not be saved due to a lack of memory.");
					}
				});
			}
		});
	}

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		dispatcher.register(cLiteral("screenshot").executes(ctx -> execute(ctx, -1, -1)).then(cArgument("width", IntegerArgumentType.integer(1, 122880)).then(cArgument("height", IntegerArgumentType.integer(1, 69120)).executes(ctx -> execute(ctx, ctx.getArgument("width", Integer.class), ctx.getArgument("height", Integer.class))))));
	}

	private int execute(CommandContext<ClientCommandSource> ctx, int width, int height) {
		width = width == -1 ? MinecraftClient.getInstance().getWindow().getWidth() : width;
		height = height == -1 ? MinecraftClient.getInstance().getWindow().getHeight() : height;
		int ogWidth = MinecraftClient.getInstance().getWindow().getWidth();
		int ogHeight = MinecraftClient.getInstance().getWindow().getHeight();
		if (width * height * 3 < 0) sendMsg(Formatting.RED + "The given dimensions are too big. The product of the width and height may at most be " + Integer.MAX_VALUE / 3 + " (product was " + (long) width * (long) height + ").");
		else {
			Map<String, Object> task = new HashMap<>();
			task.put("context", ctx);
			task.put("ogWidth", ogWidth);
			task.put("ogHeight", ogHeight);
			task.put("width", width);
			task.put("height", height);
			task.put("tries", 0);
			queue = task;
			ReflectionHelper.<MixinWindowAccessor>cast(MinecraftClient.getInstance().getWindow()).callOnFramebufferSizeChanged(MinecraftClient.getInstance().getWindow().getHandle(), width, height);
		}
		return 1;
	}

	private int shiftRGB(int r, int g, int b, int a) {
		return (a & 0xFF) << 24 | // Alpha
				(r & 0xFF) << 16 | // Red
				(g & 0xFF) << 8 | // Green
				(b & 0xFF); // Blue
	}
}
