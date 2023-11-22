package org.lukasschreiber.corpsefabric.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.lukasschreiber.corpsefabric.Corpse;
import org.lukasschreiber.corpsefabric.Logger;
import org.lukasschreiber.corpsefabric.gui.HistoryScreen;


public class JeiPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier(Corpse.NAMESPACE, "jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(HistoryScreen.class, JeiHistoryScreenProperties::new);
    }
}
