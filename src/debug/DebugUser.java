/*
 * Copyright (C) 2024 Riremito
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
package debug;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.DebugConfig;
import server.MapleItemInformationProvider;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class DebugUser {

    // 初期アイテムをキャラクターデータに追加
    public static boolean AddItem(MapleCharacter chr, int itemid) {
        return AddItem(chr, itemid, 1);
    }

    public static boolean AddItem(MapleCharacter chr, int itemid, int count) {
        if (!LoadData.IsValidItemID(itemid)) {
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        // 存在しないitemid
        if (!ii.itemExists(itemid)) {
            Debug.ErrorLog("AddItem: " + itemid);
            return false;
        }
        switch (itemid / 1000000) {
            case 1: {
                MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIP);
                equip.addItem(ii.getEquipById(itemid));
                break;
            }
            case 2: {
                MapleInventory use = chr.getInventory(MapleInventoryType.USE);
                use.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            case 3: {
                MapleInventory setup = chr.getInventory(MapleInventoryType.SETUP);
                setup.addItem(new Item(itemid, (byte) 0, (short) 1, (byte) 0));
                break;
            }
            case 4: {
                MapleInventory etc = chr.getInventory(MapleInventoryType.ETC);
                etc.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            case 5: {
                MapleInventory cash = chr.getInventory(MapleInventoryType.CASH);
                cash.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    public static boolean AddStarterSet(MapleCharacter chr) {
        if (!DebugConfig.starter_set) {
            return false;
        }
        // メル
        chr.setMeso(777000000);
        // パチンコ玉
        chr.setTama(500000);
        // 装備
        {
            AddItem(chr, 1302064); // メイプルグローリーソード
            AddItem(chr, 1402039); // メイプルソールロヘン
            AddItem(chr, 1312032); // メイプルスチールアックス
            AddItem(chr, 1412027); // メイプルデーモンアックス
            AddItem(chr, 1322054); // メイプルハバークハンマー
            AddItem(chr, 1422029); // メイプルベルゼット
            AddItem(chr, 1432040); // メイプルベリットスピア
            AddItem(chr, 1442051); // メイプルカルスタン
            AddItem(chr, 1452045); // メイプルガンディバボウ
            AddItem(chr, 1462040); // メイプルニシャダ
            AddItem(chr, 1332056); // メイプルアスラダガー
            AddItem(chr, 1332055); // メイプルダークメイト
            AddItem(chr, 1372034); // メイプルシャイニワンド
            AddItem(chr, 1382039); // メイプルウィズダムスタッフ
            AddItem(chr, 1472055); // メイプルスカンダ
            AddItem(chr, 1492022); // メイプルキャノンシューター
            AddItem(chr, 1482022); // メイプルゴールデンクロー
            AddItem(chr, 1342027); // メイプルクリート
            AddItem(chr, 1092030); // メイプルシールド
            AddItem(chr, 1002511); // メイプル帽子(揺籃)
            AddItem(chr, 1122000); // ホーンテイルのネックレス
            AddItem(chr, 1142103); // トウキョウメシア
            AddItem(chr, 1022067); // エリマキキツネサルアイ
            AddItem(chr, 1112402); // マジカルピグミーリング
            AddItem(chr, 1112403); // パワーピグミーリング
            AddItem(chr, 1132004); // 武陵ベルト(黒)
            AddItem(chr, 1082149); // 軍手(茶)
            AddItem(chr, 1072238); // アイゼン(紫)
            AddItem(chr, 1002668); // ジャクムの兜(昇華繚乱)
            AddItem(chr, 1072264); // ドロシー(銀)
            AddItem(chr, 1102040); // 冒険家のマント(黄)
            AddItem(chr, 1002391); // 緑ずきん
            AddItem(chr, 1022047); // オウルアイ
            AddItem(chr, 1012056); // 犬鼻
            AddItem(chr, 1050127); // タオル(黒)
            AddItem(chr, 1051140); // バスタオル(黄)
            AddItem(chr, 1032062); // エレメントピアス
            AddItem(chr, 1112400); // 錬金術師の指輪
            AddItem(chr, 1352003); // 無限の魔法の矢
        }
        // 消費
        {
            AddItem(chr, 2000004, 100); // エリクサー
            AddItem(chr, 2050004, 100); // 万病治療薬
            AddItem(chr, 2030008, 100); // コーヒー牛乳
            AddItem(chr, 2030009, 100); // いちご牛乳
            AddItem(chr, 2030010, 100); // フルーツ牛乳
            //AddItem(chr, 2030004, 100); // 帰還の書(ヘネシス)
            AddItem(chr, 2043005, 100); // 呪われた攻撃の書(片手剣) 30%
            AddItem(chr, 2043003, 100); // 奇跡の攻撃の書(片手剣)
            AddItem(chr, 2041200, 100); // ホーンテイルの心臓
            AddItem(chr, 2049100, 100); // 混沌の書 60%
            AddItem(chr, 2049003, 100); // 白の書 20%
            AddItem(chr, 2049300, 100); // 高級装備強化の書
            AddItem(chr, 2049400, 100); // 高級潜在能力覚醒の書
            AddItem(chr, 2060003, 100); // 弓専用の矢
            AddItem(chr, 2061003, 100); // 弩専用の矢
            AddItem(chr, 2070000, 600); // 水の手裏剣
            AddItem(chr, 2100000, 100); // 黒い包み
            AddItem(chr, 2120000, 100); // ペットのエサ
            AddItem(chr, 2120008);      // 
            AddItem(chr, 2150001);      // ファンキーモンキーベイビーズのテーマ
            AddItem(chr, 2190000);      // マクロ探知機
            AddItem(chr, 2210000);      // メイプルキノコの像
            AddItem(chr, 2230000);      // 
            AddItem(chr, 2240000);      // 指輪
            AddItem(chr, 2241000);      // 
            AddItem(chr, 2242004);      // 
            AddItem(chr, 2260000);      // 
            AddItem(chr, 2270000);      // 
            AddItem(chr, 2280000);      // スキルブック系統
            AddItem(chr, 2290000);      // マスタリーブック
            AddItem(chr, 2310000);      // 不思議なフクロウ
            AddItem(chr, 2320000);      // テレポストーン
            AddItem(chr, 2330000, 100); // ブレット
            //AddItem(chr, 2340000, 1);   // White Scroll (海外)
            //AddItem(chr, 2350000);      // キャラクタースロット
            AddItem(chr, 2370000, 100); // 兵法書(孫子)
            AddItem(chr, 2390001);      // 
            AddItem(chr, 2420004);      // ゲート
            AddItem(chr, 2430003);      // 
            AddItem(chr, 2440000);      // 
            //AddItem(chr, 2450000);      // EXP2倍バフ
            AddItem(chr, 2460003, 100);      // 鑑定の虫眼鏡
            AddItem(chr, 2461000);      // 
            AddItem(chr, 2470000, 100); // 黄金つち
            AddItem(chr, 2500000);      // 
            AddItem(chr, 2530000);      // 
            AddItem(chr, 2531000);      // 
            AddItem(chr, 2540000);      // 
            AddItem(chr, 2550000);      // 
            AddItem(chr, 2560000);      // 
            AddItem(chr, 2570000);      // 
            AddItem(chr, 2580000);      // 
            AddItem(chr, 2590000);      // 魂の書
            AddItem(chr, 2591000);      // 魂の玉
            AddItem(chr, 2592000);      // 魂の玉
            AddItem(chr, 2600000);      // 
            AddItem(chr, 2701000);      // 究極のサーキュレーター
            AddItem(chr, 2850000);      // 
            AddItem(chr, 2870000);      // デンデンファミリアカード
            AddItem(chr, 2920000);      // 
        }
        // 設置
        {
            AddItem(chr, 3010047); // ドラゴン(アビス)
            AddItem(chr, 3011000); // 釣り用の椅子
            AddItem(chr, 3012000); // ハートラブチェア
            AddItem(chr, 3013001); // 封絶
            AddItem(chr, 3049002); // 上級分解機
            AddItem(chr, 3050000);
            AddItem(chr, 3051000);
            AddItem(chr, 3052000);
            AddItem(chr, 3700000); // 妖精リシの友達
            AddItem(chr, 3990000);
            AddItem(chr, 3991000);
            AddItem(chr, 3992000);
            AddItem(chr, 3993000);
            AddItem(chr, 3994000);
        }
        // ETC
        {
            AddItem(chr, 4006000, 100); // 魔法の石
            AddItem(chr, 4006001, 100); // 召喚の石
        }
        // ポイントアイテム
        {
            AddItem(chr, 5000023);      // ペンギン
            AddItem(chr, 5010000);      // ニコニコ太陽
            //AddItem(chr, 5021001);      // 音速紙飛行機
            AddItem(chr, 5030000);      // エルフ商人
            AddItem(chr, 5040000);      // テレポストーン
            AddItem(chr, 5041000);      // 高性能テレポストーン
            //AddItem(chr, 5042000);      // 赤色のテレポストーン? v302 crash
            AddItem(chr, 5043000);      // NPC瞬間移動石
            AddItem(chr, 5044000);      // テレポートワールドマップ
            AddItem(chr, 5050000, 100); // AP再分配の書
            AddItem(chr, 5050001);      // 1次スキルSP再分配の書
            AddItem(chr, 5050100);      // AP初期化の書
            //AddItem(chr, 5051000);      // 
            AddItem(chr, 5051001);      // SP初期化の書
            AddItem(chr, 5062000, 100); // ミラクルキューブ
            AddItem(chr, 5062001, 100); // ハイパーミラクルキューブ
            AddItem(chr, 5062002, 100); // マスターミラクルキューブ
            AddItem(chr, 5062003, 100); // ハイパーマスターミラクルキューブ
            AddItem(chr, 5071000, 100); // 拡声器
            AddItem(chr, 5076000, 100); // アイテム拡声器
            AddItem(chr, 5140000);      // 営業許可証
            AddItem(chr, 5220000, 100); // ガシャポンチケット
            AddItem(chr, 5370000);      // 黒板
            AddItem(chr, 5570000, 100); // ビシャスのハンマー
            AddItem(chr, 5610000, 100); // ベガの呪文書(10%)
            AddItem(chr, 5610001, 100); // ベガの呪文書(60%)
            // プレミアムさすらいの商人ミョミョ
        }
        return true;
    }

}
