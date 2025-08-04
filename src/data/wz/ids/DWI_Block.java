/*
 * Copyright (C) 2025 Riremito
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
package data.wz.ids;

import config.ContentState;

/**
 *
 * @author Riremito
 */
public class DWI_Block {

    private static final int[] event_npcs = {
        9010000, // イベントガイド
        9010010, // カサンドラ
        9000040, // ダリア (勲章)
        9000041, // 寄付 (勲章)
        1002103, // アール (ファミリーガイド)
        9105009, // ナオミ (メイプルクリスタル)
        9105019, // 助手みどり (調髪)
        9000021, // ガガ (モンスターレイド)
        9102002, // オスト (にわとりイベント)
        9330093, // ビッキィ＆ケッキー (バレンタインイベント)
        9001102, // 月うさぎ (月)
        9120105, // キャサリン (パチンコ)
        9201023, // ナナ (結婚式)
        9000018, // マチルダ (ネットカフェ)
        2101018, // セザール (闘技場)
        2042000, // シュピゲルマン (モンスターカーニバル)
        9250120, // 公衆電話 (ビジター)
        9250121, // ゴミ箱おじさん (ビジター)
        9250123, // ??? (ビジター)
        9250156, // OSSS研究員 (ビジター)
        9250136 // ビンポス (ビジター)
    };

    private static final int[] mapleTV_npcs = {
        9250022,
        9250023,
        9250024,
        9250025,
        9250026,
        9250042,
        9250043,
        9250044,
        9250045,
        9250046
    };

    public static boolean checkNpc(int id) {
        if (ContentState.CS_HIDE_EVENT_NPC.get()) {
            for (int blocked_id : event_npcs) {
                if (blocked_id == id) {
                    return true;
                }
            }
        }

        if (ContentState.CS_HIDE_MAPLE_TV.get()) {
            // old ver = 9700001 ?_?
            // JMS147, 9250042
            for (int blocked_id : mapleTV_npcs) {
                if (blocked_id == id) {
                    return true;
                }
            }
        }

        return false;
    }

    private static final int[] event_mobs = {
        9400222 // ダンボールに入ったスイカ (JMS147)
    };

    public static boolean checkMob(int id) {
        if (ContentState.CS_HIDE_EVENT_MOB.get()) {
            for (int blocked_id : event_mobs) {
                if (blocked_id == id) {
                    return true;
                }
            }
        }

        return false;
    }

}
