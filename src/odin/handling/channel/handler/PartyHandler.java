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
import odin.handling.world.MapleParty;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import odin.handling.world.OdinWorld;
import tacos.debug.DebugLogger;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsParty;

public class PartyHandler {

    public static boolean OnPartyResult(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        int party_id = cp.Decode4();

        if (chr.getParty() != null) {
            chr.Notice("You can't join the party as you are already in one");
            return false;
        }

        MapleParty party = OdinWorld.Party.getParty(party_id);
        if (party == null) {
            chr.Notice("The party you are trying to join does not exist");
            return false;
        }

        switch (OpsParty.find(type)) {
            case PartyRes_InviteParty_Sent: {
                return true;
            }
            case PartyRes_InviteParty_BlockedUser: {
                MapleCharacter leader = chr.getChannelServer().getOnlinePlayers().findById(party.getLeader().getId());
                if (leader != null) {
                    leader.SendPacket(ResCWvsContext.partyStatusMessage(23, chr.getName()));
                }
                return true;
            }
            case PartyRes_InviteParty_AlreadyInvited: {
                return true;
            }
            case PartyRes_InviteParty_AlreadyInvitedByInviter: {
                return true;
            }
            case PartyRes_InviteParty_Rejected: {
                return true;
            }
            case PartyRes_InviteParty_Accepted: {
                if (6 <= party.getMembers().size()) {
                    chr.SendPacket(ResCWvsContext.partyStatusMessage(17));
                    return true;
                }
                OdinWorld.Party.updateParty(party_id, PartyOperation.JOIN, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnPartyResult : not coded, type = " + type);
        return false;
    }

    public static boolean OnPartyRequest(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        MapleParty party = chr.getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(chr);

        switch (OpsParty.find(type)) {
            case PartyReq_CreateNewParty: {
                if (chr.getParty() == null) {
                    party = OdinWorld.Party.createParty(partyplayer);
                    chr.setParty(party);
                    chr.SendPacket(ResCWvsContext.partyCreated(party.getId()));
                } else {
                    if (partyplayer.equals(party.getLeader()) && party.getMembers().size() == 1) { //only one, reupdate
                        chr.SendPacket(ResCWvsContext.partyCreated(party.getId()));
                    } else {
                        chr.dropMessage(5, "You can't create a party as you are already in one");
                    }
                }
                return true;
            }
            case PartyReq_WithdrawParty: {
                if (party != null) { //are we in a party? o.O"
                    if (partyplayer.equals(party.getLeader())) { // disband
                        OdinWorld.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                        if (chr.getPyramidSubway() != null) {
                            chr.getPyramidSubway().fail(chr);
                        }
                    } else {
                        OdinWorld.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                        if (chr.getPyramidSubway() != null) {
                            chr.getPyramidSubway().fail(chr);
                        }
                    }
                    chr.setParty(null);
                }
                return true;
            }
            case PartyReq_JoinParty: {
                int partyid = cp.Decode4();
                if (chr.getParty() == null) {
                    party = OdinWorld.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getMembers().size() < 6) {
                            OdinWorld.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            chr.receivePartyMemberHP();
                            chr.updatePartyMemberHP();
                        } else {
                            chr.SendPacket(ResCWvsContext.partyStatusMessage(17));
                        }
                    } else {
                        chr.dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    chr.dropMessage(5, "You can't join the party as you are already in one");
                }
                return true;
            }
            case PartyReq_InviteParty: {
                // TODO store pending invitations and check against them
                MapleCharacter invited = chr.getChannelServer().getOnlinePlayers().findByName(cp.DecodeStr());
                if (invited != null) {
                    if (invited.getParty() == null && party != null) {
                        if (party.getMembers().size() < 6) {
                            chr.SendPacket(ResCWvsContext.partyStatusMessage(22, invited.getName()));
                            invited.SendPacket(ResCWvsContext.partyInvite(chr));
                        } else {
                            chr.SendPacket(ResCWvsContext.partyStatusMessage(16));
                        }
                    } else {
                        chr.SendPacket(ResCWvsContext.partyStatusMessage(17));
                    }
                } else {
                    chr.SendPacket(ResCWvsContext.partyStatusMessage(19));
                }
                return true;
            }
            case PartyReq_KickParty: {
                if (partyplayer.equals(party.getLeader())) {
                    MaplePartyCharacter expelled = party.getMemberById(cp.Decode4());
                    if (expelled != null) {
                        OdinWorld.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                        if (chr.getPyramidSubway() != null && expelled.isOnline()) {
                            chr.getPyramidSubway().fail(chr);
                        }
                    }
                }
                return true;
            }
            case PartyReq_ChangePartyBoss: {
                if (party != null) {
                    MaplePartyCharacter newleader = party.getMemberById(cp.Decode4());
                    if (newleader != null && partyplayer.equals(party.getLeader())) {
                        OdinWorld.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                    }
                }
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnPartyRequest : not coded, type = " + type);
        return false;
    }
}
