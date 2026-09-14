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
package odin.handling.world;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import odin.client.MapleCharacter;
import tacos.database.query.DQ_Characters;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.server.TacosWorld;

public class Party {

    private final Map<Integer, MapleParty> parties = new HashMap<>();
    private final AtomicInteger runningPartyId = new AtomicInteger();

    public Party() {
        this.runningPartyId.set(DQ_Characters.getNextRunningPartyId());
    }

    public void partyChat(int partyid, String chattext, String namefrom) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            throw new IllegalArgumentException("no party with the specified partyid exists");
        }

        for (MaplePartyCharacter partychar : party.getMembers()) {
            MapleCharacter chr = TacosWorld.find(0).findOnlinePlayer(partychar.getName(), false);
            if (chr != null && !chr.getName().equalsIgnoreCase(namefrom)) { //Extra check just in case
                chr.SendPacket(ResCField.GroupMessage(OpsChatGroup.CG_Party, namefrom, chattext));
            }
        }
    }

    public void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            return; //Don't update, just return. And definitely don't throw a damn exception.
            //throw new IllegalArgumentException("no party with the specified partyid exists");
        }
        switch (operation) {
            case JOIN:
                party.addMember(target);
                break;
            case EXPEL:
            case LEAVE:
                party.removeMember(target);
                break;
            case DISBAND:
                disbandParty(partyid);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                party.updateMember(target);
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                party.setLeader(target);
                break;
            default:
                throw new RuntimeException("Unhandeled updateParty operation " + operation.name());
        }

        for (MaplePartyCharacter partychar : party.getMembers()) {
            MapleCharacter chr = TacosWorld.find(0).findOnlinePlayer(partychar.getName(), false);
            if (chr != null) {
                if (operation == PartyOperation.DISBAND) {
                    chr.setParty(null);
                } else {
                    chr.setParty(party);
                }
                chr.SendPacket(ResCWvsContext.PartyResult(chr.getClient().getChannelId(), party, operation, target));
            }
        }
        switch (operation) {
            case LEAVE:
            case EXPEL: {
                MapleCharacter chr = TacosWorld.find(0).findOnlinePlayer(target.getName(), false);
                if (chr != null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(chr.getClient().getChannelId(), party, operation, target));
                    chr.setParty(null);
                }
                break;
            }
        }
    }

    public MapleParty createParty(MaplePartyCharacter chrfor) {
        int partyid = runningPartyId.getAndIncrement();
        MapleParty party = new MapleParty(partyid, chrfor);
        parties.put(party.getId(), party);
        return party;
    }

    public MapleParty getParty(int partyid) {
        return parties.get(partyid);
    }

    public MapleParty disbandParty(int partyid) {
        return parties.remove(partyid);
    }
}
