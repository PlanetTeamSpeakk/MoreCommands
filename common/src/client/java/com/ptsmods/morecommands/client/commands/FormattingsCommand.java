package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.FormattedText;

public class FormattingsCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        dispatcher.register(cLiteral("formattings")
                .executes(ctx -> {
                    scheduleScreen(new BookViewScreen(new BookViewScreen.BookAccess() {
                        @Override
                        public int getPageCount() {
                            return 1;
                        }

                        @Override
                        public FormattedText getPageRaw(int index) {
                            char ss = '\u00A7';
                            return literalText(
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
                                    ss + "ru " + ss + "uMinecraft").build();
                        }
                    }));
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/formattings";
    }
}
