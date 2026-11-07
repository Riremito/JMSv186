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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : minar_elli (JMS186), lua の処理を利用
// 魔法の種を使ってエリニアとリプレを移動する
public class Portal_Common_Minar_elli extends PortalScriptMan {

    private final int item_magic_seed = 4031346; // 魔法の種
    private final int map_ellinia_north = 101010000; // ビクトリアロード : エリニアの北側フィールド
    private final int map_minar_west = 240010100; // リプレ : ミナル西側の境界

    @Override
    public boolean enter() {
        if (getItemCount(this.item_magic_seed) <= 0) {
            // 翻訳
            message("ポータルを通るには魔法の種が必要です。");
            return false;
        }
        // 翻訳
        message("魔法の種を使い、別の場所へ移動します。");
        if (!exchange(0, this.item_magic_seed, -1)) {
            return false;
        }
        if (getMapId() == this.map_ellinia_north) {
            return transferField(this.map_minar_west, "elli00");
        }
        return transferField(this.map_ellinia_north, "minar00");
    }
}
