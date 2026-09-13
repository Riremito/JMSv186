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

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.handling.world.OdinWorld;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import odin.handling.world.family.MapleFamilyCharacter;
import tacos.packet.response.ResCWvsContext;
import tacos.database.query.DQ_Notes;
import tacos.packet.ClientPacket;
import tacos.wz.opt.FieldOpt;

public class FamilyHandler {

    public static final void RequestFamily(ClientPacket cp, TacosClient client) {
        MapleCharacter chr = client.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (chr != null) {
            client.SendPacket(ResCWvsContext.getFamilyPedigree(chr));
        }
    }

    public static final void OpenFamily(ClientPacket cp, TacosClient client) {
        client.SendPacket(ResCWvsContext.getFamilyInfo(client.getPlayer()));
    }

    public static final void UseFamily(ClientPacket cp, TacosClient client) {
        int type = cp.Decode4();
        MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
        if (entry == null) {
            return;
        }
        boolean success = client.getPlayer().getFamilyId() > 0 && client.getPlayer().canUseFamilyBuff(entry) && client.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (type) {
            case 0: //teleport: need add check for if not a safe place
                victim = client.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
                if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(client.getPlayer().getMap().getFieldLimit()) || !client.getPlayer().isAlive()) {
                    client.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                    success = false;
                } else if (victim == null || (victim.isGM() && !client.getPlayer().isGM())) {
                    client.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
                    success = false;
                } else if (victim.getFamilyId() == client.getPlayer().getFamilyId() && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(victim.getMap().getFieldLimit()) && victim.getId() != client.getPlayer().getId()) {
                    client.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                } else {
                    client.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                    success = false;
                }
                break;
            case 1: // TODO give a check to the player being forced somewhere else..
                victim = client.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
                if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(client.getPlayer().getMap().getFieldLimit()) || !client.getPlayer().isAlive()) {
                    client.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                } else if (victim == null || (victim.isGM() && !client.getPlayer().isGM())) {
                    client.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
                } else if (victim.getTeleportName().length() > 0) {
                    client.getPlayer().dropMessage(1, "Another character has requested to summon this character. Please try again later.");
                } else if (victim.getFamilyId() == client.getPlayer().getFamilyId() && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(victim.getMap().getFieldLimit()) && victim.getId() != client.getPlayer().getId()) {
                    victim.SendPacket(ResCWvsContext.familySummonRequest(client.getPlayer().getName(), "MAP_NAME"));
                    victim.setTeleportName(client.getPlayer().getName());
                } else {
                    client.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                }
                return; //RETURN not break
            case 4: // 6 family members in pedigree online Drop Rate & Exp Rate + 100% 30 minutes
                break;

            case 2: // drop rate + 50% 15 min
            case 3: // exp rate + 50% 15 min
            case 5: // drop rate + 100% 15 min
            case 6: // exp rate + 100% 15 min
            case 7: // drop rate + 100% 30 min
            case 8: // exp rate + 100% 30 min
                break;
            case 9: // drop rate + 100% party 30 min
            case 10: // exp rate + 100% party 30 min
                break;
        }
        if (success) { //again
            client.getPlayer().setCurrentRep(client.getPlayer().getCurrentRep() - entry.rep);
            client.SendPacket(ResCWvsContext.changeRep(-entry.rep));
            client.getPlayer().useFamilyBuff(entry);
        } else {
            client.getPlayer().dropMessage(5, "An error occured.");
        }
    }

    public static final void FamilyOperation(ClientPacket cp, TacosClient client) {
        if (client.getPlayer() == null) {
            return;
        }
        MapleCharacter addChr = client.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (addChr == null) {
            client.getPlayer().dropMessage(1, "The name you requested is incorrect or he/she is currently not logged in.");
        } else if (addChr.getFamilyId() == client.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            client.getPlayer().dropMessage(1, "You belong to the same family.");
        } else if (addChr.getMapId() != client.getPlayer().getMapId()) {
            client.getPlayer().dropMessage(1, "The one you wish to add as a junior must be in the same map.");
        } else if (addChr.getSeniorId() != 0) {
            client.getPlayer().dropMessage(1, "The character is already a junior of another character.");
        } else if (addChr.getLevel() >= client.getPlayer().getLevel()) {
            client.getPlayer().dropMessage(1, "The junior you wish to add must be at a lower rank.");
        } else if (addChr.getLevel() < client.getPlayer().getLevel() - 20) {
            client.getPlayer().dropMessage(1, "The gap between you and your junior must be within 20 levels.");
            //} else if (c.getPlayer().getFamilyId() != 0 && c.getPlayer().getFamily().getGens() >= 1000) {
            //	c.getPlayer().dropMessage(5, "Your family cannot extend more than 1000 generations from above and below.");
        } else if (addChr.getLevel() < 10) {
            client.getPlayer().dropMessage(1, "The junior you wish to add must be over Level 10.");
        } else if (client.getPlayer().getJunior1() > 0 && client.getPlayer().getJunior2() > 0) {
            client.getPlayer().dropMessage(1, "You have 2 juniors already.");
        } else if (client.getPlayer().isGM() || !addChr.isGM()) {
            addChr.SendPacket(ResCWvsContext.sendFamilyInvite(client.getPlayer().getId(), client.getPlayer().getLevel(), client.getPlayer().getJob(), client.getPlayer().getName()));
        }
        MapleCharacter chr = client.getPlayer();
        chr.updateStat();
    }

    public static final void FamilyPrecept(ClientPacket cp, TacosClient client) {
        MapleFamily fam = OdinWorld.Family.getFamily(client.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != client.getPlayer().getId()) {
            return;
        }
        fam.setNotice(cp.DecodeStr());
    }

    public static final void FamilySummon(ClientPacket cp, TacosClient client) {
        int TYPE = 1; //the type of the summon request.
        MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
        MapleCharacter tt = client.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
        if (client.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == client.getPlayer().getFamilyId() && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(tt.getMap().getFieldLimit())
                && !FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(client.getPlayer().getMap().getFieldLimit()) && client.getPlayer().isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost)
                && client.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep) {
            //whew lots of checks
            boolean accepted = cp.Decode1() > 0;
            if (accepted) {
                client.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.SendPacket(ResCWvsContext.changeRep(-cost.rep));
                tt.useFamilyBuff(cost);
            } else {
                tt.dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
            }
        } else {
            client.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
        }
        client.getPlayer().setTeleportName("");
    }

    public static final void DeleteJunior(ClientPacket cp, TacosClient client) {
        int juniorid = cp.Decode4();
        if (client.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (client.getPlayer().getJunior1() != juniorid && client.getPlayer().getJunior2() != juniorid)) {
            return;
        }
        //junior is not required to be online.
        final MapleFamily fam = OdinWorld.Family.getFamily(client.getPlayer().getFamilyId());
        final MapleFamilyCharacter other = fam.getMFC(juniorid);
        final MapleFamilyCharacter oth = client.getPlayer().getMFC();
        boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        client.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        //if (!other.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        //}
        DQ_Notes.sendNote(other.getName(), client.getPlayer().getName(), client.getPlayer().getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
        if (!fam.splitFamily(juniorid)) { //juniorid splits to make their own family. function should handle the rest
            if (!junior2) {
                fam.resetGens(); //just lost a generation
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        client.getPlayer().dropMessage(1, "Broke up with (" + other.getName() + ").\r\nFamily relationship has ended.");
        MapleCharacter chr = client.getPlayer();
        chr.updateStat();
    }

    public static final void DeleteSenior(ClientPacket cp, TacosClient client) {
        if (client.getPlayer().getFamilyId() <= 0 || client.getPlayer().getSeniorId() <= 0) {
            return;
        }
        //not required to be online
        final MapleFamily fam = OdinWorld.Family.getFamily(client.getPlayer().getFamilyId()); //this is old family
        final MapleFamilyCharacter mgc = fam.getMFC(client.getPlayer().getSeniorId());
        final MapleFamilyCharacter mgc_ = client.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        boolean junior2 = mgc.getJunior2() == client.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }
        //if (!mgc.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        //}
        client.getPlayer().saveFamilyStatus();
        DQ_Notes.sendNote(mgc.getName(), client.getPlayer().getName(), client.getPlayer().getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
        if (!fam.splitFamily(client.getPlayer().getId())) { //now, we're the family leader
            if (!junior2) {
                fam.resetGens(); //just lost a generation
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        client.getPlayer().dropMessage(1, "Broke up with (" + mgc.getName() + ").\r\nFamily relationship has ended.");
        MapleCharacter chr = client.getPlayer();
        chr.updateStat();
    }

    public static final void AcceptFamily(ClientPacket cp, TacosClient client) {
        MapleCharacter inviter = client.getPlayer().getMap().getCharacterById(cp.Decode4());
        if (inviter != null && client.getPlayer().getSeniorId() == 0
                && inviter.getLevel() - 20 < client.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(cp.DecodeStr()) && inviter.getNoJuniors() < 2
                /*&& inviter.getFamily().getGens() < 1000*/ && client.getPlayer().getLevel() >= 10) {
            boolean accepted = cp.Decode1() > 0;
            inviter.SendPacket(ResCWvsContext.sendFamilyJoinResponse(accepted, client.getPlayer().getName()));
            if (accepted) {
                client.SendPacket(ResCWvsContext.getSeniorMessage(inviter.getName()));
                MapleFamilyCharacter old = client.getPlayer().getMFC();
                if (inviter.getFamilyId() != 0) {

                    MapleFamily fam = OdinWorld.Family.getFamily(inviter.getFamilyId());
                    //if old isn't null, don't set the familyid yet, mergeFamily will take care of it
                    client.getPlayer().setFamily(old == null ? inviter.getFamilyId() : old.getFamilyId(), inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
                    MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(client.getPlayer().getId());
                    } else {
                        mf.setJunior1(client.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old != null) { //has junior
                        MapleFamily.mergeFamily(fam, OdinWorld.Family.getFamily(old.getFamilyId()));
                    } else {
                        fam.addFamilyMember(client.getPlayer().getMFC());
                        fam.setOnline(client.getPlayer().getId(), true, client.getChannelId());
                        client.getPlayer().saveFamilyStatus();
                    }
                    if ((inviter.getNoJuniors() == 1 || old != null) && fam != null) {//just got their first junior whoopee
                        fam.resetGens();
                        fam.resetDescendants();
                    }
                    fam.resetPedigree(); //is this necessary?
                } else {
                    int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        //before loading the family, set sql
                        MapleFamily.setOfflineFamilyStatus(id, 0, client.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2(), client.getPlayer().getCurrentRep(), client.getPlayer().getTotalRep(), client.getPlayer().getId());
                        inviter.setFamily(id, 0, client.getPlayer().getId(), 0); //load the family
                        client.getPlayer().setFamily(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
                        MapleFamily fam = OdinWorld.Family.getFamily(id);
                        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannelId());
                        if (old != null) { //has junior
                            MapleFamily.mergeFamily(fam, OdinWorld.Family.getFamily(old.getFamilyId()));
                        } else {
                            fam.setOnline(client.getPlayer().getId(), true, client.getChannelId());
                        }
                        fam.resetGens();
                        fam.resetDescendants();
                        fam.resetPedigree();

                    }
                }
                client.SendPacket(ResCWvsContext.getFamilyInfo(client.getPlayer()));
            }
        }
    }
}
