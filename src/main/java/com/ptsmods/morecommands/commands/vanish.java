package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.ModEntityTracker;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class vanish {

	public vanish() {}

	public static class Commandvanish extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public Commandvanish() {
			try {
				loadFile();
			} catch (IOException e) {
				e.printStackTrace();
				vanished.clear();
			}
		}

		private void loadFile() throws IOException {
			vanished.clear();
			File f = new File("config/MoreCommands/vanished.json");
			if (!f.exists()) f.createNewFile();
			vanished.addAll(MoreObjects.firstNonNull(new Gson().fromJson(Reference.joinCustomChar("\n", Files.readAllLines(f.toPath()).toArray(new String[0])), List.class), new ArrayList()));
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("v");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "vanish";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		public static final List<String> vanished = new ArrayList();

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			if (modEntityTracker(player.getServerWorld())) {
				vanish(player);
				Reference.sendMessage(player, "You're now " + (vanished.contains(player.getUniqueID().toString()) ? "invisible" : "visible") + ".");
			} else Reference.sendMessage(sender, TextFormatting.RED + "The world's entity tracker could not be hacked, you are still visible. There may be an error in the console.");
		}

		@SubscribeEvent
		public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
			if (vanished.contains(event.player.getUniqueID().toString())) {
				vanish((EntityPlayerMP) event.player, true);
				Reference.sendMessage(event.player, "You are in vanish.");
			}
		}

		public static void vanish(EntityPlayerMP player) {
			vanish(player, !vanished.contains(player.getUniqueID().toString()));
		}

		public static void vanish(EntityPlayerMP player, boolean vanish) {
			if (modEntityTracker(player.getServerWorld())) if (vanish) {
				if (!vanished.contains(player.getUniqueID().toString())) vanished.add(player.getUniqueID().toString());
				player.getServerWorld().getEntityTracker().untrack(player);
			} else {
				vanished.remove(player.getUniqueID().toString());
				player.getServerWorld().getEntityTracker().track(player);
			}
		}

		// Also ran in the serverLoad event method so that it doesn't break when you
		// enter a different world where the command has not yet been ran.
		public static boolean modEntityTracker(WorldServer world) {
			try {
				Field f = WorldServer.class.getDeclaredField("entityTracker");
				f.setAccessible(true);
				Field f0 = Field.class.getDeclaredField("modifiers");
				f0.setAccessible(true);
				f0.set(f, f.getModifiers() & ~Modifier.FINAL);
				if (!(f.get(world) instanceof ModEntityTracker)) f.set(world, new ModEntityTracker(world));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return world.getEntityTracker() instanceof ModEntityTracker;
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "vanish", "Make other players unable to see you.", true);
		}

		protected String usage = "/vanish Make other players unable to see you.";

	}

}