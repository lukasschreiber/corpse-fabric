package org.lukasschreiber.corpsefabric.net;

import net.minecraft.util.Identifier;

public class NetworkingConstants {
    public final static Identifier ALL_DEATHS = new Identifier("corpse", "all_identifiers");
    public final static Identifier PLAYER_DIED = new Identifier("corpse", "player_died");
    public static final Identifier REQUEST_ALL_DEATHS = new Identifier("corpse", "request_all_deaths");
}
