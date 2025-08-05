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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import packet.ClientPacket;
import packet.response.ResCField_Coconut;
import packet.response.wrapper.ResWrapper;
import server.events.MapleCoconut;
import server.events.MapleEventType;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCField_Coconut {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        // 109080000
        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_CoconutHit: {
                short nTarget = cp.Decode2(); // not checked.
                short nDelay = cp.Decode2(); // delay?
                OnCoconutHit(c, nTarget, nDelay);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private static void OnCoconutHit(MapleClient c, short nTarget, short nDelay) {
        MapleCoconut map = (MapleCoconut) c.getChannelServer().getEvent(MapleEventType.Coconut);
        if (map == null) {
            return;
        }
        //System.out.println("Coconut1");
        MapleCoconut.MapleCoconuts nut = map.getCoconut(nTarget);
        if (nut == null || !nut.isHittable()) {
            return;
        }
        if (System.currentTimeMillis() < nut.getHitTime()) {
            return;
        }
        //System.out.println("Coconut2");
        if (nut.getHits() > 2 && Math.random() < 0.4 && !nut.isStopped()) {
            //System.out.println("Coconut3-1");
            nut.setHittable(false);
            if (Math.random() < 0.01 && map.getStopped() > 0) {
                nut.setStopped(true);
                map.stopCoconut();
                c.getPlayer().getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 1));
                return;
            }
            nut.resetHits(); // For next event (without restarts)
            //System.out.println("Coconut4");
            if (Math.random() < 0.05 && map.getBombings() > 0) {
                //System.out.println("Coconut5-1");
                c.getPlayer().getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 2));
                map.bombCoconut();
            } else if (map.getFalling() > 0) {
                //System.out.println("Coconut5-2");
                c.getPlayer().getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 3));
                map.fallCoconut();
                if (c.getPlayer().getCoconutTeam() == 0) {
                    map.addMapleScore();
                    c.getPlayer().getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent(c.getPlayer().getName() + " of Team Maple knocks down a coconut."));
                } else {
                    map.addStoryScore();
                    c.getPlayer().getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent(c.getPlayer().getName() + " of Team Story knocks down a coconut."));
                }
                c.getPlayer().getMap().broadcastMessage(ResCField_Coconut.CoconutScore(map.getCoconutScore()));
            }
        } else {
            //System.out.println("Coconut3-2");
            nut.hit();
            c.getPlayer().getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 1));
        }
    }
}
