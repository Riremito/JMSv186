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
public class NpcRolly extends NpcScriptMan {

    private static final int MENU_ENTER = 0;
    private static final int MENU_ABOUT = 1;

    @Override
    public boolean action(int status, int answer) {
        // ルディブリアム迷路入場
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こちらはルディブリアム迷路の入口となります。是非楽しんでいって下さい。");
                nt.addMenu(MENU_ENTER, "ルディブリアム迷路に入る");
                nt.addMenu(MENU_ABOUT, "ルディブリアム迷路とは？");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : ルディブリアム迷路への入場 (グループクエスト), 迷路の説明
                // 9103000 : 809050015 ラスト
                // 9103002 : 809050016 報酬あり
                // 9103003 : 809050017 報酬なし
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
