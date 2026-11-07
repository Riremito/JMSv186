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

/**
 *
 * @author Riremito
 */
// Map.wz portal script : ghostShip_next (JMS186), js の処理を利用
public class Portal_Aquarium_GhostShip_next extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (!isLeader()) {
            // TODO : 文章 (js の英文を翻訳, 原文 : The leader must be here)
            playerMessage(5, "パーティーリーダーがここにいる必要があります。");
            return false;
        }
        if (getMonsterCount() != 0) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Please, clear all of the monsters!)
            playerMessage(5, "全てのモンスターを倒してください！");
            return false;
        }
        int map_id = getMapId();
        if (map_id % 10 != 4) {
            return warpParty(map_id + 1, map_id % 10 == 3 ? 1 : 2);
        }
        // 最後のステージ
        if (getReactorState("switch0") < 1 || getReactorState("switch1") < 1) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Both switches must be turned on.)
            playerMessage(5, "両方のスイッチを入れる必要があります。");
            return false;
        }
        int map_boss = map_id + 66;
        if (map_boss % 100 != 90) {
            map_boss += 10;
        }
        return warpParty(map_boss, 0);
    }
}
