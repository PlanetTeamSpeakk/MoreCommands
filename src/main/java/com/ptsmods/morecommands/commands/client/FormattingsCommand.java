package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;

public class FormattingsCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
		dispatcher.register(cLiteral("formattings").executes(ctx -> {
			scheduleScreen(new BookScreen(new BookScreen.Contents() {
				@Override
				public int getPageCount() {
					return 1;
				}

				@Override
				public StringVisitable getPageUnchecked(int index) {
					char ss = '\u00A7';
					return new LiteralText(
							ss + "00 " + ss + "11 " + ss + "22 " + ss + "33\n" +
							ss + "44 " + ss + "55 " + ss + "66 " + ss + "77\n" +
							ss + "88 " + ss + "99 " + ss + "aa " + ss + "bb\n" +
							ss + "cc " + ss + "dd " + ss + "ee " + ss + "ff" + ss + "r\n\n" +
							ss + "rk " + ss + "kMinecraft" + ss + "r\n" +
							ss + "rl " + ss + "lMinecraft" + ss + "r\n" +
							ss + "rm " + ss + "mMinecraft" + ss + "r\n" +
							ss + "rn " + ss + "nMinecraft" + ss + "r\n" +
							ss + "ro " + ss + "oMinecraft" + ss + "r\n" +
							ss + "rr " + ss + "rMinecraft" + ss + "r\n" +
							ss + "ru " + ss + "uMinecraft");
				}
			}));
			return 1;
		}));
	}
}
