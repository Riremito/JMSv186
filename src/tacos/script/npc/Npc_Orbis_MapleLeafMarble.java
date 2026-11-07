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
public class Npc_Orbis_MapleLeafMarble extends NpcScriptMan {

    private final int quest_maple_leaf_marble = 6230; // 紅葉玉
    private final int item_maple_leaf_marble = 4031456; // 紅葉玉
    private final int item_glass_marble = 4031476; // ガラス玉

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_maple_leaf_marble) != 1 || checkItem(this.item_maple_leaf_marble) || !checkItem(this.item_glass_marble)) {
                    return false;
                }
                if (!canHold(this.item_maple_leaf_marble, 1)) {
                    // 翻訳
                    say("ETCウィンドウに空きがないので、メイプルマーブルを受け取れません。空きを作ってからもう一度お試しください。");
                    return false;
                }
                removeItem(this.item_glass_marble, 1);
                gainItem(this.item_maple_leaf_marble, 1);
                // 翻訳
                say("カエデの葉がきらめくガラス玉に吸い込まれました。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
