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
            case 1:
                {
                    MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIP);
                    equip.addItem(ii.getEquipById(itemid));
                    break;
                }
            case 2:
                {
                    MapleInventory use = chr.getInventory(MapleInventoryType.USE);
                    use.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                    break;
                }
            case 3:
                {
                    MapleInventory setup = chr.getInventory(MapleInventoryType.SETUP);
                    setup.addItem(new Item(itemid, (byte) 0, (short) 1, (byte) 0));
                    break;
                }
            case 4:
                {
                    MapleInventory etc = chr.getInventory(MapleInventoryType.ETC);
                    etc.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                    break;
                }
            case 5:
                {
                    MapleInventory cash = chr.getInventory(MapleInventoryType.CASH);
                    cash.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                    break;
                }
            default:
                {
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
        // エリクサー
        AddItem(chr, 2000004, 100);
        // 万病治療薬
        AddItem(chr, 2050004, 100);
        // コーヒー牛乳
        AddItem(chr, 2030008, 100);
        // いちご牛乳
        AddItem(chr, 2030009, 100);
        // フルーツ牛乳
        AddItem(chr, 2030010, 100);
        // 帰還の書(ヘネシス)
        AddItem(chr, 2030004, 100);
        // 強化書
        AddItem(chr, 2040303, 100);
        AddItem(chr, 2040506, 100);
        AddItem(chr, 2040710, 100);
        AddItem(chr, 2040807, 100);
        AddItem(chr, 2044703, 100);
        AddItem(chr, 2044503, 100);
        AddItem(chr, 2043803, 100);
        AddItem(chr, 2043003, 100);
        AddItem(chr, 2049100, 100);
        AddItem(chr, 2049003, 100);
        AddItem(chr, 2049300, 100);
        AddItem(chr, 2049400, 100);
        AddItem(chr, 2470000, 100);
        // 魔法の石
        AddItem(chr, 4006000, 100);
        // 召喚の石
        AddItem(chr, 4006001, 100);
        // 軍手(茶)
        AddItem(chr, 1082149);
        // ドロシー(銀)
        AddItem(chr, 1072264);
        // 冒険家のマント(黄)
        AddItem(chr, 1102040);
        // 緑ずきん
        AddItem(chr, 1002391);
        // オウルアイ
        AddItem(chr, 1022047);
        // 犬鼻
        AddItem(chr, 1012056);
        // メイプルシールド
        AddItem(chr, 1092030);
        // タオル(黒)
        AddItem(chr, 1050127);
        // バスタオル(黄)
        AddItem(chr, 1051140);
        // エレメントピアス
        AddItem(chr, 1032062);
        // 錬金術師の指輪
        AddItem(chr, 1112400);
        // ドラゴン(アビス)
        AddItem(chr, 3010047);
        // ポイントアイテム
        AddItem(chr, 5071000, 100); // 拡声器
        AddItem(chr, 5076000, 100); // アイテム拡声器
        AddItem(chr, 5370000, 100); // 黒板
        AddItem(chr, 5140000); // 営業許可証
        AddItem(chr, 5041000, 100); // 高性能テレポストーン
        AddItem(chr, 5220000, 100); // ガシャポンチケット
        AddItem(chr, 5570000, 100); // ビシャスのハンマー
        AddItem(chr, 5062000, 100); // ミラクルキューブ
        AddItem(chr, 5610000, 100); // ベガの呪文書(10%)
        AddItem(chr, 5610001, 100); // ベガの呪文書(60%)
        AddItem(chr, 5050000, 100); // AP再分配の書
        // プレミアムさすらいの商人ミョミョ
        return true;
    }
    
}
