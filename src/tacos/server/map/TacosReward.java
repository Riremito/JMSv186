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
package tacos.server.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import odin.constants.GameConstants;
import odin.provider.IMapleData;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MapleMonsterInformationProvider;
import odin.server.life.MonsterDropEntry;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.unofficial.CustomMonsterBookDrop;
import tacos.wz.ServerImg;
import tacos.wz.WzDataStorage;
import tacos.wz.WzDataTool;

/**
 *
 * @author Riremito
 */
public class TacosReward {

    public static final int PROB_MAX = 1000000;

    public static class Reward {

        public int money;
        public int item;
        public int prob;
        public int min;
        public int max;
    }

    private static TreeMap<Integer, ArrayList<Reward>> REWARDS = new TreeMap<>();

    public static ArrayList<Reward> getRewardData(int mob_id) {
        ArrayList<Reward> list_reward = REWARDS.get(mob_id);
        if (list_reward != null) {
            // already loaded.
            return list_reward;
        }

        list_reward = new ArrayList<>();
        IMapleData mob_drop_table = ServerImg.SI.getReward().getChildByPath(String.format("m%07d", mob_id));
        // found.
        if (mob_drop_table != null) {
            for (IMapleData mob_drop : mob_drop_table.getChildren()) {
                Reward reward = new Reward();
                reward.money = WzDataTool.getIntPath("money", mob_drop, 0);
                reward.item = WzDataTool.getIntPath("item", mob_drop, 0);
                String prob_str = WzDataTool.getStringPath("prob", mob_drop, "[R8]0.0").replace("[R8]", "");
                reward.prob = (int) (Double.parseDouble(prob_str) * PROB_MAX);
                reward.min = WzDataTool.getIntPath("min", mob_drop, 1);
                reward.max = WzDataTool.getIntPath("max", mob_drop, 1);

                if (reward.item != 0) {
                    if (!WzDataStorage.ITEM.check(reward.item)) {
                        DebugLogger.ErrorLog("getReward : " + mob_id + ", invalid item = " + reward.item);
                        continue;
                    }
                }

                list_reward.add(reward);
            }
        }

        REWARDS.put(mob_id, list_reward);
        DebugLogger.XmlLog("getReward : " + mob_id + ", count = " + list_reward.size());
        return list_reward;
    }

    public static boolean getReward(MapleCharacter chr, MapleMonster monster) {
        MapleMap map = monster.getMap();
        int mob_id = monster.getId();

        /*
        ArrayList<Reward> list_reward = REWARDS.get(mob_id);
        if (list_reward == null) {
            IMapleData mob_drop_table = ServerImg.SI.getReward().getChildByPath(String.format("m%07d", mob_id));
            if (mob_drop_table != null) {
                list_reward = new ArrayList<>();
                for (IMapleData mob_drop : mob_drop_table.getChildren()) {
                    Reward reward = new Reward();
                    reward.money = WzDataTool.getIntPath("money", mob_drop, 0);
                    reward.item = WzDataTool.getIntPath("item", mob_drop, 0);
                    String prob_str = WzDataTool.getStringPath("prob", mob_drop, "[R8]0.0").replace("[R8]", "");
                    reward.prob = (int) (Double.parseDouble(prob_str) * PROB_MAX);
                    reward.min = WzDataTool.getIntPath("min", mob_drop, 1);
                    reward.max = WzDataTool.getIntPath("max", mob_drop, 1);

                    if (reward.item != 0) {
                        if (!WzDataStorage.ITEM.check(reward.item)) {
                            chr.DebugMsg("getReward : " + mob_id + ", invalid item = " + reward.item);
                            DebugLogger.ErrorLog("getReward : " + mob_id + ", invalid item = " + reward.item);
                            continue;
                        }
                    }

                    list_reward.add(reward);
                }

                DebugLogger.XmlLog("getReward : " + mob_id + ", count = " + list_reward.size());
                REWARDS.put(mob_id, list_reward);
            }
        }
         */
        ArrayList<Reward> list_reward = getRewardData(mob_id);
        if (!list_reward.isEmpty()) {
            MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
            int drop_count = 0;
            for (Reward reward : list_reward) {
                if (Randomizer.nextInt(PROB_MAX) <= reward.prob) {
                    if (reward.money != 0) {
                        map.spawnMobMesoDrop(reward.money, map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, false, (byte) 0);
                    } else {
                        IItem idrop = null;
                        // 装備

                        if (GameConstants.getInventoryType(reward.item) == MapleInventoryType.EQUIP) {
                            idrop = miip.randomizeStats((Equip) miip.getEquipById(reward.item));
                        } else {
                            // 通常アイテム
                            int range = reward.max - reward.min;
                            int quantity = reward.min + ((0 < range) ? Randomizer.nextInt(range) : 0);
                            idrop = new Item(reward.item, (byte) 0, (short) quantity, (byte) 0);
                        }
                        map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, (byte) 0, (short) 0);
                    }
                    drop_count++;
                }
            }
            return true;
        }

        // drop database
        if (0 < dropFromDatabase(chr, monster)) {
            return true;
        }
        // drop monster book
        if (0 < CustomMonsterBookDrop.dropFromMonsterBook(chr, monster)) {
            return true;
        }

        return false;
    }

    // odin style.
    public static int dropFromDatabase(MapleCharacter chr, MapleMonster monster) {
        MapleMap map = monster.getMap();

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte drop_type = (byte) (monster.getStats().isExplosiveReward() ? 3 : monster.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        int cmServerrate = chr.getChannelServer().getMesoRate();
        int chServerrate = chr.getChannelServer().getDropRate();

        double showdown = 100.0;
        MonsterStatusEffect mse = monster.getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        List<MonsterDropEntry> dropEntry = mi.retrieveDrop(monster.getId());
        Collections.shuffle(dropEntry);

        boolean forced_drop = monster.getStats().isBoss();

        int dropped_count = 0;

        // この辺でドロップ確定させるMobのIDのチェック処理を入れる
        for (MonsterDropEntry de : dropEntry) {
            if (de.itemId == monster.getStolen()) {
                continue;
            }

            // モンスターカード
            if (GameConstants.isMonsterCard(de.itemId)) {
                if (chr.getMonsterBook().getLevel(de.itemId) >= 5) {
                    continue;
                }
            }

            // ボスは無条件でドロップ確定, 通常Mobはx/1000の確率でDBの値を参照してドロップする
            if (forced_drop || (Math.floor(Math.random() * 1000) < (int) (de.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0) * (showdown / 100.0)))) {
                // メル
                if (de.itemId == 0) {
                    int mesos = de.Minimum;
                    if (de.Maximum > de.Minimum) {
                        mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;
                    }

                    if (mesos > 0) {
                        map.spawnMobMesoDrop((int) (mesos * (chr.getStat().mesoBuff / 100.0) * chr.getDropMod() * cmServerrate), map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, chr, false, drop_type);
                        dropped_count++;
                    }
                } else {
                    IItem idrop = null;
                    // 装備

                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        // 通常アイテム
                        int range = Math.abs(de.Maximum - de.Minimum);
                        idrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte) 0);
                    }

                    map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, chr, drop_type, de.questid);
                    dropped_count++;
                }
            }
        }

        return dropped_count;
    }

    public static Point getDropPosition(MapleMonster monster, int drop_type, int dropped_count) {
        Point drop_pos = monster.getPosition();

        if (drop_type == 3) {
            drop_pos.x += (dropped_count % 2 == 0) ? (40 * (dropped_count + 1) / 2) : -(40 * (dropped_count / 2));
            return drop_pos;
        }

        drop_pos.x += (dropped_count % 2 == 0) ? (25 * (dropped_count + 1) / 2) : -(25 * (dropped_count / 2));
        return drop_pos;
    }
}
