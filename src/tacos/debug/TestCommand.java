/*
 * Copyright (C) 2026 Riremito
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
package tacos.debug;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.provider.IMapleData;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.shops.HiredMerchant;
import tacos.packet.ops.OpsUI;
import tacos.packet.response.ResCEmployeePool;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCNpcPool;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.server.TacosChannel;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TestCommand {

    public static boolean executeCommand(DebugCommander dcmd, MapleCharacter chr) {
        MapleClient client = chr.getClient();
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/split": {
                map.getMapSplit().sendInfo(chr);
                return true;
            }
            case "/check": {
                chr.DebugMsg("X  : " + chr.getPosition().x);
                chr.DebugMsg("Y  : " + chr.getPosition().y);
                chr.DebugMsg("FH : " + chr.getFH());
                chr.DebugMsg("Ac : " + chr.getStance());
                return true;
            }
            case "/testmsg": {
                chr.DebugMsg("BLUE.");
                chr.DebugMsg2("PINK.");
                chr.DebugMsg3("YELLOW.");
                return true;
            }
            case "/threadid": {
                chr.DebugMsg("thread id = " + DebugLogger.getThreadId());
                return true;
            }
            case "/debugmode": {
                chr.SetDebugger();
                chr.DebugMsg("DebugMode = " + chr.GetDebugger());
                return true;
            }
            case "/infomode": {
                chr.SetInformation();
                chr.DebugMsg("InfoMode = " + chr.GetInformation());
                return true;
            }
            case "/test":
            case "/help": {
                DebugCommand.remoteNPCTalk(client, 9010021, 1012003);
                return true;
            }

            case "/hm": {
                List<Integer> ids = new ArrayList<>();
                IMapleData md_item_sub_type = WzXML.ITEM.getItemImg(503);
                if (md_item_sub_type != null) {
                    for (IMapleData md_item : md_item_sub_type.getChildren()) {
                        int item_id = Integer.parseInt(md_item.getName());
                        ids.add(item_id);
                    }
                }

                if (chr.getFH() <= 0) {
                    return true;
                }

                List<HiredMerchant> hms = new ArrayList<>();
                Random rand = new Random();
                int count = 0;
                for (MapleFoothold mfh : map.getFootholds().getAll()) {
                    if (30 < count) {
                        break;
                    }
                    if (mfh.getId() < chr.getFH() - 15) {
                        continue;
                    }
                    count++;
                    int id_inc = 0;
                    int fh_id = 0;
                    int fh_x = 0;
                    int fh_y = 0;
                    if (mfh.getId() == chr.getFH()) {
                        fh_id = chr.getFH();
                        fh_x = chr.getPosition().x;
                        fh_y = chr.getPosition().y;
                    } else {
                        fh_id = mfh.getId();
                        fh_x = mfh.getX1();
                        fh_y = mfh.getY1();
                        id_inc = fh_id;

                        int fh_width = mfh.getX2() - mfh.getX1();

                        if (fh_width == 0) {
                            continue;
                        }

                        fh_x = mfh.getX1() + fh_width / 2;
                        boolean bOK = true;
                        for (HiredMerchant hm : hms) {
                            int distance = (int) Math.sqrt((hm.getPosition().x - fh_x) * (hm.getPosition().x - fh_x) + (hm.getPosition().y - fh_y) * (hm.getPosition().y - fh_y));
                            if (distance <= 100) {
                                bOK = false;
                                break;
                            }
                        }
                        if (!bOK) {
                            continue;
                        }
                    }
                    int item_id = ids.get(rand.nextInt(ids.size()));
                    HiredMerchant hm = new HiredMerchant(chr, item_id, "DebugHiredMarchant");
                    hm.setTest(chr.getId() + id_inc, fh_id, ids.get(rand.nextInt(ids.size())), 7777 + id_inc);
                    hm.setPosition(new Point(fh_x, fh_y));
                    map.addMapObject(hm);
                    chr.SendPacket(ResCEmployeePool.EmployeeLeaveField(hm));
                    chr.SendPacket(ResCEmployeePool.EmployeeEnterField(hm));
                    hms.add(hm);
                }
                return true;
            }
            // packet test.
            case "/msg": {
                TacosChannel srv_channel = chr.getChannelServer();
                if (!dcmd.check(1)) {
                    srv_channel.setServerMessage("");
                    srv_channel.broadcastPacket(ResWrapper.BroadCastMsgSlide(srv_channel.getServerMessage()));
                    return true;
                }
                srv_channel.setServerMessage(dcmd.get(1));
                srv_channel.broadcastPacket(ResWrapper.BroadCastMsgSlide(srv_channel.getServerMessage()));
                return true;
            }
            case "/repair": {
                chr.SendPacket(ResCUserLocal.UserOpenUIWithOption(OpsUI.UI_REPAIRDURABILITY, 1012003));
                return true;
            }
            case "/mg": {
                chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStaticOmokTest(chr));
                return true;
            }
            case "/poll": {
                String questions[] = {"Question1", "Question2"};
                String answers[][] = {
                    {"123", "aiueo", "asdf"},
                    {"456", "qwert"}
                };

                // client strings won't be cleared, buggy...
                chr.SendPacket(ResCUserLocal.PollQuestion(questions, answers));
                return true;
            }
            case "/npccon": {
                for (MapleMapObject mmo : map.getMapObjects(MapleMapObjectType.NPC)) {
                    MapleNPC npc = map.getNPCByOid(mmo.getObjectId());
                    chr.SendPacket(ResCNpcPool.NpcChangeController(npc, true, true));
                    chr.DebugMsg("NpcControl : id = " + npc.getId() + ", oid = " + npc.getObjectId());
                }
                return true;
            }
            case "/mobtest": {
                List<MapleMonster> monsters = map.getAllMonsters();
                MapleMonster monster = null;
                if (map.getAllMonsters().isEmpty()) {
                    monster = MapleLifeFactory.getMonster(130101);
                    map.spawnMonsterOnGroundBelow(monster, chr.getPosition());
                } else {
                    monster = monsters.get(0);
                }

                int index = dcmd.check(1) ? dcmd.getInt(1) : 1;
                chr.DebugMsg("monsterPacketTest : " + index);
                monsterPacketTest(chr, monster, index);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static void monsterPacketTest(MapleCharacter chr, MapleMonster monster, int index) {
        switch (index) {
            case 1 -> {
                chr.SendPacket(ResCMobPool.MobAttackedByMob(monster, 1, 7777));
            }
            case 2 -> {
                chr.SendPacket(ResCMobPool.MobNextAttack(monster, 1));
            }
            case 3 -> {
                chr.SendPacket(ResCMobPool.MobEscortReturnBefore(monster));
            }
            case 4 -> {
                chr.SendPacket(ResCMobPool.MobEscortStopSay(monster, 5120035, "MobEscortStopSay"));
            }
            case 5 -> {
                chr.SendPacket(ResCMobPool.MobRequestResultEscortInfo(monster, chr.getMap()));
            }
            case 6 -> {
                chr.SendPacket(ResCMobPool.MobEscortStopEndPermmision(monster));
            }
            case 7 -> {
                chr.SendPacket(ResCMobPool.MobSkillDelay(monster));
            }
            case 8 -> {
                chr.SendPacket(ResCMobPool.MobChargeCount(monster, 1, 1));
            }
            case 9 -> {
                chr.SendPacket(ResCMobPool.MobSpeaking(monster, 0, 0));
            }
            case 10 -> {
                chr.SendPacket(ResCMobPool.MobEffectByItem(monster, 2270004, true));
            }
            case 11 -> {
                chr.SendPacket(ResCMobPool.MobCatchEffect(monster, true));
            }
            case 12 -> {
                chr.SendPacket(ResCMobPool.MobCrcKeyChanged(monster, 0xBEEF));
            }
            case 13 -> {
                chr.SendPacket(ResCMobPool.MobSpecialEffectBySkill(monster, chr, 3110001, 1000));
            }
            case 14 -> {
                chr.SendPacket(ResCMobPool.MobAffected(monster, 4341003, 1000));
            }
            case 15 -> {
                chr.SendPacket(ResCMobPool.MobSuspendReset(monster));
            }
            case 16 -> {
                chr.SendPacket(ResCMobPool.MobStatReset(monster));
            }
            case 17 -> {
                chr.SendPacket(ResCMobPool.MobStatSet(monster));
            }
            default -> {
            }
        }
    }
}
