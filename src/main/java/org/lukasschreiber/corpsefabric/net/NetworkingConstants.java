package org.lukasschreiber.corpsefabric.net;

import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;

public class NetworkingConstants {
    public final static Identifier ALL_DEATHS = new Identifier(Corpse.NAMESPACE, "all_identifiers");
    public final static Identifier PLAYER_DIED = new Identifier(Corpse.NAMESPACE, "player_died");
    public static final Identifier REQUEST_ALL_DEATHS = new Identifier(Corpse.NAMESPACE, "request_all_deaths");
}
