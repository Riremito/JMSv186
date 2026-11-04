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
public class Npc_Sleepywood_MysteriousStatue extends NpcScriptMan {

    // 後の段階のクエストから順に確認する
    private final int[] quest_ids = {
        2054, // 忍耐の森「5段階」, 「6段階」, 「7段階」
        2053, // 忍耐の森「3段階」, 「4段階」
        2052 // 忍耐の森「1段階」, 「2段階」
    };
    private final int[] item_ids = {
        4031028, // ホワイトビオラ
        4031026, // ブルービオラ
        4031025 // ピンクビオラ
    };
    private final int[] map_ids = {
        105040314, // 隠れ道 : 忍耐の森「5段階」
        105040312, // 隠れ道 : 忍耐の森「3段階」
        105040310 // 隠れ道 : 忍耐の森「1段階」
    };
    private int to_map_id = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("像から手を離すと、何事もなかったかのように像は静かになった。");
                return false;
            }
            case 0 -> {
                for (int i = 0; i < this.quest_ids.length; i++) {
                    if (getPlayer().getQuestStatus(this.quest_ids[i]) == 0) {
                        continue;
                    }
                    // 花を持っている場合は移動できない
                    if (checkItem(this.item_ids[i])) {
                        // 翻訳
                        say("像に手を置いたが、何も起こらなかった。私が持っている#t" + this.item_ids[i] + "#のせいかもしれない。像の力を妨げているようだ。");
                        return false;
                    }
                    this.to_map_id = this.map_ids[i];
                    askYesNo("石像に手を出してみると不思議な光に全身を包まれ、中に吸い込まれるような感覚に襲われました。このまま移動しますか？");
                    return true;
                }
                say("石像に手を出してみても何も起こりませんでした。");
                return false;
            }
            case 1 -> {
                warp(this.to_map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
