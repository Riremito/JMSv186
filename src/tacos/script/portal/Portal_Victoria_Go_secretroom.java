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
// Map.wz portal script : go_secretroom (JMS186), js の処理を利用
public class Portal_Victoria_Go_secretroom extends PortalScriptMan {

    private final int item_secret_room_key = 4032405; // 密室鍵
    private final int map_base = 106021000; // キノコ城 : 摩天楼3
    private final int map_dungeon = 106021001;
    private final int dungeons = 10;

    @Override
    public boolean enter() {
        if (!haveItem(this.item_secret_room_key)) {
            // TODO : 文章 (js の英文を翻訳, 原文 : It's locked.)
            playerMessage(5, "鍵がかかっている。");
            return false;
        }
        if (getMapId() != this.map_base) {
            return warp(this.map_base, "in00");
        }
        for (int i = 0; i < this.dungeons; i++) {
            if (getUserCount(this.map_dungeon + i) == 0) {
                return warp(this.map_dungeon + i, 0);
            }
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : All of the Mini-Dungeons are in use right now, please try again later.)
        playerMessage(5, "現在、全てのミニダンジョンが使用中です。後でもう一度お試しください。");
        return false;
    }
}
