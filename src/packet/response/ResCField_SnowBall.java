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

import handling.MaplePacket;
import packet.ServerPacket;
import server.events.MapleSnowball;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCField_SnowBall {

    public static MaplePacket leftKnockBack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallTouch.get());
        return mplew.getPacket();
    }

    public static MaplePacket enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static MaplePacket snowballMessage(int team, int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallMsg.get());
        mplew.write(team); // 0 is down, 1 is up
        mplew.writeInt(message);
        return mplew.getPacket();
    }

    public static MaplePacket hitSnowBall(int team, int damage, int distance, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallHit.get());
        mplew.write(team); // 0 is down, 1 is up
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);
        return mplew.getPacket();
    }

    public static MaplePacket rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallState.get());
        mplew.write(type); // 0 = normal, 1 = rolls from start to end, 2 = down disappear, 3 = up disappear, 4 = move
        mplew.writeInt(ball1 == null ? 0 : (ball1.getSnowmanHP() / 75));
        mplew.writeInt(ball2 == null ? 0 : (ball2.getSnowmanHP() / 75));
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);
        return mplew.getPacket();
    }
    
}
