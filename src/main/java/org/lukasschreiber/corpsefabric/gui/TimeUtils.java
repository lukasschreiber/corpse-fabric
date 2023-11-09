package org.lukasschreiber.corpsefabric.gui;

import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static Text timeDescription(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if(diff < 60 * 1000) {
            return Text.translatable("time.corpse.past.seconds", TimeUnit.MILLISECONDS.toSeconds(diff));
        } else if(diff < 60 * 60 * 1000) {
            return Text.translatable("time.corpse.past.minutes", TimeUnit.MILLISECONDS.toMinutes(diff));
        } else if(diff < 60 * 60 * 60 * 1000) {
            return Text.translatable("time.corpse.past.hours", TimeUnit.MILLISECONDS.toHours(diff));
        } else {
            return Text.translatable("time.corpse.past.long_time");
        }
    }
}
