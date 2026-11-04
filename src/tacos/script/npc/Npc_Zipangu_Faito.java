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
public class Npc_Zipangu_Faito extends NpcScriptMan {

    private static final int GRADE_A = 0;
    private static final int GRADE_B = 1;
    private static final int GRADE_C = 2;
    private static final int GRADE_D = 3;
    private static final int GRADE_E = 4;
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
    // item_ids ごとの報酬テーブル
    private final int[] prize_table_index = {
        3, // 鴉の羽
        3, // 火狸の薪
        3, // 野狐の尾
        1, // 夜行の三角巾
        2, // 夜狐の尾
        3, // 手下Aのバッジ
        2, // 手下Bの御札
        2, // 手下Cの磁気ネックレス
        1, // 幹部Aのサングラス
        1, // 幹部Bのお守り
        0 // 親分のポマード
    };
    // {確率 (累積, 10000分率), アイテムID, 個数, 品質}
    private final int[][][] prizes = {
        {
            {2, 1102040, 1, GRADE_A}, // 冒険家のマント(黄)
            {4, 1002393, 1, GRADE_A}, // 桃ずきん
            {6, 1082149, 1, GRADE_A}, // 軍手(茶)
            {250, 2022025, 15, GRADE_B}, // たこ焼き(ジャンボ)
            {500, 2022027, 15, GRADE_B}, // 焼きそば(大盛)
            {3200, 2022000, 20, GRADE_C}, // ミネラルウォーター
            {5900, 2022018, 20, GRADE_C}, // キノコラーメン(焼豚)
            {6700, 2022000, 10, GRADE_C}, // ミネラルウォーター
            {7500, 2022018, 10, GRADE_C}, // キノコラーメン(焼豚)
            {7700, 4020008, 2, GRADE_C}, // 黒水晶の原石
            {7950, 4020001, 2, GRADE_C}, // 紫水晶の原石
            {8950, 2022000, 5, GRADE_D}, // ミネラルウォーター
            {9950, 2022018, 5, GRADE_D}, // キノコラーメン(焼豚)
            {10000, 2000006, 1, GRADE_E} // マナエリクサー
        },
        {
            {2, 1102040, 1, GRADE_A}, // 冒険家のマント(黄)
            {6, 1102043, 1, GRADE_A}, // 冒険家のマント(茶)
            {250, 2001001, 15, GRADE_B}, // チューチューアイス
            {500, 2001002, 15, GRADE_B}, // 小豆かき氷
            {2100, 2022019, 15, GRADE_C}, // キノコラーメン(豚骨)
            {3700, 2022022, 15, GRADE_C}, // おでん(皿)
            {4600, 2022019, 10, GRADE_C}, // キノコラーメン(豚骨)
            {5500, 2022022, 10, GRADE_C}, // おでん(皿)
            {5700, 4010006, 2, GRADE_C}, // 金の原料
            {5900, 4010004, 2, GRADE_C}, // 銀の原料
            {7900, 2022019, 5, GRADE_D}, // キノコラーメン(豚骨)
            {9900, 2022022, 5, GRADE_D}, // おでん(皿)
            {10000, 2000006, 1, GRADE_E} // マナエリクサー
        },
        {
            {2, 1082149, 1, GRADE_A}, // 軍手(茶)
            {6, 1082150, 1, GRADE_A}, // 軍手(灰)
            {150, 2060003, 1000, GRADE_B}, // 尖晶の矢（弓専用）
            {300, 2061003, 1000, GRADE_B}, // 藍晶の矢（弩専用）
            {2100, 2000006, 15, GRADE_C}, // マナエリクサー
            {3900, 2022019, 15, GRADE_C}, // キノコラーメン(豚骨)
            {4800, 2000006, 10, GRADE_C}, // マナエリクサー
            {5700, 2022019, 10, GRADE_C}, // キノコラーメン(豚骨)
            {5950, 4010003, 2, GRADE_C}, // アダマンティウムの原料
            {6200, 4010002, 2, GRADE_C}, // ミスリルの原料
            {8000, 2000006, 5, GRADE_D}, // マナエリクサー
            {9800, 2022019, 5, GRADE_D}, // キノコラーメン(豚骨)
            {10000, 2000003, 1, GRADE_E} // 青い薬
        },
        {
            {1, 1002393, 1, GRADE_A}, // 桃ずきん
            {4, 1002395, 1, GRADE_A}, // 紫ずきん
            {154, 2022024, 15, GRADE_B}, // たこ焼き
            {300, 2022026, 15, GRADE_B}, // 焼きそば
            {2300, 2000002, 20, GRADE_C}, // 白い薬
            {4300, 2000003, 20, GRADE_C}, // 青い薬
            {5100, 2000002, 10, GRADE_C}, // 白い薬
            {5900, 2000003, 10, GRADE_C}, // 青い薬
            {6100, 4020000, 2, GRADE_C}, // ガーネットの原石
            {6300, 4020006, 2, GRADE_C}, // トパーズの原石
            {7900, 2000002, 5, GRADE_D}, // 白い薬
            {9500, 2000003, 5, GRADE_D}, // 青い薬
            {9900, 2000006, 1, GRADE_E}, // マナエリクサー
            {10000, 2000000, 1, GRADE_E} // 赤い薬
        }
    };
    // 品質ごとの文章 (翻訳)
    private final String[] grade_texts = {
        "ふむ…美しい輝き、なめらかな表面、そして部屋中に広がるようなこのエネルギー…そう、こういうアイテムを探していたんだ！しかも100個も持ってきてくれた！苦労にふさわしい報酬をあげなければ！どれどれ…ほら、これを持っていきなさい、#t%d#。",
        "おお…これは気に入った。うん、うん！これは間違いなく簡単には手に入らない品だ。間違いなく私のコレクションの一部になるだろう。こんなものを見つけて、しかも大量に集めてくるなんて信じられない！これほど素晴らしいものには、この報酬がふさわしい、#t%d#だ。遠慮なく持っていきなさい～！",
        "ふむ…この小さな傷さえなければ…はあ。残念だが、標準品質のアイテムとしか判断できないな。では、#t%d#をどうぞ。",
        "ふむ…あちこちにへこみがあるし、この傷は何だ？怒った猫にでも出くわしたのか？それとも野生のベインか？正直なところ、これは大したことないな。君のような戦士に期待している水準を下回っている。だから、いつもどおり、引き換えたい品の品質に見合ったアイテムを報酬としてあげよう。ほら、#t%d#。",
        "何だこれは？何だ…！これは…これは間違いなく毎日お目にかかれるような品じゃない…まったくのガラクタだ！こんな明らかな低品質のアイテムを受け取るのはお人よしだけだ！わざとじゃないとは思うが…こんなものを渡されては、とても気分よくはいられないな。今はとりあえず#t%d#しかあげられないが、また機会があれば戻ってきてくれ！"
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
                if (!getPlayer().haveItem(item_id, this.required_quantity)) {
                    say("おいおい、ふざけちゃいけねーな。違いのわかる男に嘘は通用しないぜ。");
                    return false;
                }
                int[][] table = this.prizes[this.prize_table_index[answer]];
                int rnum = (int) (Math.random() * 10000) + 1;
                int[] prize = table[table.length - 1];
                for (int[] row : table) {
                    if (rnum <= row[0]) {
                        prize = row;
                        break;
                    }
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), prize[1], prize[2], "")) {
                    // 翻訳
                    say("何だって？装備、消費、ETCインベントリのどれかがいっぱいだと報酬をあげられないんだ。インベントリに空きを作ってくれたら、喜んで望みのものをあげよう。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(item_id), item_id, this.required_quantity, true, false);
                MapleInventoryManipulator.addById(getPlayer(), prize[1], (short) prize[2]); // TODO : item effect.
                say(String.format(this.grade_texts[prize[3]], prize[1]));
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
