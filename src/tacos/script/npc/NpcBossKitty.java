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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcBossKitty extends NpcScriptMan {

    private final int quest_boss_cat = 8012; // ボス猫の問題
    private final int item_chicken = 2020001; // チキン
    private final int chicken_count = 300;
    private final int item_marble = 4031064; // ビー玉
    // 問題 (翻訳)
    private final String[][] questions = {
        {
            "第1問：ファイアラクーンが落とさないアイテムはどれ？ #b\r\n#L0#ラクーンの薪 #l\r\n#L1#硬い角 #l\r\n#L2#赤いレンガ #l",
            "第2問：職業やレベルの説明が間違っているアイテムはどれ？ #b\r\n#L0#竹槍 - 戦士専用武器 #l\r\n#L1#ピコピコハンマー - 片手剣 #l\r\n#L2#日本地図 - レベル50装備 #l",
            "第3問：カニングシティからキノコ神社へ運んでくれるNPCの名前は？ #b\r\n#L0# ペリカン #l\r\n#L1# スピネル #l\r\n#L2# 運び屋 #l",
            "第4問：チンピラが落とさないアイテムはどれ？#b\r\n#L0#チンピラAのバッジ#l\r\n#L1#チンピラBのコルセット#l\r\n#L2#チンピラCのネックレス#l",
            "第5問：キノコ神社にある像の名前は？ #b\r\n#L0#キノコ像 #l\r\n#L1#自由の女神像 #l\r\n#L2#メイプル像 #l"
        },
        {
            "第1問：昭和村の八百屋の主人の名前は？ #b\r\n#L0#サミ #l\r\n#L1#カミ #l\r\n#L2#ウミ #l",
            "第2問：昭和村の映画館の前に立っていないNPCはどれ？ #b\r\n#L0#スカイ #l\r\n#L1#フラノ #l\r\n#L2#シンタ #l",
            "第3問：カニングシティからキノコ神社へ旅人を運んでくれるNPCの名前は？ #b\r\n#L0# ペリカン #l\r\n#L1# スピネル #l\r\n#L2# 運び屋 #l",
            "第4問：実在するアイテムはどれ？ #b\r\n#L0#雲狐の歯 #l\r\n#L1#幽霊の花束#l\r\n#L2#夜狐のしっぽ #l",
            "第5問：存在しないアイテムはどれ？？ #b\r\n#L0#冷凍マグロ #l\r\n#L1#酒 #l\r\n#L2#ハエたたき #l"
        },
        {
            "第1問：ファイアラクーンが落とさないアイテムはどれ？ #b\r\n#L0#ラクーンの薪 #l\r\n#L1#硬い角 #l\r\n#L2#赤いレンガ #l",
            "第2問：カニングシティとジパングの間で旅人を運んでいるNPCはどれ？ #b\r\n#L0#ペリ #l\r\n#L1#スピネル#l\r\n#L2#ポリ #l",
            "第3問：キノコ神社で売っているアイテムのうち、攻撃力を上げるものはどれ？ #b\r\n#L0#たこ焼き #l\r\n#L1#焼きそば #l\r\n#L2#天ぷら #l",
            "第4問：チンピラが落とさないアイテムはどれ？ #b\r\n#L0#チンピラAのバッジ #l\r\n#L1#チンピラBのコルセット #l\r\n#L2#チンピラCのネックレス #l",
            "第5問：キノコ神社のロボが売っていないラーメンはどれ？ #b\r\n#L0#キノコラーメン（豚骨） #l\r\n#L1#キノコラーメン（塩） #l\r\n#L2#キノコ味噌ラーメン#l"
        },
        {
            "第1問：キノコ神社にある像の名前は？ #b\r\n#L0#キノコ像 #l\r\n#L1#自由の女神像 #l\r\n#L2#メイプル像 #l",
            "第2問：職業やレベルの説明が間違っているアイテムはどれ？ #b\r\n#L0#竹槍 - 戦士専用武器 #l\r\n#L1#ピコピコハンマー - 片手剣 #l\r\n#L2#日本地図 - レベル50装備 #l",
            "第3問：キノコ神社のロボが売っていないラーメンはどれ？ #b\r\n#L0#キノコラーメン（豚骨） #l\r\n#L1#キノコラーメン（塩） #l\r\n#L2#キノコ味噌ラーメン#l",
            "第4問：昭和映画館の前に立っていないNPCはどれ？ #b\r\n#L0#スカイ #l\r\n#L1#フラノ #l\r\n#L2#シンタ #l",
            "第5問：カニングシティとジパングの間で旅人を運んでいるNPCはどれ？ #b\r\n#L0#ペリ #l\r\n#L1#スピネル #l\r\n#L2#ポリ #l"
        },
        {
            "第1問：チンピラが落とさないアイテムはどれ？#b\r\n#L0#チンピラAのバッジ #l\r\n#L1#チンピラBのコルセット #l\r\n#L2#チンピラCのネックレス #l",
            "第2問：存在しないアイテムはどれ？？ #b\r\n#L0#冷凍マグロ #l\r\n#L1#酒 #l\r\n#L2#ハエたたき #l",
            "第3問：キノコ神社で売っているアイテムのうち、攻撃力を上げるものはどれ？ #b\r\n#L0#たこ焼き #l\r\n#L1#焼きそば #l\r\n#L2#天ぷら #l",
            "第4問：昭和村の八百屋の主人の名前は？ #b\r\n#L0#サミ #l\r\n#L1#カミ #l\r\n#L2#ウミ #l",
            "第5問：実在するアイテムはどれ？ #b\r\n#L0#雲狐の歯 #l\r\n#L1#幽霊の花束#l\r\n#L2#夜狐のしっぽ #l"
        },
        {
            "第1問：チンピラが落とさないアイテムはどれ？ #b\r\n#L0#チンピラAのバッジ #l\r\n#L1#チンピラBのコルセット #l\r\n#L2#チンピラCのネックレス #l",
            "第2問：ファイアラクーンが落とさないアイテムはどれ？ #b\r\n#L0#ラクーンの薪 #l\r\n#L1#硬い角 #l\r\n#L2#赤いレンガ #l",
            "第3問：カニングシティからキノコ神社へ旅人を運んでくれるNPCの名前は？ #b\r\n#L0# ペリカン #l\r\n#L1# スピネル #l\r\n#L2# 運び屋 #l",
            "第4問：カニングシティとジパングの間で旅人を運んでいるNPCはどれ？ #b\r\n#L0#ペリ #l\r\n#L1#スピネル#l\r\n#L2#ポリ #l",
            "第5問：キノコ神社にある像の名前は？ #b\r\n#L0#キノコ像 #l\r\n#L1#自由の女神像 #l\r\n#L2#メイプル像 #l"
        }
    };
    private final int[][] answers = {
        {1, 0, 1, 1, 0},
        {2, 2, 1, 2, 2},
        {1, 1, 0, 1, 2},
        {0, 0, 2, 2, 1},
        {1, 2, 0, 2, 2},
        {1, 1, 1, 1, 0}
    };
    private int question_set = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("お前にはこの質問に立ち向かう度胸がないんだな。やっぱりな…失せろ！");
                return false;
            }
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_boss_cat) == 2 || checkItem(this.item_marble)) {
                    // 翻訳
                    say("Meeeoooowww! ");
                    return false;
                }
                if (getPlayer().getQuestStatus(this.quest_boss_cat) != 1) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("ふふ･･･私が出す問題に挑戦したいのか。でも、誰でもただで挑戦出来るもんじゃない。");
                    say(nt);
                    return false;
                }
                // 翻訳
                askYesNo("全部集めてきたか？俺の質問に全部答えるつもりか？");
                return true;
            }
            case 1 -> {
                if (getPlayer().getItemQuantity(this.item_chicken, false) < this.chicken_count) {
                    // 翻訳
                    say("おい、本当に頼んだフライドチキン" + this.chicken_count + "個を持ってきたのか？足りているかもう一度確認しな。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.USE, this.item_chicken, this.chicken_count, true, false);
                this.question_set = (int) (Math.random() * this.questions.length);
                // 翻訳
                say("よくやった！じゃあちょっと待て…おい見ろよ！食い物があるぞ！遠慮なく食え。よし、それじゃあ質問の時間だ。わかっているとは思うが、覚えておけよ、間違えたらそこで終わりだ。オール・オア・ナッシングだぜ！", false, true);
                return true;
            }
            default -> {
            }
        }
        // 問題
        int index = status - 2;
        if (0 < index && answer != this.answers[this.question_set][index - 1]) {
            // 翻訳
            say("ふむ…人間なんてみんな間違えるもんだ！もう一度挑戦したいなら、フライドチキンを" + this.chicken_count + "個持ってきな。");
            return false;
        }
        if (index < this.questions[this.question_set].length) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add(this.questions[this.question_set][index], false);
            askMenu(nt);
            return true;
        }
        // 全問正解
        if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_marble, 1, "")) {
            // 翻訳
            say("ETCインベントリがいっぱいだと！？！オレンジマーブルがほしいなら、空きを作りな。");
            return false;
        }
        MapleInventoryManipulator.addById(getPlayer(), this.item_marble, (short) 1);
        // 翻訳
        say("ちくしょう、全問正解か！人間は好きじゃないが、約束を破るのはもっと嫌いなんでな。約束どおり、オレンジマーブルだ。お前の手柄だよ！");
        return false;
    }
}
