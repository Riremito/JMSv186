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
package tacos.shared;

import java.util.Map;
import odin.client.inventory.Equip;
import odin.server.MapleItemInformationProvider;
import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public class TacosShared {

    public static int getDurabilityMax(Equip equip) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<String, Integer> eqStats = ii.getEquipStats(equip.getItemId());
        int durability_max = eqStats.get("durability");

        return durability_max;
    }

    public static int getRepairPrice(Equip equip) {
        int item_id = equip.getItemId();
        int target_durability = equip.getDurability();
        int durability_max = getDurabilityMax(equip);

        if (target_durability < 0 || durability_max < 0 || durability_max <= target_durability) {
            return 0;
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        double per = target_durability * 100.0 / durability_max;
        double price_t = ii.getPrice(item_id) * 100.0 / durability_max * 200.0;
        int price = (int) (price_t * per);

        return price;
    }

    public static boolean is_ignore_master_level_for_common(int skill_id) {
        // JMS v302
        switch (skill_id) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2121009:
            case 2221009:
            case 2321010:
            case 3120010:
            case 3120011:
            case 3120012:
            case 3220009:
            case 3220010:
            case 3220012:
            case 4110012:
            case 4210012:
            case 4340010:
            case 5120011:
            case 5220012:
            case 5220014:
            case 5321003:
            case 5321004:
            case 5321006:
            case 5320007:
            case 21120011:
            case 22181004:
            case 23120011:
            case 23121008:
            case 33120010:
            case 33121005:
            case 1: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean is_skill_need_master_level(int skill_id) {
        // JMS v302
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            return is_skill_need_master_level_302(skill_id);
        }
        // JMS v188-v194
        if (Config.PostBB()) {
            return is_skill_need_master_level_188(skill_id);
        }
        // JMS under 186
        int job_id = skill_id / 10000;
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // 初心者
        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return false;
            }
            default: {
                break;
            }
        }
        // 4次转縍
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }

    public static boolean is_skill_need_master_level_188(int skill_id) {
        int job_id = skill_id / 10000;
        // 除外スキル
        switch (skill_id) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2120009:
            case 2220009:
            case 2320010:
            case 3120010:
            case 3120011:
            case 3220009:
            case 3220010:
            case 4120010:
            case 4220009:
            case 5120011:
            case 5220012:
            case 32120009:
            case 33120010:
            case 1: {
                return false;
            }
            default: {
                break;
            }
        }

        // デギアルブレイド
        switch (skill_id) {
            case 4311003:
            case 4321000:
            case 4331002:
            case 4331005: {
                return true;
            }
            default: {
                break;
            }
        }
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // 初心者
        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return false;
            }
            default: {
                break;
            }
        }
        // 4次转縍
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }

    public static boolean is_skill_need_master_level_302(int skill_id) {
        int job_id = skill_id / 10000;

        if (is_ignore_master_level_for_common(skill_id)) {
            return false;
        }

        // JMS v302
        if (9200 <= job_id) {
            return false;
        }
        // ライディング
        if (job_id == 8000) {
            if (80001063 <= skill_id && skill_id <= 80001077) {
                return true;
            }
            if (skill_id == 80001123) {
                return true;
            }
            return false;
        }

        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 2002:
            case 2003:
            case 3000:
            case 3001:
            case 4001:
            case 4002: {
                return false;
            }
            default: {
                break;
            }
        }

        if (skill_id == 42120024) {
            return false;
        }

        switch (skill_id) {
            case 4311003:
            case 4321000:
            case 4331002:
            case 4331005: {
                return true;
            }
            default: {
                break;
            }
        }

        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュカルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // JMS v164
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }
}
