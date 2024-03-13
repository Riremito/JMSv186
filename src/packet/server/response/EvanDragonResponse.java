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
package packet.server.response;

import handling.MaplePacket;
import java.awt.Point;
import java.util.List;
import packet.server.ServerPacket;
import packet.server.response.struct.TestHelper;
import server.maps.MapleDragon;
import server.movement.LifeMovementFragment;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class EvanDragonResponse {

    public static MaplePacket moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_DragonMove);
        sp.Encode4(d.getOwner());
        sp.Encode2(startPos.x);
        sp.Encode2(startPos.y);
        sp.Encode4(0);

        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        TestHelper.serializeMovementList(mplew, moves);

        sp.EncodeBuffer(mplew.getPacket().getBytes());

        /*
        List<LifeMovementFragment> res = null;
        try {
            // player OK
            res = MovementPacket.CMovePath_Decode(cp, 3);
        } catch (ArrayIndexOutOfBoundsException e) {
            Debug.ErrorLog("AIOBE Type3");
        }

        if (res == null) {
            Debug.ErrorLog("AIOBE Type3 res == null");
        }
         */
        return sp.Get();
    }

    public static MaplePacket removeDragon(int chrid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_DragonLeaveField);

        sp.Encode4(chrid);
        return sp.Get();
    }

    public static MaplePacket spawnDragon(MapleDragon d) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_DragonEnterField);

        sp.Encode4(d.getOwner());
        sp.Encode4(d.getPosition().x);
        sp.Encode4(d.getPosition().y);
        sp.Encode1(d.getStance());
        sp.Encode2(0);
        sp.Encode2(d.getJobId());
        return sp.Get();
    }

}
