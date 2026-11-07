package odin.server;

import tacos.client.TacosCharacter;

/**
 * Note for this class : TacosCharacter reference must be removed immediately
 * after cpq or upon dc.
 *
 * @author Rob
 */
public class MapleCarnivalParty {

    private byte team;
    private int availableCP = 0;
    private int totalCP = 0;

    public void addCP(TacosCharacter player, int amount) {
        totalCP += amount;
        availableCP += amount;
        player.addCP(amount);
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void useCP(TacosCharacter player, int amount) {
        availableCP -= amount;
        player.useCP(amount);
    }

    // used by script
    public int getTotalCP() {
        return totalCP;
    }

    // used by script
    public int getTeam() {
        return team;
    }
}
