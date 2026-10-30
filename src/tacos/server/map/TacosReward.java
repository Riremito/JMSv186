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
import tacos.server.map.object.TacosReactor;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosMonster;
import java.util.ArrayList;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import tacos.client.TacosSkill;
import tacos.unofficial.CustomMonsterBookDrop;
import tacos.wz.ServerImg;
import tacos.wz.ServerImg.RewardData;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosReward {

    public static boolean getReward(TacosCharacter chr, TacosMonster monster) {
        TacosMap map = monster.getMap();
        int mob_id = monster.getId();
        TacosSkill ts = (monster.getLastHitSkillId() != 0) ? WzXML.SKILL.getSkill(monster.getLastHitSkillId(), 1) : null; // temporary lv1.
        int hitAfter = (ts != null) ? ts.getHitAfter() : 0;

        int server_drop_rate = chr.getChannelServer().getDropRate();

        ArrayList<RewardData> list_reward = ServerImg.getAutoRewardData(mob_id);
        if (list_reward != null) {
            MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
            int drop_count = 0;
            for (RewardData reward : list_reward) {
                if (reward.getItem() != 0 && reward.getItem() == monster.getStolen()) {
                    continue;
                }
                if (Randomizer.nextInt(ServerImg.PROB_MAX) <= (reward.getProb() * server_drop_rate)) {
                    if (reward.getMoney() != 0) {
                        map.spawnMobMesoDrop(reward.getMoney(), map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, false, (byte) 0, hitAfter);
                    } else {
                        Item idrop = null;
                        // 装備

                        if (GameConstants.getInventoryType(reward.getItem()) == MapleInventoryType.EQUIP) {
                            idrop = miip.getEquipById(reward.getItem());
                        } else {
                            // 通常アイテム
                            int range = reward.getMax() - reward.getMin();
                            int quantity = reward.getMin() + ((0 < range) ? Randomizer.nextInt(range) : 0);
                            idrop = new Item(reward.getItem(), (byte) 0, (short) quantity, (byte) 0);
                        }
                        map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, 0, drop_count), monster.getPosition()), monster, chr, (byte) 0, reward.getQuestId(), hitAfter);
                    }
                    drop_count++;
                }
            }
            return true;
        }

        // drop monster book
        if (0 < CustomMonsterBookDrop.dropFromMonsterBook(chr, monster)) {
            return true;
        }

        return false;
    }

    // Reward.img (r + reactor id), returns the number of drops or -1 if the reactor does not have the data.
    public static int getReactorReward(TacosCharacter chr, TacosReactor reactor) {
        ArrayList<RewardData> list_reward = ServerImg.getAutoReactorRewardData(reactor.getId());
        if (list_reward == null || list_reward.isEmpty()) {
            return -1;
        }

        TacosMap map = reactor.getMap();
        int server_drop_rate = chr.getChannelServer().getDropRate();
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int drop_count = 0;
        for (RewardData reward : list_reward) {
            if ((reward.getProb() * server_drop_rate) < Randomizer.nextInt(ServerImg.PROB_MAX)) {
                continue;
            }
            Point drop_pos = getDropPosition(reactor.getPosition(), drop_count);
            if (reward.getMoney() != 0) {
                map.spawnMesoDrop(reward.getMoney(), drop_pos, reactor.getPosition(), chr, false, (byte) 0);
            } else {
                Item idrop;
                if (GameConstants.getInventoryType(reward.getItem()) == MapleInventoryType.EQUIP) {
                    idrop = miip.getEquipById(reward.getItem());
                } else {
                    int range = reward.getMax() - reward.getMin();
                    int quantity = reward.getMin() + ((0 < range) ? Randomizer.nextInt(range) : 0);
                    idrop = new Item(reward.getItem(), (byte) 0, (short) quantity, (byte) 0);
                }
                map.spawnItemDropByReactor(reactor, chr, idrop, drop_pos, reward.getQuestId(), false);
            }
            drop_count++;
        }
        return drop_count;
    }

    private static Point getDropPosition(Point base_pos, int dropped_count) {
        Point drop_pos = new Point(base_pos);
        drop_pos.x += (dropped_count % 2 == 0) ? (25 * (dropped_count + 1) / 2) : -(25 * (dropped_count / 2));
        return drop_pos;
    }

    public static Point getDropPosition(TacosMonster monster, int drop_type, int dropped_count) {
        Point drop_pos = monster.getPosition();

        if (drop_type == 3) {
            drop_pos.x += (dropped_count % 2 == 0) ? (40 * (dropped_count + 1) / 2) : -(40 * (dropped_count / 2));
            return drop_pos;
        }

        drop_pos.x += (dropped_count % 2 == 0) ? (25 * (dropped_count + 1) / 2) : -(25 * (dropped_count / 2));
        return drop_pos;
    }
}
