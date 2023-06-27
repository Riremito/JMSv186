/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.content;

import handling.MaplePacket;
import packet.ServerPacket;
import server.life.MapleNPC;

/**
 *
 * @author elfenlied
 */
public class NPCPacket {
      public static MaplePacket spawnNPC(MapleNPC life, boolean show) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcEnterField);

        p.Encode4(life.getObjectId());
        p.Encode4(life.getId());
        p.Encode2(life.getPosition().x);
        p.Encode2(life.getCy());
        p.Encode1(life.getF() == 1 ? 0 : 1);
        p.Encode2(life.getFh());
        p.Encode2(life.getRx0());
        p.Encode2(life.getRx1());
        p.Encode1(show ? 1 : 0);

        return p.Get();
    }

    public static MaplePacket removeNPC(final int objectid) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcLeaveField);

        p.Encode4(objectid);
        return p.Get();
    }  
}
