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
package tacos.unofficial;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import tacos.wz.data.StringWz;
import tacos.wz.data.StringWz.DropMonsterBook;

/**
 *
 * @author Riremito
 */
public class CustomMonsterBookDrop {

    private static int DROP_PROB_MESO = 50;
    private static int DROP_PROB_EQUIP = 1;
    private static int DROP_PROB_CONSUME = 5;
    private static int DROP_PROB_CONSUME_SCROLL = 1;
    private static int DROP_PROB_CONSUME_STAR = 1;
    private static int DROP_PROB_SETUP = 1;
    private static int DROP_PROB_ETC = 50;
    private static int DROP_PROB_ETC_SUB = 5;

    private DropMonsterBook book_info = null;

    public CustomMonsterBookDrop(MapleMonster monster) {
        this.book_info = StringWz.getMonseterBookDrop(monster.getId());
    }

    public List<Integer> getDropItems() {
        List<Integer> drop_list = new ArrayList<>();

        for (int item_id : this.book_info.drop_ids) {
            if (!checkDropProb(item_id)) {
                continue;
            }
            drop_list.add(item_id);
        }

        return drop_list;
    }

    public boolean checkDropProb(int item_id) {
        int type = item_id / 1000000;
        int type_sub = item_id / 10000;
        int rand = Randomizer.nextInt(100);

        if (this.book_info.drop_ids.isEmpty()) {
            return false;
        }

        switch (type) {
            // Equip
            case 1: {
                if (rand < DROP_PROB_EQUIP) {
                    return true;
                }
                return false;
            }
            // Consume
            case 2: {
                // Scroll
                if (type_sub == 204) {
                    if (rand < DROP_PROB_CONSUME_SCROLL) {
                        return true;
                    }
                    return false;
                }
                // Star
                if (type_sub == 207) {
                    if (rand < DROP_PROB_CONSUME_STAR) {
                        return true;
                    }
                    return false;
                }
                if (rand < DROP_PROB_CONSUME) {
                    return true;
                }
                return false;
            }
            // Setup
            case 3: {
                if (rand < DROP_PROB_SETUP) {
                    return true;
                }
                return false;
            }
            // Etc
            case 4: {
                // main etc drop
                if (this.book_info.drop_ids.get(0) == item_id) {
                    if (rand < DROP_PROB_ETC) {
                        return true;
                    }
                    return false;
                }
                // sub etc drop
                if (rand < DROP_PROB_ETC_SUB) {
                    return true;
                }
                return false;
            }
            // Cash
            case 5: {
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static int dropFromMonsterBook(MapleCharacter player, MapleMonster monster) {
        MapleMap map = monster.getMap();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte drop_type = (byte) (monster.getStats().isExplosiveReward() ? 3 : monster.getStats().isFfaLoot() ? 2 : player.getParty() != null ? 1 : 0);

        int dropped_count = 0;
        // meso
        if (Randomizer.nextInt(100) < DROP_PROB_MESO) {
            int level = monster.getStats().getLevel();
            int base_meso = 5 + (level * 5);
            int meso_value = base_meso + Randomizer.nextInt((base_meso / 10));
            Point drop_pos = getDropPosition(monster, drop_type, dropped_count);
            map.spawnMobMesoDrop(meso_value, map.calcDropPos(drop_pos, monster.getPosition()), monster, player, false, drop_type);
            dropped_count++;
        }

        if (!StringWz.checkBookAvailable()) {
            return dropped_count;
        }

        CustomMonsterBookDrop cmbd = new CustomMonsterBookDrop(monster);
        for (int item_id : cmbd.getDropItems()) {
            IItem idrop = (GameConstants.getInventoryType(item_id) == MapleInventoryType.EQUIP) ? ii.randomizeStats((Equip) ii.getEquipById(item_id)) : new Item(item_id, (byte) 0, (short) 1, (byte) 0);
            Point drop_pos = getDropPosition(monster, drop_type, dropped_count);

            map.spawnMobDrop(idrop, map.calcDropPos(drop_pos, monster.getPosition()), monster, player, drop_type, (short) 0);
            dropped_count++;
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
