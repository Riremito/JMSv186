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

import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : Sky_BossOut (JMS186), js の処理を利用
public class Portal_Leafre_Sky_BossOut extends PortalScriptMan {

    private final int item_box_base = 2022651; // ドラゴンライダーの箱 (+ 職業)
    private final int item_box_warrior = 2022652; // ドラゴンライダーの戦士箱
    private final int map_cave = 240080050; // リプレ : 亡子の洞窟

    @Override
    public boolean enter() {
        if (!isPartyMember() || getMonsterCount() != 0 || !isLeader()) {
            // TODO : 文章 (js の英文を翻訳, 原文 : This portal is not available.)
            playerMessage(5, "このポータルは利用できません。");
            return false;
        }
        for (TacosCharacter chr : getMapPlayers()) {
            int item_id = this.item_box_base + chr.getJob() % 1000 / 100;
            if (item_id == this.item_box_base) {
                item_id = this.item_box_warrior;
            }
            gainItem(chr, item_id, 1);
        }
        // TODO : js では gainNX(3000)
        return warpParty(this.map_cave);
    }
}
