package org.lukasschreiber.corpsefabric.net;

import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;

public class NetworkingConstants {
    public final static Identifier PLAYER_DIED = new Identifier(Corpse.NAMESPACE, "player_died");
    public static final Identifier OPEN_DEATH_HISTORY = new Identifier(Corpse.NAMESPACE, "open_death_history");

}

