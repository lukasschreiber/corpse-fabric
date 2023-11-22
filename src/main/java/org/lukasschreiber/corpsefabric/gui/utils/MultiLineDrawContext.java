package org.lukasschreiber.corpsefabric.gui.utils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class MultiLineDrawContext {
    private DrawContext context;
    private int lineOffset;
    public int x;
    public int y;
    public int gap;

    public MultiLineDrawContext(DrawContext context, int x, int y, int gap) {
        this.context = context;
        this.x = x;
        this.y = y;
        this.lineOffset = 0;
        this.gap = gap;
    }

    public void drawLine(TextRenderer textRenderer, Text text, int color, boolean shadow) {
        this.context.drawText(textRenderer, text, this.x, this.y + lineOffset, color, shadow);
        this.lineOffset += textRenderer.fontHeight + this.gap;
    }

    public void drawLine(TextRenderer textRenderer, String text, int color, boolean shadow) {
        this.drawLine(textRenderer, Text.literal(text), color, shadow);
    }

    public void drawLineWithTitle(TextRenderer textRenderer, Text title, Text text, int titleColor, int color, boolean shadow) {
        this.context.drawText(textRenderer, title, this.x, this.y + this.lineOffset, titleColor, shadow);
        this.context.drawText(textRenderer, text, this.x + textRenderer.getWidth(title), this.y + this.lineOffset, color, shadow);
        this.lineOffset += textRenderer.fontHeight + this.gap;
    }

    public void drawLineWithTitle(TextRenderer textRenderer, String title, String text, int titleColor, int color, boolean shadow) {
        this.drawLineWithTitle(textRenderer, Text.literal(title), Text.literal(text), titleColor, color, shadow);
    }

    public int getBottom() {
        return this.lineOffset;
    }
}
