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

import odin.client.MapleCharacter;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import odin.server.MapleCarnivalParty;

/**
 *
 * @author Riremito
 */
public class ResCField_MonsterCarnival {

    public static MaplePacket CPUpdate(boolean party, int curCP, int totalCP, int team) {
        // ?_?
        ServerPacket sp = new ServerPacket((party) ? ServerPacket.Header.LP_MCarnivalTeamCP : ServerPacket.Header.LP_MCarnivalPersonalCP);

        if (party) {
            sp.Encode1(team);
        }

        sp.Encode2(curCP);
        sp.Encode2(totalCP);
        return sp.get();
    }

    public static MaplePacket playerSummoned(String name, int tab, int number) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MCarnivalResultSuccess);

        sp.Encode1(tab);
        sp.Encode1(number);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket startMonsterCarnival(final MapleCharacter chr, final int enemyavailable, final int enemytotal) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MCarnivalEnter);

        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        sp.Encode1(friendly.getTeam());
        sp.Encode2(chr.getAvailableCP());
        sp.Encode2(chr.getTotalCP());
        sp.Encode2(friendly.getAvailableCP());
        sp.Encode2(friendly.getTotalCP());
        sp.Encode2(enemyavailable);
        sp.Encode2(enemytotal);
        sp.Encode8(0);
        sp.Encode2(0);
        return sp.get();
    }

    //CPQ
    public static MaplePacket playerDiedMessage(String name, int lostCP, int team) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MCarnivalDeath);

        sp.Encode1(team); //team
        sp.EncodeStr(name);
        sp.Encode1(lostCP);
        return sp.get();
    }

}
