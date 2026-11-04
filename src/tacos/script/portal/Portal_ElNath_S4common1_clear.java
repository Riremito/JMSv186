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
// Map.wz portal script : s4common1_clear (JMS186), lua の処理を利用
// タイラスを 5 分間守るとクリア
public class Portal_ElNath_S4common1_clear extends PortalScriptMan {

    private final int map_tylus = 921100300; // タイラス護衛
    private final int map_tylus_clear = 921100301; // タイラス護衛完遂
    private final int time_clear = 300;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("S4common1");
        if (field_set == null) {
            return false;
        }
        if (field_set.getVar("clear").equals("1")) {
            return transferField(this.map_tylus_clear, "");
        }
        if (field_set.getQuestTime() <= this.time_clear) {
            // 翻訳
            message("まだタイラスを守れていない。");
            return false;
        }
        // TODO : lua の field.setMobGen( 0, 0 ) (モンスターの出現の停止) は未実装
        removeAllMob(this.map_tylus);
        field_set.setVar("clear", "1");
        return transferField(this.map_tylus_clear, "");
    }
}
