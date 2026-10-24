/*
 * Copyright (C) 2024 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.packet.response;

import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMCarnivalFail;
import tacos.client.TacosCharacter;
import odin.server.MapleCarnivalParty;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCField_MonsterCarnival {

    // CField_MonsterCarnival::OnPersonalCP, CField_MonsterCarnival::OnTeamCP
    public static ServerPacket CPUpdate(boolean party, int curCP, int totalCP, int team) {
        // ?_?
        ServerPacket sp = new ServerPacket((party) ? ServerPacketHeader.LP_MCarnivalTeamCP : ServerPacketHeader.LP_MCarnivalPersonalCP);

        if (party) {
            sp.Encode1(team);
        }

        sp.Encode2(curCP);
        sp.Encode2(totalCP);
        return sp;
    }

    // CField_MonsterCarnival::OnRequestResult
    public static ServerPacket MCarnivalResultSuccess(String name, int tab, int number) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MCarnivalResultSuccess);

        sp.Encode1(tab);
        sp.Encode1(number);
        sp.EncodeStr(name);
        return sp;
    }

    // CField_MonsterCarnival::OnRequestResult
    public static ServerPacket MCarnivalResultFail(OpsMCarnivalFail reason) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MCarnivalResultFail);

        sp.Encode1(reason.get());
        return sp;
    }

    // CField_MonsterCarnival::OnEnter
    public static ServerPacket MCarnivalEnter(TacosCharacter chr, int enemyavailable, int enemytotal) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MCarnivalEnter);

        MapleCarnivalParty friendly = chr.getCarnivalParty();
        sp.Encode1(friendly.getTeam());
        sp.Encode2(chr.getAvailableCP());
        sp.Encode2(chr.getTotalCP());
        sp.Encode2(friendly.getAvailableCP());
        sp.Encode2(friendly.getTotalCP());
        sp.Encode2(enemyavailable);
        sp.Encode2(enemytotal);
        // m_aSummonedMob : spelled count of each monster in the summon list.
        int mob_count = (chr.getMap().getNodeInfo() != null) ? chr.getMap().getNodeInfo().getMobsToSpawn().size() : 0;
        for (int i = 0; i < mob_count; i++) {
            sp.Encode1(0);
        }
        return sp;
    }

    //CPQ
    // CField_MonsterCarnival::OnProcessForDeath
    public static ServerPacket MCarnivalDeath(String name, int lostCP, int team) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MCarnivalDeath);

        sp.Encode1(team); //team
        sp.EncodeStr(name);
        sp.Encode1(lostCP);
        return sp;
    }
}
