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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcSpinel extends NpcScriptMan {

    private final int[] map_ids = {
        740000000, // 台湾 : 台湾西門町
        701000000, // 中国 : 上海ワイタン
        800000000, // ジパング : キノコ神社
        500000000, // タイランド : 水上市場
        702000000 // 中国 : 宋山里
    };
    private final String[] map_names = {
        "西門街(台湾)",
        "上海(中国)",
        "キノコ神社(日本)",
        "水上市場(タイランド)",
        "宋山里(中国)"
    };
    private final int default_return_map_id = 100000000; // ヘネシス : ヘネシス
    // 初心者は 1/10
    private final int cost = 3000;
    private boolean traveling = false;
    private int return_map_id = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag menu = new NpcScriptTag();
                for (int i = 0; i < this.map_ids.length; i++) {
                    // 現在のマップはスキップ
                    if (getMapId() == this.map_ids[i]) {
                        this.traveling = true;
                        continue;
                    }
                    menu.addMenu(i, this.map_names[i]);
                }
                NpcScriptTag nt = new NpcScriptTag();
                if (this.traveling) {
                    // 旅行中
                    this.return_map_id = getPlayer().getSavedLocation(SavedLocationType.WORLDTOUR);
                    if (this.return_map_id == -1) {
                        this.return_map_id = this.default_return_map_id;
                    }
                    menu.addMenu(this.map_ids.length, "旅行を終え#m" + this.return_map_id + "#に戻りたいです。");
                    nt.add("どこに行ってみたいですか？");
                } else {
                    nt.add("#b国内のキノコ神社を含め、中国の上海、台湾の西門町#kに続き、#bタイランドの水上市場#kへのコースが用意出来ています。各旅行地でも私が皆様の楽しい旅行のために頑張ります。では、どこから行ってみたいですか？");
                }
                nt.add(menu.get(), false);
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.traveling && answer == this.map_ids.length) {
                    getPlayer().clearSavedLocation(SavedLocationType.WORLDTOUR);
                    warp(this.return_map_id);
                    return false;
                }
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                if (!this.traveling) {
                    if (!payMoney(getPlayer().getJob() == 0 ? this.cost / 10 : this.cost)) {
                        // 翻訳
                        say("旅費に十分なメルをお持ちか確認してください。");
                        return false;
                    }
                    getPlayer().saveLocation(SavedLocationType.WORLDTOUR);
                }
                warp(this.map_ids[answer]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
