/*
 * Copyright (C) 2025 Riremito
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
package packet.response;

import server.network.MaplePacket;
import packet.ServerPacket;
import odin.server.events.MapleSnowball;

/**
 *
 * @author Riremito
 */
public class ResCField_SnowBall {

    public static MaplePacket SnowBallState(int m_nState, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SnowBallState);
        sp.Encode1(m_nState); // 0 = normal, 1 = rolls from start to end, 2 = down disappear, 3 = up disappear, 4 = move
        sp.Encode4(ball1 == null ? 0 : (ball1.getSnowmanHP() / 75)); // m_aSnowMan[0].m_nHP
        sp.Encode4(ball2 == null ? 0 : (ball2.getSnowmanHP() / 75)); // m_aSnowMan[1].m_nHP

        sp.Encode2(ball1 == null ? 0 : ball1.getPosition()); // m_nXPos
        sp.Encode1(0); // m_nSpeedDegree
        sp.Encode2(ball2 == null ? 0 : ball2.getPosition()); // m_nXPos
        sp.Encode1(0); // m_nSpeedDegree

        // first
        sp.Encode2(0); // m_nDamageSnowBall
        sp.Encode2(0); // m_nDamageSnowMan[0]
        sp.Encode2(0); // m_nDamageSnowMan[1]
        return sp.get();
    }

    public static MaplePacket SnowBallHit(int nTarget, int nDamage, int unused, int tDelay) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SnowBallHit);
        sp.Encode1(nTarget); // nTarget
        sp.Encode2(nDamage); // nDamage
        sp.Encode2(tDelay); // tDelay
        return sp.get();
    }

    public static MaplePacket SnowBallMsg(int team, int message) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SnowBallMsg);
        sp.Encode1(team); // 0 is down, 1 is up
        sp.Encode1(message);
        return sp.get();
    }

    public static MaplePacket SnowBallTouch() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SnowBallTouch);
        return sp.get();
    }

}
