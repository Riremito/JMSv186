// GM Command
package packet.content;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import debug.Debug;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.Pair;
import wz.LoadData;

// CP_Admin, CP_Log
public class AdminPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        switch (header) {
            case CP_Admin: {
                //GMCommand.Accept(op, c);
                AdminCommand(p, c, chr);
                return true;
            }
            // 入力したコマンド文字列
            case CP_Log: {
                //GMCommand.AcceptMessage(op, c);
                AdminCommandLogger(p, c);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private static boolean AdminCommand(ClientPacket p, MapleClient c, MapleCharacter chr) {
        byte command = p.Decode1();

        Debug.AdminLog("[Official GM Command] " + String.format("%02X", command));

        switch (command) {
            // /create
            case 0x00: {
                // アイテム作成
                int itemid = p.Decode4();
                return true;
            }
            // /ban test
            case 0x03: {
                // @0086 [03] 04 00 74 65 73 74
                String text = p.DecodeStr();
                return true;
            }
            // /norank test
            case 0x06: {
                // @0086 [06] 04 00 74 65 73 74
                String text = p.DecodeStr();
                return true;
            }
            // /unblock test
            case 0x05: {
                // @0086 [05] 04 00 74 65 73 74
                String text = p.DecodeStr();
                return true;
            }
            // /pton test
            // /ptoff test
            case 0x07: {
                // @0086 [07] 01 04 00 74 65 73 74
                // @0086 [07] 00 04 00 74 65 73 74
                boolean flag = (p.Decode1() != 0);
                String text = p.DecodeStr();
                return true;
            }
            // /hide 0, 1
            case 0x0F: {
                // @0086 [0F] 00
                boolean flag = (p.Decode1() != 0);
                return true;
            }
            // /questreset 111
            case 0x16: {
                // @0086 [16] 6F 00
                int questid = p.Decode2();
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
                int mobid = p.Decode4();
                int count = p.Decode4();
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
                int jobid = p.Decode4();
                ChangeJob(chr, jobid);
                return true;
            }
            // /apget 111
            case 0x1F: {
                // @0086 [1F] 6F 00 00 00
                int point = p.Decode4();

                GetAP(chr, point);
                return true;
            }
            // /spget 111
            case 0x20: {
                // @0086 [20] 6F 00 00 00
                int point = p.Decode4();

                GetSP(chr, point);
                return true;
            }
            // /str
            case 0x21: {
                int point = p.Decode4();

                UpdateStat(c, chr, MapleStat.STR, point);
                return true;
            }
            // /dex
            case 0x22: {
                int point = p.Decode4();

                UpdateStat(c, chr, MapleStat.DEX, point);
                return true;
            }
            // /int
            case 0x23: {
                int point = p.Decode4();

                UpdateStat(c, chr, MapleStat.INT, point);
                return true;
            }
            // /luk
            case 0x24: {
                int point = p.Decode4();

                UpdateStat(c, chr, MapleStat.LUK, point);
                return true;
            }
            // /mmon test
            case 0x26: {
                // @0086 [26] 04 00 74 65 73 74
                String text = p.DecodeStr();
                return true;
            }
            // /mmoff test
            case 0x27: {
                // @0086 [24] 04 00 74 65 73 74
                String text = p.DecodeStr();
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
                String text = p.DecodeStr();
                int stage = p.Decode1();
                return true;
            }
            // /activatestagesystem 1
            case 0x34: {
                // @0086 [34] [01]
                boolean flag = (p.Decode1() != 0);
                return true;
            }
            // /cubecomplete
            case 0x36: {
                // @0086 [36]
                return true;
            }
            // /createnpc
            case 0x3B: {
                int mapid = p.Decode4();
                int npcid = p.Decode4();
                int x = p.Decode4();
                int y = p.Decode4();

                CreateNPC(chr, npcid, x, y);
                return true;
            }
            default: {
                Debug.ErrorLog("Unknown Official GM Command");
                break;
            }
        }

        return false;
    }

    private static boolean AdminCommandLogger(ClientPacket p, final MapleClient c) {
        String text = p.DecodeStr();
        Debug.AdminLog("[GM Command Text] " + text);
        return true;
    }

    private static boolean ChangeJob(MapleCharacter chr, int jobid) {
        if (!LoadData.IsValidJobID(jobid)) {
            chr.Notice("Invalid JobID");
            chr.Notice("Vaild JobID: " + LoadData.GetJobIDs());
            return false;
        }

        chr.changeJob(jobid);
        chr.Notice("JobID -> " + jobid);
        return true;
    }

    private static void GetAP(MapleCharacter chr, int point) {
        chr.gainAp((short) point);
    }

    private static void GetSP(MapleCharacter chr, int point) {
        chr.gainSP((short) point);
    }

    private static boolean UpdateStat(MapleClient c, MapleCharacter chr, MapleStat stat, int point) {
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

        final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>(2);
        statupdate.add(new Pair<>(stat, point));
        c.getPlayer().UpdateStat(true);
        return true;
    }

    private static boolean CreateNPC(MapleCharacter chr, int npcid, int x, int y) {
        if (!LoadData.IsValidNPCID(npcid)) {
            chr.Notice("Invalid NPCID");
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
        map.broadcastMessage(NPCPacket.spawnNPC(npc, true));
        return true;
    }
}
