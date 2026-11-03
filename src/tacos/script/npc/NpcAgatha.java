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
public class NpcAgatha extends NpcScriptMan {

    private static final int MENU_ELLINIA = 0;
    private static final int MENU_LUDIBRIUM = 1;
    private static final int MENU_LEAFRE = 2;
    private static final int MENU_ARIANT = 3;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私は各地域に行く船のチケットを売っています。どのチケットを購入しますか？");
                nt.addMenu(MENU_ELLINIA, "ビクトリアアイランドのエリニア");
                nt.addMenu(MENU_LUDIBRIUM, "ルディブリアム城");
                nt.addMenu(MENU_LEAFRE, "リプレ村");
                nt.addMenu(MENU_ARIANT, "アリアント");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : チケットの購入 (乗船券のアイテムIDと値段が不明)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
