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
public class NpcJohnBarricade extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // iTCG
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("おや、何を持っているんだ？おお！それはとてもレアな物だよ。どこで手に入れたのかな？");
                nt.addMenu(0, "これが何なのか教えて下さい。");
                nt.addMenu(1, "この古い物が？私が履きこんだ靴下よりは価値あるだろうけどね！");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : iTCG のアイテムの交換 (未実装)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
