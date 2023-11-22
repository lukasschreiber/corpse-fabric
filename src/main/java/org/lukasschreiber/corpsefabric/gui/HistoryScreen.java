package org.lukasschreiber.corpsefabric.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.Vec2i;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.entities.DummyPlayerEntity;
import org.lukasschreiber.corpsefabric.gui.utils.MultiLineDrawContext;
import org.lukasschreiber.corpsefabric.gui.utils.TextUtils;
import org.lukasschreiber.corpsefabric.gui.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Environment(EnvType.CLIENT)
public class HistoryScreen extends HandledScreen<HistoryScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Corpse.NAMESPACE, "textures/gui/death-history.png");
    private static final int TAB_WIDTH = 32;
    private static final int TAB_HEIGHT = 26;
    private final PlayerEntity player;
    private final Map<UUID, DummyPlayerEntity> dummyPlayers = new HashMap<>();

    public HistoryScreen(HistoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.player = inventory.player;
        this.titleX = 98;
    }

    @Override
    protected void init() {
        super.init();
        this.textRenderer = MinecraftClient.getInstance().textRenderer;

        int selectedTab = this.handler.getSelectedDeathIndex();

        this.addDrawableChild(new TexturedButtonWidget(this.x + 76, this.y + 21, 18, 18, 0, 166, 18, TEXTURE, button -> {
            if (this.handler.getDeaths().size() > selectedTab) {
                this.handler.getDeaths().get(selectedTab).teleport();
            }
        }));

        this.addDrawableChild(new TexturedButtonWidget(this.x + 76, this.y + 41, 18, 18, 18, 166, 18, TEXTURE, button -> {
            if (this.handler.getDeaths().size() > selectedTab) {
                Death selectedDeath = this.handler.getDeaths().get(selectedTab);
                this.handler.setInventory(selectedDeath.transferInventory(this.player));
                this.removeDummyPlayer(selectedDeath);
            }
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
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int countOfDeaths = this.handler.getDeaths().size();
        int selectedTab = this.handler.getSelectedDeathIndex();

        for (int i = 0; i < this.handler.getDeaths().size(); i++) {
            if (i == selectedTab) continue;
            this.renderTabIcon(context, i);
        }

        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (selectedTab < countOfDeaths) {
            Death selectedDeath = this.handler.getDeaths().get(selectedTab);
            this.renderTabIcon(context, selectedTab);

            InventoryScreen.drawEntity(context, this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.getOrCreateDummyPlayer(selectedDeath));
        }

        // this should be done in the foreground, but then it stops working
        for (int i = 0; i < countOfDeaths; i++) {
            if (this.isMouseInTab(i, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, Text.literal(this.handler.getDeaths().get(i).getCauseOfDeath()), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        MultiLineDrawContext multiLineContext = new MultiLineDrawContext(context, this.titleX, this.titleY, 2);
        multiLineContext.drawLine(this.textRenderer, this.title, TextUtils.TITLE_COLOR, false);

        int selectedTab = this.handler.getSelectedDeathIndex();

        if (selectedTab < this.handler.getDeaths().size()) {
            Death selectedDeath = this.handler.getDeaths().get(selectedTab);
            multiLineContext.drawLine(this.textRenderer, TimeUtils.timeDescription(selectedDeath.getTimestamp()), TextUtils.TEXT_COLOR, false);
            multiLineContext.drawLine(this.textRenderer, TextUtils.getIdentifierName(selectedDeath.getDimension()), TextUtils.TEXT_COLOR, false);

            Vec3d pos = selectedDeath.getPos();

            multiLineContext.drawLineWithTitle(this.textRenderer, "X: ", String.valueOf((int) pos.x), TextUtils.TITLE_COLOR, TextUtils.TEXT_COLOR, false);
            multiLineContext.drawLineWithTitle(this.textRenderer, "Y: ", String.valueOf((int) pos.y), TextUtils.TITLE_COLOR, TextUtils.TEXT_COLOR, false);
            multiLineContext.drawLineWithTitle(this.textRenderer, "Z: ", String.valueOf((int) pos.z), TextUtils.TITLE_COLOR, TextUtils.TEXT_COLOR, false);

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < this.handler.getDeaths().size(); i++) {
            if (isMouseInTab(i, mouseX, mouseY)) {
                this.handler.setSelectedDeathIndex(i);
                this.handler.setInventory(this.handler.getDeaths().get(i).getInventory());

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private DummyPlayerEntity getOrCreateDummyPlayer(Death death) {
        DummyPlayerEntity dummy = this.dummyPlayers.get(death.getUuid());
        if (dummy != null) return dummy;
        dummy = DummyPlayerEntity.fromDeath(death, this.player.getWorld());
        this.dummyPlayers.put(death.getUuid(), dummy);
        return dummy;
    }

    private void removeDummyPlayer(Death death) {
        this.dummyPlayers.remove(death.getUuid());
    }

    private Vec2i getTabPosition(int index) {
        return new Vec2i(this.x + this.backgroundWidth, this.y + index * (TAB_HEIGHT + 1));
    }

    private int getNumberOfMaxVisibleTabs() {
        return (this.backgroundHeight / (TAB_HEIGHT + 1)) - 1;
    }

    private void renderTabIcon(DrawContext context, int index) {
        if(index > this.getNumberOfMaxVisibleTabs()) return;

        Vec2i pos = this.getTabPosition(index);
        Death death = this.handler.getDeaths().get(index);
        boolean selected = index == this.handler.getSelectedDeathIndex();

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
