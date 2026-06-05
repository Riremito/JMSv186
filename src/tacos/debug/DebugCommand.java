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
package tacos.debug;

import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.SkillFactory;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import tacos.property.Property_Packet;
import odin.constants.GameConstants;
import tacos.shared.SharedExpTable;
import tacos.wz.ids.DWI_Random;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.Item;
import tacos.packet.request.ReqCUser;
import tacos.packet.response.ResCNpcPool;
import tacos.packet.response.ResCUserLocal;
import odin.server.MapleItemInformationProvider;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleMonsterInformationProvider;
import odin.server.life.MapleNPC;
import odin.server.life.MonsterDropEntry;
import odin.server.life.PlayerNPC;
import odin.server.life.Spawns;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.maps.SavedLocationType;
import tacos.database.query.DQ_Accounts;
import odin.provider.IMapleData;
import odin.server.life.MobSkill;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleReactorStats;
import tacos.client.TacosForcedStat;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.OpsMobSkill;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.arg.ArgFieldEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptPortal;
import tacos.script.TacosScriptQuest;
import tacos.script.TacosScriptReactor;
import tacos.server.TacosChannel;
import tacos.server.TacosLogin;
import tacos.server.TacosWorld;
import tacos.server.map.TacosReward;
import tacos.server.map.TacosReward.Reward;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;
import tacos.wz.WzName;
import tacos.wz.WzNameStorage;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class DebugCommand {

    public static boolean checkCommand(MapleCharacter chr, String message) {
        DebugCommander dcmd = new DebugCommander(message);
        if (!dcmd.checkPrefix()) {
            // show shat message.
            return false;
        }

        if (executeCommand(dcmd, chr)) {
            return true;
        }

        if (TestCommand.executeCommand(dcmd, chr)) {
            return true;
        }

        if (CustomCommand.executeCommand(dcmd, chr)) {
            return true;
        }

        return true;
    }

    public static boolean executeCommand(DebugCommander dcmd, MapleCharacter chr) {
        MapleClient client = chr.getClient();
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/reload": {
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
            case "/shutdown": {
                // CTRL + C & Y
                if (chr.getName().equals("リレミト") || chr.getName().equals("Riremito")) {
                    System.exit(0);
                }
                return true;
            }
            case "/resetpassword": {
                if (!dcmd.check(2)) {
                    return true;
                }

                DQ_Accounts.resetPassword(dcmd.get(1), dcmd.get(2));
                chr.DebugMsg("reset password : " + dcmd.get(1));
                return true;
            }
            case "/save": {
                chr.saveToDB(false, false);
                chr.DebugMsg("save : done.");
                return true;
            }
            case "/ea":
            case "/stuck":
            case "/unlock": {
                chr.sendStatChanged(true);
                return true;
            }
            case "/players": {
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
            case "/npctalk": {
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
            case "/npctalk2": {
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
            case "/dm": {
                if (!dcmd.check(1)) {
                    DebugManTest dm_test = new DebugManTest();
                    dm_test.start(chr);
                    return true;
                }
                if (dcmd.get(1).equals("nm")) {
                    DebugMan_NM dm = new DebugMan_NM();
                    dm.start(chr);
                    return true;
                }
                if (dcmd.get(1).equals("cc")) {
                    DebugMan_CC dm = new DebugMan_CC();
                    dm.start(chr);
                    return true;
                }
                return true;
            }
            case "/ds": {
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
            case "/ds2": {
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
            // npc.
            case "/npc": {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);
                if (!WzDataStorage.NPC.check(npc_id)) {
                    chr.DebugMsg("npc : invalid id.");
                    return true;
                }
                MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
                npc.setPosition(chr.getPosition());
                npc.setCy(chr.getPosition().y);
                npc.setRx0(chr.getPosition().x - 50);
                npc.setRx1(chr.getPosition().x + 50);
                npc.setF(chr.getStance());
                npc.setFh(chr.getFH());
                npc.setCustom(true);
                map.addMapObject(npc);
                map.broadcastMessage(ResCNpcPool.NpcEnterField(npc, true));
                chr.DebugMsg("npc : " + npc_id);
                return true;
            }
            case "/pnpc": {
                PlayerNPC pnpc = new PlayerNPC(9901000, chr);
                pnpc.setPosition(chr.getPosition());
                pnpc.setCy(chr.getPosition().y);
                pnpc.setRx0(chr.getPosition().x - 50);
                pnpc.setRx1(chr.getPosition().x + 50);
                pnpc.setF(chr.getStance());
                pnpc.setFh(chr.getFH());
                map.addMapObject(pnpc);
                pnpc.sendSpawnData(chr.getClient());
                return true;
            }
            case "/npclocation": {
                if (!dcmd.check(1)) {
                    return true;
                }
                int npc_id = dcmd.getInt(1);
                if (!WzDataStorage.NPC.check(npc_id)) {
                    chr.DebugMsg("npclocation : invalid id.");
                    return true;
                }

                IMapleData npc_location = WzXML.ETC.getNpcLocation();
                if (npc_location == null) {
                    chr.DebugMsg("npclocation : NpcLocation.img is not found.");
                    return true;
                }
                npc_location = npc_location.getChildByPath(Integer.toString(npc_id));
                if (npc_location == null) {
                    chr.DebugMsg("npclocation : NpcLocation.img/npc_id is not found.");
                    return true;
                }

                WzName nd_npc = WzNameStorage.NPC.get(npc_id);

                if (nd_npc == null) {
                    chr.DebugMsg("npclocation : error.");
                    return true;
                }

                nd_npc.sendDebugMsg(chr);
                for (IMapleData data : npc_location) {
                    int map_id = WzDataTool.getInt(data);
                    WzName nd_map = WzNameStorage.MAP.get(map_id);
                    if (nd_map == null) {
                        chr.DebugMsg("ERROR.");
                        continue;
                    }
                    nd_map.sendMapDebugMsg(chr);
                }

                return true;
            }
            // reactor.
            case "/reactor": {
                if (!dcmd.check(1)) {
                    return true;
                }

                int reactor_id = dcmd.getInt(1);
                if (!WzDataStorage.REACTOR.check(reactor_id)) {
                    chr.DebugMsg("reactor : invalid id.");
                    return true;
                }

                MapleReactorStats reactorSt = WzXML.REACTOR.getReactor(reactor_id);
                if (reactorSt == null) {
                    chr.DebugMsg("reactor : reactorSt = null.");
                    return true;
                }

                MapleReactor reactor = new MapleReactor(reactorSt, reactor_id);
                reactor.setDelay(-1);

                Point pos = new Point(chr.getPosition());
                int foothold_id = chr.getFH();
                if (foothold_id == 0) {
                    chr.DebugMsg("reactor : foothold_id = 0.");
                    return true;
                }

                MapleFoothold fh = map.getFootholds().findFootHold(foothold_id);
                if (fh == null) {
                    chr.DebugMsg("reactor : fh = null.");
                    return true;
                }
                if (reactorSt.getBR() != null && reactorSt.getTL() != null) {
                    pos.y = fh.getY1() + ((reactorSt.getBR().y - reactorSt.getTL().y) / 2);
                }
                reactor.setPosition(pos);
                // spawn & hit
                map.spawnReactor(reactor);
                TacosScriptReactor.getInstance().act(client, reactor);
                chr.DebugMsg("reactor : " + reactor_id);
                return true;
            }
            case "/search": {
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
            case "/checkmapdata":
            case "/mapdata": {
                checkMapData(chr);
                return true;
            }
            case "/md2": {
                ArrayList<Integer> mob_ids = new ArrayList<>();
                for (Spawns sp : map.getMonsterSpawn()) {
                    int mob_id = sp.getMonster().getId();
                    if (!mob_ids.contains(mob_id)) {
                        mob_ids.add(mob_id);
                    }
                }
                for (int mob_id : mob_ids) {
                    chr.DebugMsg("[" + mob_id + " - " + WzNameStorage.MOB.get(mob_id).getName() + "]");
                    for (Reward reward : TacosReward.getRewardData(mob_id)) {
                        if (reward.item != 0) {
                            chr.DebugMsgItem(reward.item + " : " + String.format("%05.2f%%", reward.prob * 100.0 / TacosReward.PROB_MAX) + " - " + WzNameStorage.ITEM.get(reward.item).getName(), reward.item);
                        } else {
                            chr.DebugMsg("meso : " + String.format("%05.2f%%", reward.prob * 100.0 / TacosReward.PROB_MAX) + " - " + reward.money);
                        }
                    }
                }
                return true;
            }
            // client
            case "/dc":
            case "/disconnect": {
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
            // item
            case "/drop": {
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
                IItem item = is_equip ? ii.getEquipById(item_id) : new Item(item_id, (byte) 0, (short) item_quantity, (byte) 0);
                if (is_equip) {
                    item = ii.randomizeStats((Equip) item);
                }

                map.spawnItemDrop(chr, chr, item, chr.getPosition(), true, true);
                chr.DebugMsg("drop : " + item_id);
                return true;
            }
            // ボス関連
            case "/bosstest": {
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
            // Mob
            case "/mob":
            case "/spawn": {
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
                    if (15 < count) {
                        count = 15;
                    }
                }

                if (!WzDataStorage.MOB.check(mob_id)) {
                    chr.DebugMsg("mob : invalid id.");
                    return true;
                }

                for (int i = 0; i < count; i++) {
                    MapleMonster mosnter = MapleLifeFactory.getMonster(mob_id);
                    map.spawnMonsterOnGroundBelow(mosnter, chr.getPosition());
                }

                chr.DebugMsg("mob : " + mob_id);
                return true;
            }
            case "/killmob": {
                int count = 300;
                if (dcmd.check(1)) {
                    count = dcmd.getInt(1);
                }
                for (MapleMapObject mmo : map.getMapObjects(MapleMapObjectType.MONSTER)) {
                    if (count <= 0) {
                        break;
                    }
                    MapleMonster mob = (MapleMonster) mmo;
                    if (mob.getStats().getHPDisplayType() == 0) {
                        mob.setHp(0);
                        map.broadcastMessage(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_MobHPTag, mob)));
                    }
                    map.killMonster(mob, chr, true, false, (byte) 1);
                    count--;
                }

                chr.DebugMsg("killmob : done.");
                return true;
            }
            // mob skill.
            case "/ms":
            case "/disease": {
                if (!dcmd.check(1)) {
                    return true;
                }

                int mob_skill_id = dcmd.getInt(1);
                int mob_skill_level = 1;
                OpsMobSkill oms = OpsMobSkill.find(mob_skill_id);
                if (oms == OpsMobSkill.UNKNOWN) {
                    chr.DebugMsg("mobdkill : invalid or not supported id.");
                    return true;
                }
                MobSkill ms = WzXML.SKILL.getMobSkillData(mob_skill_id, mob_skill_level);
                if (ms == null) {
                    chr.DebugMsg("MobSkill : not found.");
                    return true;
                }

                int cts = oms.getDisease().get();
                int buff_id = mob_skill_id | (mob_skill_level << 16);
                int buff_effect = Math.max(ms.getX(), 1);
                int buff_time = dcmd.check(2) ? dcmd.getInt(2) : 5000;
                if (chr.getBuff().updateTest(cts, buff_id, buff_effect, buff_time)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }

                chr.DebugMsg("mobdkill : " + mob_skill_id);
                return true;
            }
            case "/mt": {
                if (!dcmd.check(1)) {
                    return true;
                }
                int cts = dcmd.getInt(1);
                int buff_id = -4000000;
                int buff_effect = dcmd.check(2) ? dcmd.getInt(2) : 1;
                int buff_time = dcmd.check(3) ? dcmd.getInt(3) : 5000;
                if (chr.getBuff().updateTest(dcmd.getInt(1), buff_id, buff_effect, buff_time)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                chr.DebugMsg("mt : " + OpsSecondaryStat.find(cts) + "(" + cts + "), buff_effect = " + buff_effect + ", buff_time =" + buff_time);
                return true;
            }
            case "/mt2": {
                if (!dcmd.check(3)) {
                    return true;
                }

                int cts = dcmd.getInt(1);
                if (!OpsSecondaryStat.find(cts).isTwoState()) {
                    chr.DebugMsg("mt2 : not a two state buff.");
                    return false;
                }

                int buff_id = -4000000;
                int buff_effect = dcmd.getInt(2);
                int buff_effect_2 = dcmd.getInt(3);
                int buff_time = dcmd.check(4) ? dcmd.getInt(4) : 5000;

                if (chr.getBuff().updateTest(dcmd.getInt(1), buff_id, buff_effect, buff_time, buff_effect_2)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                chr.DebugMsg("mt : " + OpsSecondaryStat.find(cts) + "(" + cts + "), buff_effect = " + buff_effect + ", " + buff_effect_2);
                return true;
            }
            case "/ride": {
                int buff_id = -4000000;
                int buff_effect = 1902000;
                int buff_effect_2 = 1004;
                int buff_time = dcmd.check(2) ? dcmd.getInt(2) : 5000;

                if (chr.getBuff().updateTest(OpsSecondaryStat.CTS_RideVehicle.get(), buff_id, buff_effect, buff_time, buff_effect_2)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                return true;
            }
            // ステータス関連
            case "/heal": {
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
            case "/autosp": {
                int skill_id = chr.getLastSkillUp();
                if (skill_id != 0) {
                    while (ReqCUser.OnSkillUpRequestInternal(chr, skill_id));
                }

                chr.DebugMsg("autosp : " + skill_id);
                return true;
            }
            case "/allskill":
            case "/job": {
                if (dcmd.check(1)) {
                    chr.setJob(dcmd.getInt(1));
                }

                DebugJob.AllSkill(chr);
                chr.DebugMsg("allskill : done.");
                return true;
            }
            case "/allskill0": {
                DebugJob.AllSkill(chr, true);
                chr.DebugMsg("allskill0 : done.");
                return true;
            }
            case "/allstat": {
                DebugJob.AllStat(chr);
                chr.DebugMsg("allstat : done.");
                return true;
            }
            case "/resetstat": {
                DebugJob.ResetStat(chr);
                chr.DebugMsg("resetstat : done.");
                return true;
            }
            case "/defstat": {
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
            case "/levelup": {
                int next_level = chr.getLevel() + 1;
                if (next_level <= 0 || 200 < next_level) {
                    return true;
                }
                if (GameConstants.isKOC(chr.getJob())) {
                    if (120 < next_level) {
                        return true;
                    }
                }

                chr.gainExp(SharedExpTable.getExpNeededForLevel(chr.getLevel()), true, true, true);
                chr.DebugMsg("levelup : done.");
                return true;
            }
            case "/level":
            case "/levelset": {
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
                    chr.gainExp(SharedExpTable.getExpNeededForLevel(i), true, true, true);
                }

                chr.DebugMsg("level : done.");
                return true;
            }
            case "/bs": {
                getBasicSkill(chr);
                chr.DebugMsg("beginner skill : done.");
                return true;
            }
            case "/rbs": {
                resetBasicSkill(chr);
                chr.DebugMsg("reset beginner skill : done.");
                return true;
            }
            case "/fs": {
                TacosForcedStat fs = chr.getForcedStat();
                int index = 1;
                fs.setSTR(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setDEX(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setINT(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setLUK(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setPAD(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setACC(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setEVA(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setSpeed(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setJump(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                chr.SendPacket(ResCWvsContext.ForcedStatSet(chr));
                chr.DebugMsg("forced stat set : STR DEX INT LUK PAD ACC EVA Speed Jump");
                return true;
            }
            // Map移動関連
            case "/map2":
            case "/mapt":
            case "/warp": {
                if (!dcmd.check(1)) {
                    return true;
                }
                int map_id = dcmd.getInt(1);

                if (map_id <= 0) {
                    return true;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/prevmap": {
                int index = DWI_Random.getMapIndex(chr.getPosMap());
                int map_id = DWI_Random.getMapByIndex(index - 1);

                if (map_id <= 0) {
                    return true;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/nextmap": {
                int index = DWI_Random.getMapIndex(chr.getPosMap());
                int map_id = DWI_Random.getMapByIndex(index + 1);

                if (map_id <= 0) {
                    return true;
                }

                changeMap(chr, map_id);
                return true;
            }
            case "/townmap": {
                int count = 0;
                for (int map_id : WzDataStorage.MAP.getIds()) {
                    IMapleData data = WzXML.MAP.getImg(map_id);
                    if (data != null) {
                        if (WzDataTool.getIntPath("info/town", data, 0) != 0) {
                            int return_map_id = WzDataTool.getIntPath("info/returnMap", data, 0);
                            if (map_id == return_map_id) {
                                WzName nd = WzNameStorage.MAP.get(map_id);
                                if (nd != null) {
                                    nd.sendMapDebugMsg(chr);
                                } else {
                                    chr.DebugMsg(map_id + " : ERROR.");
                                }
                                count++;
                            }
                        }
                    }
                }
                chr.DebugMsg("town map : " + count);
                return true;
            }
            case "/fm":
            case "/フリマ": {
                chr.saveLocation(SavedLocationType.FREE_MARKET, map.getReturnMap().getId());
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
                remoteNPCTalk(client, 9330104, 1012003);
                return true;
            }
            // ランダム関連
            case "/randombeauty": {
                int skin_id = WzDataStorage.SKIN.getRandom();
                int face_id = WzDataStorage.FACE.getRandom();
                int hair_id = WzDataStorage.HAIR.getRandom();

                chr.setSkinColor((byte) (skin_id % 100));
                chr.setFace(face_id);
                chr.setHair(hair_id);
                chr.sendStatChanged(false);
                chr.DebugMsg("random beauty : SkinID = " + skin_id + ", FaceID = " + face_id + ", HairID = " + hair_id);
                DebugLogger.InfoLog("random beauty : SkinID = " + skin_id + ", FaceID = " + face_id + ", HairID = " + hair_id);
                return true;
            }
            case "/randomdrop": {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int itemid = WzDataStorage.ITEM.getRandom();
                IItem toDrop = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? ii.randomizeStats((Equip) ii.getEquipById(itemid)) : new odin.client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);
                map.spawnItemDrop(chr, chr, toDrop, chr.getPosition(), true, true);
                String item_name = MapleItemInformationProvider.getInstance().getName(toDrop.getItemId());

                if (item_name == null) {
                    item_name = "<null>";
                }

                chr.DebugMsgItem("random drop : " + toDrop.getItemId() + " - " + item_name, toDrop.getItemId());
                DebugLogger.InfoLog("random drop : " + toDrop.getItemId() + " - " + item_name);
                return true;
            }
            case "/randomspawn": {
                int mob_count = 1;
                if (dcmd.check(1)) {
                    mob_count = dcmd.getInt(1);
                }

                if (10 < mob_count) {
                    mob_count = 10;
                }

                for (int i = 0; i < mob_count; i++) {
                    int mobid = WzDataStorage.MOB.getRandom();
                    DebugLogger.InfoLog("random spawn: " + mobid);
                    MapleMonster mob = MapleLifeFactory.getMonster(mobid);
                    map.spawnMonsterOnGroundBelow(mob, chr.getPosition());
                    chr.DebugMsg("random spawn : " + mob.getId() + " - " + mob.getStats().getName());
                    DebugLogger.InfoLog("random spawn : " + mob.getId() + " - " + mob.getStats().getName());
                }

                return true;
            }
            case "/randommap": {
                int mapid = WzDataStorage.MAP.getRandom();
                MapleMap map_to = chr.findMap(mapid);
                chr.changeMap(map_to, map_to.getPortal(0));
                chr.DebugMsg("random map : " + map_to.getId());
                DebugLogger.InfoLog("random map : " + map_to.getId());
                return true;
            }
            case "/randombgm": {
                String bgm = WzXML.SOUND.getRandomBGM();
                map.setChangeBGM(bgm);
                chr.DebugMsg("random BGM : " + bgm);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean changeMap(MapleCharacter chr, int map_id) {
        if (!WzDataStorage.MAP.check(map_id)) {
            return false;
        }

        MapleMap map = chr.findMap(map_id);
        chr.changeMap(map, map.getPortal(0));
        return true;
    }

    // bypass npc data checks
    public static boolean remoteNPCTalk(MapleClient client, int npc_id) {
        return remoteNPCTalk(client, npc_id, npc_id);
    }

    public static boolean remoteNPCTalk(MapleClient client, int npc_script_id, int npc_id) {
        MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }
        TacosScriptNPC.getInstance().start(client, npc_script_id, npc_id);
        return true;
    }

    public static boolean bossTest(MapleClient client, String boss_name) {
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
            ISkill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, skill.getMaxLevel(), skill.getMaxLevel());
        }
        return true;
    }

    private static boolean resetBasicSkill(MapleCharacter chr) {
        for (int skill_id : WzXML.SKILL.getBasicSkill(chr, debug_basic_job)) {
            chr.DebugMsg("RemoveSkill : " + skill_id);
            ISkill skill = SkillFactory.getSkill(skill_id);
            chr.changeSkillLevel(skill, (byte) 0, (byte) 0);
        }
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
            IMapleData md_mob = WzXML.STRING.getMob().getChildByPath(Integer.toString(mob_id));
            String mob_name = md_mob != null ? WzDataTool.getString(md_mob.getChildByPath("name"), "NO_NAME") : "NO_NAME";
            if (!WzDataStorage.MOB.check(mob_id)) {
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
