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
import java.util.LinkedHashMap;
import java.util.List;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MonsterDropEntry;
import odin.server.maps.MapleMap;
import tacos.client.TacosSkill;
import tacos.constants.TacosConstants;
import tacos.database.query.DQ_DropData;
import tacos.unofficial.CustomMonsterBookDrop;
import tacos.wz.ServerImg;
import tacos.wz.ServerImg.RewardData;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosReward {

    public static boolean getReward(MapleCharacter chr, MapleMonster monster) {
        MapleMap map = monster.getMap();
        int mob_id = monster.getId();
        TacosSkill ts = (monster.getLastHitSkillId() != 0) ? WzXML.SKILL.getSkill(monster.getLastHitSkillId(), 1) : null; // temporary lv1.
        int hitAfter = (ts != null) ? ts.getHitAfter() : 0;

        ArrayList<RewardData> list_reward = ServerImg.BMS8.getRewardData(mob_id);
        if (list_reward != null) {
            MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
            int drop_count = 0;
            for (RewardData reward : list_reward) {
                if (Randomizer.nextInt(ServerImg.PROB_MAX) <= reward.getProb()) {
                    if (reward.getMoney() != 0) {
                        map.spawnMobMesoDrop(reward.getMoney(), map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, false, (byte) 0, hitAfter);
                    } else {
                        Item idrop = null;
                        // 装備

                        if (GameConstants.getInventoryType(reward.getItem()) == MapleInventoryType.EQUIP) {
                            idrop = miip.randomizeStats((Equip) miip.getEquipById(reward.getItem()));
                        } else {
                            // 通常アイテム
                            int range = reward.getMax() - reward.getMin();
                            int quantity = reward.getMin() + ((0 < range) ? Randomizer.nextInt(range) : 0);
                            idrop = new Item(reward.getItem(), (byte) 0, (short) quantity, (byte) 0);
                        }
                        map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, (byte) 0, (short) 0, hitAfter);
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
    private static final LinkedHashMap<Integer, ArrayList<MonsterDropEntry>> drop_table = new LinkedHashMap<>();

    public static ArrayList<MonsterDropEntry> getMonsterDrops(int mob_id) {
        if (drop_table.containsKey(mob_id)) {
            return drop_table.get(mob_id);
        }

        ArrayList<MonsterDropEntry> ret = DQ_DropData.getDropByMobId(mob_id);
        drop_table.put(mob_id, ret);
        return ret;
    }

    public static int dropFromDatabase(MapleCharacter chr, MapleMonster monster) {
        MapleMap map = monster.getMap();

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte drop_type = (byte) (monster.getStats().isExplosiveReward() ? 3 : monster.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        int cmServerrate = chr.getChannelServer().getMesoRate();
        int chServerrate = chr.getChannelServer().getDropRate();

        List<MonsterDropEntry> dropEntry = getMonsterDrops(monster.getId());
        Collections.shuffle(dropEntry);

        boolean forced_drop = monster.getStats().isBoss();

        int dropped_count = 0;

        // この辺でドロップ確定させるMobのIDのチェック処理を入れる
        for (MonsterDropEntry de : dropEntry) {
            if (de.itemId == monster.getStolen()) {
                continue;
            }

            // モンスターカード
            if (TacosConstants.is_monster_card(de.itemId)) {
                if (5 <= chr.getMonsterBook().getCardCount(de.itemId)) {
                    continue;
                }
            }

            // ボスは無条件でドロップ確定, 通常Mobはx/1000の確率でDBの値を参照してドロップする
            if (forced_drop || (Math.floor(Math.random() * 1000) < (int) (de.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0)))) {
                // メル
                if (de.itemId == 0) {
                    int mesos = de.Minimum;
                    if (de.Maximum > de.Minimum) {
                        mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;
                    }

                    if (mesos > 0) {
                        map.spawnMobMesoDrop((int) (mesos * (chr.getStat().mesoBuff / 100.0) * chr.getDropMod() * cmServerrate), map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, chr, false, drop_type, 0);
                        dropped_count++;
                    }
                } else {
                    Item idrop = null;
                    // 装備

                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        // 通常アイテム
                        int range = Math.abs(de.Maximum - de.Minimum);
                        idrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte) 0);
                    }

                    map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, chr, drop_type, de.questid, 0);
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
