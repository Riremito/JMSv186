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
package tacos.packet.request;

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCField_Coconut;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.maps.MapleMap;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class ReqCField_Coconut {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
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
                OnCoconutHit(chr, nTarget, nDelay);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // fake
    public static class MapleCoconut {

        public MapleCoconuts getCoconut(int id) {
            return null;
        }

        public int getStopped() {
            return 0;
        }

        public void stopCoconut() {
        }

        public int getBombings() {
            return 0;
        }

        public void bombCoconut() {
        }

        public int getFalling() {
            return 0;
        }

        public void fallCoconut() {
        }

        public int getMapleScore() {
            return 0;
        }

        public int getStoryScore() {
            return 0;
        }

        public void addMapleScore() {
        }

        public void addStoryScore() {
        }

        public int[] getCoconutScore() {
            return null;
        }

    }

    public static class MapleCoconuts {

        public boolean isHittable() {
            return false;
        }

        public long getHitTime() {
            return 0;
        }

        public int getHits() {
            return 0;
        }

        public void setHittable(boolean v) {
        }

        public boolean isStopped() {
            return false;
        }

        public void setStopped(boolean v) {
        }

        public void resetHits() {
        }

        public void hit() {
        }

    }

    private static void OnCoconutHit(MapleCharacter chr, short nTarget, short nDelay) {
        MapleCoconut coconut_map = null;
        if (coconut_map == null) {
            return;
        }
        MapleCoconuts nut = coconut_map.getCoconut(nTarget);
        if (nut == null || !nut.isHittable()) {
            return;
        }
        if (System.currentTimeMillis() < nut.getHitTime()) {
            return;
        }
        if (nut.getHits() > 2 && Math.random() < 0.4 && !nut.isStopped()) {
            nut.setHittable(false);
            if (Math.random() < 0.01 && coconut_map.getStopped() > 0) {
                nut.setStopped(true);
                coconut_map.stopCoconut();
                chr.getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 1));
                return;
            }
            nut.resetHits();
            if (Math.random() < 0.05 && coconut_map.getBombings() > 0) {
                chr.getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 2));
                coconut_map.bombCoconut();
            } else if (coconut_map.getFalling() > 0) {
                chr.getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 3));
                coconut_map.fallCoconut();
                if (chr.getCoconutTeam() == 0) {
                    coconut_map.addMapleScore();
                    chr.getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent(chr.getName() + " of Team Maple knocks down a coconut."));
                } else {
                    coconut_map.addStoryScore();
                    chr.getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent(chr.getName() + " of Team Story knocks down a coconut."));
                }
                chr.getMap().broadcastMessage(ResCField_Coconut.CoconutScore(coconut_map.getCoconutScore()));
            }
        } else {
            nut.hit();
            chr.getMap().broadcastMessage(ResCField_Coconut.CoconutHit(nTarget, nDelay, 1));
        }
    }
}
