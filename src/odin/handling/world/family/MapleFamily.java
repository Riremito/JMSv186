/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.handling.world.family;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import odin.client.MapleCharacter;
import tacos.database.query.DQ_Characters;
import tacos.database.query.DQ_Families;
import odin.handling.world.OdinWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import tacos.packet.ServerPacket;
import tacos.packet.response.ResCWvsContext;

public class MapleFamily {

    public static enum FCOp {

        NONE, DISBAND;
    }
    //does not need to be in order :) CID -> MFC
    private final Map<Integer, MapleFamilyCharacter> members = new ConcurrentHashMap<Integer, MapleFamilyCharacter>();
    private String leadername = null, notice;
    private int id, leaderid, generations = 0;
    private boolean proper = true, bDirty = false;

    public MapleFamily(final int fid) {
        super();

        DQ_Families.FamilyRow row = DQ_Families.load(fid);
        if (row == null) {
            id = -1;
            return;
        }

        id = fid;
        leaderid = row.leaderid;
        notice = row.notice;

        //does not need to be in any order
        for (DQ_Characters.FamilyMemberRow m : DQ_Characters.getFamilyMembers(fid)) {
            if (m.id == leaderid) {
                leadername = m.name;
            }
            members.put(m.id, new MapleFamilyCharacter(m.id, m.level, m.name, (byte) -1, m.job, fid, m.seniorId, m.junior1, m.junior2, m.currentRep, m.totalRep, false));
        }

        if (leadername == null || members.size() < 2) {
            System.err.println("Leader " + leaderid + " isn't in family " + id + ".  Impossible... family is disbanding.");
            writeToDB(true);
            proper = false;
            return;
        }
        resetPedigree();
        resetDescendants(); //set
        resetGens(); //set
    }

    public int getGens() {
        return generations;
    }

    public void resetPedigree() {
        for (MapleFamilyCharacter mfc : members.values()) {
            mfc.resetPedigree(this);
        }
    }

    public void resetGens() {
        MapleFamilyCharacter mfc = getMFC(leaderid);
        if (mfc != null) {
            generations = mfc.resetGenerations(this);
        }
    }

    public void resetDescendants() { //not stored here, but rather in the MFC
        MapleFamilyCharacter mfc = getMFC(leaderid);
        if (mfc != null) {
            mfc.resetDescendants(this);
        }
    }

    public boolean isProper() {
        return proper;
    }

    public static final Collection<MapleFamily> loadAll() {
        final Collection<MapleFamily> ret = new ArrayList<MapleFamily>();
        MapleFamily g;
        for (int familyid : DQ_Families.getAllFamilyIds()) {
            g = new MapleFamily(familyid);
            if (g.getId() > 0) {
                ret.add(g);
            }
        }
        return ret;
    }

    public final void writeToDB(final boolean bDisband) {
        if (!bDisband) {
            DQ_Families.update(id, notice, leaderid);
        } else {
            //members is less than 2, this shall be executed
            if (leadername == null || members.size() < 2) {
                DQ_Characters.resetFamilyForMembers(id);
                broadcast(null, -1, FCOp.DISBAND, null);
            }

            DQ_Families.delete(id);
        }
    }

    public final int getId() {
        return id;
    }

    public final int getLeaderId() {
        return leaderid;
    }

    public String getNotice() {
        if (notice == null) {
            return "";
        }
        return notice;
    }

    public String getLeaderName() {
        return leadername;
    }

    public void broadcast(ServerPacket packet, List<Integer> cids) {
        broadcast(packet, -1, FCOp.NONE, cids);
    }

    public void broadcast(ServerPacket packet, final int exception, List<Integer> cids) {
        broadcast(packet, exception, FCOp.NONE, cids);
    }

    public void broadcast(ServerPacket packet, final int exceptionId, final FCOp bcop, List<Integer> cids) {
        //passing null to cids will ensure all
        buildNotifications();
        for (MapleFamilyCharacter mgc : members.values()) {
            if (cids == null || cids.contains(mgc.getId())) {
                if (bcop == FCOp.DISBAND) {
                    if (mgc.isOnline()) {
                        OdinWorld.Family.setFamily(0, 0, 0, 0, mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                    } else {
                        setOfflineFamilyStatus(0, 0, 0, 0, mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                    }
                } else if (mgc.isOnline() && mgc.getId() != exceptionId) {
                    OdinWorld.Broadcast.sendFamilyPacket(mgc.getId(), packet, exceptionId, id);
                }
            }
        }

    }

    private final void buildNotifications() {
        if (!bDirty) {
            return;
        }
        final Iterator<Entry<Integer, MapleFamilyCharacter>> toRemove = members.entrySet().iterator();
        while (toRemove.hasNext()) {
            if (toRemove.next().getValue().getFamilyId() != id) {
                toRemove.remove();
                continue;
            }
        }
        bDirty = false;
    }

    public final void setOnline(final int cid, final boolean online, final int channel) {
        final MapleFamilyCharacter mgc = getMFC(cid);
        if (mgc != null && mgc.getFamilyId() == id) {
            if (mgc.isOnline() != online) {
                broadcast(ResCWvsContext.familyLoggedIn(online, mgc.getName()), cid, mgc.getId() == leaderid ? null : mgc.getPedigree());
            }
            mgc.setOnline(online);
            mgc.setChannel((byte) channel);
        }
        bDirty = true; // member formation has changed, update notifications
    }

    public final int setRep(final int cid, int addrep, final int oldLevel) {
        final MapleFamilyCharacter mgc = getMFC(cid);
        if (mgc != null && mgc.getFamilyId() == id) {
            if (oldLevel > mgc.getLevel()) {
                addrep /= 2; //:D
            }
            //mgc.setCurrentRep(mgc.getCurrentRep()+addrep);
            //mgc.setTotalRep(mgc.getTotalRep()+addrep);
            if (mgc.isOnline()) {
                List<Integer> dummy = new ArrayList<Integer>();
                dummy.add(mgc.getId());
                broadcast(ResCWvsContext.changeRep(addrep), -1, dummy);
                OdinWorld.Family.setFamily(id, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep() + addrep, mgc.getTotalRep() + addrep, mgc.getId());
            } else {
                setOfflineFamilyStatus(id, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep() + addrep, mgc.getTotalRep() + addrep, mgc.getId());
            }
            return mgc.getSeniorId();
        }
        return 0;
    }

    public final MapleFamilyCharacter addFamilyMemberInfo(final MapleCharacter mc, final int seniorid, final int junior1, final int junior2) {
        final MapleFamilyCharacter ret = new MapleFamilyCharacter(mc, id, seniorid, junior1, junior2);
        members.put(mc.getId(), ret);
        ret.resetPedigree(this);
        bDirty = true;
        for (int i : ret.getPedigree()) {
            getMFC(i).resetPedigree(this);
        }
        return ret;
    }

    public final int addFamilyMember(final MapleFamilyCharacter mgc) {
        members.put(mgc.getId(), mgc);
        mgc.resetPedigree(this);
        bDirty = true;
        for (int i : mgc.getPedigree()) {
            getMFC(i).resetPedigree(this);
        }
        return 1;
    }

    public final void leaveFamily(final int id) {
        leaveFamily(getMFC(id), true);
    }

    public final void leaveFamily(final MapleFamilyCharacter mgc, final boolean skipLeader) {
        bDirty = true;
        if (mgc.getId() == leaderid && !skipLeader) {
            //disband
            leadername = null; //to disband family completely
            OdinWorld.Family.disbandFamily(id);
        } else {
            //we also have to update anyone below us
            if (mgc.getJunior1() > 0) {
                splitFamily(mgc.getJunior1()); //junior1 makes his own family
            }
            if (mgc.getJunior2() > 0) {
                splitFamily(mgc.getJunior2()); //junior2 makes his own family
            }
            List<Integer> dummy = new ArrayList<Integer>();
            dummy.add(mgc.getId());
            broadcast(null, -1, FCOp.DISBAND, dummy);
            resetPedigree(); //ex but eh
        }
        members.remove(mgc.getId());
    }

    public final void setNotice(final String notice) {
        this.notice = notice;
    }

    public final void memberLevelJobUpdate(final MapleCharacter mgc) {
        final MapleFamilyCharacter member = getMFC(mgc.getId());
        if (member != null) {
            int old_level = member.getLevel();
            int old_job = member.getJobId();
            member.setJobId(mgc.getJob());
            member.setLevel((short) mgc.getLevel());
            if (old_level != mgc.getLevel()) {
                this.broadcast(ResCWvsContext.NotifyLevelUp(true, mgc.getLevel(), mgc.getName()), mgc.getId(), mgc.getId() == leaderid ? null : member.getPedigree());
            }
            if (old_job != mgc.getJob()) {
                this.broadcast(ResCWvsContext.NotifyJobChange(true, mgc.getJob(), mgc.getName()), mgc.getId(), mgc.getId() == leaderid ? null : member.getPedigree());
            }
        }
    }

    public final void disbandFamily() {
        writeToDB(true);
    }

    public final MapleFamilyCharacter getMFC(final int cid) {
        return members.get(cid);
    }

    public int getMemberSize() {
        return members.size();
    }

    public static void setOfflineFamilyStatus(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
        DQ_Characters.setOfflineFamilyStatus(cid, familyid, seniorid, junior1, junior2, currentrep, totalrep);
    }

    public static int createFamily(int leaderId) {
        return DQ_Families.create(leaderId);
    }

    public static void mergeFamily(MapleFamily newfam, MapleFamily oldfam) {
        //happens when someone in newfam juniors LEADER in oldfam
        //update all the members.
        for (MapleFamilyCharacter mgc : oldfam.members.values()) {
            mgc.setFamilyId(newfam.getId());
            if (mgc.isOnline()) {
                OdinWorld.Family.setFamily(newfam.getId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
            } else {
                setOfflineFamilyStatus(newfam.getId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
            }
            newfam.members.put(mgc.getId(), mgc); //reset pedigree after
            newfam.setOnline(mgc.getId(), mgc.isOnline(), mgc.getChannel());
        }
        newfam.resetPedigree();
        //do not reset characters, so leadername is fine
        OdinWorld.Family.disbandFamily(oldfam.getId()); //and remove it
    }

    //return disbanded or not.
    public boolean splitFamily(int splitId) {
        //toSplit = initiator who either broke off with their junior/senior, splitId is the ID of the one broken off
        //if it's junior, splitId will be the new leaderID, if its senior it's toSplit thats the new leader
        //happens when someone in fam breaks off with anyone else, either junior/senior
        //update all the members.
        final MapleFamilyCharacter leader = getMFC(splitId);
        try {
            List<MapleFamilyCharacter> all = leader.getAllJuniors(this); //leader is included in this collection
            if (all.size() <= 1) { //but if leader is the only person, then we're done
                leaveFamily(leader, false);
                return true;
            }
            final int newId = createFamily(leader.getId());
            if (newId <= 0) {
                return false;
            }
            for (MapleFamilyCharacter mgc : all) {
                // need it for sql
                mgc.setFamilyId(newId);
                setOfflineFamilyStatus(newId, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                members.remove(mgc.getId()); //clean remove
            }
            final MapleFamily newfam = OdinWorld.Family.getFamily(newId);
            for (MapleFamilyCharacter mgc : all) {
                if (mgc.isOnline()) { //NOW we change the char info
                    OdinWorld.Family.setFamily(newId, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                }
                newfam.setOnline(mgc.getId(), mgc.isOnline(), mgc.getChannel());
            }
        } finally {
            if (members.size() <= 1) { //only one person is left :|
                OdinWorld.Family.disbandFamily(id); //disband us.
                return true;
            }
        }
        bDirty = true;
        return false;
    }
}
