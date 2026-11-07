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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
public class Npc_Henesys_DragonNest extends NpcScriptMan {

    private final int map_dragon_egg = 900090103; // 夢の中 : 竜卵獲得

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : jsではクエスト22011を開始しているが、JMS186には存在しない
                // TODO : メッセージ表示 (竜の卵を手に入れた)
                warp(this.map_dragon_egg);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
