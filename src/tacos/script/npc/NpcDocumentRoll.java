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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcDocumentRoll extends NpcScriptMan {

    private final int quest_scroll_of_life = 3240; // 生命の呪文書
    private final int item_scroll_of_life = 4031034; // 生命の呪文書
    private final int npc_cloy = 1012005; // クロイ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_scroll_of_life) != 1) {
                    return false;
                }
                if (checkItem(this.item_scroll_of_life)) {
                    // 翻訳
                    say("すでに#b#t" + this.item_scroll_of_life + "##kを持っています。");
                    return false;
                }
                // 翻訳
                askYesNo("#b" + getNpcName(this.npc_cloy) + "#kが残したたくさんの書の中に、まばゆい光を放っているものが1枚あります。拾いますか？");
                return true;
            }
            case 1 -> {
                if (checkItem(this.item_scroll_of_life)) {
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_scroll_of_life, 1, "")) {
                    // 翻訳
                    say("インベントリに空きがありません。この書を持ち歩けるよう、空きを作った方がよさそうです。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_scroll_of_life, (short) 1);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
