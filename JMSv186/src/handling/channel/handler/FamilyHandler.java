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
package handling.channel.handler;

import client.MapleCharacterUtil;
import client.MapleCharacter;
import client.MapleClient;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import handling.world.family.MapleFamilyCharacter;
import java.util.List;
import server.maps.FieldLimitType;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.FamilyPacket;

public class FamilyHandler {

    public static final void RequestFamily(final SeekableLittleEndianAccessor slea, MapleClient c) {
	MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
	if (chr != null) {
	    c.getSession().write(FamilyPacket.getFamilyPedigree(chr));
	}
    }

    public static final void OpenFamily(final SeekableLittleEndianAccessor slea, MapleClient c) {
	c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }

    public static final void UseFamily(final SeekableLittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt();
	MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
	if (entry == null)
	    return;
        boolean success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
	if (!success)
	    return;
        MapleCharacter victim = null;
        switch (type) {
            case 0: //teleport: need add check for if not a safe place
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
		if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
			c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
			success = false;
		} else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
			c.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
			success = false;
		} else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    	c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                } else {
			c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                    	success = false;
		}
                break;
            case 1: // TODO give a check to the player being forced somewhere else..
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
		if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
			c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
		} else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
			c.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
		} else if (victim.getTeleportName().length() > 0) {
			c.getPlayer().dropMessage(1, "Another character has requested to summon this character. Please try again later.");
		} else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
			victim.getClient().getSession().write(FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
			victim.setTeleportName(c.getPlayer().getName());
		} else {
			c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
		}
		return; //RETURN not break
            case 4: // 6 family members in pedigree online Drop Rate & Exp Rate + 100% 30 minutes
		final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
		List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
        	if (chrs.size() < 7) {
		    success = false;
		} else { 
		    for (MapleFamilyCharacter chrz : chrs) {
			int chr = World.Find.findChannel(chrz.getId());
			if (chr == -1)
				continue; //STOP WTF?! take reps though..
			MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
			entry.applyTo(chrr);
			//chrr.getClient().getSession().write(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration*60000));
		    }
		}
                break;

            case 2: // drop rate + 50% 15 min
            case 3: // exp rate + 50% 15 min
            case 5: // drop rate + 100% 15 min
            case 6: // exp rate + 100% 15 min
            case 7: // drop rate + 100% 30 min
            case 8: // exp rate + 100% 30 min
		//c.getSession().write(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration*60000));
		entry.applyTo(c.getPlayer());
		break;
            case 9: // drop rate + 100% party 30 min
            case 10: // exp rate + 100% party 30 min
		entry.applyTo(c.getPlayer());
                //c.getSession().write(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration*60000));
		if (c.getPlayer().getParty() != null) {
			for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
				if (mpc.getId() != c.getPlayer().getId()) {
					MapleCharacter chr = c.getPlayer().getMap().getCharacterById(mpc.getId());
					if (chr != null) {
						entry.applyTo(chr);
						//chr.getClient().getSession().write(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration*60000));
					}
				}
			}
		}
                break;
        }
        if (success){ //again
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
	    c.getSession().write(FamilyPacket.changeRep(-entry.rep));
	    c.getPlayer().useFamilyBuff(entry);
        } else {
            c.getPlayer().dropMessage(5, "An error occured.");
	}
    }

    public static final void FamilyOperation(final SeekableLittleEndianAccessor slea, MapleClient c) {
	if (c.getPlayer() == null) {
	    return;
	}
        MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
	if (addChr == null) {
           	c.getPlayer().dropMessage(1, "The name you requested is incorrect or he/she is currently not logged in.");
	} else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
		c.getPlayer().dropMessage(1, "You belong to the same family.");
	} else if (addChr.getMapId() != c.getPlayer().getMapId()) {
		c.getPlayer().dropMessage(1, "The one you wish to add as a junior must be in the same map.");
	} else if (addChr.getSeniorId() != 0) {
		c.getPlayer().dropMessage(1, "The character is already a junior of another character.");
	} else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
		c.getPlayer().dropMessage(1, "The junior you wish to add must be at a lower rank.");
	} else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
		c.getPlayer().dropMessage(1, "The gap between you and your junior must be within 20 levels.");
	//} else if (c.getPlayer().getFamilyId() != 0 && c.getPlayer().getFamily().getGens() >= 1000) {
	//	c.getPlayer().dropMessage(5, "Your family cannot extend more than 1000 generations from above and below.");
	} else if (addChr.getLevel() < 10) {
		c.getPlayer().dropMessage(1, "The junior you wish to add must be over Level 10.");
	} else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0){
		c.getPlayer().dropMessage(1, "You have 2 juniors already.");
	} else if (c.getPlayer().isGM() || !addChr.isGM()) {
            addChr.getClient().getSession().write(FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void FamilyPrecept(final SeekableLittleEndianAccessor slea, MapleClient c) {
	MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
	if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
	    return;
	}
	fam.setNotice(slea.readMapleAsciiString());
    }

    public static final void FamilySummon(final SeekableLittleEndianAccessor slea, MapleClient c) {
	int TYPE = 1; //the type of the summon request.
	MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
	MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
	if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit()) &&
		!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && c.getPlayer().isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost) && 
		c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && c.getPlayer().getEventInstance() == null && tt.getEventInstance() == null) {
		//whew lots of checks
		boolean accepted = slea.readByte() > 0;
		if (accepted) {
			c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
           		tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
			tt.getClient().getSession().write(FamilyPacket.changeRep(-cost.rep));
			tt.useFamilyBuff(cost);
		} else {
			tt.dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
		}
	} else {
		c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
	}
	c.getPlayer().setTeleportName("");
    }

    public static final void DeleteJunior(final SeekableLittleEndianAccessor slea, MapleClient c) {
	int juniorid = slea.readInt();
	if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid)) {
		return;
	}
	//junior is not required to be online.
	final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
	final MapleFamilyCharacter other = fam.getMFC(juniorid);
	final MapleFamilyCharacter oth = c.getPlayer().getMFC();
	boolean junior2 = oth.getJunior2() == juniorid;
	if (junior2) {
		oth.setJunior2(0);
	} else {
		oth.setJunior1(0);
	}
	c.getPlayer().saveFamilyStatus();
	other.setSeniorId(0);
	//if (!other.isOnline()) {
	    MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());   
	//}
	MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
	if (!fam.splitFamily(juniorid)) { //juniorid splits to make their own family. function should handle the rest
		if (!junior2) {
			fam.resetGens(); //just lost a generation
			fam.resetDescendants();
		}
		fam.resetPedigree();
	}
	c.getPlayer().dropMessage(1, "Broke up with (" + other.getName() + ").\r\nFamily relationship has ended.");
	c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void DeleteSenior(final SeekableLittleEndianAccessor slea, MapleClient c) {
	if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
		return;
	}
	//not required to be online
	final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId()); //this is old family
	final MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
	final MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
	mgc_.setSeniorId(0);
	boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();
	if (junior2) {
		mgc.setJunior2(0);
	} else {
		mgc.setJunior1(0);
	}
	//if (!mgc.isOnline()) {
	    MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());   
	//}
	c.getPlayer().saveFamilyStatus();
	MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " has requested to sever ties with you, so the family relationship has ended.", 0);
	if (!fam.splitFamily(c.getPlayer().getId())) { //now, we're the family leader
		if (!junior2) {
			fam.resetGens(); //just lost a generation
			fam.resetDescendants();
		}
        	fam.resetPedigree();
	}
	c.getPlayer().dropMessage(1, "Broke up with (" + mgc.getName() + ").\r\nFamily relationship has ended.");
	c.getSession().write(MaplePacketCreator.enableActions());
    }

	public static final void AcceptFamily(SeekableLittleEndianAccessor slea, MapleClient c) {
        	MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        	if (inviter != null && c.getPlayer().getSeniorId() == 0 && (c.getPlayer().isGM() || !inviter.isHidden()) 
			&& inviter.getLevel() - 20 < c.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(slea.readMapleAsciiString()) && inviter.getNoJuniors() < 2
			/*&& inviter.getFamily().getGens() < 1000*/ && c.getPlayer().getLevel() >= 10) {
			boolean accepted = slea.readByte() > 0;
			inviter.getClient().getSession().write(FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
			if (accepted) {
				//c.getSession().write(FamilyPacket.sendFamilyMessage(0));
				c.getSession().write(FamilyPacket.getSeniorMessage(inviter.getName()));
                                MapleFamilyCharacter old = c.getPlayer().getMFC();
				if (inviter.getFamilyId() != 0) {

					MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());
                                        //if old isn't null, don't set the familyid yet, mergeFamily will take care of it
					c.getPlayer().setFamily(old == null ? inviter.getFamilyId() : old.getFamilyId(), inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
					MapleFamilyCharacter mf = inviter.getMFC();
					if (mf.getJunior1() > 0) {
						mf.setJunior2(c.getPlayer().getId());
					} else {
						mf.setJunior1(c.getPlayer().getId());
					}
					inviter.saveFamilyStatus();
					if (old != null) { //has junior
						MapleFamily.mergeFamily(fam, World.Family.getFamily(old.getFamilyId()));
					} else {
						fam.addFamilyMember(c.getPlayer().getMFC());
						fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
						c.getPlayer().saveFamilyStatus();
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
                                                MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                                                MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2(), c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
						inviter.setFamily(id, 0, c.getPlayer().getId(), 0); //load the family
                                                c.getPlayer().setFamily(id, inviter.getId(), old == null ? 0 : old.getJunior1(), old == null ? 0 : old.getJunior2());
						MapleFamily fam = World.Family.getFamily(id);
						fam.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
						if (old != null) { //has junior
							MapleFamily.mergeFamily(fam, World.Family.getFamily(old.getFamilyId()));
						} else {
							fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
						}
						fam.resetGens();
						fam.resetDescendants();
						fam.resetPedigree();
						
					}
				}
				c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
			}
		}
	}
}
