package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.FPProvider;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;

public class fakePlayer {

	public fakePlayer() {}

	public static class CommandfakePlayer extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 2 && args[0].equals("kick")) {
				List list = new ArrayList();
				for (EntityPlayerMP player : server.getPlayerList().getPlayers())
					if (player.getCapability(FPProvider.fpCap, null).isFake) list.add(player.getName());
				return getListOfStringsMatchingLastWord(args, list);
			} else if (args.length == 1) return getListOfStringsMatchingLastWord(args, Lists.newArrayList("create", "kick", "delete"));
			else return Lists.newArrayList();
		}

		@Override
		public String getName() {
			return "fakeplayer";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			// This entire command is like a wild rollercoaster.
			if (args.length == 0 || (!args[0].equals("create") && !args[0].equals("kick") && !args[0].equals("delete"))) Reference.sendCommandUsage(sender, usage);
			else if (args[0].equals("create")) try {
				args = Reference.removeArg(args, 0);
				if (args.length == 0 || args[0].length() > 16) {
					Reference.sendCommandUsage(sender, "/fakeplayer create <username> [skinname] [UUID] Create a fake player, the limit of the username is 16 characters as is the limit of the skinname. Username is what the actual name of the player will be, skinname is the name of the player whose skin to use, defaults to username if not set. UUID is the unique user identifier of the fake player. The created player is not just a living entity, but rather an actual player and can thus be used to activate spawners, load chunks etc.");
					return;
				}
				String username = args[0];
				try {
					getPlayer(server, sender, username);
					Reference.sendMessage(sender, TextFormatting.RED + "A player with the given username/UUID appears to be online.");
					return;
				} catch (CommandException e) {}
				String skinname = args.length >= 2 ? args[1] : username;
				boolean random = false;
				int i = 0;
				UUID userId = args.length >= 3 && Reference.isUUID(args[2]) ? UUID.fromString(args[2]) : null;
				UUID skinId = null;
				try {
					if (username.length() > 16) {
						Reference.sendCommandUsage(sender, TextFormatting.RED + "The given username was more than 16 characters.");
						return;
					} else if (skinname.length() > 16) {
						Reference.sendMessage(sender, TextFormatting.RED + "The given skinname was more than 16 characters.");
						return;
					}
					Map skinIdData = new Gson().fromJson(Reference.getHTML("https://api.mojang.com/users/profiles/minecraft/" + skinname), Map.class);
					if (skinIdData == null) skinId = userId;
					else skinId = UUIDTypeAdapter.fromString((String) skinIdData.get("id"));
					if (!username.equals(skinname) && userId == null) {
						i = 1;
						Map userIdData = new Gson().fromJson(Reference.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username), Map.class);
						if (userIdData == null) throw new IOException("Could not get UUID of player " + username + ".");
						userId = UUIDTypeAdapter.fromString((String) userIdData.get("id"));
						if (skinId == null) skinId = userId;
					} else userId = userId == null ? skinId : userId;
				} catch (JsonSyntaxException | IOException e1) {
					e1.printStackTrace();
					Reference.sendMessage(sender, TextFormatting.RED + "The UUID of the player with the given username '" + (username.equals(skinname) || i == 1 ? username : skinname) + "' could not be gotten, is the playername valid? Defaulting to random UUID.");
					userId = UUID.randomUUID();
					if (skinId == null) skinId = userId;
					random = skinId == userId;
				}
				NBTTagCompound tag = hasPlayerData(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), userId) ? getPlayerData(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), userId) : new NBTTagCompound();
				if (!tag.isEmpty() && !tag.getCompoundTag("ForgeCaps").isEmpty() && !tag.getCompoundTag("ForgeCaps").getCompoundTag(new ResourceLocation(Reference.MOD_ID, "fakeplayer").toString()).getBoolean("isFake")) {
					Reference.sendMessage(sender, TextFormatting.RED + "You cannot spawn a fake player entity of a player who has played on this world before while not being fake.");
					return;
				}
				GameProfile profile = new GameProfile(userId, username);
				if (!random) {
					Map<String, Object> data0 = new Gson().fromJson(Reference.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(skinId) + "?unsigned=false"), Map.class);
					List<Map> properties = (List<Map>) data0.get("properties");
					if (properties.isEmpty()) Reference.sendMessage(sender, TextFormatting.RED + "The gameprofile of the given player has no properties thus the fake player cannot be made, this is not a good sign.");
					else {
						String data = null;
						String signature = null;
						for (Map map : properties)
							if (map.get("name").equals("textures")) {
								data = map.get("value").toString();
								signature = map.get("signature").toString();
								break;
							}
						// Huge thanks to FishyLP from Spigot for the help here, the only thing I was
						// stuck on was getting the skin to be rendered, but that works now thanks to
						// him!
						// https://www.spigotmc.org/threads/heads-with-uuids.193123/
						if (data == null || signature == null) Reference.sendMessage(sender, "The skin of the given player could not be gotten, defaulting to Minecraft defaults (In this case " + ((userId.getLeastSignificantBits() & 1) == 0 ? "Alex" : "Steve") + ").");
						else profile.getProperties().put("textures", new Property("textures", data, signature));
					}
				}
				EntityPlayerMP player = new EntityPlayerMP(server, server.getWorld(sender.getEntityWorld().provider.getDimension()), profile, new PlayerInteractionManager(sender.getEntityWorld()));
				NetworkManager nMan = new NetworkManager(EnumPacketDirection.SERVERBOUND);
				Field f = NetworkManager.class.getDeclaredField("channel");
				f.setAccessible(true);
				// I beg you, do not look at the following line. Please! @formatter:off
				f.set(nMan, new AbstractChannel(null) {@Override public ChannelConfig config() {return new ChannelConfig() {@Override public Map<ChannelOption<?>, Object> getOptions() {return Maps.newHashMap();}@Override public boolean setOptions(Map<ChannelOption<?>, ?> options) {return false;}@Override public <T> T getOption(ChannelOption<T> option) {return null;}@Override public <T> boolean setOption(ChannelOption<T> option, T value) {return false;}@Override public int getConnectTimeoutMillis() {return 0;}@Override public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {return this;}@Override public int getMaxMessagesPerRead() {return 1;}@Override public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {return this;}@Override public int getWriteSpinCount() {return 0;}@Override public ChannelConfig setWriteSpinCount(int writeSpinCount) {return this;}@Override public ByteBufAllocator getAllocator() {return new AbstractByteBufAllocator() {@Override public boolean isDirectBufferPooled() {return false;}@Override protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {return null;}@Override protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {return null;}};}@Override public ChannelConfig setAllocator(ByteBufAllocator allocator) {return this;}@Override public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {return null;}@Override public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {return this;}@Override public boolean isAutoRead() {return false;}@Override public ChannelConfig setAutoRead(boolean autoRead) {return this;}@Override public boolean isAutoClose() {return false;}@Override public ChannelConfig setAutoClose(boolean autoClose) {return this;}@Override public int getWriteBufferHighWaterMark() {return 0;}@Override public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {return this;}@Override public int getWriteBufferLowWaterMark() {return 0;}@Override public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {return this;}@Override public MessageSizeEstimator getMessageSizeEstimator() {return null;}@Override public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {return this;}@Override public WriteBufferWaterMark getWriteBufferWaterMark() {return null;}@Override public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {return this;}};}@Override public boolean isOpen() {return false;}@Override public boolean isActive() {return false;}@Override public ChannelMetadata metadata() {return new ChannelMetadata(false);}@Override protected AbstractUnsafe newUnsafe() {return new AbstractUnsafe() {@Override public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {}};}@Override protected boolean isCompatible(EventLoop loop) {return false;}@Override protected SocketAddress localAddress0() {return null;}@Override protected SocketAddress remoteAddress0() {return null;}@Override protected void doBind(SocketAddress localAddress) throws Exception {}@Override protected void doDisconnect() throws Exception {}@Override protected void doClose() throws Exception {}@Override protected void doBeginRead() throws Exception {}@Override protected void doWrite(ChannelOutboundBuffer in) throws Exception {}});
				// Yuck @formatter:on
				if (sender instanceof Entity) {
					player.rotationYaw = ((Entity) sender).rotationYaw;
					player.rotationPitch = ((Entity) sender).rotationPitch;
				}
				server.getPlayerList().initializeConnectionToPlayer(nMan, player, new NetHandlerPlayServer(server, nMan, player));
				player.setPositionAndUpdate(sender.getPosition().getX(), sender.getPosition().getY(), sender.getPosition().getZ());
				player.setGameType(GameType.CREATIVE);
				player.getCapability(FPProvider.fpCap, null).set(true, player.getName());
				player.capabilities.disableDamage = true;
				player.setEntityInvulnerable(true);
				player.sendPlayerAbilities();
				if (player.dimension != sender.getEntityWorld().provider.getDimension()) server.getPlayerList().transferPlayerToDimension(player, sender.getEntityWorld().provider.getDimension(), (world, entity, yaw) -> {});
				Reference.sendMessage(sender, "A fake player by the name of " + player.getName() + " has been spawned." + (new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + player.getUniqueID().toString() + ".dat").exists() ? "" : " It may take a while before the player gets teleported to you as this appears to be the first time a player by this ID has been spawned."));
			} catch (Exception e) {
				e.printStackTrace();
				Reference.sendMessage(sender, TextFormatting.RED + "Something went wrong while creating the player. Message: " + e.getMessage());
			}
			else if (args[0].equals("kick")) {
				args = Reference.removeArg(args, 0);
				if (args.length == 0) Reference.sendCommandUsage(sender, "/fakeplayer kick <name> Kick a player from the server if and only if it is fake.");
				else {
					EntityPlayerMP player;
					try {
						player = getPlayer(server, sender, args[0]);
					} catch (CommandException e) {
						Reference.sendMessage(sender, TextFormatting.RED + "The given player could not be found.");
						return;
					}
					if (player.getCapability(FPProvider.fpCap, null).isFake) {
						player.connection.disconnect(new TextComponentString("You have been kicked by " + sender.getName() + "."));
						Reference.sendMessage(sender, "The fake player '" + player.getName() + "' has been kicked.");
					} else Reference.sendMessage(sender, TextFormatting.RED + "The given player is not fake.");
				}
			} else if (args[0].equals("delete")) {
				args = Reference.removeArg(args, 0);
				if (args.length == 0) Reference.sendCommandUsage(sender, "/fakeplayer delete <name/UUID> Deletes a fake player entirely, even from the disk.");
				else {
					String username = args[0];
					UUID id = null;
					if (Reference.isUUID(username)) UUIDTypeAdapter.fromString(username);
					else try {
						Map userIdData = new Gson().fromJson(Reference.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username), Map.class);
						if (userIdData == null) throw new IOException("The Mojang api returned invalid data.");
						id = UUIDTypeAdapter.fromString((String) userIdData.get("id"));
					} catch (IOException e) {
						e.printStackTrace();
						Reference.sendMessage(sender, TextFormatting.RED + "Could not get the UUID of player '" + username + "', assuming the fake player does not really exist, getting UUID dangerously, this might take a while...");
						for (File data : new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/").listFiles()) {
							NBTTagCompound tag;
							try {
								tag = CompressedStreamTools.readCompressed(data.toURI().toURL().openStream());
							} catch (IOException ignored) {
								continue;
							}
							if (!tag.isEmpty() && !tag.getCompoundTag("ForgeCaps").isEmpty() && tag.getCompoundTag("ForgeCaps").getString(new ResourceLocation(Reference.MOD_ID, "playername").toString()).equals(username)) {
								id = tag.getUniqueId("UUID");
								break;
							}
						}
					}
					for (EntityPlayerMP player : server.getPlayerList().getPlayers())
						if (player.getName().equals(username) || player.getUniqueID().equals(id)) {
							if (player.getCapability(FPProvider.fpCap, null).isFake) Reference.sendMessage(sender, TextFormatting.RED + "The given player appears to be online, please kick them first.");
							else Reference.sendMessage(sender, TextFormatting.DARK_RED + "The given player appears to be online and not to be fake.");
							return;
						}
					if (id == null) Reference.sendMessage(sender, TextFormatting.RED + "No UUID belonging to the given username could be found.");
					else if (!new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + id.toString() + ".dat").exists()) Reference.sendMessage(sender, TextFormatting.RED + "No file belonging to the found UUID could be found.");
					else try {
						FileUtils.touch(new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + id.toString() + ".dat"));
						NBTTagCompound tag = getPlayerData(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), id);
						if (!tag.isEmpty() && !tag.getCompoundTag("ForgeCaps").isEmpty() && tag.getCompoundTag("ForgeCaps").getCompoundTag(new ResourceLocation(Reference.MOD_ID, "fakeplayer").toString()).getBoolean("isFake")) if (new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + id.toString() + ".dat").delete()) Reference.sendMessage(sender, "The fake player '" + tag.getCompoundTag("ForgeCaps").getCompoundTag(new ResourceLocation(Reference.MOD_ID, "fakeplayer").toString()).getString("name") + "' has been fully deleted.");
						else Reference.sendMessage(sender, TextFormatting.RED + "The file could not be deleted.");
						else Reference.sendMessage(sender, TextFormatting.RED + "A file belonging to the given player has been found, but the player appears not to be fake.");
					} catch (IOException e) {
						Reference.sendMessage(sender, TextFormatting.RED + "The file belonging to the found UUID appears to be in use. Is the player online?");
					}
				}
			}
		}

		public boolean hasPlayerData(File worldDir, UUID id) {
			return new File(worldDir, "playerdata/" + id.toString() + ".dat").exists();
		}

		public static NBTTagCompound getPlayerData(ICommandSender sender, UUID id) throws IOException {
			return getPlayerData(sender.getEntityWorld(), id);
		}

		public static NBTTagCompound getPlayerData(World world, UUID id) throws IOException {
			return getPlayerData(world.getSaveHandler(), id);
		}

		public static NBTTagCompound getPlayerData(ISaveHandler handler, UUID id) throws IOException {
			return getPlayerData(handler.getWorldDirectory(), id);
		}

		public static NBTTagCompound getPlayerData(File worldDir, UUID id) throws IOException {
			return worldDir == null || id == null ? new NBTTagCompound() : MoreObjects.firstNonNull(CompressedStreamTools.readCompressed(new File(worldDir, "playerdata/" + id.toString() + ".dat").toURI().toURL().openStream()), new NBTTagCompound());
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "fakeplayer", "Create fake players.", true);
		}

		private String usage = "/fakeplayer <create|kick|delete> Create, kick or completely delete a fake player.";

	}

}