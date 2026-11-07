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
// Npc.wz script : goldrich (JMS165-302), js の処理を利用
// 白紙のコンパスを使ったとき (ReqCUser_Item から呼ばれる)
public class Npc_Leafre_GoldRichie extends NpcScriptMan {

    private final int item_gold_compass = 2430008; // ゴールドコンパス

    @Override
    public boolean action(int status, int answer) {
        if (status != 0) {
            return false;
        }
        if (haveItem(this.item_gold_compass, 1)) {
            // TODO : 文章 (js の英文を翻訳)
            say("ゴールドコンパスが完成した。");
            return false;
        }
        // TODO : 文章 (js の英文を翻訳)
        say("ゴールドコンパスを作るための材料がそろっているか確認してみよう。白紙のコンパス1個と、コンパスに使うNの文字、Eの文字、Wの文字、Sの文字がそれぞれ20個ずつ必要だ。");
        return false;
    }
}
