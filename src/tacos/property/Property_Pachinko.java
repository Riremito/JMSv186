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
package tacos.property;

/**
 *
 * @author Riremito
 */
public class Property_Pachinko {

    // 報酬アイテム (アイテムID)
    public static String[] equip_items; // 装備
    public static String[] mount_items; // 騎乗
    public static String[] consume_items; // 消費
    // 図柄の確率 : [0], [1] = 表示される確率, [2], [3] = 当たる確率
    public static String[] big_white_mob_rates; // 白いモンスター (大)
    public static String[] small_white_mob_rates; // 白いモンスター (小)
    public static String[] purple_mob_rates; // 紫のモンスター
    public static String[] pink_mob_rates; // ピンクのモンスター
    public static String[] thief_rates; // 盗賊
    public static String[] pirate_rates; // 海賊
    public static String[] magician_rates; // 魔法使い
    public static String[] warrior_rates; // 戦士
    public static String[] archer_rates; // 弓使い
    public static String[] queen_rates; // 女王
    // 当たり図柄ごとの報酬の確率
    public static String[] white_mob_reward_rates; // 白いモンスター
    public static String[] color_mob_reward_rates; // 紫, ピンクのモンスター
    public static String[] job_reward_rates; // 5職業
    public static String[] queen_reward_rates; // 女王
    public static String[] golden_dog_rates; // 黄金の犬
    public static int ocean_hat_rate; // 海の帽子
    public static int power_random; // 発射の強さに加える乱数の範囲
    public static int tama_reward_unit; // 当たりで獲得する玉の数 (図柄の番号 + 1 倍)

    public static boolean init() {
        Property conf = new Property("properties/beans.properties");
        if (!conf.open()) {
            return false;
        }
        equip_items = conf.get("ddzb").split(",");
        mount_items = conf.get("ddzq").split(",");
        consume_items = conf.get("xhp").split(",");
        ocean_hat_rate = conf.getInt("hymzjl");
        golden_dog_rates = conf.get("hjgjl").split(",");
        big_white_mob_rates = conf.get("dbg").split(",");
        small_white_mob_rates = conf.get("xbg").split(",");
        purple_mob_rates = conf.get("zsg").split(",");
        pink_mob_rates = conf.get("fsg").split(",");
        thief_rates = conf.get("fx").split(",");
        pirate_rates = conf.get("hd").split(",");
        magician_rates = conf.get("fs").split(",");
        warrior_rates = conf.get("zs").split(",");
        archer_rates = conf.get("gjs").split(",");
        queen_rates = conf.get("nh").split(",");
        white_mob_reward_rates = conf.get("bgjl").split(",");
        color_mob_reward_rates = conf.get("sgjl").split(",");
        job_reward_rates = conf.get("wzyjl").split(",");
        queen_reward_rates = conf.get("nhjl").split(",");
        power_random = conf.getInt("ldgj");
        tama_reward_unit = conf.getInt("ddjlfw");
        return true;
    }

}
