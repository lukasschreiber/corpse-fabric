package org.lukasschreiber.corpsefabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.death.Death;

import static org.lukasschreiber.corpsefabric.gui.utils.TextUtils.TITLE_COLOR;

public class CorpseInventoryScreen extends HandledScreen<CorpseInventoryScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Corpse.NAMESPACE, "textures/gui/corpse-inventory.png");
    private final PlayerEntity player;

    public CorpseInventoryScreen(CorpseInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.titleX = 7;
        this.backgroundHeight = 212;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.player = inventory.player;
    }

    @Override
    protected void init() {
        super.init();
        this.textRenderer = MinecraftClient.getInstance().textRenderer;

        this.addDrawableChild(new TexturedButtonWidget(this.x + 151, this.y + 17, 18, 18, 0, 212, 18, TEXTURE, button -> {
            Death selectedDeath = this.handler.getDeath();
//            this.handler.setInventory(selectedDeath.transferInventory(this.player));
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        this.renderBackground(context);

        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, TITLE_COLOR, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, TITLE_COLOR, false);
    }
}
