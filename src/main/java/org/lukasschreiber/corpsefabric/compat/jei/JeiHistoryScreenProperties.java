package org.lukasschreiber.corpsefabric.compat.jei;

import mezz.jei.api.gui.handlers.IGuiProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.NotNull;

/**
 * IGuiProperties that define a screen that spans the entire window
 * therefore the Jei GUI is not shown
 * <p>
 * For example used by the History Screen, which is a HandledScreen
 * but has no use for Jei GUI.
 * </p>
 */
public class JeiHistoryScreenProperties implements IGuiProperties {

    private final Window window;
    private final Screen screen;

    public JeiHistoryScreenProperties(Screen screen) {
        this.window = MinecraftClient.getInstance().getWindow();
        this.screen = screen;
    }

    @Override
    public @NotNull Class<? extends Screen> getScreenClass() {
        return this.screen.getClass();
    }

    @Override
    public int getGuiLeft() {
        return 0;
    }

    @Override
    public int getGuiTop() {
        return 0;
    }

    @Override
    public int getGuiXSize() {
        return this.window.getWidth();
    }

    @Override
    public int getGuiYSize() {
        return this.window.getHeight();
    }

    @Override
    public int getScreenWidth() {
        return this.window.getWidth();
    }

    @Override
    public int getScreenHeight() {
        return this.window.getHeight();
    }
}
