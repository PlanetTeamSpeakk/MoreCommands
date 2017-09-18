package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import scala.actors.threadpool.Arrays;

public class curConvert {

	public curConvert() {
	}

	public static class CommandcurConvert extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1 || args.length == 2)
				return getListOfStringsMatchingLastWord(args, Arrays.asList(Reference.currencies.keySet().toArray(new String[0])));
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "curconvert";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Reference.currencies.isEmpty())
						try {
							String data = Reference.getHTML("http://free.currencyconverterapi.com/api/v3/currencies");
							Map<String, Map> results = (Map) new Gson().fromJson(data, Map.class).get("results");
							for (String currency : results.keySet())
								Reference.currencies.put(currency, (String) results.get(currency).get("currencyName"));
						} catch (IOException e) {
							Reference.sendMessage(sender, "An unknown error occured while getting the currencies, please try again.");
							e.printStackTrace();
							return;
						}
					if (args.length >= 1 && args[0].equals("list")) Reference.sendMessage(sender, "Currently available currencies:\n" + getCurrenciesString());
					else if (args.length < 2) Reference.sendCommandUsage(sender, usage);
					else if (!Reference.currencies.keySet().contains(args[0]) || !Reference.currencies.keySet().contains(args[1])) Reference.sendMessage(sender, "One of the given currencies could not be found, do /curconvert list for a list of currencies.");
					else {
						String data = "";
						try {
							data = Reference.getHTML("http://free.currencyconverterapi.com/api/v3/convert?q=" + args[0] + "_" + args[1] + "&compact=y");
						} catch (IOException e) {
							Reference.sendMessage(sender, "An unknown error occured while converting the currencies, please try again.");
							e.printStackTrace();
							return;
						}
						double amount = 1;
						Map<String, Double> dataMap = ((Map<String, Map<String, Double>>) new Gson().fromJson(data, Map.class)).get(args[0] + "_" + args[1]);
						if (args.length >= 3 && Reference.isDouble(args[2])) amount = Double.parseDouble(String.format("%.2f", Double.parseDouble(args[2])));
						Reference.sendMessage(sender, String.format("%.2f", amount) + " " + args[0] + " = " + String.format("%.2f", dataMap.get("val") * amount) + " " + args[1]);
					}
				}
			}).start();
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private static String getCurrenciesString() {
			String output = "" + TextFormatting.GOLD;
			for (int x = 0; x < Reference.currencies.keySet().size(); x++) {
				String currency = Reference.currencies.keySet().toArray(new String[0])[x];
				String output1;
				output += output1 = currency + TextFormatting.YELLOW + " " + Reference.currencies.get(currency) + TextFormatting.GOLD;
				if (x+1 != Reference.currencies.keySet().size()) x++;
				else break;
				currency = Reference.currencies.keySet().toArray(new String[0])[x];
				output += Reference.multiplyString(" ", 26 - TextFormatting.getTextWithoutFormattingCodes(output1).length()) + currency + TextFormatting.YELLOW + " " + Reference.currencies.get(currency) + "\n" + TextFormatting.GOLD;
			}
			return output.substring(0, output.length()-("\n" + TextFormatting.GOLD).length());
		}

		private String usage = "/curconvert <from_cur> <to_cur> [amount] Converts a currency to another one, you can also do /curconvert list for a list of currencies.";

	}

}