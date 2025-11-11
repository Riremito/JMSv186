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
package tacos.packet.request.sub;

import odin.client.MapleCharacter;
import odin.client.MapleStat;
import tacos.data.wz.ids.DWI_Validation;
import tacos.data.wz.ids.DWI_LoadXML;
import tacos.debug.DebugLogger;
import java.awt.Point;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCNpcPool;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleNPC;
import odin.server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqSub_Admin {

    public static boolean OnAdmin(MapleCharacter chr, ClientPacket cp) {
        byte command = cp.Decode1();

        switch (command) {
            // /create
            case 0x00: {
                // アイテム作成
                int itemid = cp.Decode4();
                return true;
            }
            // /exp
            case 0x02: {
                int exp = cp.Decode4();
                chr.setExp(exp);
                chr.UpdateStat(true);
                return true;
            }

            // /ban test
            case 0x03: {
                // @0086 [03] 04 00 74 65 73 74
                String text = cp.DecodeStr();
                return true;
            }
            // /norank test
            case 0x06: {
                // @0086 [06] 04 00 74 65 73 74
                String text = cp.DecodeStr();
                return true;
            }
            // /unblock test
            case 0x05: {
                // @0086 [05] 04 00 74 65 73 74
                String text = cp.DecodeStr();
                return true;
            }
            // /pton test
            // /ptoff test
            case 0x07: {
                // @0086 [07] 01 04 00 74 65 73 74
                // @0086 [07] 00 04 00 74 65 73 74
                boolean flag = (cp.Decode1() != 0);
                String text = cp.DecodeStr();
                return true;
            }
            // /hide 0, 1
            case 0x0F: {
                // @0086 [0F] 00
                boolean flag = (cp.Decode1() != 0);
                return true;
            }
            // /questreset 111
            case 0x16: {
                // @0086 [16] 6F 00
                int questid = cp.Decode2();
                return true;
            }
            // /hackcheckcountreload
            case 0x19: {
                // @0086 [19]
                return true;
            }
            // /summon
            case 0x1A: {
                // Mob召喚
                int mobid = cp.Decode4();
                int count = cp.Decode4();
                return true;
            }
            // /levelset 111
            case 0x1C: {
                // @0086 [1C] 6F
                return true;
            }
            // /job 900
            case 0x1D: {
                // @0086 [1D] 84 03 00 00
                int jobid = cp.Decode4();
                ChangeJob(chr, jobid);
                return true;
            }
            // /apget 111
            case 0x1F: {
                // @0086 [1F] 6F 00 00 00
                int point = cp.Decode4();

                GetAP(chr, point);
                return true;
            }
            // /spget 111
            case 0x20: {
                // @0086 [20] 6F 00 00 00
                int point = cp.Decode4();

                GetSP(chr, point);
                return true;
            }
            // /str
            case 0x21: {
                int point = cp.Decode4();

                UpdateStat(chr, MapleStat.STR, point);
                return true;
            }
            // /dex
            case 0x22: {
                int point = cp.Decode4();

                UpdateStat(chr, MapleStat.DEX, point);
                return true;
            }
            // /int
            case 0x23: {
                int point = cp.Decode4();

                UpdateStat(chr, MapleStat.INT, point);
                return true;
            }
            // /luk
            case 0x24: {
                int point = cp.Decode4();

                UpdateStat(chr, MapleStat.LUK, point);
                return true;
            }
            // /mmon test
            case 0x26: {
                // @0086 [26] 04 00 74 65 73 74
                String text = cp.DecodeStr();
                return true;
            }
            // /mmoff test
            case 0x27: {
                // @0086 [24] 04 00 74 65 73 74
                String text = cp.DecodeStr();
                return true;
            }
            // /refreshweatherevent
            case 0x30: {
                // @0086 [30]
                return true;
            }
            // /stagesystem test 7
            case 0x33: {
                // @0086 [33] 04 00 74 65 73 74 [07]
                String text = cp.DecodeStr();
                int stage = cp.Decode1();
                return true;
            }
            // /activatestagesystem 1
            case 0x34: {
                // @0086 [34] [01]
                boolean flag = (cp.Decode1() != 0);
                return true;
            }
            // /cubecomplete
            case 0x36: {
                // @0086 [36]
                return true;
            }
            // /createnpc
            case 0x3B: {
                int mapid = cp.Decode4();
                int npcid = cp.Decode4();
                int x = cp.Decode4();
                int y = cp.Decode4();

                CreateNPC(chr, npcid, x, y);
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.AdminLog("[OnAdmin] not coded = " + command);
        return false;
    }

    private static boolean ChangeJob(MapleCharacter chr, int jobid) {
        if (!DWI_Validation.isValidJobID(jobid)) {
            chr.DebugMsg("Invalid JobID");
            chr.DebugMsg("Vaild JobID: " + DWI_LoadXML.GetJobIDs());
            return false;
        }

        chr.changeJob(jobid);
        chr.DebugMsg("JobID -> " + jobid);
        return true;
    }

    private static void GetAP(MapleCharacter chr, int point) {
        chr.gainAp((short) point);
    }

    private static void GetSP(MapleCharacter chr, int point) {
        chr.gainSP((short) point);
    }

    private static boolean UpdateStat(MapleCharacter chr, MapleStat stat, int point) {
        if (point < 4 || 30000 < point) {
            return false;
        }

        switch (stat) {
            case STR: {
                chr.getStat().setStr((short) point);
                break;
            }
            case DEX: {
                chr.getStat().setDex((short) point);
                break;
            }
            case INT: {
                chr.getStat().setInt((short) point);
                break;
            }
            case LUK: {
                chr.getStat().setLuk((short) point);
                break;
            }
            default: {
                return false;
            }
        }

        chr.UpdateStat(true);
        return true;
    }

    private static boolean CreateNPC(MapleCharacter chr, int npcid, int x, int y) {
        if (!DWI_Validation.isValidNPCID(npcid)) {
            chr.DebugMsg("Invalid NPCID");
            return false;
        }

        MapleMap map = chr.getMap();

        if (map == null) {
            return false;
        }

        MapleNPC npc = MapleLifeFactory.getNPC(npcid);

        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }

        Point npc_xy = new Point(x, y);

        npc.setPosition(npc_xy);
        npc.setCy(x);
        npc.setRx0(x + 50);
        npc.setRx1(y - 50);
        npc.setFh(map.getFootholds().findBelow(npc_xy).getId());
        npc.setCustom(true);
        map.addMapObject(npc);
        map.broadcastMessage(ResCNpcPool.NpcEnterField(npc, true));
        return true;
    }
}
