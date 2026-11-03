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

import odin.server.MapleInventoryManipulator;
import odin.constants.GameConstants;

/**
 *
 * @author Riremito
 */
public class NpcFaito extends NpcScriptMan {

    private final int[] item_ids = {
        4000064, // 鴉の羽
        4000065, // 火狸の薪
        4000066, // 野狐の尾
        4000075, // 夜行の三角巾
        4000077, // 夜狐の尾
        4000089, // 手下Aのバッジ
        4000090, // 手下Bの御札
        4000091, // 手下Cの磁気ネックレス
        4000092, // 幹部Aのサングラス
        4000093, // 幹部Bのお守り
        4000094 // 親分のポマード
    };
    // {アイテムID, 個数}
    private final int[][][] prizes = {
        // 鴉の羽
        {
            {2000000, 1}, // 赤い薬
            {2000006, 1}, // マナエリクサー
            {2000003, 5}, // 青い薬
            {2000002, 5}, // 白い薬
            {4020006, 2}, // トパーズの原石
            {4020000, 2}, // ガーネットの原石
            {4020004, 2}, // オパールの原石
            {2000003, 10}, // 青い薬
            {2000003, 20}, // 青い薬
            {2000002, 10}, // 白い薬
            {2000002, 20}, // 白い薬
            {2022026, 15}, // 焼きそば
            {2022024, 15}, // たこ焼き
            {1002393, 1} // 桃ずきん
        },
        // 火狸の薪
        {
            {2000006, 1}, // マナエリクサー
            {2000002, 5}, // 白い薬
            {4020006, 2}, // トパーズの原石
            {2000002, 10}, // 白い薬
            {2000003, 10}, // 青い薬
            {2000002, 20}, // 白い薬
            {2000003, 20}, // 青い薬
            {2022024, 15}, // たこ焼き
            {2022026, 15} // 焼きそば
        },
        // 野狐の尾
        {
            {2000006, 1}, // マナエリクサー
            {2000002, 5}, // 白い薬
            {2000003, 5}, // 青い薬
            {4020000, 2}, // ガーネットの原石
            {2000003, 10}, // 青い薬
            {2000002, 10}, // 白い薬
            {2000003, 20}, // 青い薬
            {2000002, 20}, // 白い薬
            {2022024, 15}, // たこ焼き
            {1002393, 1} // 桃ずきん
        },
        // 夜行の三角巾
        {
            {2060003, 1000}, // 尖晶の矢（弓専用）
            {4010004, 2}, // 銀の原料
            {4010006, 2}, // 金の原料
            {2022022, 5}, // おでん(皿)
            {2022022, 10}, // おでん(皿)
            {2022022, 15}, // おでん(皿)
            {2022019, 5}, // キノコラーメン(豚骨)
            {2022019, 10}, // キノコラーメン(豚骨)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2001002, 15}, // 小豆かき氷
            {2001001, 15}, // チューチューアイス
            {1102040, 1}, // 冒険家のマント(黄)
            {1102043, 1} // 冒険家のマント(茶)
        },
        // 夜狐の尾
        {
            {2000003, 1}, // 青い薬
            {2022019, 5}, // キノコラーメン(豚骨)
            {2000006, 5}, // マナエリクサー
            {4010002, 2}, // ミスリルの原料
            {4010003, 2}, // アダマンティウムの原料
            {2000006, 10}, // マナエリクサー
            {2000006, 15}, // マナエリクサー
            {2022019, 10}, // キノコラーメン(豚骨)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2060003, 1000}, // 尖晶の矢（弓専用）
            {2061003, 1000}, // 藍晶の矢（弩専用）
            {1082150, 1}, // 軍手(灰)
            {1082149, 1} // 軍手(茶)
        },
        // 手下Aのバッジ
        {
            {2000006, 1}, // マナエリクサー
            {2000003, 5}, // 青い薬
            {2000002, 5}, // 白い薬
            {2000003, 10}, // 青い薬
            {2000003, 20}, // 青い薬
            {2000002, 10}, // 白い薬
            {2000002, 15}, // 白い薬
            {2060003, 1000}, // 尖晶の矢（弓専用）
            {2061003, 1000}, // 藍晶の矢（弩専用）
            {2022026, 15}, // 焼きそば
            {1002395, 1} // 紫ずきん
        },
        // 手下Bの御札
        {
            {2022019, 5}, // キノコラーメン(豚骨)
            {2000006, 5}, // マナエリクサー
            {4010003, 2}, // アダマンティウムの原料
            {2022019, 10}, // キノコラーメン(豚骨)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2000006, 10}, // マナエリクサー
            {2000006, 15}, // マナエリクサー
            {2060003, 1000}, // 尖晶の矢（弓専用）
            {2061003, 1000} // 藍晶の矢（弩専用）
        },
        // 手下Cの磁気ネックレス
        {
            {2000003, 1}, // 青い薬
            {2000006, 1}, // マナエリクサー
            {2022019, 1}, // キノコラーメン(豚骨)
            {2000006, 5}, // マナエリクサー
            {4010002, 2}, // ミスリルの原料
            {4020001, 2}, // 紫水晶の原石
            {2022019, 10}, // キノコラーメン(豚骨)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2000006, 10}, // マナエリクサー
            {2000006, 15}, // マナエリクサー
            {2060003, 1000}, // 尖晶の矢（弓専用）
            {2061003, 1000} // 藍晶の矢（弩専用）
        },
        // 幹部Aのサングラス
        {
            {2022019, 5}, // キノコラーメン(豚骨)
            {2022022, 5}, // おでん(皿)
            {4010006, 2}, // 金の原料
            {2022019, 10}, // キノコラーメン(豚骨)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2022022, 10}, // おでん(皿)
            {2022022, 15}, // おでん(皿)
            {2001002, 15}, // 小豆かき氷
            {2001001, 15}, // チューチューアイス
            {1102043, 1} // 冒険家のマント(茶)
        },
        // 幹部Bのお守り
        {
            {4010004, 5}, // 銀の原料
            {2022019, 5}, // キノコラーメン(豚骨)
            {2022022, 15}, // おでん(皿)
            {2022019, 15}, // キノコラーメン(豚骨)
            {2001002, 15}, // 小豆かき氷
            {2001001, 15}, // チューチューアイス
            {1102043, 1} // 冒険家のマント(茶)
        },
        // 親分のポマード
        {
            {2000006, 1}, // マナエリクサー
            {4020008, 15}, // 黒水晶の原石
            {2022018, 5}, // キノコラーメン(焼豚)
            {2022018, 10}, // キノコラーメン(焼豚)
            {2022018, 15}, // キノコラーメン(焼豚)
            {2022000, 10}, // ミネラルウォーター
            {2022000, 20}, // ミネラルウォーター
            {2022025, 15} // たこ焼き(ジャンボ)
        }
    };
    private final int required_quantity = 100;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("あらゆる物の違いがわかる男とは俺のことだ。今探しているものがあるんだが、俺の話を聞いてみるかい。");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("俺が探しているアイテムは1,2,3…たくさんあって数えられねぇな。とにかく同じものを" + this.required_quantity + "個集めてくれば、その中のレア度に応じたアイテムと交換するぜ。おっと君達にはわからないだろうけど、俺は決して嘘を言うような男じゃねえから安心しな。さあ、交換するかい。");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.add(" #L" + i + "##v" + this.item_ids[i] + "##t" + this.item_ids[i] + "##l");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                int item_id = this.item_ids[answer];
                int[] prize = this.prizes[answer][(int) (Math.random() * this.prizes[answer].length)];
                // TODO : 英語の文章
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), prize[0], prize[1], "")) {
                    say("What? I can't give you the reward if your equip., use, or etc. inventory is full. Please go take a look right now.");
                    return false;
                }
                if (!getPlayer().haveItem(item_id, this.required_quantity)) {
                    say("おいおい、ふざけちゃいけねーな。違いのわかる男に嘘は通用しないぜ。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(item_id), item_id, this.required_quantity, true, false);
                MapleInventoryManipulator.addById(getPlayer(), prize[0], prize[1]); // TODO : item effect.
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Hmmm ... if not for this minor scratch ... sigh. I'm afaird I can only deem this a standard-quality item. Well, here's ");
                nt.add("#t" + prize[0] + "# for you.");
                say(nt);
                return false;
            }
            case -1 -> {
                say("そうか、気が変わったら俺に話しかけな。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
