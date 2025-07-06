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

    public static boolean checkCommandPrefix(String message) {
        if (message.length() <= 2) {
            return false;
        }

        switch (message.charAt(0)) {
            case '!':
            case '@':
            case '/': // クライアント編集かGMフラグが必要
            {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean checkCommand(MapleClient c, String message) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            return false;
        }

        if (!checkCommandPrefix(message)) {
            return false;
        }

        String text = '/' + message.substring(1);
        String[] splitted = text.split(" ");
        splitted[0] = splitted[0].toLowerCase(); // 小文字

        switch (splitted[0]) {
            // デバッグ関連
            case "/reload": {
                // PacketHeader設定再読み込み
                ServerConfig.ReloadHeader();
                chr.UpdateStat(true);
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
            case "/ea":
            case "/stuck":
            case "/unlock": {
                // フリーズ解除
                chr.UpdateStat(true);
                return true;
            }
            case "/save": {
                chr.saveToDB(false, false);
                chr.DebugMsg("Character data is saved to database.");
                return true;
            }
            case "/test":
            case "/help": {
                CustomNPCTalk(c, 1012003, 9010021);
                return true;
            }
            case "/npctalk": {
                if (splitted.length < 2) {
                    return false;
                }
                int npc_id = Integer.parseInt(splitted[1]);
                if (npc_id <= 0) {
                    return false;
                }
                if (!NPCTalk(c, npc_id)) {
                    chr.DebugMsg("[NPCTalk] Invalid NPCID.");
                }
                return true;
            }
            case "/npctalk2": {
                if (splitted.length < 2) {
                    return false;
                }
                int npc_id = Integer.parseInt(splitted[1]);
                if (npc_id <= 0) {
                    return false;
                }
                if (!CustomNPCTalk(c, 1012003, npc_id)) {
                    chr.DebugMsg("[NPCTalk2] Invalid NPCID.");
                }
                return true;
            }
            // ステータス関連
            case "/heal": {
                int new_hp = chr.getStat().getMaxHp();
                int new_mp = chr.getStat().getMaxMp();

                if (3 <= splitted.length) {
                    int ratio_hp = Integer.parseInt(splitted[1]);
                    int ratio_mp = Integer.parseInt(splitted[2]);
                    if (ratio_hp <= 0 || ratio_mp <= 0) {
                        ratio_hp = 100;
                        ratio_mp = 100;
                        chr.DebugMsg("Please, enter values between 1 - 100.");
                    }
                    new_hp = (int) (new_hp * (ratio_hp / 100.0));
                    new_mp = (int) (new_mp * (ratio_mp / 100.0));
                } else if (2 <= splitted.length) {
                    int ratio = Integer.parseInt(splitted[1]);
                    if (ratio <= 0) {
                        ratio = 100;
                        chr.DebugMsg("Please, enter values between 1 - 100.");
                    }
                    new_hp = (int) (new_hp * (ratio / 100.0));
                    new_mp = (int) (new_mp * (ratio / 100.0));
                }

                chr.getStat().setHp(new_hp);
                chr.getStat().setMp(new_mp);
                chr.UpdateStat(true);

                chr.DebugMsg("HP : " + chr.getStat().getHp() + " / " + chr.getStat().getMaxHp());
                chr.DebugMsg("MP : " + chr.getStat().getMp() + " / " + chr.getStat().getMaxMp());
                return true;
            }
            case "/autosp": {
                int skillid = chr.getLastSkillUp();
                if (skillid != 0) {
                    while (ReqCUser.OnSkillUpRequestInternal(chr, skillid));
                }
                chr.DebugMsg("Skill = " + skillid);
                return true;
            }
            case "/allskill": {
                if (2 <= splitted.length) {
                    chr.setJob(Integer.parseInt(splitted[1]));
                }
                DebugJob.AllSkill(chr);
                return true;
            }
            case "/allskill0": {
                DebugJob.AllSkill(c.getPlayer(), true);
                return true;
            }
            case "/allstat": {
                DebugJob.AllStat(c.getPlayer());
                return true;
            }
            case "/resetstat": {
                DebugJob.ResetStat(chr);
                return true;
            }
            case "/defstat": {
                if (splitted.length < 2) {
                    return false;
                }
                int level = 0;
                if (splitted.length >= 3) {
                    level = Integer.parseInt(splitted[2]);
                }

                int job_id = Integer.parseInt(splitted[1]);
                DebugJob.DefStat(chr, job_id, level);
                return true;
            }
            // Map移動関連
            case "/map2":
            case "/mapt":
            case "/warp": {
                if (splitted.length < 2) {
                    return false;
                }
                int map_id = Integer.parseInt(splitted[1]);
                if (map_id <= 0 || 999999999 <= map_id) {
                    return false;
                }
                warp(c, map_id);
                return true;
            }
            case "/prevmap": {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int map_id = LoadData.GetMapIDByIndex(index - 1);

                if (map_id <= 0) {
                    return false;
                }

                warp(c, map_id);
                return true;
            }
            case "/nextmap": {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int map_id = LoadData.GetMapIDByIndex(index + 1);
                if (map_id <= 0) {
                    return false;
                }
                warp(c, map_id);
                return true;
            }
            case "/fm":
            case "/フリマ": {
                chr.saveLocation(SavedLocationType.FREE_MARKET, chr.getMap().getReturnMap().getId());
                warp(c, 910000000);
                return true;
            }
            case "/henesys":
            case "/ヘネシス": {
                warp(c, 100000000);
                return true;
            }
            case "/leafre":
            case "/リプレ": {
                warp(c, 240000000);
                return true;
            }
            case "/magatia":
            case "/マガティア": {
                warp(c, 261000000);
                return true;
            }
            case "/jc":
            case "/転職": {
                CustomNPCTalk(c, 1012003, 9330104);
                return true;
            }
            // ランダム関連
            case "/randombeauty": {
                int skinid = LoadData.GetRandomID(LoadData.DataType.SKIN);
                int faceid = LoadData.GetRandomID(LoadData.DataType.FACE);
                int hairid = LoadData.GetRandomID(LoadData.DataType.HAIR);

                chr.setSkinColor((byte) (skinid % 100));
                chr.setFace(faceid);
                chr.setHair(hairid);
                chr.UpdateStat(false);
                chr.DebugMsg("[RandomBeauty] SkinID = " + skinid + ", FaceID = " + faceid + ", HairID = " + hairid);
                return true;
            }
            case "/randomdrop": {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int itemid = LoadData.GetRandomID(LoadData.DataType.ITEM);
                IItem toDrop = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? ii.randomizeStats((Equip) ii.getEquipById(itemid)) : new client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);
                chr.getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
                String item_name = MapleItemInformationProvider.getInstance().getName(toDrop.getItemId());
                if (item_name == null) {
                    item_name = "<null>";
                }
                chr.DebugMsgItem("[RandomDrop] " + toDrop.getItemId() + " - " + item_name, toDrop.getItemId());
                return true;
            }
            case "/randomspawn": {
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
                    chr.DebugMsg("[RandomSpawn] " + mob.getId() + " - " + mob.getStats().getName());
                }

                return true;
            }
            case "/randommap": {
                int mapid = LoadData.GetRandomID(LoadData.DataType.MAP);
                MapleMap map = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                chr.changeMap(map, map.getPortal(0));
                chr.DebugMsg("[RandomMap] " + map.getId() + " - " + map.getStreetName() + "_" + map.getMapName()); // MapName code is buggy.
                return true;
            }
            // カスタムコマンド
            case "/wh": {
                for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    if (victim != chr) {
                        victim.changeMap(chr.getMap(), chr.getMap().findClosestSpawnpoint(chr.getPosition()));
                    }
                }
                return true;
            }
            case "/addportal": {
                if (splitted.length < 2) {
                    return false;
                }
                int map_id_to = Integer.parseInt(splitted[1]);

                if (!LoadData.IsValidMapID(map_id_to)) {
                    chr.DebugMsg("[AddPortal] Invalid MapID.");
                    return false;
                }

                Point player_xy = chr.getPosition();
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(2420004, map_id_to, player_xy.x, player_xy.y);
                chr.getMap().addMapObject(dynamic_portal);
                chr.getMap().broadcastMessage(Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(dynamic_portal));
                chr.DebugMsg("[AddPortal] " + chr.getMapId() + " -> " + map_id_to);
                return true;
            }
            case "/slot": {
                ResWrapper.MiroSlot(chr);
                return true;
            }
            case "/xxxx": {
                return true;
            }
            default: {
                break;
            }
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
