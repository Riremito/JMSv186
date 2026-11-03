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
import tacos.wz.NpcWz.NpcInfo;

/**
 *
 * @author Riremito
 */
public class NpcMan {

    public static boolean start(TacosCharacter chr, NpcInfo npc_info, int npc_script_id, int npc_id) {
        NpcScriptMan npc_script;
        switch (npc_script_id) {
            case 1002002 -> {
                // 1002002, ペィソン
                // 104000000, 港口
                npc_script = new NpcPason();
            }
            case 1002004, 1032005 -> {
                // 1002004, リス港口高級タクシー
                // 104000000, 港口
                // 1032005, エリニア高級タクシー
                // 101000000, エリニア
                npc_script = new NpcVIPCab();
            }
            case 1012000, 1022001, 1032000, 1052016 -> {
                // 1012000, メイプル運輸大型タクシー
                // 100000000, ヘネシス
                // 101010000, エリニアの北側フィールド
                // 101010100, そびえ立つ木1
                // 101010101, そびえ立つ木2
                // 101010102, そびえ立つ木3
                // 101020000, 北の森
                // 1022001, メイプル運輸大型タクシー
                // 101030101, 遺跡発掘地1
                // 102000000, ぺリオン
                // 1032000, メイプル運輸大型タクシー
                // 101000000, エリニア
                // 1052016, メイプル運輸大型タクシー
                // 103000000, カニングシティー
                npc_script = new NpcRegularCab();
            }
            case 1052012 -> {
                // 1052012, モンロン
                // 103000000, カニングシティー
                npc_script = new NpcMongFromKong();
            }
            case 1052013 -> {
                // 1052013, パソコン
                // 193000000, ネットカフェ
                npc_script = new NpcComputer();
            }
            case 1092014 -> {
                // 1092014, ノーチラス大型タクシー
                // 120000000, ノーチラス
                npc_script = new NpcNautilusMidSizedTaxi();
            }
            case 2023000 -> {
                // 2023000, 危険地域弾丸タクシー
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                npc_script = new NpcDangerZoneTaxi();
            }
            case 2030006 -> {
                // 2030006, 聖なる岩
                // 211040401, 雪原の聖地
                npc_script = new NpcHolyStone();
            }
            case 2030008 -> {
                // 2030008, アドビス
                // 211042300, ジャクムへの門
                npc_script = new NpcAdobis();
            }
            case 2060009 -> {
                // 2060009, イルカ
                // 230000000, アクアリウム
                // 251000100, 海側波止場
                npc_script = new NpcDolphin();
            }
            case 2082003 -> {
                // 2082003, コルバ
                // 240000110, ステーション<オルビス行き>
                npc_script = new NpcCorba();
            }
            case 2090005 -> {
                // 2090005, ツノーレ
                // 200000141, ステーション通路<武陵行き>
                // 250000100, 武陵神社
                // 251000000, 白草村
                npc_script = new NpcCrane();
            }
            case 2101014 -> {
                // 2101014, セザール
                // 980010000, 闘技場の控え室
                npc_script = new NpcCesar();
            }
            case 2110005 -> {
                // 2110005, ラクダ
                // 260020000, アリアント北門の外
                // 260020700, サヘル地帯1
                npc_script = new NpcCamel();
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
                npc_script = new NpcSpinel();
            }
            case 9110000, 9110008 -> {
                // 9110000, ペリー
                // 103000000, カニングシティー
                // 9110008, ペリー
                // 800000000, キノコ神社
                npc_script = new NpcPerry();
            }
            case 9110107 -> {
                // 9110107, 駕籠
                // 800000000, キノコ神社
                // 800040000, 楓城 天下泰平
                npc_script = new NpcPalakeen();
            }
            case 9120003 -> {
                // 9120003, ヒカリ
                // 801000000, ショーワ町
                npc_script = new NpcHikari();
            }
            case 9120015 -> {
                // 9120015, コンペイ
                // 801000000, ショーワ町
                npc_script = new NpcKonpei();
            }
            case 9120026 -> {
                // 9120026, クリスタル
                // 800040000, 楓城 天下泰平
                // 802000101, カムナ
                // 802000313, 公園 2095年
                npc_script = new NpcCrystal();
            }
            case 9120201 -> {
                // 9120201, コンペイ
                // 801040004, 武器庫
                npc_script = new NpcKonpeiBodyguard();
            }
            case 9310004 -> {
                // 9310004, 婦人警官ポリン
                // 701010320, 中原山岳地帯2
                npc_script = new NpcOfficerLim();
            }
            case 9310005 -> {
                // 9310005, 警察官ミャオ
                // 701010321, ブラックシープンの領域
                npc_script = new NpcOfficerChung();
            }
            case 9310006 -> {
                // 9310006, 警察官ミカーファイ
                // 701010322, 抜け道
                npc_script = new NpcOfficerHuh();
            }
            case 9310007 -> {
                // 9310007, 警察官ハーク
                // 701010322, 抜け道
                // 701010323, 危険な丘陵
                // 701010324, 悲鳴の丘陵
                npc_script = new NpcOfficerKang();
            }
            case 9310039 -> {
                // 9310039, 掛け軸
                // 702070400, 蔵経閣7階
                npc_script = new NpcHangingScrollEnter();
            }
            case 9310044 -> {
                // 9310044, 掛け軸
                // 702060000, 修行の間
                npc_script = new NpcHangingScrollExit();
            }
            case 9330028 -> {
                // 9330028, メロノン
                // 741020100, 夜市場分かれ道2
                npc_script = new NpcMelonon();
            }
            case 9330032 -> {
                // 9330032, 果物屋トレホレ
                // 741020101, 夜市場裏道1
                // 741020102, 夜市場裏道2
                npc_script = new NpcFruitStandGramps();
            }
            case 9330045 -> {
                // 9330045, 釣り場管理人
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 105040300, スリーピーウッド
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 701000000, 上海ワイタン
                // 702000000, 宋山里
                // 740000000, 台湾西門町
                // 741000000, 夜市場
                // 800000000, キノコ神社
                // 801000000, ショーワ町
                npc_script = new NpcKedrick();
            }
            default -> {
                return false;
            }
        }
        npc_script.setNpcId(npc_script_id);
        npc_script.start(chr);
        return true;
    }
}
