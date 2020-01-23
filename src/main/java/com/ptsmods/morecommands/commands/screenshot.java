package com.ptsmods.morecommands.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Thanks to Mineshot for quite some fragments of code I used in this.
 * https://github.com/ata4/mineshot
 */
public class screenshot {

	public screenshot() {}

	public static class Commandscreenshot extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		private final List<Map<String, Object>> tasks = new ArrayList();

		public Commandscreenshot() {
			// @formatter:off
//			for (Field f : Reference.getFields(ClippingHelperImpl.class))
//				if (f.getType() == ClippingHelperImpl.class) {
//					Reference.removeFinalModifier(f);
//					f.setAccessible(true);
//					try {
//						f.set(null, ToggleableClippingHelper.INSTANCE);
//					} catch (IllegalArgumentException | IllegalAccessException e) {
//						e.printStackTrace();
//					}
//					break;
//				}
			// @formatter:on
		}

		@Override
		public List getAliases() {
			return Lists.newArrayList("ss");
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "screenshot";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			int width;
			int height;
			int ogWidth = width = Minecraft.getMinecraft().displayWidth;
			int ogHeight = height = Minecraft.getMinecraft().displayHeight;
			if (args.length > 0) {
				if (Reference.isInteger(args[0]) || args[0].endsWith("x") && Reference.isInteger(args[0].substring(0, args[0].length() - 1))) width = args[0].endsWith("x") ? width * Integer.parseInt(args[0].substring(0, args[0].length() - 1)) : Integer.parseInt(args[0]);
				if (args.length > 1 && (Reference.isInteger(args[1]) || args[1].endsWith("x") && Reference.isInteger(args[1].substring(0, args[1].length() - 1)))) height = args[1].endsWith("x") ? height * Integer.parseInt(args[1].substring(0, args[1].length() - 1)) : Integer.parseInt(args[1]);
			}
			if (width * height * 3 < 0) Reference.sendMessage(sender, TextFormatting.RED + "The given dimensions are too big. The product of the width and height may at most be " + Integer.MAX_VALUE / 3 + " (product was " + (long) width * (long) height + ").");
			else {
				Map<String, Object> task = new HashMap();
				task.put("server", server);
				task.put("sender", sender);
				task.put("args", args);
				task.put("ogWidth", ogWidth);
				task.put("ogHeight", ogHeight);
				task.put("width", width);
				task.put("height", height);
				task.put("tries", 0);
				tasks.add(task);
				// if (!(Minecraft.getMinecraft().entityRenderer instanceof
				// TransformingEntityRenderer)) Minecraft.getMinecraft().entityRenderer =
				// TransformingEntityRenderer.fromEntityRenderer(Minecraft.getMinecraft().entityRenderer);
				Minecraft.getMinecraft().resize(width, height);
			}
		}

		@SubscribeEvent
		public void onRenderTick(RenderTickEvent event) {
			for (Map<String, Object> task : new ArrayList<>(tasks)) {
				long startTake = System.currentTimeMillis();
				task.put("tries", (int) task.get("tries") + 1);
				if ((int) task.get("tries") <= 2) continue;
				int width = Minecraft.getMinecraft().displayWidth;
				int height = Minecraft.getMinecraft().displayHeight;
				GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 3);
				if (OpenGlHelper.isFramebufferEnabled()) {
					Framebuffer fb = Minecraft.getMinecraft().getFramebuffer();
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.framebufferTexture);
					GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
				} else GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);
				Minecraft.getMinecraft().resize((int) task.get("ogWidth"), (int) task.get("ogHeight"));
				tasks.remove(task);
				long takeTime = System.currentTimeMillis() - startTake;
				Reference.execute(() -> {
					Reference.sendMessage((ICommandSender) task.get("sender"), "Saving the screenshot, this may take a while depending on its dimensions...");
					long start = System.currentTimeMillis();
					try {
						byte[] bytes = new byte[buf.remaining()];
						buf.get(bytes);
						BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						for (int x = 0; x < width; x++)
							for (int y = height - 1; y >= 0; y--) { // Image is flipped because this game starts drawing at the bottom left instead
																	// of the top left for some reason.
								int i = (x + width * y) * 3;
								img.setRGB(x, height - y - 1, Reference.shiftRGB(buf.get(i) & 0xFF, buf.get(i + 1) & 0xFF, buf.get(i + 2) & 0xFF, 0xFF));
							}
						String fileName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss_" + task.get("width") + "'x'" + task.get("height") + ".'png'").format(new Date());
						File out = Paths.get(Minecraft.getMinecraft().gameDir.getAbsolutePath(), "screenshots", fileName).toFile();
						ImageIO.write(img, "png", out);
						ITextComponent text = new TextComponentString("Saved screenshot as ");
						text.setStyle(new Style().setColor(TextFormatting.GOLD));
						ITextComponent text0 = new TextComponentString(fileName);
						text0.setStyle(new Style().setColor(TextFormatting.GOLD).setUnderlined(true).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, out.getCanonicalPath())));
						text.appendSibling(text0);
						long saveTime = System.currentTimeMillis() - start;
						ITextComponent text1 = new TextComponentString(", took " + (saveTime / 1000 + takeTime / 1000) + " seconds (" + takeTime / 1000 + " seconds to take the screenshot and " + saveTime / 1000 + " seconds to save it).");
						text1.setStyle(new Style().setColor(TextFormatting.GOLD));
						text.appendSibling(text1);
						((ICommandSender) task.get("sender")).sendMessage(text);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) { // I know you shouldn't catch these, but there is no way to know beforehand if
													// the screenshot is too big or not.
						Reference.sendMessage((ICommandSender) task.get("sender"), TextFormatting.RED + "The screenshot was too big and could thus not be saved due to a lack of memory.");
					}
				});
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private String usage = "/screenshot [width] [height] Creates a screenshot. Width and height default to the game's window's width and height. Width and height both can end with an x which serves as a multiplier for the default value, e.g. if the window's dimensions are 1920 wide and 1080 high and the values passed are 2x and 2x, then the resulting width will be 1920*2 = 3840 and the resulting height will be 1080*2 = 2160.";

	}

	// The next two classes are for tiled rendering of huge screenshots, not used at
	// the moment as I cannot get tiled screenshotting to work.
	// Maybe someday...

	public static class TransformingEntityRenderer extends EntityRenderer {

		public int						offsetX, offsetY;
		private final Minecraft			mc0;
		private final EntityRenderer	parent;

		private TransformingEntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn, EntityRenderer parent) {
			super(mcIn, resourceManagerIn);
			mc0 = mcIn;
			this.parent = parent;
		}

		public static TransformingEntityRenderer fromEntityRenderer(EntityRenderer renderer) {
			// Apparently there's a class called ReflectionHelper, it would've been nice to
			// have known this sooner. -_-
			return new TransformingEntityRenderer(ReflectionHelper.getPrivateValue((Class<? super EntityRenderer>) renderer.getClass(), renderer, new String[] {"mc", "field_146297_k"}), ReflectionHelper.getPrivateValue((Class<? super EntityRenderer>) renderer.getClass(), renderer, new String[] {"resourceManager", "field_110451_am", "field_110582_d", "field_147695_g", "field_147711_ac", "field_148033_b", "field_177598_f", ""}), renderer);
		}

		public EntityRenderer getParent() {
			return parent;
		}

		@Override
		public void setupOverlayRendering() {
			ScaledResolution scaledresolution = new ScaledResolution(mc0);
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GL11.glTranslated(offsetX, -offsetY, 0); // Only this line was added, used for tiled screenshotting.
			GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		}

	}

	public static class ToggleableClippingHelper extends ClippingHelperImpl {

		private static final ToggleableClippingHelper	INSTANCE	= new ToggleableClippingHelper();
		private boolean									enabled;

		public static ToggleableClippingHelper getInstance() {
			INSTANCE.init();
			return INSTANCE;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public boolean isBoxInFrustum(double x1, double y1, double z1, double x2, double y2, double z2) {
			if (enabled) return super.isBoxInFrustum(x1, y1, z1, x2, y2, z2);
			else return true;
		}
	}

}