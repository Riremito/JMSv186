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

    public static String[] 五职业奖励;
    public static String[] 豆豆坐骑;
    public static int 豆豆奖励范围;
    public static String[] 白怪奖励;
    public static String[] 色怪奖励;
    public static String[] 粉色怪;
    public static int 海洋帽子几率;
    public static String[] 黄金狗几率;
    public static String[] 飞侠;
    public static String[] 战士;
    public static String[] 紫色怪;
    public static String[] 弓箭手;
    public static int 力度搞假;
    public static String[] 消耗品;
    public static String[] 女皇奖励;
    public static String[] 女皇;
    public static String[] 小白怪;
    public static String[] 法师;
    public static String[] 豆豆装备;
    public static String[] 大白怪;
    public static String[] 海盗;

    public static boolean init() {
        Property conf = new Property("properties/beans.properties");
        if (!conf.open()) {
            return false;
        }
        豆豆装备 = conf.get("ddzb").split(",");
        豆豆坐骑 = conf.get("ddzq").split(",");
        消耗品 = conf.get("xhp").split(",");
        海洋帽子几率 = conf.getInt("hymzjl");
        黄金狗几率 = conf.get("hjgjl").split(",");
        大白怪 = conf.get("dbg").split(",");
        小白怪 = conf.get("xbg").split(",");
        紫色怪 = conf.get("zsg").split(",");
        粉色怪 = conf.get("fsg").split(",");
        飞侠 = conf.get("fx").split(",");
        海盗 = conf.get("hd").split(",");
        法师 = conf.get("fs").split(",");
        战士 = conf.get("zs").split(",");
        弓箭手 = conf.get("gjs").split(",");
        女皇 = conf.get("nh").split(",");
        白怪奖励 = conf.get("bgjl").split(",");
        色怪奖励 = conf.get("sgjl").split(",");
        五职业奖励 = conf.get("wzyjl").split(",");
        女皇奖励 = conf.get("nhjl").split(",");
        力度搞假 = conf.getInt("ldgj");
        豆豆奖励范围 = conf.getInt("ddjlfw");
        return true;
    }

}
