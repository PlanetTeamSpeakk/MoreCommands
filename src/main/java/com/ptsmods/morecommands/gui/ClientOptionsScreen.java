package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ClientOptionsScreen extends Screen {

    private final Screen parent;

    public ClientOptionsScreen(Screen parent) {
        super(new LiteralText("MoreCommands").setStyle(MoreCommands.DS).append(new LiteralText(" client options").setStyle(MoreCommands.SS)));
        this.parent = parent;
    }

    @Override
    protected void init() {
        boolean right = false;
        int row = 0;
        for (Class<?> c : ClientOptions.class.getClasses())
            if (!c.isInterface()) {
                int x = width / 2 + (right ? 5 : -155);
                int y = height / 6 + 24 * (row + 1) - 6;
                addButton(new ButtonWidget(x, y, 150, 20, new LiteralText(ClientOptionsChildScreen.getCleanName(c.getSimpleName()).trim()), button -> client.openScreen(new ClientOptionsChildScreen(this, c))));
                if (right) row++;
                right = !right;
            }
        addButton(new ButtonWidget(width / 2 - 100, height / 6 + 168, 200, 20, ScreenTexts.DONE, (buttonWidget) -> client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, client.textRenderer, getTitle(), width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }
}
