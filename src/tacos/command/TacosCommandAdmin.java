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
package tacos.command;

import odin.client.Skill;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.SkillFactory;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import tacos.property.Property_Packet;
import odin.constants.GameConstants;
import tacos.shared.TacosSharedExpTable;
import java.awt.Point;
import tacos.packet.response.ResCNpcPool;
import tacos.packet.response.ResCUserLocal;
import odin.server.MapleItemInformationProvider;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.life.PlayerNPC;
import odin.server.maps.MapleMap;
import tacos.database.query.DQ_Accounts;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleReactorStats;
import tacos.debug.DebugJob;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.response.ResCField;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptPortal;
import tacos.script.TacosScriptQuest;
import tacos.script.TacosScriptReactor;
import tacos.server.TacosChannel;
import tacos.server.TacosLogin;
import tacos.server.TacosWorld;
import tacos.server.map.TacosFoothold;
import tacos.wz.WzXML;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class TacosCommandAdmin {

    public static boolean executeCommand(TacosCommander dcmd, MapleCharacter chr) {
        TacosClient client = chr.getClient();
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/shutdown" -> {
                // CTRL + C & Y
                if (chr.getName().equals("リレミト") || chr.getName().equals("Riremito")) {
                    System.exit(0);
                }
                return true;
            }
            case "/reload" -> {
                Property_Packet.reload();
                TacosScriptPortal.getInstance().clearScripts();
                TacosScriptNPC.getInstance().clearScripts();
                TacosScriptQuest.getInstance().clearScripts();
                TacosScriptReactor.getInstance().clearScripts();
                DebugLogger.InfoLog("reload : done.");
                chr.sendStatChanged(true);
                chr.DebugMsg("reload : done.");
                return true;
            }
            case "/resetpassword" -> {
                if (!dcmd.check(2)) {
                    return true;
                }

                DQ_Accounts.resetPassword(dcmd.get(1), dcmd.get(2));
                chr.DebugMsg("ResetPassword : " + dcmd.get(1));
                return true;
            }
            case "/players" -> {
                TacosWorld world = chr.getWorld();
                {
                    TacosLogin srv_login = world.getLogin();
                    String msg = srv_login.getName() + " : ";
                    msg += "clients = " + srv_login.getClients().size() + ", ";
                    msg += "authorized = " + srv_login.getAuthorizedClients().size();
                    chr.DebugMsg(msg);
                }
                for (TacosChannel srv_channel : world.getChannels()) {
                    String msg = srv_channel.getName() + " : ";
                    String player_names = "";
                    for (MapleCharacter player : srv_channel.getOnlinePlayers().get()) {
                        if (player_names.length() != 0) {
                            player_names += ", ";
                        }
                        player_names += player.getName();
                    }
                    msg += player_names;
                    chr.DebugMsg(msg);
                }
                {
                    String msg = world.getITC().getName() + " : ";
                    String player_names = "";
                    for (MapleCharacter player : world.getITC().getOnlinePlayers().get()) {
                        if (player_names.length() != 0) {
                            player_names += ", ";
                        }
                        player_names += player.getName();
                    }
                    msg += player_names;
                    chr.DebugMsg(msg);
                }
                {
                    String msg = world.getCashShop().getName() + " : ";
                    String player_names = "";
                    for (MapleCharacter player : world.getCashShop().getOnlinePlayers().get()) {
                        if (player_names.length() != 0) {
                            player_names += ", ";
                        }
                        player_names += player.getName();
                    }
                    msg += player_names;
                    chr.DebugMsg(msg);
                }
                return true;
            }
            case "/warp" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int map_id = dcmd.getInt(1);

                if (map_id <= 0) {
                    return true;
                }

                chr.changeMapById(map_id);
                return true;
            }
            case "/npctalk" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);

                if (!WzDataStorage.NPC.check(npc_id) || !remoteNPCTalk(client, npc_id)) {
                    chr.DebugMsg("npctalk : invalid id.");
                    return true;
                }

                chr.DebugMsg("npctalk : " + npc_id);
                return true;
            }
            case "/npctalk2" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);
                // set Chief Stan
                if (!WzDataStorage.NPC.check(npc_id) || !remoteNPCTalk(client, npc_id, 1012003)) {
                    chr.DebugMsg("npctalk2 : invalid id.");
                    return true;
                }
                chr.DebugMsg("npctalk2 : " + npc_id);
                return true;
            }
            case "/npc" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);
                if (!WzDataStorage.NPC.check(npc_id)) {
                    chr.DebugMsg("npc : invalid id.");
                    return true;
                }
                MapleNPC npc = new MapleNPC(npc_id);
                npc.setPosition(chr.getPosition());
                npc.setCy(chr.getPosition().y);
                npc.setRx0(chr.getPosition().x - 50);
                npc.setRx1(chr.getPosition().x + 50);
                npc.setF(dcmd.check(2) ? dcmd.getInt(2) : chr.getMoveAction());
                npc.setFootholdId(chr.getFootholdId());

                map.addNPC(npc);
                chr.DebugMsg("npc : " + npc_id);
                return true;
            }
            case "/pnpc" -> {
                PlayerNPC pnpc = new PlayerNPC(9901000, chr);
                pnpc.setPosition(chr.getPosition());
                pnpc.setCy(chr.getPosition().y);
                pnpc.setRx0(chr.getPosition().x - 50);
                pnpc.setRx1(chr.getPosition().x + 50);
                pnpc.setF(dcmd.check(1) ? dcmd.getInt(1) : chr.getMoveAction());
                pnpc.setFootholdId(chr.getFootholdId());

                map.addNPC(pnpc);
                chr.SendPacket(ResCNpcPool.ImitatedNPCData(pnpc));
                chr.SendPacket(ResCNpcPool.NpcChangeController(pnpc, false));
                return true;
            }
            case "/reactor" -> {
                if (!dcmd.check(1)) {
                    return true;
                }

                int reactor_id = dcmd.getInt(1);
                if (!WzDataStorage.REACTOR.check(reactor_id)) {
                    chr.DebugMsg("reactor : invalid id.");
                    return true;
                }

                MapleReactor reactor = new MapleReactor(reactor_id);
                Point pos = new Point(chr.getPosition());
                int foothold_id = chr.getFootholdId();
                if (foothold_id == 0) {
                    chr.DebugMsg("reactor : foothold_id = 0.");
                    return true;
                }

                TacosFoothold fh = map.getFootholds().get(foothold_id);
                if (fh == null) {
                    chr.DebugMsg("reactor : fh = null.");
                    return true;
                }
                MapleReactorStats reactorSt = reactor.getStats();
                if (reactorSt.getBR() != null && reactorSt.getTL() != null) {
                    pos.y = fh.getY1() + ((reactorSt.getBR().y - reactorSt.getTL().y) / 2);
                }
                reactor.setPosition(pos);
                reactor.setMap(map);
                map.addReactor(reactor);
                TacosScriptReactor.getInstance().act(client, reactor);
                chr.DebugMsg("reactor : " + reactor_id);
                return true;
            }
            case "/dc", "/disconnect" -> {
                MapleCharacter target = chr;
                if (!dcmd.check(1)) {
                    target = chr.getWorld().findOnlinePlayer(dcmd.get(1));
                    if (target == null) {
                        chr.DebugMsg("dc : not found.");
                        return true;
                    }
                }
                chr.DebugMsg("dc : " + target.getName());
                target.getClient().getSession().close();
                return true;
            }
            case "/drop" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int item_id = dcmd.getInt(1);

                if (!WzDataStorage.ITEM.check(item_id)) {
                    return true;
                }
                int item_quantity = 1;
                boolean is_equip = item_id / 1000000 == 1;
                boolean is_pet = item_id / 10000 == 500;
                if ((!is_equip || !is_pet) && dcmd.check(2)) {
                    item_quantity = dcmd.getInt(2);
                }
                if (item_quantity < 0) {
                    item_quantity = 1;
                }
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                Item item = is_equip ? ii.getEquipById(item_id) : new Item(item_id, (byte) 0, (short) item_quantity, (byte) 0);
                if (is_equip) {
                    item = ii.randomizeStats((Equip) item);
                }

                map.spawnItemDrop(chr, chr, item, chr.getPosition(), true, true);
                chr.DebugMsg("drop : " + item_id);
                return true;
            }
            case "/bosstest" -> {
                if (!dcmd.check(1)) {
                    return true;
                }

                String boss_name = dcmd.get(1);
                if (!bossTest(client, boss_name)) {
                    chr.DebugMsg("bosstest : invalid Boss name.");
                    return true;
                }

                chr.DebugMsg("bosstest : " + boss_name);
                return true;
            }
            case "/mob", "/spawn" -> {
                int mob_id = 130101;
                int count = 1;
                if (dcmd.check(1)) {
                    mob_id = dcmd.getInt(1);
                }

                if (dcmd.check(2)) {
                    count = dcmd.getInt(2);
                    if (count < 0) {
                        count = 1;
                    }
                    if (1000 < count) {
                        count = 1000;
                    }
                }

                if (!WzDataStorage.MOB.check(mob_id)) {
                    chr.DebugMsg("mob : invalid id.");
                    return true;
                }

                for (int i = 0; i < count; i++) {
                    MapleMonster monster = MapleLifeFactory.getMonster(mob_id);
                    map.spawnMonsterOnGroundBelow(monster, chr.getPosition());
                }

                chr.DebugMsg("mob : " + mob_id);
                return true;
            }
            case "/killmob", "/killall" -> {
                int count = 1000;
                if (dcmd.check(1)) {
                    count = dcmd.getInt(1);
                }
                for (MapleMonster mob : map.getAllMonsters()) {
                    if (count <= 0) {
                        break;
                    }
                    if (mob.getStats().getHPDisplayType() == 0) {
                        mob.setHp(0);
                        map.broadcastMessage(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_MobHPTag, PB_FieldEffect.builder().monster(mob).build()));
                    }
                    map.killMonster(mob, chr, true, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC);
                    count--;
                }

                chr.DebugMsg("killmob : done.");
                return true;
            }
            case "/heal" -> {
                int new_hp = chr.getStat().getMaxHp();
                int new_mp = chr.getStat().getMaxMp();

                if (dcmd.check(2)) {
                    int ratio_hp = dcmd.getInt(1);
                    int ratio_mp = dcmd.getInt(2);
                    if (ratio_hp <= 0 || ratio_mp <= 0) {
                        ratio_hp = 100;
                        ratio_mp = 100;
                        chr.DebugMsg("Please, enter values between 1 - 100.");
                    }
                    new_hp = (int) (new_hp * (ratio_hp / 100.0));
                    new_mp = (int) (new_mp * (ratio_mp / 100.0));
                } else if (dcmd.check(1)) {
                    int ratio = dcmd.getInt(1);
                    if (ratio <= 0) {
                        ratio = 100;
                        chr.DebugMsg("Please, enter values between 1 - 100.");
                    }
                    new_hp = (int) (new_hp * (ratio / 100.0));
                    new_mp = (int) (new_mp * (ratio / 100.0));
                }
                int damage = chr.getStat().getHp() - new_hp;

                chr.SendPacket(ResCUserLocal.NotifyHPDecByField(damage));
                chr.getStat().setHp(new_hp);
                chr.getStat().setMp(new_mp);
                chr.sendStatChanged(true);

                chr.DebugMsg("heal : HP = " + chr.getStat().getHp() + " / " + chr.getStat().getMaxHp());
                chr.DebugMsg("heal : MP = " + chr.getStat().getMp() + " / " + chr.getStat().getMaxMp());
                return true;
            }
            case "/allskill", "/job" -> {
                if (dcmd.check(1)) {
                    chr.setJob(dcmd.getInt(1));
                }

                DebugJob.AllSkill(chr);
                chr.DebugMsg("allskill : done.");
                return true;
            }
            case "/allskill0" -> {
                DebugJob.AllSkill(chr, true);
                chr.DebugMsg("allskill0 : done.");
                return true;
            }
            case "/allstat" -> {
                DebugJob.AllStat(chr);
                chr.DebugMsg("allstat : done.");
                return true;
            }
            case "/resetstat" -> {
                DebugJob.ResetStat(chr);
                chr.DebugMsg("resetstat : done.");
                return true;
            }
            case "/defstat" -> {
                if (!dcmd.check(1)) {
                    return true;
                }

                int job_id = dcmd.getInt(1);
                int level = 0;
                if (dcmd.check(2)) {
                    level = dcmd.getInt(2);
                }

                DebugJob.DefStat(chr, job_id, level);
                chr.DebugMsg("defstat : done.");
                return true;
            }
            case "/levelup" -> {
                int next_level = chr.getLevel() + 1;
                if (next_level <= 0 || 200 < next_level) {
                    return true;
                }
                if (GameConstants.isKOC(chr.getJob())) {
                    if (120 < next_level) {
                        return true;
                    }
                }

                chr.gainExp(TacosSharedExpTable.getExpNeededForLevel(chr.getLevel()), true, true, true);
                chr.DebugMsg("levelup : done.");
                return true;
            }
            case "/level", "/levelset" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int new_level = dcmd.getInt(1);
                if (new_level <= 0 || 200 < new_level) {
                    return true;
                }
                if (GameConstants.isKOC(chr.getJob())) {
                    if (120 < new_level) {
                        return true;
                    }
                }

                if (new_level < chr.getLevel()) {
                    DebugJob.DefStat(chr, chr.getJob(), new_level);
                    return true;
                }

                for (int i = chr.getLevel(); i < new_level; i++) {
                    chr.gainExp(TacosSharedExpTable.getExpNeededForLevel(i), true, true, true);
                }

                chr.DebugMsg("level : done.");
                return true;
            }
            case "/bs" -> {
                getBasicSkill(chr);
                chr.DebugMsg("beginner skill : done.");
                return true;
            }
            case "/rbs" -> {
                resetBasicSkill(chr);
                chr.DebugMsg("reset beginner skill : done.");
                return true;
            }
            case "/jc", "/転職" -> {
                remoteNPCTalk(client, 9330104, 1012003);
                return true;
            }
            default -> {
            }
        }

        return false;
    }

    // bypass npc data checks
    public static boolean remoteNPCTalk(TacosClient client, int npc_id) {
        return remoteNPCTalk(client, npc_id, npc_id);
    }

    public static boolean remoteNPCTalk(TacosClient client, int npc_script_id, int npc_id) {
        TacosScriptNPC.getInstance().start(client, npc_script_id, npc_id);
        return true;
    }

    public static boolean bossTest(TacosClient client, String boss_name) {
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

        if (WzDataStorage.NPC.check(npc_id)) {
            remoteNPCTalk(client, npc_id);
        } else {
            remoteNPCTalk(client, npc_id, def_npc_id);
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
        for (int skill_id : WzXML.SKILL.getBasicSkill(chr, debug_basic_job)) {
            if (!checkDebugBasicSkill(skill_id)) {
                continue;
            }
            chr.DebugMsg("AddSkill : " + skill_id);
            Skill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, skill.getMaxLevel(), skill.getMaxLevel());
        }
        return true;
    }

    private static boolean resetBasicSkill(MapleCharacter chr) {
        for (int skill_id : WzXML.SKILL.getBasicSkill(chr, debug_basic_job)) {
            chr.DebugMsg("RemoveSkill : " + skill_id);
            Skill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, (byte) 0, (byte) 0);
        }
        return true;
    }
}
