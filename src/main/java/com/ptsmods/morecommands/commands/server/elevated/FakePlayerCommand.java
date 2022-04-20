package com.ptsmods.morecommands.commands.server.elevated;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.arguments.CrampedStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import com.ptsmods.morecommands.util.CompatHolder;
import io.netty.channel.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakePlayerCommand extends Command {
	private final List<UUID> fake = new ArrayList<>();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("fakeplayer")
				.then(literal("create")
						.then(argument("name", CrampedStringArgumentType.crampedWord(3, 16))
								.executes(ctx -> executeCreate(ctx, null, null))
								.then(argument("skinname", CrampedStringArgumentType.crampedWord(3, 16))
										.executes(ctx -> executeCreate(ctx, ctx.getArgument("skinname", String.class), null))
										.then(argument("userid", new UuidArgumentType())
												.executes(ctx -> executeCreate(ctx, ctx.getArgument("skinname", String.class), ctx.getArgument("userid", UUID.class)))))))
				.then(literal("kick")
						.then(argument("player", EntityArgumentType.player())
								.executes(ctx -> {
									ServerPlayerEntity p = EntityArgumentType.getPlayer(ctx, "player");
									if (!fake.contains(p.getUuid())) sendError(ctx, "To kick normal players, please use the /kick command instead.");
									else {
										try {
											p.networkHandler.disconnect(new LiteralText("yeET"));
											sendMsg(ctx, "The player has been disconnected.");
										} catch (Exception e) {
											log.catching(e);
										}
										return 1;
									}
									return 0;
								}))));
	}

	private int executeCreate(CommandContext<ServerCommandSource> ctx, String skinname, UUID userId) {
		String username = ctx.getArgument("name", String.class);
		if (skinname == null) skinname = username;
		try {
			if (ctx.getSource().getServer().getPlayerManager().getPlayer(username) != null || userId != null && ctx.getSource().getServer().getPlayerManager().getPlayer(userId) != null)
				sendError(ctx, "A player with that name is already logged in.");
			else {
				boolean random = false;
				int i = 0;
				UUID skinId = null;

				try {
					Map<?, ?> skinIdData = new Gson().fromJson(MoreCommands.getHTML("https://api.mojang.com/users/profiles/minecraft/" + skinname), Map.class);
					if (skinIdData == null) skinId = userId;
					else skinId = UUIDTypeAdapter.fromString((String) skinIdData.get("id"));

					if (!username.equals(skinname) && userId == null) {
						i = 1;
						Map<?, ?> userIdData = new Gson().fromJson(MoreCommands.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username), Map.class);
						if (userIdData == null) throw new IOException("Could not get UUID of player " + username + ".");

						userId = UUIDTypeAdapter.fromString((String) userIdData.get("id"));
						if (skinId == null) skinId = userId;
					} else userId = userId == null ? skinId : userId;
				} catch (JsonSyntaxException | IOException e1) {
					sendError(ctx, "The UUID of the player with the given username '" + (username.equals(skinname) || i == 1 ? username : skinname) +
							"' could not be gotten, is the playername valid? Defaulting to random UUID.");

					userId = UUID.randomUUID();
					if (skinId == null) skinId = userId;
					random = skinId == userId;
				}

				GameProfile profile = new GameProfile(userId, username);
				if (!random) {
					Map<?, ?> data0 = new Gson().fromJson(MoreCommands.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + skinId + "?unsigned=false"), Map.class);
					List<Map<?, ?>> properties = (List<Map<?, ?>>) data0.get("properties");

					if (properties.isEmpty()) sendError(ctx, "The gameprofile of the given player has no properties, this is not a good sign.");
					else {
						String data = null;
						String signature = null;
						for (Map<?, ?> map : properties)
							if (map.get("name").equals("textures")) {
								data = map.get("value").toString();
								signature = map.get("signature").toString();
								break;
							}
						// Huge thanks to FishyLP from Spigot for the help here, the only thing I was
						// stuck on was getting the skin to be rendered, but that works now thanks to
						// him!
						// https://www.spigotmc.org/threads/heads-with-uuids.193123/
						if (data == null || signature == null) sendMsg(ctx, "The skin of the given player could not be gotten, defaulting to Minecraft defaults (in this case " +
								((userId.getLeastSignificantBits() & 1) == 0 ? "Alex" : "Steve") + ").");
						else profile.getProperties().put("textures", new Property("textures", data, signature));
					}
				}

				ServerPlayerEntity player = CompatHolder.getCompat().newServerPlayerEntity(ctx.getSource().getServer(), ctx.getSource().getWorld(), profile);
				ClientConnection ccon = new ClientConnection(NetworkSide.SERVERBOUND);

				Field f = ReflectionHelper.getYarnField(ClientConnection.class, "channel", "field_11651");
				// I beg you, do not look at the following line. Please!
				f.set(ccon, new AbstractChannel(null) {@Override public ChannelConfig config() {return new DefaultChannelConfig(this);} @Override public boolean isOpen() {return false;} @Override public boolean isActive() {return false;} @Override public ChannelMetadata metadata() {return new ChannelMetadata(true);} @Override protected AbstractUnsafe newUnsafe() {return null;} @Override protected boolean isCompatible(EventLoop loop) {return false;} @Override protected SocketAddress localAddress0() {return null;} @Override protected SocketAddress remoteAddress0() {return null;} @Override protected void doBind(SocketAddress localAddress) throws Exception {} @Override protected void doDisconnect() throws Exception {} @Override protected void doClose() throws Exception {} @Override protected void doBeginRead() throws Exception {} @Override protected void doWrite(ChannelOutboundBuffer in) throws Exception {}});
				// Yuck

				if (ctx.getSource().getEntity() != null) {
					MixinEntityAccessor entityAccessor = (MixinEntityAccessor) ctx.getSource().getEntity();
					MixinEntityAccessor playerAccessor = (MixinEntityAccessor) player;

					playerAccessor.setYaw_(entityAccessor.getYaw_());
					playerAccessor.setPitch_(entityAccessor.getPitch_());
				}

				player.getDataTracker().set(MixinPlayerEntityAccessor.getPlayerModelParts(), (byte) 255);

				player.networkHandler = new ServerPlayNetworkHandler(ctx.getSource().getServer(), ccon, player);
				ctx.getSource().getServer().getPlayerManager().onPlayerConnect(ccon, player);

				player.updatePosition(ctx.getSource().getPosition().x, ctx.getSource().getPosition().y, ctx.getSource().getPosition().z);
				player.changeGameMode(GameMode.CREATIVE);

				((MixinPlayerEntityAccessor) player).getAbilities_().invulnerable = true;
				player.setInvulnerable(true);
				player.sendAbilitiesUpdate();

				MoreCommands.teleport(player, ctx.getSource().getWorld(), ctx.getSource().getPosition().x, ctx.getSource().getPosition().y, ctx.getSource().getPosition().z,
						((MixinEntityAccessor) player).getYaw_(), ((MixinEntityAccessor) player).getPitch_());

				fake.add(player.getUuid());
				sendMsg(ctx, "A fake player by the name of " + MoreCommands.textToString(player.getName(), null, true) + " has been spawned.");
				return 1;
			}
		} catch (Exception e) {
			log.catching(e);
			sendError(ctx, "Something went wrong while creating the player. Message: " + e.getMessage());
		}
		return 0;
	}

}
