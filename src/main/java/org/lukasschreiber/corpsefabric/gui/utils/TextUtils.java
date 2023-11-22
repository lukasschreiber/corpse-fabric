package org.lukasschreiber.corpsefabric.gui.utils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtils {
    public static final int TITLE_COLOR = 4210752;
    public static final int TEXT_COLOR = 8158332;

    public static Text getIdentifierName(Identifier identifier) {
        return Text.literal(Arrays.stream(identifier.getPath().split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" ")));
    }

}
