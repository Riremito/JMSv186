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
public class Npc_Zipangu_CatherinePachinkoParlor extends NpcScriptMan {

    private final int item_soul_of_pachi_balrog = 4001315; // パチバルログの魂
    private final int item_challenge_of_pachi_balrog = 4001312; // パチバルログの挑戦状
    private final int item_return_scroll_pachinko = 2031007; // 帰還の書(パチンコ屋)
    private static final int MENU_PRIZE = 0;
    private static final int MENU_DUNGEON = 1;
    private static final int MENU_EXCHANGE = 2;
    private static final int MENU_TITLE = 3;
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.selected == MENU_DUNGEON) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("わかったわ。またね。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いらっしゃいませ。何かご用ですか？");
                nt.add("#L" + MENU_PRIZE + "#パチンコ景品を貰いたい#l");
                nt.add("#L" + MENU_DUNGEON + "#パチンコダンジョンに行きたい#l");
                nt.add("#L" + MENU_EXCHANGE + "#パチバルログの魂と景品を交換したい#l");
                nt.add("#L" + MENU_TITLE + "#パチンコの帝王の称号について聞く#l");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected = answer;
                switch (this.selected) {
                    case MENU_PRIZE -> {
                        // TODO : 景品交換UI
                        return false;
                    }
                    case MENU_DUNGEON -> {
                        int level = getPlayer().getLevel();
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("あなたのレベルは" + level + "ね。");
                        nt.add((level / 10) + "ダンジョンに入りたいのかしら？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_EXCHANGE -> {
                        // TODO : パチバルログの魂 (item_soul_of_pachi_balrog) の確認, 景品の一覧と交換処理
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("えーっと…はい、確かに#b#v" + this.item_soul_of_pachi_balrog + "##t" + this.item_soul_of_pachi_balrog + "##kを持っているわね。どちらのアイテムを賞品と交換するのかしら？");
                        nt.addMenu(0, "#v" + this.item_return_scroll_pachinko + "##t" + this.item_return_scroll_pachinko + "#");
                        nt.addMenu(1, "呪われた強化書(70%)");
                        nt.addMenu(2, "呪われた強化書(30%)");
                        askMenu(nt);
                        return false;
                    }
                    case MENU_TITLE -> {
                        // TODO : パチンコの帝王とは?, 当たりランクを確認する, パチンコの帝王の座に着く
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("パチンコの帝王、ね。なんて素敵な響きかしら。");
                        nt.add("#L0#パチンコの帝王とは?#l");
                        nt.add("#L1#当たりランクを確認する#l");
                        nt.add("#L2#パチンコの帝王の座に着く#l");
                        askMenu(nt);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                if (this.selected != MENU_DUNGEON) {
                    return false;
                }
                // TODO : パチバルログの挑戦状 (item_challenge_of_pachi_balrog) を持っている場合のパチンコダンジョンへの入場
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("パチンコダンジョンに入るには、#b#v" + this.item_challenge_of_pachi_balrog + "##t" + this.item_challenge_of_pachi_balrog + "##kが必要よ。");
                say(nt);
                return false;
            }
            case -1 -> {
                // TODO : 文章 (元のスクリプトは "XXXX")
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
