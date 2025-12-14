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
import java.util.Collections;
import java.util.List;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MapleMonsterInformationProvider;
import odin.server.life.MonsterDropEntry;
import odin.server.maps.MapleMap;
import tacos.server.ServerOdinGame;

/**
 *
 * @author Riremito
 */
public class MonsterDrop {

    public static int dropFromDatabase(int channel, MapleCharacter player, MapleMonster monster) {
        MapleMap map = monster.getMap();

        if (map.getItemsSize() >= 225) {
            map.removeDrops();
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte drop_type = (byte) (monster.getStats().isExplosiveReward() ? 3 : monster.getStats().isFfaLoot() ? 2 : player.getParty() != null ? 1 : 0);
        int cmServerrate = ServerOdinGame.getInstance(channel).getMesoRate();
        int chServerrate = ServerOdinGame.getInstance(channel).getDropRate();

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
                if (player.getMonsterBook().getLevel(de.itemId) >= 5) {
                    continue;
                }
            }

            // ボスは無条件でドロップ確定, 通常Mobはx/1000の確率でDBの値を参照してドロップする
            if (forced_drop || (Math.floor(Math.random() * 1000) < (int) (de.chance * chServerrate * player.getDropMod() * (player.getStat().dropBuff / 100.0) * (showdown / 100.0)))) {
                // メル
                if (de.itemId == 0) {
                    int mesos = de.Minimum;
                    if (de.Maximum > de.Minimum) {
                        mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;
                    }

                    if (mesos > 0) {
                        map.spawnMobMesoDrop((int) (mesos * (player.getStat().mesoBuff / 100.0) * player.getDropMod() * cmServerrate), map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, player, false, drop_type);
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

                    map.spawnMobDrop(idrop, map.calcDropPos(getDropPosition(monster, drop_type, dropped_count), monster.getPosition()), monster, player, drop_type, de.questid);
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
