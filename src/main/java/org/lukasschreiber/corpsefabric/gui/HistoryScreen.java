package org.lukasschreiber.corpsefabric.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.Vec2i;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.entities.DummyPlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Environment(EnvType.CLIENT)
public class HistoryScreen extends HandledScreen<HistoryScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Corpse.NAMESPACE, "textures/gui/death-history.png");
    private static final int TEXT_HEIGHT = 11;
    private static final int TAB_WIDTH = 32;
    private static final int TAB_HEIGHT = 26;
    private final List<Death> deaths;
    private final ClientPlayerEntity player;
    private int selectedTab = 0;
    private final Map<UUID, DummyPlayerEntity> dummyPlayers = new HashMap<>();

    public HistoryScreen(ClientPlayerEntity player, List<Death> deaths) {
        super(new HistoryScreenHandler(!deaths.isEmpty() ? deaths.get(0).getInventory() : new SimpleInventory()), player.getInventory(), Text.literal("Death History"));
        this.titleX = 95;
        this.deaths = deaths;
        this.player = player;
    }


    @Override
    protected void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        for (int i = 0; i < this.deaths.size(); i++) {
            if (i == this.selectedTab) continue;
            this.renderTabIcon(context, i);
        }

        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.selectedTab < this.deaths.size()) {
            Death selectedDeath = deaths.get(this.selectedTab);
            this.renderTabIcon(context, this.selectedTab);

            InventoryScreen.drawEntity(context, this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.getOrCreateDummyPlayer(selectedDeath));
        }

        // this should be done in the foreground, but then it stops working
        for (int i = 0; i < this.deaths.size(); i++) {
            if (this.isMouseInTab(i, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, Text.literal(deaths.get(i).getCauseOfDeath()), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);

        if (this.selectedTab < this.deaths.size()) {
            Death selectedDeath = deaths.get(this.selectedTab);
            context.drawText(this.textRenderer, TimeUtils.timeDescription(selectedDeath.getTimestamp()), this.titleX, this.titleY + TEXT_HEIGHT, 8158332, false);
            Vec3d pos = selectedDeath.getPos();

            // should use appended and styled text
            int positionOffset = TextUtils.computeStringLength("X: ");
            context.drawText(this.textRenderer, "X:", this.titleX, this.titleY + TEXT_HEIGHT * 2, 4210752, false);
            context.drawText(this.textRenderer, String.valueOf((int) pos.x), this.titleX + positionOffset, this.titleY + TEXT_HEIGHT * 2, 8158332, false);

            context.drawText(this.textRenderer, "Y:", this.titleX, this.titleY + TEXT_HEIGHT * 3, 4210752, false);
            context.drawText(this.textRenderer, String.valueOf((int) pos.y), this.titleX + positionOffset, this.titleY + TEXT_HEIGHT * 3, 8158332, false);

            context.drawText(this.textRenderer, "Z:", this.titleX, this.titleY + TEXT_HEIGHT * 4, 4210752, false);
            context.drawText(this.textRenderer, String.valueOf((int) pos.z), this.titleX + positionOffset, this.titleY + TEXT_HEIGHT * 4, 8158332, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < this.deaths.size(); i++) {
            if (isMouseInTab(i, mouseX, mouseY)) {
                this.selectedTab = i;
                this.handler.setInventory(this.deaths.get(i).getInventory());

                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private DummyPlayerEntity getOrCreateDummyPlayer(Death death) {
        DummyPlayerEntity dummy = this.dummyPlayers.get(death.getUuid());
        if(dummy != null) return dummy;
        dummy = DummyPlayerEntity.fromDeath(death, this.player.getWorld());
        this.dummyPlayers.put(death.getUuid(), dummy);
        return dummy;
    }

    private Vec2i getTabPosition(int index) {
        return new Vec2i(this.x + this.backgroundWidth, this.y + index * (TAB_HEIGHT + 1));
    }

    private void renderTabIcon(DrawContext context, int index) {
        Vec2i pos = this.getTabPosition(index);
        Death death = this.deaths.get(index);
        boolean selected = index == this.selectedTab;

        if (selected) {
            if (index == 0) {
                context.drawTexture(TEXTURE, pos.x - 4, pos.y, 176, 0, TAB_WIDTH, TAB_HEIGHT);
            } else {
                context.drawTexture(TEXTURE, pos.x - 4, pos.y, 176, 26, TAB_WIDTH, TAB_HEIGHT);
            }

        } else {
            context.drawTexture(TEXTURE, pos.x - 2, pos.y, 208, 0, TAB_WIDTH, TAB_HEIGHT);
        }
        ItemStack stack = PlayerCorpseItem.fromDeath(death);
        context.drawItem(stack, pos.x + 4, pos.y + 5);
        context.drawItemInSlot(this.textRenderer, stack, pos.x + 4, pos.y + 5);
    }

    private boolean isMouseInTab(int index, double mouseX, double mouseY) {
        Vec2i pos = this.getTabPosition(index);
        return pos.x <= mouseX && mouseX <= pos.x + TAB_WIDTH && pos.y <= mouseY && mouseY <= pos.y + TAB_HEIGHT;
    }
}
