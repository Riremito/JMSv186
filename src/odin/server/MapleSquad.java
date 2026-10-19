package odin.server;

import java.util.LinkedList;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import tacos.packet.response.ResCField;
import odin.server.Timer.CloneTimer;
import tacos.server.TacosWorld;

public class MapleSquad {

    private TacosCharacter leader;
    private final String leaderName;
    private Map<String, String> members = new LinkedHashMap<>();
    private Map<String, String> bannedMembers = new LinkedHashMap<>();
    private final String type;
    private final int ch;
    private final int expiration;
    private final int beginMapId;
    private final long startTime;
    private byte status = 0;
    private ScheduledFuture<?> removal;

    public MapleSquad(int ch, String type, TacosCharacter leader, int expiration) {
        this.leader = leader;
        this.members.put(leader.getName(), MapleCarnivalChallenge.getJobBasicNameById(leader.getJob()));
        this.leaderName = leader.getName();
        this.ch = ch;
        this.type = type;
        this.status = 1;
        this.beginMapId = leader.getMapId();
        leader.getMap().setSquad(type);
        this.startTime = System.currentTimeMillis();
        this.expiration = expiration;
        scheduleRemoval(expiration);
    }

    public TacosMap getBeginMap() {
        return this.leader.findMap(beginMapId);
    }

    public TacosCharacter getChar(String name) {
        return this.leader.getChannelServer().getOnlinePlayers().findByName(name);
    }

    private void scheduleRemoval(int time) {
        removal = CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (status != 0 && leader != null && (getLeader() == null || status == 1)) { //leader itself = null means we're already cleared
                    clear();
                }
            }
        }, time);
    }

    public String getLeaderName() {
        return leaderName;
    }

    public List<String> getBannedMembers() {
        return new LinkedList<>(bannedMembers.keySet());
    }

    public boolean isBanned(TacosCharacter member) {
        return bannedMembers.containsKey(member.getName());
    }

    public void acceptMember(int pos) {
        if (pos < 0 || pos >= bannedMembers.size()) {
            return;
        }
        List<String> membersAsList = getBannedMembers();
        String toadd = membersAsList.get(pos);
        if (toadd != null && getChar(toadd) != null) {
            members.put(toadd, bannedMembers.get(toadd));
            bannedMembers.remove(toadd);

            getChar(toadd).dropMessage(5, getLeaderName() + " has decided to add you back to the squad.");
        }
    }

    public void banMember(int pos) {
        if (pos <= 0 || pos >= members.size()) { //may not ban leader
            return;
        }
        List<String> membersAsList = getMembers();
        String toban = membersAsList.get(pos);
        if (toban != null && getChar(toban) != null) {
            bannedMembers.put(toban, members.get(toban));
            members.remove(toban);

            getChar(toban).dropMessage(5, getLeaderName() + " has removed you from the squad.");
        }
    }

    public void setStatus(byte status) {
        this.status = status;
        if (status == 2 && removal != null) {
            removal.cancel(false);
            removal = null;
        }
    }

    public int getStatus() {
        return status;
    }

    public int getBannedMemberSize() {
        return bannedMembers.size();
    }

    public String getSquadMemberString(byte type) {
        switch (type) {
            case 0: {
                StringBuilder sb = new StringBuilder("Squad members : ");
                sb.append("#b").append(members.size()).append(" #k ").append("List of participants : \n\r ");
                int i = 0;
                for (Entry<String, String> chr : members.entrySet()) {
                    i++;
                    sb.append(i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
                    if (chr.getKey().equals(getLeader().getName())) {
                        sb.append("(Leader of the squad)");
                    }
                    sb.append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 1: {
                StringBuilder sb = new StringBuilder("Squad members : ");
                sb.append("#b").append(members.size()).append(" #n ").append("List of participants : \n\r ");
                int i = 0, selection = 0;
                for (Entry<String, String> chr : members.entrySet()) {
                    i++;
                    sb.append("#b#L").append(selection).append("#");
                    selection++;
                    sb.append(i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
                    if (chr.getKey().equals(getLeader().getName())) {
                        sb.append("(Leader of the squad)");
                    }
                    sb.append("#l").append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 2: {
                StringBuilder sb = new StringBuilder("Squad members : ");
                sb.append("#b").append(members.size()).append(" #n ").append("List of participants : \n\r ");
                int i = 0, selection = 0;
                for (Entry<String, String> chr : bannedMembers.entrySet()) {
                    i++;
                    sb.append("#b#L").append(selection).append("#");
                    selection++;
                    sb.append(i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
                    if (chr.getKey().equals(getLeader().getName())) {
                        sb.append("(Leader of the squad)"); //WTF
                    }
                    sb.append("#l").append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 3: { //CWKPQ
                StringBuilder sb = new StringBuilder("Jobs : ");
                Map<String, Integer> jobs = getJobs();
                for (Entry<String, Integer> chr : jobs.entrySet()) {
                    sb.append("\r\n").append(chr.getKey()).append(" : ").append(chr.getValue());
                }
                return sb.toString();
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public Map<String, Integer> getJobs() {
        Map<String, Integer> jobs = new LinkedHashMap<>();
        for (Entry<String, String> chr : members.entrySet()) {
            if (jobs.containsKey(chr.getValue())) {
                jobs.put(chr.getValue(), jobs.get(chr.getValue()) + 1);
            } else {
                jobs.put(chr.getValue(), 1);
            }
        }
        return jobs;
    }

    // used by script
    public void clear() {
        if (removal != null) {
            getBeginMap().broadcastPacket(ResCField.DestroyClock());
            removal.cancel(false);
            removal = null;
        }
        members.clear();
        bannedMembers.clear();
        leader = null;
        TacosWorld.find(0).getChannelServer(ch).removeMapleSquad(type); // TODO : fix
        this.status = 0;

    }

    // used by script
    public long getTimeLeft() {
        return expiration - (System.currentTimeMillis() - startTime);
    }

    // used by script
    public TacosCharacter getLeader() {
        if (leader == null) {
            if (members.size() > 0 && getChar(leaderName) != null) {
                leader = getChar(leaderName);
            } else {
                if (status != 0) {
                    clear();
                }
                return null;
            }
        }
        return leader;
    }

    // used by script
    public List<String> getMembers() {
        return new LinkedList<>(members.keySet());
    }

    // used by script
    public int addMember(TacosCharacter member, boolean join) {
        String job = MapleCarnivalChallenge.getJobBasicNameById(member.getJob());
        if (join) {
            if (!members.containsKey(member.getName())) {
                if (members.size() <= 30) {
                    members.put(member.getName(), job);
                    getLeader().dropMessage(5, member.getName() + " (" + job + ") has joined the fight!");
                    return 1;
                }
                return 2;
            }
            return -1;
        }
        if (members.containsKey(member.getName())) {
            members.remove(member.getName());
            getLeader().dropMessage(5, member.getName() + " (" + job + ") have withdrawed from the fight.");
            return 1;
        }
        return -1;
    }

    // used by script
    public void removeMember(TacosCharacter chr) {
        if (members.containsKey(chr.getName())) {
            members.remove(chr.getName());
        }
    }

    // used by script
    public void removeMember(String chr) {
        if (members.containsKey(chr)) {
            members.remove(chr);
        }
    }

}
