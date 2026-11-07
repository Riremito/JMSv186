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
// Npc.wz script : multipet_success, multipet_fail (JMS146-302), js の処理を利用
// ペットの散歩路の草むら (調教師の能力証明で隠されたメモを探す)
public class Npc_Henesys_PetWalkingRoadGrass extends NpcScriptMan {

    private final int npc_success = 9102100; // ? (multipet_success), 9102101 は multipet_fail
    private final int quest_proof = 4646; // 調教師の能力証明
    private final int item_hidden_memo = 4031921; // 隠されたメモ
    private final int item_pet_poop = 4031922; // ペットのうんこ

    @Override
    public boolean action(int status, int answer) {
        boolean success = (getNpcId() == this.npc_success);
        switch (status) {
            case 0 -> {
                if (success) {
                    if (getQuestStatus(this.quest_proof) != 1) {
                        // TODO : 文章 (js の英文を翻訳)
                        say("#b(何も見つからなかった。)");
                        return false;
                    }
                    if (haveItem(this.item_hidden_memo, 1)) {
                        // TODO : 文章 (js の英文を翻訳)
                        say("#b(これは…うわっ…ペットのうんこが入っていた！)");
                        return false;
                    }
                }
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("#b(草に覆われた何かが見える。引っぱり出してみようか？)");
                return true;
            }
            case 1 -> {
                if (success) {
                    gainItem(this.item_hidden_memo, 1);
                    // TODO : 文章 (js の英文を翻訳)
                    say("調教師のバルトスが隠したアイテムを見つけた…このメモだ。");
                    return false;
                }
                gainItem(this.item_pet_poop, 1);
                // TODO : 文章 (js の英文を翻訳)
                say("#b(うえっ…ペットのうんこだ！)");
                return false;
            }
            case -1 -> {
                if (success) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("#b(草に覆われた隠されたアイテムには触らなかった。)");
                    return false;
                }
                // TODO : 文章 (js の英文を翻訳)
                say("#b(大したものではなさそうなので、触らなかった。)");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
