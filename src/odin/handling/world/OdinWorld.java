package odin.handling.world;

import tacos.server.TacosWorld;

public class OdinWorld extends TacosWorld {

    public OdinWorld() {
    }

    //Touch everything...
    public static void init() {
        Guild.lock.toString();
        Alliance.lock.toString();
        Family.lock.toString();
        Party.getParty(0);
    }
}
