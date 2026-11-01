package odin.server;

import java.util.LinkedList;
import tacos.server.map.TacosPortal;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.List;

/**
 * Note for this class : TacosCharacter reference must be removed immediately
 * after cpq or upon dc.
 *
 * @author Rob
 */
public class MapleCarnivalParty {

    private final List<Integer> members = new LinkedList<>();
    private final TacosCharacter leader;
    private final byte team;
    private int availableCP = 0;
    private int totalCP = 0;
    private final boolean winner = false;

    public MapleCarnivalParty(TacosCharacter owner, List<TacosCharacter> members1, byte team1) {
        leader = owner;
        for (TacosCharacter mem : members1) {
            members.add(mem.getId());
            mem.setCarnivalParty(this);
        }
        team = team1;
    }

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
    public TacosCharacter getLeader() {
        return leader;
    }

    // used by script
    public int getTotalCP() {
        return totalCP;
    }

    // used by script
    public List<Integer> getMembers() {
        return members;
    }

    // used by script
    public int getTeam() {
        return team;
    }

    // used by script
    public void warp(TacosMap map, String portalname) {
        warp(map, map.getPortal(portalname));
    }

    // used by script
    public void warp(TacosMap map, int portalid) {
        warp(map, map.getPortal(portalid));
    }

    private void warp(TacosMap map, TacosPortal portal) {
        for (int character_id : members) {
            TacosCharacter player = getLeader().getChannelServer().getOnlinePlayers().findById(character_id);
            if (player != null) {
                player.changeMapPortal(map, portal);
            }
        }
    }

    // used by script
    public void removeMember(TacosCharacter chr) {
        if (members.removeIf(id -> id == chr.getId())) {
            chr.setCarnivalParty(null);
        }
    }

    // used by script
    public boolean isWinner() {
        return winner;
    }

}
