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
// Npc.wz script : shammos_Potion (JMS186-302), lua が存在しないため旧 js (scripts_JMS) を参考に実装
public class Npc_ElNath_GlacialWater extends NpcScriptMan {

    private final int item_bottle = 4032649; // 万年氷下水用の空き瓶
    private final int item_water = 2022698; // 万年氷下水

    @Override
    public boolean action(int status, int answer) {
        if (status != 0) {
            return false;
        }
        if (getItemCount(this.item_bottle) < 1) {
            // TODO : 文章 (適当)
            say("#t" + this.item_bottle + "#が必要だ。");
            return false;
        }
        if (!exchange(0, this.item_bottle, -1, this.item_water, 1)) {
            // TODO : 文章 (適当)
            say("消費タブに空きがないようだ。");
        }
        return false;
    }
}
