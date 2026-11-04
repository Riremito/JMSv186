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

import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_Common_Gachapon extends NpcScriptMan {

    private final int item_ticket = 5220000; // ガシャポンチケット
    private final int item_ticket_pachinko = 4110010; // パチンコガシャポンカード
    private final int item_ticket_internet_cafe = 5220020; // ガシャポンチケット(ネットカフェ専用)
    private final int item_ticket_campaign = 5220030; // ガシャポンカード(キャンペーン専用)
    private final int item_ticket_mobile = 5220050; // ガシャポンチケット(モバイル専用)
    private final int item_ticket_coca_cola_1 = 5220060; // コカ・コーラガシャポンチケット1
    private final int item_ticket_coca_cola_2 = 5220070; // コカ・コーラガシャポンチケット2
    private final int item_ticket_coca_cola_3 = 5220080; // コカ・コーラガシャポンチケット3
    // TODO : 景品 (仮のリスト)
    private final int[] rewards_default = {
        1102040, // 冒険家のマント(黄)
        1102041, // 冒険家のマント(桃)
        1102042, // 冒険家のマント(紫)
        1102084, // ガイアのマント(桃)
        1102085, // ガイアのマント(黄)
        1102086 // ガイアのマント(紫)
    };
    private final int[] rewards_henesys = {
        3010013, // パラソルビーチ（桃）
        3010058, // World End
        3010091, // 澪のベース
        3010087, // 紬のキーボード
        3010089, // 梓のギター
        3010088, // 唯のギター
        3010090, // 律のドラム
        3010098, // テレビ&amp;リクライニングチェア
        3010020, // 猫とおこた
        3010174, // ウィッチチェア
        4001396, // 機会の証書
        4130012, // 弓製作の促進剤
        4001010, // ママシュ消しゴム
        4001039, // ママシュ消しゴム
        2049002, // 白の書5%
        2000019, // パワーエリクサー 
        2000004, // エリクサー
        2022025, // たこ焼き(ジャンボ)
        2022027 // 焼きそば(大盛)
    };
    private final int[] rewards_kerning_city = {
        3010013, // パラソルビーチ（桃）
        3010058, // World End
        3010091, // 澪のベース
        3010087, // 紬のキーボード
        3010089, // 梓のギター
        3010088, // 唯のギター
        3010090, // 律のドラム
        3010098, // テレビ&amp;リクライニングチェア
        3010020, // 猫とおこた
        3010174, // ウィッチチェア
        3010149, // にゃんぷうき
        3010148, // トーガ
        3013001, // 封絶
        3010151, // 無人島チェア
        3010153, // マリーンズメイプルキノコ椅子
        3010146, // 7周年クリスタルチェア
        3010173, // ハロウィンタロットチェア
        4001396, // 機会の証書
        4130012, // 弓製作の促進剤
        4001012, // レイス消しゴム
        4001041, // レイス消しゴム
        4001197, // 青い鉱石
        2049002, // 白の書5%
        2000019, // パワーエリクサー 
        2000004, // エリクサー
        2022025, // たこ焼き(ジャンボ)
        2022027 // 焼きそば(大盛)
    };
    private final int[] rewards_mushroom_shrine = {
        1102040, // 冒険家のマント(黄)
        1102041, // 冒険家のマント(桃)
        1102042, // 冒険家のマント(紫)
        1102084, // ガイアのマント(桃)
        1102085, // ガイアのマント(黄)
        1102086, // ガイアのマント(紫)
        1002391, // 緑ずきん
        1002392, // 茶ずきん
        1002894, // 水玉ヘアバンド（桃)
        1002895, // 水玉ヘアバンド（赤)
        1002896, // 水玉ヘアバンド（紫)
        1002897, // 水玉ヘアバンド（橙)
        1002898, // 水玉ヘアバンド（緑)
        1002899, // 水玉ヘアバンド（黄)
        1002900, // 水玉ヘアバンド（青)
        1002901, // 水玉ヘアバンド（白)
        1002902, // 水玉ヘアバンド（黒)
        1082002, // 軍手
        1082145, // 軍手(黄)
        1082146, // 軍手(赤)
        1082147, // 軍手(青)
        1082148, // 軍手(紫)
        1082149, // 軍手(茶)
        1082150, // 軍手(灰)
        1072264, // ドロシー(銀)
        1022047, // オウルアイ
        1022060, // Ｗｏｗ！キツネサルアイ
        1022067, // エリマキキツネサルアイ
        1012056, // 犬鼻
        1012135, // ピグ鼻
        1402013, // 日本列島
        1302105, // クリスマスフェアリー
        1312039, // クリスマスフェアリー
        1322065, // クリスマスフェアリー
        1332081, // クリスマスフェアリー
        1372046, // クリスマスフェアリー
        1382062, // クリスマスフェアリー
        1402053, // クリスマスフェアリー
        1412035, // クリスマスフェアリー
        1422039, // クリスマスフェアリー
        1432050, // クリスマスフェアリー
        1442071, // クリスマスフェアリー
        1452062, // クリスマスフェアリー
        1462056, // クリスマスフェアリー
        1472077, // クリスマスフェアリー
        1482029, // クリスマスフェアリー
        1492030, // クリスマスフェアリー
        1050100, // タオル
        1050127, // タオル(黒)
        1051098, // バスタオル
        1051140, // バスタオル(黄)
        1002972, // ヴェイカントサークレット
        1132004, // 武陵ベルト(黒)
        1132040, // ビジタープレミアムベルト
        1112439, // ビジターリングプレミアム
        1112412, // シュピゲルマンの指輪
        1112400, // 錬金術師の指輪
        1152001, // 黒虎の爪
        1012132, // ピヨピヨマウス
        1122007, // シュピゲルマンのネックレス
        1122069, // シュピゲルマンの混沌のネックレス
        1122085, // ビジタープレミアムネックレス
        1122000, // ホーンテイルのネックレス
        1122076, // カオスホーンテイルのネックレス
        1032062, // エレメントピアス
        1032084, // ビジタープレミアムイヤリング
        1072344, // 邪悪な戦士の靴
        1082223, // ギャラクシーグローブ
        1002751, // ファンタ ドリルヘッド
        1002788, // りすみみ
        1002844, // しまりすみみ
        1002668, // ジャクムの兜（昇華繚乱)
        1003112, // カオスジャクムの兜
        1002574, // デイビーゾーンの帽子
        2022025 // たこ焼き(ジャンボ)
    };
    private static final int MENU_PROB = 0;
    private static final int MENU_USE = 1;

    @Override
    public boolean action(int status, int answer) {
        int ticket = this.item_ticket;
        int[] rewards = this.rewards_default;
        String name = "ガシャポン";
        // 排出率のメニューを表示する
        boolean show_menu = false;
        switch (getNpcId()) {
            case 9100100 -> {
                // 未実装的something
                name = "謎のガシャポン";
            }
            case 9100101 -> {
                // ヘネシス
                rewards = this.rewards_henesys;
                show_menu = true;
            }
            case 9100103 -> {
                // カニングシティー
                rewards = this.rewards_kerning_city;
                show_menu = true;
            }
            case 9100106 -> {
                // キノコ神社
                rewards = this.rewards_mushroom_shrine;
                show_menu = true;
            }
            case 9100109 -> {
                // パチンコ屋
                ticket = this.item_ticket_pachinko;
            }
            case 9100110 -> {
                // ネットカフェ
                ticket = this.item_ticket_internet_cafe;
            }
            case 9100111 -> {
                // キャンペーン
                ticket = this.item_ticket_campaign;
            }
            case 9100113 -> {
                // モバイル
                ticket = this.item_ticket_mobile;
            }
            case 9100114 -> {
                // ファンタ
                ticket = this.item_ticket_coca_cola_2;
            }
            case 9100115 -> {
                // アクエリアス
                ticket = this.item_ticket_coca_cola_3;
            }
            case 9100116 -> {
                // コカ･コーラ ゼロ
                ticket = this.item_ticket_coca_cola_1;
            }
            case 9100117 -> {
                // ノーチラスガシャポン 未実装的な奴?
                name = "ノーチラスガシャポン";
            }
            default -> {
                // 9100102 ペリオン, 9100104 スリーピーウッド, 9100105 エルナス, 9100107 銭湯（男）, 9100108 銭湯（女）
            }
        }
        // メニューを表示しない場合は利用の確認から始める
        int step = show_menu ? status : status + 1;
        switch (step) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ガシャポンです。");
                nt.addMenu(MENU_USE, "ガシャポンを利用したいです");
                nt.addMenu(MENU_PROB, "排出率");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (show_menu && answer == MENU_PROB) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("排出確率");
                    for (int reward : rewards) {
                        nt.add("#v" + reward + "##t" + reward + "# #b(" + (100 / rewards.length) + "%)#k");
                    }
                    say(nt);
                    return false;
                }
                if (!checkItem(ticket)) {
                    // 原文ママ
                    say(name + "が置いてある…");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(name + "が置いてある。#b#z" + ticket + "##kを使いますか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                if (!checkItem(ticket)) {
                    return false;
                }
                int reward = rewards[(int) (Math.random() * rewards.length)];
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), reward, 1, "")) {
                    // TODO : 文章
                    say("[test]アイテム欄が不足しています");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(ticket), ticket, 1, true, false);
                // TODO : ランダムステータス, ガシャポンの告知 (BM_GACHAPONANNOUNCE, ヘネシスとキノコ神社以外)
                MapleInventoryManipulator.addById(getPlayer(), reward, 1); // TODO : item effect.
                say("#b#t" + reward + "##k１個を獲得しました！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
