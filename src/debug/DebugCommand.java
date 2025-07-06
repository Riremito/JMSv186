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
package debug;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import java.awt.Point;
import packet.request.ReqCUser;
import packet.response.Res_JMS_CInstancePortalPool;
import packet.response.wrapper.ResWrapper;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.maps.MapleDynamicPortal;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class DebugCommand {

    public static boolean checkCommand(MapleClient c, String text) {
        // 通常クライアントのコマンドの処理
        if (text.charAt(0) == '!' || text.charAt(0) == '@') {
            text = '/' + text.substring(1);
        }
        if (text.charAt(0) == ServerConstants.PlayerGMRank.MAPLE.getCommandPrefix()) {
            String[] splitted = text.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            if ("/reload".equals(splitted[0])) {
                ServerConfig.ReloadHeader();
                c.getPlayer().UpdateStat(true);
                return true;
            }

            if ("/ea".equals(splitted[0]) || "/stuck".equals(splitted[0]) || "/unlock".equals(splitted[0])) {
                c.getPlayer().UpdateStat(true);
                return true;
            }

            if ("/heal".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                chr.getStat().setHp(chr.getStat().getMaxHp());
                chr.getStat().setMp(chr.getStat().getMaxMp());
                chr.UpdateStat(true);
                return true;
            }

            if ("/autosp".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                int skillid = chr.getLastSkillUp();
                if (skillid != 0) {
                    while (ReqCUser.OnSkillUpRequestInternal(chr, skillid));
                }
                return true;
            }

            if ("/randombeauty".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                int skinid = LoadData.GetRandomID(LoadData.DataType.SKIN);
                int faceid = LoadData.GetRandomID(LoadData.DataType.FACE);
                int hairid = LoadData.GetRandomID(LoadData.DataType.HAIR);

                chr.Notice("SkinID = " + skinid + ", FaceID = " + faceid + ", HairID = " + hairid);
                chr.setSkinColor((byte) (skinid % 100));
                chr.setFace(faceid);
                chr.setHair(hairid);
                chr.UpdateStat(false);
                return true;
            }

            if ("/randomdrop".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int itemid = LoadData.GetRandomID(LoadData.DataType.ITEM);
                IItem toDrop = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? ii.randomizeStats((Equip) ii.getEquipById(itemid)) : new client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);
                chr.getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
                return true;
            }

            if ("/randommap".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                int mapid = LoadData.GetRandomID(LoadData.DataType.MAP);
                MapleMap map = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                chr.changeMap(map, map.getPortal(0));
                return true;
            }

            if ("/randomspawn".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();

                int mob_count = 1;
                if (splitted.length >= 2) {
                    mob_count = Integer.parseInt(splitted[1]);
                }

                if (10 < mob_count) {
                    mob_count = 10;
                }

                for (int i = 0; i < mob_count; i++) {
                    int mobid = LoadData.GetRandomID(LoadData.DataType.MOB);
                    Debug.InfoLog("RandomSpawn: " + mobid);
                    MapleMonster mob = MapleLifeFactory.getMonster(mobid);
                    chr.getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
                }
                return true;
            }

            // デバッグモード
            if ("/debug".equals(splitted[0])) {
                c.getPlayer().SetDebugger();
                if (c.getPlayer().GetDebugger()) {
                    c.getPlayer().Notice("デバッグモード有効");
                } else {
                    c.getPlayer().Notice("デバッグモード無効");
                }
                return true;
            }

            // スクリプト情報
            if ("/info".equals(splitted[0])) {
                c.getPlayer().SetInformation();
                if (c.getPlayer().GetInformation()) {
                    c.getPlayer().Notice("スクリプト情報有効");
                } else {
                    c.getPlayer().Notice("スクリプト情報無効");
                }
                return true;
            }
            // ヘルプ
            if ("/help".equals(splitted[0])) {
                c.getPlayer().Notice("/save, /map2, /fm, /henesys, /leafre, /magatia");
                return true;
            }

            // セーブ
            if ("/save".equals(splitted[0]) || "/セーブ".equals(splitted[0])) {
                c.getPlayer().saveToDB(false, false);
                c.getPlayer().Notice("現在のキャラクターの状態がDBへ反映されました");
                return true;
            }
            // map2
            if ("/map2".equals(splitted[0])) {
                try {
                    int mapid = Integer.parseInt(splitted[1]);
                    if (0 < mapid && mapid < 999999999) {
                        warp(c, mapid);
                    }
                } catch (Exception e) {
                }
                return true;
            }

            // FM
            if ("/fm".equals(splitted[0]) || "/フリマ".equals(splitted[0])) {
                c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
                warp(c, 910000000);
                return true;
            }
            // ヘネシス
            if ("/henesys".equals(splitted[0]) || "/ヘネシス".equals(splitted[0])) {
                warp(c, 100000000);
                return true;
            }
            // リプレ
            if ("/leafre".equals(splitted[0]) || "/リプレ".equals(splitted[0])) {
                warp(c, 240000000);
                return true;
            }
            // マガティア
            if ("/magatia".equals(splitted[0]) || "/マガティア".equals(splitted[0])) {
                warp(c, 261000000);
                return true;
            }

            if ("/nextmap".equals(splitted[0])) {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int mapid = LoadData.GetMapIDByIndex(index + 1);

                if (0 <= mapid) {
                    warp(c, mapid);
                }
                return true;
            }

            if ("/prevmap".equals(splitted[0]) || "/previousmap".equals(splitted[0])) {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int mapid = LoadData.GetMapIDByIndex(index - 1);

                if (0 <= mapid) {
                    warp(c, mapid);
                }
                return true;
            }

            // warphere all
            if ("/wh".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    if (victim != chr) {
                        victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
                    }
                }
                return true;
            }

            // 転職
            if ("/jc".equals(splitted[0]) || "/jobchange".equals(splitted[0]) || "/転職".equals(splitted[0])) {
                return CustomNPCTalk(c, 1012003, 9330104);
            }

            // NPC
            if ("/npctalk".equals(splitted[0]) || "/NPC会話".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return true;
                }
                try {
                    int npcid = Integer.parseInt(splitted[1]);
                    if (NPCTalk(c, npcid)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
                c.getPlayer().Notice("無効なNPCIDです");
                return true;
            }
            if ("/npctalk2".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return true;
                }
                try {
                    int npcid = Integer.parseInt(splitted[1]);
                    if (CustomNPCTalk(c, 1012003, npcid)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
                c.getPlayer().Notice("無効なNPCIDです");
                return true;
            }
            // ポータル追加
            if ("/addportal".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return true;
                }
                int map_id_to = Integer.parseInt(splitted[1]);

                if (!LoadData.IsValidMapID(map_id_to)) {
                    return false;
                }

                MapleCharacter chr = c.getPlayer();
                Point player_xy = chr.getPosition();
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(2420004, map_id_to, player_xy.x, player_xy.y);
                c.getPlayer().getMap().addMapObject(dynamic_portal);
                //ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(chr.getMapId()).addMapObject(dynamic_portal);

                c.getPlayer().getMap().broadcastMessage(Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(dynamic_portal));

                c.getPlayer().Notice("AddPortal: from " + c.getPlayer().getMapId() + " to " + map_id_to);
                return true;
            }
            // ステータス初期化
            if ("/resetstat".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                DebugJob.ResetStat(chr);
                chr.Notice("Reset Stat!");
                return true;
            }
            if ("/defstat".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return false;
                }
                int level = 0;
                if (splitted.length >= 3) {
                    level = Integer.parseInt(splitted[2]);
                }

                int job_id = Integer.parseInt(splitted[1]);
                MapleCharacter chr = c.getPlayer();
                DebugJob.DefStat(chr, job_id, level);
                chr.Notice("Def Stat!");
                return true;
            }

            if ("/slot".equals(splitted[0])) {
                ResWrapper.MiroSlot(c.getPlayer());
                return true;
            }

            if ("/allskill".equals(splitted[0])) {
                if (2 <= splitted.length) {
                    c.getPlayer().setJob(Integer.parseInt(splitted[1]));
                }
                DebugJob.AllSkill(c.getPlayer());
                return true;
            }

            if ("/allskill0".equals(splitted[0])) {
                DebugJob.AllSkill(c.getPlayer(), true);
                return true;
            }

            if ("/allstat".equals(splitted[0])) {
                DebugJob.AllStat(c.getPlayer());
                return true;
            }

            // テスト
            if ("/test".equals(splitted[0]) || "/テスト".equals(splitted[0])) {
                return CustomNPCTalk(c, 1012003, 9010021);
            }

            /*
            splitted[0] = splitted[0].replace("/", "!");
            CommandObject co = commands.get(splitted[0]);
            if (co == null) {
                c.getPlayer().dropMessage(6, "error");
                return true;
            }

            co.execute(c, splitted);
            c.getPlayer().dropMessage(6, splitted[0]);
             */
            return false;
        }

        return false;
    }

    public static boolean NPCTalk(MapleClient c, int npcid) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcid);
        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }
        NPCScriptManager.getInstance().start(c, npcid);
        return true;
    }

    public static boolean warp(MapleClient c, int mapid) {
        try {
            if (!(0 < mapid && mapid < 999999999)) {
                c.getPlayer().Notice("存在しないMapIDです");
                return false;
            }
            MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
            if (map == null) {
                c.getPlayer().Notice("存在しないMapIDです");
                return false;
            }
            c.getPlayer().changeMap(map, map.getPortal(0));
            return true;
        } catch (Exception e) {
        }
        c.getPlayer().Notice("例外発生");
        return false;
    }

    // テスト用
    public static boolean CustomNPCTalk(MapleClient c, final int npcid, final int npc_script) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcid);
        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }
        NPCScriptManager.getInstance().start(c, npcid, npc_script);
        return true;
    }
}
