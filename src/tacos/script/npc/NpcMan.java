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

import tacos.client.TacosCharacter;
import tacos.debug.DebugMan;
import tacos.debug.IDebugMan;
import tacos.wz.NpcWz.NpcInfo;

/**
 *
 * @author Riremito
 */
public class NpcMan {

    public static boolean start(TacosCharacter chr, NpcInfo npc_info, int npc_script_id, int npc_id) {
        DebugMan script;
        switch (npc_script_id) {
            case 1002002 -> {
                // 1002002, ペィソン
                // 104000000, 港口
                script = new NpcPason();
            }
            case 1002004 -> {
                // 1002004, リス港口高級タクシー
                // 104000000, 港口
                script = new NpcVIPCab();
            }
            case 2060009 -> {
                // 2060009, イルカ
                // 230000000, アクアリウム
                // 251000100, 海側波止場
                script = new NpcDolphin();
            }
            case 2082003 -> {
                // 2082003, コルバ
                // 240000110, ステーション<オルビス行き>
                script = new NpcCorba();
            }
            case 2090005 -> {
                // 2090005, ツノーレ
                // 200000141, ステーション通路<武陵行き>
                // 250000100, 武陵神社
                // 251000000, 白草村
                script = new NpcCrane();
            }
            case 2110005 -> {
                // 2110005, ラクダ
                // 260020000, アリアント北門の外
                // 260020700, サヘル地帯1
                script = new NpcCamel();
            }
            case 9000020 -> {
                // 9000020, スピネル
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 104000000, 港口
                // 200000000, オルビス
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                // 250000000, 武陵
                // 251000000, 白草村
                // 260000000, アリアント
                // 261000000, マガティア
                // 500000000, 水上市場
                // 701000000, 上海ワイタン
                // 702000000, 宋山里
                // 740000000, 台湾西門町
                // 800000000, キノコ神社
                // 950000000, 여행을 끝내고
                script = new NpcSpinel();
            }
            case 9110000, 9110008 -> {
                // 9110000, ペリー
                // 103000000, カニングシティー
                // 9110008, ペリー
                // 800000000, キノコ神社
                script = new NpcPerry();
            }
            case 9110107 -> {
                // 9110107, 駕籠
                // 800000000, キノコ神社
                // 800040000, 楓城 天下泰平
                script = new NpcPalakeen();
            }
            case 9120003 -> {
                // 9120003, ヒカリ
                // 801000000, ショーワ町
                script = new NpcHikari();
            }
            case 9120015 -> {
                // 9120015, コンペイ
                // 801000000, ショーワ町
                script = new NpcKonpei();
            }
            case 9120026 -> {
                // 9120026, クリスタル
                // 800040000, 楓城 天下泰平
                // 802000101, カムナ
                // 802000313, 公園 2095年
                script = new NpcCrystal();
            }
            default -> {
                return false;
            }
        }
        script.setNpcId(npc_script_id);
        script.start((IDebugMan) script, chr);
        return true;
    }
}
