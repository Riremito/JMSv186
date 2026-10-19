package odin.server;

import java.util.LinkedList;
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

    private List<Integer> members = new LinkedList<>();
    private TacosCharacter leader;
    private byte team;
    private int channel;
    private short availableCP = 0;
    private short totalCP = 0;
    private boolean winner = false;

    public MapleCarnivalParty(TacosCharacter owner, List<TacosCharacter> members1, byte team1) {
        leader = owner;
        for (TacosCharacter mem : members1) {
            members.add(mem.getId());
            mem.setCarnivalParty(this);
        }
        team = team1;
        channel = owner.getClient().getChannelId();
    }

    public void addCP(TacosCharacter player, int ammount) {
        totalCP += ammount;
        availableCP += ammount;
        player.addCP(ammount);
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void useCP(TacosCharacter player, int ammount) {
        availableCP -= ammount;
        player.useCP(ammount);
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
        for (int character_id : members) {
            TacosCharacter player = getLeader().getChannelServer().getOnlinePlayers().findById(character_id);
            if (player != null) {
                player.changeMapPortal(map, map.getPortal(portalname));
            }
        }
    }

    // used by script
    public void warp(TacosMap map, int portalid) {
        for (int character_id : members) {
            TacosCharacter player = getLeader().getChannelServer().getOnlinePlayers().findById(character_id);
            if (player != null) {
                player.changeMapPortal(map, map.getPortal(portalid));
            }
        }
    }

    // used by script
    public void removeMember(TacosCharacter chr) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i) == chr.getId()) {
                members.remove(i);
                chr.setCarnivalParty(null);
            }
        }

    }

    // used by script
    public boolean isWinner() {
        return winner;
    }

}
