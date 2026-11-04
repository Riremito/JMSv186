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
package tacos.script.reactor;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : (なし) (JMS186), js の処理を利用
public class Reactor_Zipangu_8098000 extends ReactorScriptMan {

    private final int map_maze_center = 809050005;
    private final int map_maze_entrance1 = 809050000;
    private final int map_maze_entrance2 = 809050010;
    private final int mob_entrance1 = 9400209;
    private final int mob_entrance2 = 9400210;
    private final int mob_center1 = 9400217;
    private final int mob_center2 = 9400218;

    @Override
    public void action() {
        int map_id = getMap().getId();
        if (map_id == this.map_maze_entrance1 || map_id == this.map_maze_entrance2) {
            summonMob(this.mob_entrance1, 6);
            summonMob(this.mob_entrance2, 9);
        } else {
            int b = Math.abs(map_id - this.map_maze_center);
            summonMob(this.mob_center1 - b, 2);
            summonMob(this.mob_center2 - b, 3);
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : Some monsters are summoned.)
        mapMessage(5, "モンスターが召喚されました。");
    }
}
