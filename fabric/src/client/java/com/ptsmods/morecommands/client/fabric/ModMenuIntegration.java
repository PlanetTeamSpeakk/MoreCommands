package com.ptsmods.morecommands.client.fabric;

import com.ptsmods.morecommands.client.gui.ClientOptionsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClientOptionsScreen::new;
    }
}
