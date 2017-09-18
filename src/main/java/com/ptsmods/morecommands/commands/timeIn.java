package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class timeIn {

	public timeIn() {
	}

	public static class CommandtimeIn extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "timein";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0)
				new Thread(new Runnable() {
					@Override
					public void run() {
						Gson gson = new Gson();
						String location = Reference.join(args);
						Reference.sendMessage(sender, "Getting the time in " + TextFormatting.GRAY + TextFormatting.ITALIC + location + TextFormatting.RESET + "...");
						String dataGeocode;
						try {
							dataGeocode = Reference.getHTML("https://maps.googleapis.com/maps/api/geocode/json?address=" + location.replaceAll(" ", "%20") + "&key=" + Reference.apiKeys.get("geocoding"));
						} catch (IOException e) {
							Reference.sendMessage(sender, "An unknown error occured while getting the longitude and latitude from the Google API.");
							e.printStackTrace();
							return;
						}
						Map dataGeocodeMap = gson.fromJson(dataGeocode, Map.class);
						if (!((String) dataGeocodeMap.get("status")).equals("OK")) {
							Reference.sendMessage(sender, "Could not find any results for " + location + ".");
							return;
						}
						Map lngNlat = (Map) ((Map) ((Map) ((List) dataGeocodeMap.get("results")).get(0)).get("geometry")).get("location");
						Double lng = (Double) lngNlat.get("lng");
						Double lat = (Double) lngNlat.get("lat");
						Long timestamp = new Timestamp(System.currentTimeMillis()).getTime() / 1000;
						String fullAddr = (String) ((Map) ((List) dataGeocodeMap.get("results")).get(0)).get("formatted_address");
						String dataTimezone;
						try {
							dataTimezone = Reference.getHTML("https://maps.googleapis.com/maps/api/timezone/json?location=" + lat + "," + lng + "&timestamp=" + timestamp + "&key=" + Reference.apiKeys.get("timezone"));
						} catch (IOException e) {
							Reference.sendMessage(sender, "An unknown error occured while getting the time and timezone from the Google API.");
							e.printStackTrace();
							return;
						}
						Map dataTimezoneMap = gson.fromJson(dataTimezone, Map.class);
						if (!((String) dataTimezoneMap.get("status")).equals("OK")) {
							Reference.sendMessage(sender, "Could not find any results for " + location + ".");
							return;
						}
						timestamp += ((Double) dataTimezoneMap.get("dstOffset")).longValue() + ((Double) dataTimezoneMap.get("rawOffset")).longValue();
						Reference.sendMessage(sender, "" + TextFormatting.GRAY + TextFormatting.ITALIC + fullAddr + TextFormatting.RESET + ":\n" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(timestamp * 1000L)) + " (" + dataTimezoneMap.get("timeZoneName") + ")");
					}
				}).start();
			else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private String usage = "/timein <place> Uses Google apis to get the location in a place somewhere on the world, can be a country, postal code, whatever you want.";

	}

}