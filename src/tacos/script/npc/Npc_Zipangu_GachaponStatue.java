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
public class Npc_Zipangu_GachaponStatue extends NpcScriptMan {

    private final int item_return_scroll_pachinko = 2031007; // 帰還の書(パチンコ屋)

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("スノーボードを持ってきたのか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 所持しているスノーボードの一覧, 交換処理 (https://www.nicovideo.jp/watch/sm5929362)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("渡したいスノーボードはどれだ？同じものが2つ以上ある場合は1番左上にあるものになるから、気をつけろよ。");
                nt.addMenu(0, "#v" + this.item_return_scroll_pachinko + "##t" + this.item_return_scroll_pachinko + "#");
                nt.addMenu(1, "呪われた強化書(70%)");
                nt.addMenu(2, "呪われた強化書(30%)");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                // TODO : 選択したスノーボードの交換
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
