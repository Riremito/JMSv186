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
public class NpcHenkel extends NpcScriptMan {

    private final int[] npc_ids = {
        1012119, // ヘンケル (ヘネシス : 東の丘)
        1022105, // ヘンケル (ぺリオン : 分かれ道-東)
        1032113, // ヘンケル (ビクトリアロード : 魔法使い修行場)
        1032114, // ヘンケル (エリニア : 南フィールド)
        1052113, // ヘンケル (ビクトリアロード : 盗賊修行場)
        1052114, // ヘンケル (カニングシティー : 工事現場)
        1095001, // ヘンケル (ビクトリアロード : 海賊修行場)
        1095002 // ヘンケル (ビクトリアロード : 船着場への道)
    };
    private final int[] map_training_centers = {
        910060000, // ビクトリアロード : 弓使い修行場
        910220000, // ビクトリアロード : 戦士修行場
        910120000, // ビクトリアロード : 魔法使い修行場
        910120000, // ビクトリアロード : 魔法使い修行場
        910310000, // ビクトリアロード : 盗賊修行場
        910310000, // ビクトリアロード : 盗賊修行場
        912030000, // ビクトリアロード : 海賊修行場
        912030000 // ビクトリアロード : 海賊修行場
    };
    private final int npc_henkel_archer = 1012119; // ヘンケル (ヘネシス : 東の丘)
    private final int map_spore_training_center = 910060100; // ビックトリアロード : ヘンケルのスポア修練場
    // エヴァンのクエスト
    private final int[] quest_spores = {
        22515, // ヘンケルの修練場
        22516, // ヘンケルの妄想
        22517, // 変わらぬヘンケルの妄想
        22518 // 絶えないヘンケルの妄想
    };
    private final int room_count = 5;
    private final int max_players = 5;
    private int map_base = -1;
    private boolean spore = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.npc_ids.length; i++) {
                    if (getNpcId() == this.npc_ids[i]) {
                        this.map_base = this.map_training_centers[i];
                    }
                }
                if (this.map_base < 0) {
                    return false;
                }
                if (getNpcId() == this.npc_henkel_archer) {
                    for (int quest_id : this.quest_spores) {
                        if (getPlayer().getQuestStatus(quest_id) == 1) {
                            this.spore = true;
                        }
                    }
                }
                if (this.spore) {
                    // TODO : 文章 (適当)
                    askYesNo("特別なスポア修練場に入りますか？");
                    return true;
                }
                // TODO : 文章 (適当)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("修行場に入りますか？");
                for (int i = 0; i < this.room_count; i++) {
                    nt.addMenu(i, "修行場" + (i + 1) + " (" + getPlayer().findMap(this.map_base + i).getCharactersSize() + "/" + this.max_players + ")");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.spore) {
                    warp(this.map_spore_training_center);
                    return false;
                }
                if (answer < 0 || this.room_count <= answer) {
                    return false;
                }
                if (this.max_players <= getPlayer().findMap(this.map_base + answer).getCharactersSize()) {
                    // TODO : 文章 (適当)
                    say("この修行場は満員です。");
                    return false;
                }
                warp(this.map_base + answer);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
