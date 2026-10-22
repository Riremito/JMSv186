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
package odin.handling.channel.handler;

import tacos.client.TacosClient;
import tacos.client.TacosCharacter;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import odin.handling.world.family.MapleFamilyCharacter;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsFamily;
import tacos.database.query.DQ_Notes;
import tacos.packet.ClientPacket;
import tacos.wz.opt.FieldOpt;
import tacos.wz.WzNameStorage;
import tacos.wz.WzName;

public class FamilyHandler {

    // CP_FamilyChartRequest
    public static void RequestFamily(ClientPacket cp, TacosCharacter chr) {
        TacosCharacter target = chr.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (target != null) {
            chr.SendPacket(ResCWvsContext.FamilyChartResult(target));
        }
    }

    // CP_FamilyInfoRequest
    public static void OpenFamily(ClientPacket cp, TacosCharacter chr) {
        chr.SendPacket(ResCWvsContext.FamilyInfoResult(chr));
    }

    // CP_FamilyUsePrivilege
    public static void UseFamily(ClientPacket cp, TacosCharacter chr) {
        int type = cp.Decode4(); // nPrivilegeIndex
        MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
        if (entry == null) {
            return;
        }
        if (chr.getFamilyId() <= 0 || !chr.canUseFamilyBuff(entry) || chr.getCurrentRep() < entry.rep) {
            return;
        }
        boolean success = true;
        switch (type) {
            case 0: { // SP_Jump : move to a family member
                success = teleportToFamily(chr, cp.DecodeStr());
                break;
            }
            case 1: { // SP_Summon : the rep is used when the member accepts
                requestFamilySummon(chr, cp.DecodeStr());
                return;
            }
            default: { // exp and drop rate
                break;
            }
        }
        if (!success) {
            chr.Notice("An error occured.");
            return;
        }
        chr.setCurrentRep(chr.getCurrentRep() - entry.rep);
        chr.SendPacket(ResCWvsContext.FamilyFamousPointIncResult(-entry.rep, chr.getName()));
        chr.useFamilyBuff(entry);
    }

    private static boolean canFamilyTeleport(TacosCharacter chr, TacosCharacter target) {
        return target.getFamilyId() == chr.getFamilyId() && target.getId() != chr.getId() && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(target.getMap().getFieldLimit());
    }

    private static boolean teleportToFamily(TacosCharacter chr, String name) {
        TacosCharacter target = chr.getChannelServer().getOnlinePlayers().findByName(name);
        if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(chr.getMap().getFieldLimit()) || !chr.isAlive()) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
            return false;
        }
        if (target == null || (target.isGM() && !chr.isGM())) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_WrongName));
            return false;
        }
        if (!canFamilyTeleport(chr, target)) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
            return false;
        }
        chr.changeMapPortal(target.getMap(), target.getMap().getPortal(0));
        return true;
    }

    private static void requestFamilySummon(TacosCharacter chr, String name) {
        TacosCharacter target = chr.getChannelServer().getOnlinePlayers().findByName(name);
        if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(chr.getMap().getFieldLimit()) || !chr.isAlive()) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
        } else if (target == null || (target.isGM() && !chr.isGM())) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_WrongName));
        } else if (!target.getTeleportName().isEmpty()) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_AlreadySummon));
        } else if (canFamilyTeleport(chr, target)) {
            target.SendPacket(ResCWvsContext.FamilySummonRequest(chr.getName(), getMapName(chr.getMapId())));
            target.setTeleportName(chr.getName());
        } else {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
        }
    }

    private static String getMapName(int map_id) {
        WzName name = WzNameStorage.MAP.get(map_id);
        return (name != null) ? name.getMapName() : "";
    }

    public static void FamilyOperation(ClientPacket cp, TacosCharacter chr) {
        if (chr == null) {
            return;
        }
        TacosCharacter addChr = chr.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (addChr == null) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_WrongName));
        } else if (addChr.getFamilyId() == chr.getFamilyId() && addChr.getFamilyId() > 0) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_SameFamily));
        } else if (addChr.getMapId() != chr.getMapId()) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_NotSameMap));
        } else if (addChr.getSeniorId() != 0) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_AleadyHasParent));
        } else if (addChr.getLevel() >= chr.getLevel()) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_OverLevel));
        } else if (addChr.getLevel() < chr.getLevel() - 20) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_UnderLevel));
        } else if (addChr.getLevel() < 10) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_MinLevel));
        } else if (chr.getJunior1() > 0 && chr.getJunior2() > 0) {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_CanNotRegister));
        } else if (chr.isGM() || !addChr.isGM()) {
            addChr.SendPacket(ResCWvsContext.FamilyJoinRequest(chr.getId(), chr.getLevel(), chr.getJob(), chr.getName()));
        }
        chr.updateStat();
    }

    public static void FamilyPrecept(ClientPacket cp, TacosCharacter chr) {
        MapleFamily fam = chr.getWorld().getFamily().getFamily(chr.getFamilyId());
        if (fam == null || fam.getLeaderId() != chr.getId()) {
            return;
        }
        fam.setNotice(cp.DecodeStr());
    }

    public static void FamilySummon(ClientPacket cp, TacosCharacter chr) {
        int TYPE = 1; //the type of the summon request.
        MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
        TacosCharacter tt = chr.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (chr.getFamilyId() > 0 && tt != null && tt.getFamilyId() == chr.getFamilyId() && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(tt.getMap().getFieldLimit())
                && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(chr.getMap().getFieldLimit()) && chr.isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost)
                && chr.getTeleportName().equals(tt.getName()) && tt.getCurrentRep() >= cost.rep) {
            //whew lots of checks
            boolean accepted = cp.Decode1() > 0;
            if (accepted) {
                chr.changeMapPortal(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.SendPacket(ResCWvsContext.FamilyFamousPointIncResult(-cost.rep, tt.getName()));
                tt.useFamilyBuff(cost);
            } else {
                tt.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
            }
        } else {
            chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Fail_Summon));
        }
        chr.setTeleportName("");
    }

    public static void DeleteJunior(ClientPacket cp, TacosCharacter chr) {
        int juniorid = cp.Decode4();
        if (chr.getFamilyId() <= 0 || juniorid <= 0 || (chr.getJunior1() != juniorid && chr.getJunior2() != juniorid)) {
            return;
        }
        //junior is not required to be online.
        MapleFamily fam = chr.getWorld().getFamily().getFamily(chr.getFamilyId());
        MapleFamilyCharacter other = fam.getMFC(juniorid);
        MapleFamilyCharacter oth = chr.getMFC();
        boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        chr.saveFamilyStatus();
        other.setSeniorId(0);
        //if (!other.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        //}
        DQ_Notes.sendNote(other.getName(), chr.getName(), chr.getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
        if (!fam.splitFamily(juniorid)) { //juniorid splits to make their own family. function should handle the rest
            if (!junior2) {
                fam.resetGens(); //just lost a generation
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Success_Unregister));
        chr.updateStat();
    }

    public static void DeleteSenior(ClientPacket cp, TacosCharacter chr) {
        if (chr.getFamilyId() <= 0 || chr.getSeniorId() <= 0) {
            return;
        }
        //not required to be online
        MapleFamily fam = chr.getWorld().getFamily().getFamily(chr.getFamilyId()); //this is old family
        MapleFamilyCharacter mgc = fam.getMFC(chr.getSeniorId());
        MapleFamilyCharacter mgc_ = chr.getMFC();
        mgc_.setSeniorId(0);
        boolean junior2 = mgc.getJunior2() == chr.getId();
        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }
        //if (!mgc.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        //}
        chr.saveFamilyStatus();
        DQ_Notes.sendNote(mgc.getName(), chr.getName(), chr.getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
        if (!fam.splitFamily(chr.getId())) { //now, we're the family leader
            if (!junior2) {
                fam.resetGens(); //just lost a generation
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        chr.SendPacket(ResCWvsContext.FamilyResult(OpsFamily.FamilyRes_Success_Unregister));
        chr.updateStat();
    }

    public static void AcceptFamily(ClientPacket cp, TacosCharacter chr) {
        TacosCharacter inviter = chr.getMap().getPlayerById(cp.Decode4());
        // note : the name is decoded only when the preceding conditions are true.
        if (!(inviter != null && chr.getSeniorId() == 0
                && inviter.getLevel() - 20 < chr.getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(cp.DecodeStr()) && inviter.getNoJuniors() < 2
                /*&& inviter.getFamily().getGens() < 1000*/ && chr.getLevel() >= 10)) {
            return;
        }
        boolean accepted = cp.Decode1() > 0;
        inviter.SendPacket(ResCWvsContext.FamilyJoinRequestResult(accepted, chr.getName()));
        if (!accepted) {
            return;
        }
        chr.SendPacket(ResCWvsContext.FamilyJoinAccepted(inviter.getName()));
        MapleFamilyCharacter old = chr.getMFC();
        if (inviter.getFamilyId() != 0) {
            joinFamily(chr.getClient(), chr, inviter, old);
        } else {
            createFamily(chr.getClient(), chr, inviter, old);
        }
        chr.SendPacket(ResCWvsContext.FamilyInfoResult(chr));
    }

    // join the family of the inviter.
    private static void joinFamily(TacosClient client, TacosCharacter chr, TacosCharacter inviter, MapleFamilyCharacter old) {
        MapleFamily fam = client.getWorld().getFamily().getFamily(inviter.getFamilyId());
        //if old isn't null, don't set the familyid yet, mergeFamily will take care of it
        chr.setFamily(old == null ? inviter.getFamilyId() : old.getFamilyId(), inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
        MapleFamilyCharacter mf = inviter.getMFC();
        if (mf.getJunior1() > 0) {
            mf.setJunior2(chr.getId());
        } else {
            mf.setJunior1(chr.getId());
        }
        inviter.saveFamilyStatus();
        if (old != null) { //has junior
            MapleFamily.mergeFamily(fam, client.getWorld().getFamily().getFamily(old.getFamilyId()));
        } else {
            fam.addFamilyMember(chr.getMFC());
            fam.setOnline(chr.getId(), true, client.getChannelId());
            chr.saveFamilyStatus();
        }
        if (inviter.getNoJuniors() == 1 || old != null) { //just got their first junior whoopee
            fam.resetGens();
            fam.resetDescendants();
        }
        fam.resetPedigree(); //is this necessary?
    }

    // create a new family with the inviter as the leader.
    private static void createFamily(TacosClient client, TacosCharacter chr, TacosCharacter inviter, MapleFamilyCharacter old) {
        int id = MapleFamily.createFamily(inviter.getId());
        if (id <= 0) {
            return;
        }
        //before loading the family, set sql
        MapleFamily.setOfflineFamilyStatus(id, 0, chr.getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2(), chr.getCurrentRep(), chr.getTotalRep(), chr.getId());
        inviter.setFamily(id, 0, chr.getId(), 0); //load the family
        chr.setFamily(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
        MapleFamily fam = client.getWorld().getFamily().getFamily(id);
        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannelId());
        if (old != null) { //has junior
            MapleFamily.mergeFamily(fam, client.getWorld().getFamily().getFamily(old.getFamilyId()));
        } else {
            fam.setOnline(chr.getId(), true, client.getChannelId());
        }
        fam.resetGens();
        fam.resetDescendants();
        fam.resetPedigree();
    }
}
