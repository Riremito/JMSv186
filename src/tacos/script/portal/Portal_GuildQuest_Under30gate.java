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
// Map.wz portal script : under30gate (JMS186), lua の処理を利用
public class Portal_GuildQuest_Under30gate extends PortalScriptMan {

    private final int map_waterway_maze4 = 990000640; // 水路の迷路4
    private final int level_max = 30;

    @Override
    public boolean enter() {
        if (getLevel() <= this.level_max) {
            return transferField(this.map_waterway_maze4, "out00");
        }
        // 翻訳
        message("目に見えない強力な力の壁が入口をふさいでいます。");
        return false;
    }
}
