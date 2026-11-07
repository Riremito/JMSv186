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
// Map.wz portal script : party3_jail1, party3_jail2, party3_jail3 (JMS186), lua の処理を利用
// 監獄と監獄倉庫に誰もいない場合は監獄へ入れる
public class Portal_Orbis_Party3_jail extends PortalScriptMan {

    private final int map_jail_base = 920010900; // 女神の塔 : 監獄 (party3_jail1 = 920010910, party3_jail2 = 920010920, ...)

    @Override
    public boolean enter() {
        int index = integer(substring(getScriptName(), 11, 1));
        int map_jail = this.map_jail_base + index * 10;
        int count = getUserCount(map_jail) + getUserCount(map_jail + 1) + getUserCount(map_jail + 2);
        if (0 < count) {
            // 翻訳
            message("すでに誰かが中にいます。");
            return false;
        }
        return transferField(map_jail, "out00");
    }
}
