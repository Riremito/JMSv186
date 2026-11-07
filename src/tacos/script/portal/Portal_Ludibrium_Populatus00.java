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
package tacos.script.portal;

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : Populatus00 (JMS186), lua の処理を利用
public class Portal_Ludibrium_Populatus00 extends PortalScriptMan {

    private final int item_ludibrium_medal = 4031172; // ルディブリアムのメダル
    private final int quest_enter_time = 7200; // 最後に入場した時刻
    private final int quest_enter_count = 7201; // 入場回数
    private final int map_origin = 220080001; // ルディブリアム : 時計塔の深層部
    private final int user_max = 12;
    private final int minutes_per_day = 1440;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Populatus");
        if (field_set == null) {
            return false;
        }
        if (getItemCount(this.item_ludibrium_medal) < 1) {
            return false;
        }
        if (field_set.getVar("ludiboss").equals("yes")) {
            // 翻訳
            message("パプラトゥスとの戦いはすでに始まっているので、ここには入れません。");
            return false;
        }
        if (this.user_max <= field_set.getUserCount()) {
            // 翻訳
            message("部屋はすでにパプラトゥスと戦う人たちで満員です。");
            return false;
        }
        String time_current = currentTime();
        String count = getQuestData(this.quest_enter_count);
        // 1日経過後
        if (this.minutes_per_day <= compareTime(time_current, getQuestData(this.quest_enter_time)) || count.isEmpty()) {
            setQuestData(this.quest_enter_time, time_current);
            setQuestData(this.quest_enter_count, "1");
            return transferField(this.map_origin, "st00");
        }
        if (count.equals("1")) {
            setQuestData(this.quest_enter_count, "2");
            return transferField(this.map_origin, "st00");
        }
        // 翻訳
        message("時計塔の根源に入れるのは1日2回までです。");
        return false;
    }
}
