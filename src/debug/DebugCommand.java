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

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.property.Property_Packet;
import constants.GameConstants;
import data.client.DC_Exp;
import data.wz.DW_Skill;
import data.wz.DW_String;
import data.wz.ids.DWI_Random;
import data.wz.ids.DWI_Validation;
import data.wz.ids.DWI_LoadXML;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.request.ReqCUser;
import packet.response.Res_JMS_CInstancePortalPool;
import packet.response.wrapper.ResWrapper;
import provider.MapleData;
import provider.MapleDataTool;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MonsterDropEntry;
import server.life.Spawns;
import server.maps.MapleDynamicPortal;
import server.maps.MapleMap;
import server.maps.SavedLocationType;

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

    public static int parseInt(String str) {
        int ret = 0;
        try {
            ret = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // do nothing
        }
        return ret;
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
            case "/testmsg": {
                chr.DebugMsg("BLUE.");
                chr.DebugMsg2("PINK.");
                chr.DebugMsg3("YELLOW.");
                return true;
            }
            case "/reload": {
                // PacketHeader設定再読み込み
                Property_Packet.reload();
                Debug.InfoLog("Packet Header values are reloaded.");
                chr.DebugMsg("Packet Header values are reloaded.");
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
                remoteNPCTalk(c, 9010021, 1012003);
                return true;
            }
            case "/npctalk": {
                if (splitted.length < 2) {
                    return false;
                }
                int npc_id = parseInt(splitted[1]);

                if (!DWI_Validation.isValidNPCID(npc_id) || !remoteNPCTalk(c, npc_id)) {
                    chr.DebugMsg("[RemoteNPCTalk] Invalid NPCID.");
                    return false;
                }

                chr.DebugMsg("[RemoteNPCTalk] " + npc_id);
                return true;
            }
            case "/npctalk2": {
                if (splitted.length < 2) {
                    return false;
                }
                int npc_id = parseInt(splitted[1]);
                // set Chief Stan
                if (!DWI_Validation.isValidNPCID(npc_id) || !remoteNPCTalk(c, npc_id, 1012003)) {
                    chr.DebugMsg("[RemoteNPCTalk2] Invalid NPCID.");
                    return false;
                }
                chr.DebugMsg("[RemoteNPCTalk2] " + npc_id);
                return true;
            }
            case "/dm": {
                if (splitted.length < 2) {
                    DebugManTest dm_test = new DebugManTest();
                    dm_test.start(chr);
                    return true;
                }
                if (splitted[1].equals("nm")) {
                    DebugMan_NM dm = new DebugMan_NM();
                    dm.start(chr);
                    return true;
                }
                return true;
            }
            case "/ds": {
                DebugShop ds = new DebugShop();

                if (splitted.length < 2) {
                    ds.setRandomItems(100);
                    ds.setRechargeAll();
                    ds.start(chr);
                    return true;
                }

                int item_sub_type = parseInt(splitted[1]);
                if (item_sub_type == 207 || item_sub_type == 233) {
                    ds.setRechargeAll();
                }
                ds.setItemTest(item_sub_type);
                ds.start(chr);
                return true;
            }

            case "/search": {
                if (splitted.length < 3) {
                    return false;
                }
                searchString(chr, splitted[1].toLowerCase(), splitted[2]);
                return true;
            }
            case "/checkmapdata":
            case "/mapdata": {
                checkMapData(chr);
                return true;
            }
            // ボス関連
            case "/bosstest": {
                if (splitted.length < 2) {
                    return false;
                }
                String boss_name = splitted[1];
                if (!bossTest(c, boss_name)) {
                    chr.DebugMsg("[BossTest] Invalid Boss name.");
                    return true;
                }
                chr.DebugMsg("[BossTest] " + boss_name);
                return true;
            }
            // ステータス関連
            case "/heal": {
                int new_hp = chr.getStat().getMaxHp();
                int new_mp = chr.getStat().getMaxMp();

                if (3 <= splitted.length) {
                    int ratio_hp = parseInt(splitted[1]);
                    int ratio_mp = parseInt(splitted[2]);
                    if (ratio_hp <= 0 || ratio_mp <= 0) {
                        ratio_hp = 100;
                        ratio_mp = 100;
                        chr.DebugMsg("Please, enter values between 1 - 100.");
                    }
                    new_hp = (int) (new_hp * (ratio_hp / 100.0));
                    new_mp = (int) (new_mp * (ratio_mp / 100.0));
                } else if (2 <= splitted.length) {
                    int ratio = parseInt(splitted[1]);
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
                    chr.setJob(parseInt(splitted[1]));
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
                int job_id = parseInt(splitted[1]);
                int level = 0;
                if (splitted.length >= 3) {
                    level = parseInt(splitted[2]);
                }
                DebugJob.DefStat(chr, job_id, level);
                return true;
            }
            case "/levelup": {
                int next_level = chr.getLevel() + 1;
                if (next_level <= 0 || 200 < next_level) {
                    return false;
                }
                if (GameConstants.isKOC(chr.getJob())) {
                    if (120 < next_level) {
                        return false;
                    }
                }
                chr.gainExp(DC_Exp.getExpNeededForLevel(chr.getLevel()), true, true, true);
                return true;
            }
            case "/level":
            case "/levelset": {
                if (splitted.length < 2) {
                    return false;
                }
                int new_level = parseInt(splitted[1]);
                if (new_level <= 0 || 200 < new_level) {
                    return false;
                }
                if (GameConstants.isKOC(chr.getJob())) {
                    if (120 < new_level) {
                        return false;
                    }
                }

                if (new_level < chr.getLevel()) {
                    DebugJob.DefStat(chr, chr.getJob(), new_level);
                    return true;
                }

                for (int i = chr.getLevel(); i < new_level; i++) {
                    chr.gainExp(DC_Exp.getExpNeededForLevel(i), true, true, true);
                }
                return true;
            }
            case "/bs": {
                getBasicSkill(chr);
                return true;
            }
            case "/rbs": {
                resetBasicSkill(chr);
                return true;
            }
            // Map移動関連
            case "/map2":
            case "/mapt":
            case "/warp": {
                if (splitted.length < 2) {
                    return false;
                }
                int map_id = parseInt(splitted[1]);

                if (map_id <= 0) {
                    return false;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/prevmap": {
                int index = DWI_Random.getMapIndex(c.getPlayer().getMapId());
                int map_id = DWI_Random.getMapByIndex(index - 1);

                if (map_id <= 0) {
                    return false;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/nextmap": {
                int index = DWI_Random.getMapIndex(c.getPlayer().getMapId());
                int map_id = DWI_Random.getMapByIndex(index + 1);

                if (map_id <= 0) {
                    return false;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/fm":
            case "/フリマ": {
                chr.saveLocation(SavedLocationType.FREE_MARKET, chr.getMap().getReturnMap().getId());
                changeMap(chr, 910000000);
                return true;
            }
            case "/henesys":
            case "/ヘネシス": {
                changeMap(chr, 100000000);
                return true;
            }
            case "/leafre":
            case "/リプレ": {
                changeMap(chr, 240000000);
                return true;
            }
            case "/magatia":
            case "/マガティア": {
                changeMap(chr, 261000000);
                return true;
            }
            case "/jc":
            case "/転職": {
                remoteNPCTalk(c, 9330104, 1012003);
                return true;
            }
            // ランダム関連
            case "/randombeauty": {
                int skinid = DWI_LoadXML.getSkin().getRandom();
                int faceid = DWI_LoadXML.getFace().getRandom();
                int hairid = DWI_LoadXML.getHair().getRandom();

                chr.setSkinColor((byte) (skinid % 100));
                chr.setFace(faceid);
                chr.setHair(hairid);
                chr.UpdateStat(false);
                chr.DebugMsg("[RandomBeauty] SkinID = " + skinid + ", FaceID = " + faceid + ", HairID = " + hairid);
                return true;
            }
            case "/randomdrop": {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int itemid = DWI_LoadXML.getItem().getRandom();
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
                    mob_count = parseInt(splitted[1]);
                }

                if (10 < mob_count) {
                    mob_count = 10;
                }

                for (int i = 0; i < mob_count; i++) {
                    int mobid = DWI_LoadXML.getMob().getRandom();
                    Debug.InfoLog("RandomSpawn: " + mobid);
                    MapleMonster mob = MapleLifeFactory.getMonster(mobid);
                    chr.getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
                    chr.DebugMsg("[RandomSpawn] " + mob.getId() + " - " + mob.getStats().getName());
                }

                return true;
            }
            case "/randommap": {
                int mapid = DWI_LoadXML.getMap().getRandom();
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
                int map_id_to = parseInt(splitted[1]);

                if (map_id_to == 0 || !DWI_Validation.isValidMapID(map_id_to)) {
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

    public static boolean changeMap(MapleCharacter chr, int map_id) {
        if (!DWI_Validation.isValidMapID(map_id)) {
            return false;
        }

        MapleMap map = chr.getClient().getChannelServer().getMapFactory().getMap(map_id);
        chr.changeMap(map, map.getPortal(0));
        return true;
    }

    // bypass npc data checks
    public static boolean remoteNPCTalk(MapleClient c, int npc_id) {
        return remoteNPCTalk(c, npc_id, npc_id);
    }

    public static boolean remoteNPCTalk(MapleClient c, int npc_script_id, int npc_id) {
        MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }
        NPCScriptManager.getInstance().start(c, npc_id, npc_script_id);
        return true;
    }

    public static boolean bossTest(MapleClient c, String boss_name) {
        int def_npc_id = 1012003; // Chief Stan
        int npc_id = 1012003;

        switch (boss_name) {
            // 遠征隊
            case "zakum": {
                npc_id = 2030008;
                break;
            }
            case "horntail":
            case "ht": {
                npc_id = 2083004;
                break;
            }
            case "pinkbean":
            case "pb": {
                npc_id = 2141001;
                break;
            }
            // JMS - 未来東京
            case "bergamot":
            case "odaiba": {
                npc_id = 9120040;
                break;
            }
            case "nibelung":
            case "sky": {
                npc_id = 9120039;
                break;
            }
            case "dunas1":
            case "akihabara": {
                npc_id = 0;
                break;
            }
            case "dunas2":
            case "shibuya": {
                npc_id = 9120052;
                break;
            }
            case "royalguard":
            case "roppongi1": {
                npc_id = 9120053;
                break;
            }
            case "coreblaze":
            case "roppongi2": {
                npc_id = 9120050;
                break;
            }
            case "aufhaven":
            case "roppongi3": // アウフヘーベン
            {
                npc_id = 0;
                break;
            }
            // エリアボス
            case "vicious": // ビシャスプラント
            {
                npc_id = 2041024; // test
                break;
            }
            // JMS - ジパング
            case "showa": // ボディーガード & 大親分
            {
                npc_id = 9120201;
                break;
            }
            // JMS - クリムゾンウッド
            case "cw": // クリムゾンウッド
            {
                npc_id = 9201112;
                break;
            }
            // JMS - 中国
            case "china1": // 大王ムカデ
            {
                npc_id = 9310004;
                break;
            }
            case "china2": // 武林妖僧
            {
                npc_id = 9310039;
                break;
            }
            // JMS - 台湾
            case "taiwan": // 屋台
            {
                npc_id = 9330028;
                break;
            }
            default: {
                return false;
            }
        }

        if (DWI_Validation.isValidNPCID(npc_id)) {
            remoteNPCTalk(c, npc_id);
        } else {
            remoteNPCTalk(c, npc_id, def_npc_id);
        }

        return true;
    }

    // basic skill test
    private static String debug_basic_job = "000.img";
    private static int debug_basic_skill_ids[] = {
        1003, // legendary spirit
        1004, // riding
        //1006, // jump down
        1007, // item maker
    };

    private static boolean checkDebugBasicSkill(int skill_id) {
        for (int id : debug_basic_skill_ids) {
            if (id == skill_id) {
                return true;
            }
        }
        return false;
    }

    private static boolean getBasicSkill(MapleCharacter chr) {
        for (int skill_id : DW_Skill.getBasicSkill(chr, debug_basic_job)) {
            if (!checkDebugBasicSkill(skill_id)) {
                continue;
            }
            chr.DebugMsg("AddSkill : " + skill_id);
            ISkill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, skill.getMaxLevel(), skill.getMaxLevel());
        }
        return true;
    }

    private static boolean resetBasicSkill(MapleCharacter chr) {
        for (int skill_id : DW_Skill.getBasicSkill(chr, debug_basic_job)) {
            chr.DebugMsg("RemoveSkill : " + skill_id);
            ISkill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, (byte) 0, (byte) 0);
        }
        return true;
    }

    static public class NameData {

        public int id = 0;
        boolean available = true;
        public String name = null;
        public String mapName = null;
        public String streetName = null;
    }

    private static ArrayList<NameData> list_NameData_Npc = null;
    private static ArrayList<NameData> list_NameData_Mob = null;
    private static ArrayList<NameData> list_NameData_Item = null;
    private static ArrayList<NameData> list_NameData_Map = null;
    private static ArrayList<NameData> list_NameData_Skill = null;

    private static boolean searchString(MapleCharacter chr, String type, String search_name) {

        switch (type) {
            case "npc": {
                if (list_NameData_Npc == null) {
                    list_NameData_Npc = new ArrayList<>();
                    for (MapleData wz_data : DW_String.getNpc().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidNPCID(id);
                        nd.name = name;
                        list_NameData_Npc.add(nd);
                    }

                }
                for (NameData nd : list_NameData_Npc) {
                    if (nd.name.contains(search_name)) {
                        if (nd.available) {
                            chr.DebugMsg(nd.id + " : \"" + nd.name + "\"");
                        } else {
                            chr.DebugMsg2(nd.id + " : \"" + nd.name + "\"");
                        }
                    }
                }
                return true;
            }
            case "mob": {
                if (list_NameData_Mob == null) {
                    list_NameData_Mob = new ArrayList<>();
                    for (MapleData wz_data : DW_String.getMob().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidMobID(id);
                        nd.name = name;
                        list_NameData_Mob.add(nd);
                    }

                }
                for (NameData nd : list_NameData_Mob) {
                    if (nd.name.contains(search_name)) {
                        if (nd.available) {
                            chr.DebugMsg(nd.id + " : \"" + nd.name + "\"");
                        } else {
                            chr.DebugMsg2(nd.id + " : \"" + nd.name + "\"");
                        }
                    }
                }
                return true;
            }
            case "item": {
                if (list_NameData_Item == null) {
                    list_NameData_Item = new ArrayList<>();
                    for (MapleData wz_root : DW_String.getEqp().getChildren()) {
                        for (MapleData wz_data : wz_root.getChildren()) {
                            int id = Integer.parseInt(wz_data.getName());
                            String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                            NameData nd = new NameData();
                            nd.id = id;
                            nd.available = DWI_Validation.isValidItemID(id);
                            nd.name = name;
                            list_NameData_Item.add(nd);
                        }
                    }
                    for (MapleData wz_data : DW_String.getConsume().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidItemID(id);
                        nd.name = name;
                        list_NameData_Item.add(nd);
                    }
                    for (MapleData wz_data : DW_String.getIns().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidItemID(id);
                        nd.name = name;
                        list_NameData_Item.add(nd);
                    }
                    for (MapleData wz_data : DW_String.getEtc().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidItemID(id);
                        nd.name = name;
                        list_NameData_Item.add(nd);
                    }
                    for (MapleData wz_data : DW_String.getPet().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidItemID(id);
                        nd.name = name;
                        list_NameData_Item.add(nd);
                    }
                    for (MapleData wz_data : DW_String.getCash().getChildren()) {
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = DWI_Validation.isValidItemID(id);
                        nd.name = name;
                        list_NameData_Item.add(nd);
                    }
                }
                for (NameData nd : list_NameData_Item) {
                    if (nd.name.contains(search_name)) {
                        if (nd.available) {
                            chr.DebugMsgItem(nd.id + " : \"" + nd.name + "\"", nd.id);
                        } else {
                            chr.DebugMsg2(nd.id + " : \"" + nd.name + "\"");
                        }
                    }
                }
                return true;
            }
            case "map": {
                if (list_NameData_Map == null) {
                    list_NameData_Map = new ArrayList<>();
                    for (MapleData wz_root : DW_String.getMap().getChildren()) {
                        for (MapleData wz_data : wz_root.getChildren()) {
                            int id = Integer.parseInt(wz_data.getName());
                            String mapName = MapleDataTool.getString(wz_data.getChildByPath("mapName"), "");
                            String streetName = MapleDataTool.getString(wz_data.getChildByPath("streetName"), "");
                            NameData nd = new NameData();
                            nd.id = id;
                            nd.available = DWI_Validation.isValidMapID(id); // test
                            nd.mapName = mapName;
                            nd.streetName = streetName;
                            list_NameData_Map.add(nd);
                        }
                    }

                }
                for (NameData nd : list_NameData_Map) {
                    if (nd.mapName.contains(search_name) || nd.streetName.contains(search_name)) {
                        if (nd.available) {
                            chr.DebugMsg(nd.id + " : \"" + nd.streetName + "\" - \"" + nd.mapName + "\"");
                        } else {
                            chr.DebugMsg2(nd.id + " : \"" + nd.streetName + "\" - \"" + nd.mapName + "\"");
                        }
                    }
                }
                return true;
            }
            case "skill": {
                if (list_NameData_Skill == null) {
                    list_NameData_Skill = new ArrayList<>();
                    for (MapleData wz_data : DW_String.getSkill().getChildren()) {
                        if (wz_data.getChildByPath("bookName") != null) {
                            continue;
                        }
                        int id = Integer.parseInt(wz_data.getName());
                        String name = MapleDataTool.getString(wz_data.getChildByPath("name"), "");
                        NameData nd = new NameData();
                        nd.id = id;
                        nd.available = true; // test
                        nd.name = name;
                        list_NameData_Skill.add(nd);
                    }

                }
                for (NameData nd : list_NameData_Skill) {
                    if (nd.name.contains(search_name)) {
                        if (nd.available) {
                            chr.DebugMsg(nd.id + " : \"" + nd.name + "\"");
                        } else {
                            chr.DebugMsg2(nd.id + " : \"" + nd.name + "\"");
                        }
                    }
                }
                return true;
            }
            default: {
                break;
            }
        }

        chr.DebugMsg("searchString==");
        return true;
    }

    private static boolean checkMapData(MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        List<Integer> mob_ids = new ArrayList<>();
        List<Integer> mob_counts = new ArrayList<>();
        for (Spawns s : map.getMonsterSpawn()) {
            int id = s.getMonster().getId();
            int index = mob_ids.indexOf(id);
            if (index != -1) {
                mob_counts.set(index, mob_counts.get(index) + 1);
                continue;
            }
            mob_ids.add(id);
            mob_counts.add(1);
        }

        for (int i = 0; i < mob_ids.size(); i++) {
            int mob_id = mob_ids.get(i);
            int mob_count = mob_counts.get(i);
            MapleData md_mob = DW_String.getMob().getChildByPath(Integer.toString(mob_id));
            String mob_name = md_mob != null ? MapleDataTool.getString(md_mob.getChildByPath("name"), "NO_NAME") : "NO_NAME";
            if (!DWI_Validation.isValidMobID(mob_id)) {
                chr.DebugMsg2("[" + mob_id + " (" + mob_count + ") : \"" + mob_name + "\" ]");
                continue;
            }
            chr.DebugMsg("[" + mob_id + " (" + mob_count + ") : \"" + mob_name + "\" ]");
            for (MonsterDropEntry mde : MapleMonsterInformationProvider.getInstance().retrieveDrop(mob_id)) {
                chr.DebugMsg(mde.itemId + " : " + mde.chance);
            }
        }

        return true;
    }

}
