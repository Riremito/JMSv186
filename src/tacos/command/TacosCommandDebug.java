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
package tacos.command;

import java.awt.Point;
import tacos.server.map.object.TacosMerchant;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosMonster;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import tacos.client.TacosMonsterBook;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.debug.DebugManTest;
import tacos.debug.DebugMan_CC;
import tacos.debug.DebugMan_NM;
import tacos.debug.DebugMan_Warp;
import tacos.debug.DebugMan_WarpBoss;
import tacos.debug.DebugShop;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.server.TacosChannel;
import tacos.server.map.TacosBossSpawnPoint;
import tacos.server.map.TacosFoothold;
import tacos.server.map.TacosPortal;
import tacos.wz.MapWz;
import tacos.wz.WzDataStorage;
import tacos.wz.WzName;
import tacos.wz.WzNameStorage;
import tacos.wz.WzXML;
import tacos.wz.ids.DWI_Random;

/**
 *
 * @author Riremito
 */
public class TacosCommandDebug {

    public static boolean executeCommand(TacosCommander dcmd, TacosCharacter chr) {
        TacosMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/search" -> {
                if (!dcmd.check(2)) {
                    return true;
                }

                WzNameStorage nds;

                switch (dcmd.get(1).toLowerCase()) {
                    case "item" -> {
                        nds = WzNameStorage.ITEM;
                        for (WzName nd : nds.find(dcmd.get(2), false)) {
                            nd.sendDebugMsgItem(chr);
                        }
                        return true;
                    }
                    case "map" -> {
                        nds = WzNameStorage.MAP;
                        for (WzName nd : nds.find(dcmd.get(2), false)) {
                            nd.sendMapDebugMsg(chr);
                        }
                        return true;
                    }
                    case "mob" -> {
                        nds = WzNameStorage.MOB;
                    }
                    case "npc" -> {
                        nds = WzNameStorage.NPC;
                    }
                    case "reactor" -> {
                        // no names.
                        return true;
                    }
                    case "skill" -> {
                        nds = WzNameStorage.SKILL;
                    }
                    default -> {
                        return true;
                    }
                }

                for (WzName nd : nds.find(dcmd.get(2), false)) {
                    nd.sendDebugMsg(chr);
                }
                return true;
            }
            // debug npc script.
            case "/dm" -> {
                if (!dcmd.check(1)) {
                    DebugManTest dm_test = new DebugManTest();
                    dm_test.start(chr);
                    return true;
                }
                // near map.
                if (dcmd.get(1).equals("nm")) {
                    DebugMan_NM dm = new DebugMan_NM();
                    dm.start(chr);
                    return true;
                }
                // character create.
                if (dcmd.get(1).equals("cc")) {
                    DebugMan_CC dm = new DebugMan_CC();
                    dm.start(chr);
                    return true;
                }
                return true;
            }
            // debug shop.
            case "/ds" -> {
                DebugShop ds = new DebugShop();

                if (!dcmd.check(1)) {
                    ds.setRandomItems(100);
                    ds.setRechargeAll();
                    ds.start(chr);
                    return true;
                }

                int item_sub_type = dcmd.getInt(1);
                if (item_sub_type == 207 || item_sub_type == 233) {
                    ds.setRechargeAll();
                }

                ds.setItemTest(item_sub_type);
                ds.start(chr);
                return true;
            }
            case "/ds2" -> {
                DebugShop ds = new DebugShop();

                if (!dcmd.check(1)) {
                    return true;
                }

                String search_string = "";
                for (int i = 1; i < dcmd.getLength(); i++) {
                    if (!search_string.isEmpty()) {
                        search_string += " ";
                    }
                    search_string += dcmd.get(i);
                }

                int shop_item_count = 0;
                for (WzName nd : WzNameStorage.ITEM.find(search_string)) {
                    ds.addItem(nd.getId());
                    shop_item_count++;
                    if (100 <= shop_item_count) {
                        chr.DebugMsg("item search hits over 100 item names.");
                        break;
                    }
                }

                chr.DebugMsg("search results = " + shop_item_count);
                ds.start(chr);
                return true;
            }
            case "/npclocation" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);
                if (!WzDataStorage.NPC.check(npc_id)) {
                    chr.DebugMsg("npclocation : invalid id.");
                    return true;
                }

                List<Integer> npc_location_map_ids = WzXML.ETC.getNpcLocationMapIds(npc_id);
                if (npc_location_map_ids == null) {
                    chr.DebugMsg("npclocation : NpcLocation.img/npc_id is not found.");
                    return true;
                }

                WzName nd_npc = WzNameStorage.NPC.get(npc_id);

                if (nd_npc == null) {
                    chr.DebugMsg("npclocation : error.");
                    return true;
                }

                nd_npc.sendDebugMsg(chr);
                for (int map_id : npc_location_map_ids) {
                    WzName nd_map = WzNameStorage.MAP.get(map_id);
                    if (nd_map == null) {
                        chr.DebugMsg("ERROR.");
                        continue;
                    }
                    nd_map.sendMapDebugMsg(chr);
                }

                return true;
            }
            case "/monsterbook" -> {
                // Item.wz/Consume/0238.img/info/mob
                Map<Integer, Integer> monster_card_items = WzXML.ITEM.getMonsterCardMobIds();
                // String.wz/MonsterBook.img
                List<Integer> monster_book_mobs = WzXML.STRING.getMonsterBookMobIds();
                if (monster_card_items == null || monster_book_mobs == null) {
                    return true;
                }

                TacosMonsterBook monster_book = chr.getMonsterBook();

                for (int mob_id : monster_book_mobs) {
                    for (Map.Entry<Integer, Integer> mc_item : monster_card_items.entrySet()) {
                        if (mc_item.getValue() == mob_id) {
                            int card_item_id = mc_item.getKey();
                            monster_book.add(card_item_id, 5, false);
                            break;
                        }
                    }
                }

                monster_book.update();
                return true;
            }
            // random data.
            case "/randombeauty" -> {
                int skin_id = WzDataStorage.SKIN.getRandom();
                int face_id = WzDataStorage.FACE.getRandom();
                int hair_id = WzDataStorage.HAIR.getRandom();

                chr.setSkinColor((byte) (skin_id % 100));
                chr.setFace(face_id);
                chr.setHair(hair_id);
                chr.sendStatChanged(false);

                chr.DebugMsg("randombeauty : SkinID = " + skin_id + ", FaceID = " + face_id + ", HairID = " + hair_id);
                DebugLogger.InfoLog("randombeauty : SkinID = " + skin_id + ", FaceID = " + face_id + ", HairID = " + hair_id);
                return true;
            }
            case "/randomspawn" -> {
                int mob_count = 1;
                if (dcmd.check(1)) {
                    mob_count = dcmd.getInt(1);
                }

                if (1000 < mob_count) {
                    mob_count = 1000;
                }

                for (int i = 0; i < mob_count; i++) {
                    int mobid = WzDataStorage.MOB.getRandom();
                    DebugLogger.InfoLog("random spawn: " + mobid);
                    TacosMonster mob = WzXML.MOB.createMob(mobid);
                    if (mob != null) {
                        map.spawnMonsterOnGroundBelow(mob, chr.getPosition());

                        chr.DebugMsg("randomspawn : " + mob.getId() + " - " + mob.getStats().getName());
                        DebugLogger.InfoLog("randomspawn : " + mob.getId() + " - " + mob.getStats().getName());
                    }
                }

                return true;
            }
            case "/randomdrop" -> {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int itemid = WzDataStorage.ITEM.getRandom();
                Item toDrop = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? ii.getEquipById(itemid) : new odin.client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);
                map.spawnItemDrop(chr, chr, toDrop, chr.getPosition(), true, true);
                String item_name = MapleItemInformationProvider.getInstance().getName(toDrop.getItemId());

                if (item_name == null) {
                    item_name = "<null>";
                }

                chr.DebugMsgItem("randomdrop : " + toDrop.getItemId() + " - " + item_name, toDrop.getItemId());
                DebugLogger.InfoLog("randomdrop : " + toDrop.getItemId() + " - " + item_name);
                return true;
            }
            case "/randommap" -> {
                int mapid = WzDataStorage.MAP.getRandom();
                TacosMap map_to = chr.findMap(mapid);
                chr.changeMapPortal(map_to, map_to.getPortal(0));

                chr.DebugMsg("randommap : " + map_to.getId());
                DebugLogger.InfoLog("randommap : " + map_to.getId());
                return true;
            }
            case "/randombgm" -> {
                String bgm = WzXML.SOUND.getRandomBGM();
                map.setChangeBGM(bgm);

                chr.DebugMsg("randombgm : " + bgm);
                return true;
            }
            case "/prevmap" -> {
                int index = DWI_Random.getMapIndex(chr.getPosMap());
                int map_id = DWI_Random.getMapByIndex(index - 1);

                if (map_id <= 0) {
                    return true;
                }

                chr.changeMapById(map_id);
                return true;
            }
            case "/nextmap" -> {
                int index = DWI_Random.getMapIndex(chr.getPosMap());
                int map_id = DWI_Random.getMapByIndex(index + 1);

                if (map_id <= 0) {
                    return true;
                }

                chr.changeMapById(map_id);
                return true;
            }
            case "/portal" -> {
                for (TacosPortal portal : map.getPortals()) {
                    int map_id = portal.getTargetMapId();
                    int type = portal.getType();
                    if (map_id == map.getId() || (map_id == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID && portal.getScriptName() == null)) {
                        continue;
                    }
                    chr.DebugMsg(portal.getId() + " : " + map_id + "(" + type + "), " + portal.getScriptName());
                }
                return true;
            }
            case "/portals" -> {
                ArrayList<Integer> map_ids = new ArrayList<>();

                for (TacosPortal portal : map.getPortals()) {
                    int map_id = portal.getTargetMapId();
                    int type = portal.getType();

                    if (map_id == map.getId() || (map_id == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID && portal.getScriptName() == null)) {
                        continue;
                    }

                    chr.DebugMsg(portal.getId() + " : " + map_id + "(" + type + "), " + portal.getScriptName());
                    map_ids.add(map_id);
                }
                if (map_ids.isEmpty()) {
                    map_ids.add(map.getId());
                }

                chr.DebugMsg("portals : " + map_ids.size());
                DebugMan_Warp dm = new DebugMan_Warp(map_ids);
                dm.start(chr);
                return true;
            }
            case "/townmap" -> {
                ArrayList<Integer> town_map_ids = new ArrayList<>();

                for (int map_id : WzDataStorage.MAP.getIds()) {
                    MapWz.MapInfo map_info = WzXML.MAP.getMapInfo(map_id);
                    if (map_info != null) {
                        if (map_info.getTown() != 0) {
                            int return_map_id = map_info.getReturnMap();
                            if (map_id == return_map_id) {
                                town_map_ids.add(map_id);
                            }
                        }
                    }
                }

                chr.DebugMsg("townmap : " + town_map_ids.size());
                DebugMan_Warp dm = new DebugMan_Warp(town_map_ids);
                dm.start(chr);
                return true;
            }
            case "/bossmap" -> {
                DebugMan_WarpBoss dm = new DebugMan_WarpBoss();
                dm.setMasterMonsters();
                dm.start(chr);
                return true;
            }
            // map.
            case "/removeall" -> {
                map.removeAllObjects();
                return true;
            }
            case "/regen" -> {
                int map_id = map.getId();
                chr.getChannelServer().removeMap(map_id);
                chr.changeMapById(map_id);
                return true;
            }
            case "/addmm" -> {
                if (!dcmd.check(1)) {
                    return true;
                }

                int mob_id = dcmd.getInt(1);

                if (!WzDataStorage.MOB.check(mob_id)) {
                    DebugLogger.ErrorLog("getMasterMonster : invalid mob id, " + mob_id);
                    return true;
                }

                TacosBossSpawnPoint bsp = new TacosBossSpawnPoint();

                bsp.setId(mob_id);
                bsp.setMobTime(1000);
                map.getBossSpawnPoint().add(bsp);
                return true;
            }
            // others.
            case "/slidemsg" -> {
                TacosChannel srv_channel = chr.getChannelServer();
                if (!dcmd.check(1)) {
                    srv_channel.setServerMessage("");
                    srv_channel.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_SLIDE, PB_BroadcastMsg.builder().message(srv_channel.getServerMessage()).build()));
                    return true;
                }
                srv_channel.setServerMessage(dcmd.get(1));
                srv_channel.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_SLIDE, PB_BroadcastMsg.builder().message(srv_channel.getServerMessage()).build()));
                return true;
            }
            case "/hm" -> {
                List<Integer> ids = WzXML.ITEM.getItemIds(503);

                if (chr.getFootholdId() <= 0) {
                    return true;
                }

                List<TacosMerchant> hms = new ArrayList<>();
                Random rand = new Random();
                int count = 0;
                for (TacosFoothold mfh : map.getFootholds().values()) {
                    if (30 < count) {
                        break;
                    }
                    if (mfh.getId() < chr.getFootholdId() - 15) {
                        continue;
                    }
                    count++;
                    int id_inc = 0;
                    int fh_id = 0;
                    int fh_x = 0;
                    int fh_y = 0;
                    if (mfh.getId() == chr.getFootholdId()) {
                        fh_id = chr.getFootholdId();
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
                        for (TacosMerchant hm : hms) {
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
                    TacosMerchant hm = new TacosMerchant(chr, item_id, "DebugHiredMarchant");
                    hm.setTest(chr.getId() + id_inc, fh_id, ids.get(rand.nextInt(ids.size())), 7777 + id_inc);
                    hm.setPosition(new Point(fh_x, fh_y));
                    map.addMerchant(hm);
                    hms.add(hm);
                }
                return true;
            }
            default -> {
            }
        }

        return false;
    }
}
