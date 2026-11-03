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
public class NpcShane extends NpcScriptMan {

    private final int[] map_ids = {
        101000100, // 隠れ道 : 忍耐の森-1段階
        101000101, // 隠れ道 : 忍耐の森-2段階
        101000102, // 隠れ道 : 忍耐の森-3段階
        101000103, // 隠れ道 : 忍耐の森-4段階
        101000104 // 隠れ道 : 忍耐の森-5段階
    };
    private final int[] quest_ids = {
        2050, // 1段階
        2050, // 2段階
        2051, // 3段階
        2051, // 4段階
        2051 // 5段階
    };
    private final int cost = 6000;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bサビトラマ#kから頼まれて薬草を取りに来ただって？　だが、ここは父から譲り受けた大切な場所なんだよ…。本当なら誰も入れる訳にはいけないんだが、#r" + this.cost + "#k メルさえ払えば話は違うぞ。どうだ、メルを払うかい？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 6000 メルの支払い, 文章 (元のスクリプトはデバッグモードで全段階を選択可能にしていた)
                NpcScriptTag nt = new NpcScriptTag();
                boolean found = false;
                for (int i = 0; i < this.map_ids.length; i++) {
                    if (getPlayer().getQuestStatus(this.quest_ids[i]) != 1) {
                        continue;
                    }
                    nt.addMenuRed(i, getMapName(this.map_ids[i]));
                    found = true;
                }
                if (!found) {
                    // TODO : クエスト未進行時の文章
                    return false;
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.map_ids.length <= answer || getPlayer().getQuestStatus(this.quest_ids[answer]) != 1) {
                    return false;
                }
                warp(this.map_ids[answer]); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
