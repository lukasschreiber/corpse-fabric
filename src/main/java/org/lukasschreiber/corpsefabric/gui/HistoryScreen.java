package org.lukasschreiber.corpsefabric.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.death.Death;
import org.lukasschreiber.corpsefabric.death.DeathManager;
import org.lukasschreiber.corpsefabric.net.NetworkingConstants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


@Environment(EnvType.CLIENT)
public class HistoryScreen extends Screen {
    ClientPlayerEntity player;
    List<Death> deaths;

    public HistoryScreen(ClientPlayerEntity player) {
        super(Text.literal("Death History"));
        this.player = player;

        ClientPlayNetworking.send(NetworkingConstants.REQUEST_ALL_DEATHS, PacketByteBufs.empty());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ALL_DEATHS, (client, handler, buf, responseSender) -> {
            NbtCompound response = buf.readNbt();
            if(response == null || !response.contains("deaths")) return;
            NbtList deathsCompounds = response.getList("deaths", NbtElement.COMPOUND_TYPE);
            deaths = deathsCompounds.stream().map(c -> Death.fromNbt((NbtCompound) c)).toList();
            Logger.INSTANCE.log(Level.INFO, "Downloaded Deaths, found "+deaths.size() + " deaths");
            Logger.INSTANCE.log(Level.INFO, deaths.stream().map(death -> death.getUuid().toString()).reduce((a, b) -> a + ", "+b));

            // does not update the text if deaths have been found...
            testText.setMessage(Text.literal(deaths.stream().map(death -> death.getUuid().toString()).reduce((a, b) -> a + ", "+b).orElse("Nothing")));

        });
    }

    public ButtonWidget button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> {
                System.out.println("You clicked button1!");
            })
            .dimensions(width / 2 - 205, 20, 200, 20)
            .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
            .build();
    public ButtonWidget button2 = ButtonWidget.builder(Text.literal("Button 2"), button -> {
                System.out.println("You clicked button2!");
            })
            .dimensions(width / 2 + 5, 20, 200, 20)
            .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
            .build();

    public TextWidget testText = new TextWidget(Text.literal("No Deaths"), MinecraftClient.getInstance().textRenderer);

    @Override
    protected void init() {
        button1.setPosition(width / 2 - 205, 20);
        button2.setPosition(width / 2 + 5, 20);

        addDrawableChild(button1);
        addDrawableChild(button2);
        addDrawableChild(testText);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, InventoryScreen.BACKGROUND_TEXTURE);

        int entitySize = 120;
        int entityHeight = entitySize / 2 - (int) (10 / player.getBoundingBox().getYLength());
        InventoryScreen.drawEntity(context,
                width / 2, height / 2 + entityHeight,
                entitySize,
                (float) (width / 2) - mouseX, (float) (height / 2 + entityHeight - 45) - mouseY,
                player);
    }
}
