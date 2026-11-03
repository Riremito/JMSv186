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
public class NpcComputer extends NpcScriptMan {

    private final int[] map_ids = {
        190000000, // プラスストリート : 別の寺院
        191000000, // プラスストリート : サルの森
        192000000, // プラスストリート : 不毛の岩山Ⅰ
        195000000, // プラスストリート : 危ないアリの巣
        197000000, // プラスストリート : 風テラス1
        196000000, // プラスストリート : 冷え冷えの坂
        880000000, // ワンワンマントル
        881000000, // 死霊の集う海岸
        260010100 // バーニングロード : サボテン砂漠1
    };
    private final String[] portal_names = {
        "out00",
        "out00",
        "out00",
        "out00",
        "west00",
        "out00",
        "out00",
        "out00",
        "sp" // TODO : portal name. (out00 が存在しない)
    };
    private final String[] map_names = {
        "草原フィールド",
        "明るい森フィールド",
        "高原フィールド",
        "アリの巣ダンジョン",
        "ルディブリアム",
        "エルナス",
        "ワンワンマントル",
        "死霊の集う海岸",
        "サボテンの砂漠"
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("接続する場所を選んでください。");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, this.map_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                warp(this.map_ids[answer], this.portal_names[answer]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
