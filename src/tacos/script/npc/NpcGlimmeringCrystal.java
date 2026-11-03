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
public class NpcGlimmeringCrystal extends NpcScriptMan {

    // 次元の世界
    private final int[] map_ids = {
        108010301, // 隠れ道 : 次元の世界
        108010201, // 隠れ道 : 次元の世界
        108010101, // 隠れ道 : 次元の世界
        108010401 // 隠れ道 : 次元の世界
    };
    private final int[] to_map_ids = {
        102000000, // ぺリオン : ぺリオン
        101000000, // エリニア : エリニア
        100000000, // ヘネシス : ヘネシス
        103000000 // カニングシティー : カニングシティー
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("輝く水晶を使って現実の世界に戻ることができます。本当に戻りますか？");
                return true;
            }
            case 1 -> {
                for (int i = 0; i < this.map_ids.length; i++) {
                    if (getMapId() == this.map_ids[i]) {
                        warp(this.to_map_ids[i]);
                        return false;
                    }
                }
                // TODO : 海賊 (108010501), デュアルブレード (804000710) の次元の世界
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
