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
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party1_play (JMS131-302), 会話文は lua_script_jp (party1_play) を利用
public class Npc_Kerning_Cloto extends NpcScriptMan {

    private final String field_set_name = "Party1";
    private final int item_coupon = 4001007; // クーポン
    private final int item_pass = 4001008; // 通行券
    private final int map_stage1 = 103000800; // 隠れ道 : 一つ目の同行<1st>
    private final int map_stage5 = 103000804; // 隠れ道 : 一つ目の同行<Last>
    private final int map_bonus = 103000805; // 隠れ道 : 一つ目の同行<Bonus>
    private final int stage5_pass_count = 10;
    // 個人問題, 答え (クーポンの枚数)
    // 翻訳
    private final String[] questions = {
        "問題。集めるクーポンの枚数は、#rレベル1からレベル2になるのに必要な経験値と同じ数です。",
        "問題です。集めるクーポンの枚数は、魔法使いとして1次転職するのに必要なレベルと同じ数です。",
        "問題です。集めるクーポンの枚数は、戦士として1次転職するのに必要なSTRの最低値と同じ数です。",
        "問題です。集めるクーポンの枚数は、魔法使いとして1次転職するのに必要なINTの最低値と同じ数です。",
        "問題です。集めるクーポンの枚数は、弓使いとして1次転職するのに必要なDEXの最低値と同じ数です。",
        "問題です。集めるクーポンの枚数は、盗賊として1次転職するのに必要なDEXの最低値と同じ数です。",
        "問題です。集めるクーポンの枚数は、戦士として1次転職するのに必要なレベルと同じ数です。",
        "問題です。集めるクーポンの枚数は、弓使いとして1次転職するのに必要なレベルと同じ数です。",
        "問題です。集めるクーポンの枚数は、盗賊として1次転職するのに必要なレベルと同じ数です。"
    };
    private final int[] answers = {15, 8, 35, 20, 25, 25, 10, 10, 10};
    // ステージ2 - 4 (ロープ, 台, 樽), マップ, ステージ名, 次のステージ名, 正解の組み合わせ, 経験値
    private final int[] area_maps = {103000801, 103000802, 103000803};
    private final String[] area_stages = {"2", "3", "4"};
    private final String[] area_next = {"3", "4", "5"};
    private final String[] area_answers = {"1110", "11100", "111000"};
    private final int[] area_exps = {200, 400, 800};
    // 翻訳
    private final String[] area_intros = {
        "こんにちは。第2ステージへようこそ。私の近くにロープがいくつか見えるでしょう。#bそのうち3本が次のステージへのポータルにつながっている#kの。#bパーティーメンバー3人がロープを見つけてつかまる#kだけでいいわ。\r\nただし、下の方にぶら下がっていると正解にはならないの。正解と認められるよう、十分上まで登ってね。それと、ロープにつかまれるのはパーティーメンバー3人だけよ。そうしたら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。さあ、正しいロープを見つけてつかまって！",
        "こんにちは。第3ステージへようこそ。台の上の近くに、子猫が入った樽がいくつか見えるでしょう。この台のうち、#b3つが次のステージへのポータルにつながっている#kの。#bパーティーメンバー3人が正しい台を見つけて乗れば、ステージクリアよ。\r\nただし、正解と認められるには、端ではなく真ん中にしっかり立っていないといけないの。それと、台に乗れるのはパーティーメンバー3人だけよ。メンバーが台に乗ったら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。さあ、正しい台を見つけて～！",
        "こんにちは。第4ステージへようこそ。近くに樽がいくつか見えるでしょう。そのうち3つの樽が次のステージへのポータルにつながっているの。#bパーティーメンバー3人が正しい樽を見つけて上に乗れば#k、ステージクリアよ。ただし、正解と認められるには、樽の端ではなく真ん中にしっかり立っていないといけないの。それと、樽に乗れるのはパーティーメンバー3人だけよ。メンバーが乗ったら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。さあ、正しい樽を見つけて～！"
    };
    // 翻訳
    private final String[] area_not_found = {
        "まだ3本のロープを見つけていないみたいね。ロープの別の組み合わせを考えてみて。ロープにつかまれるのは3人だけよ。それと、下の方にぶら下がっていると正解にならないからね。頑張って！",
        "まだ3つの台を見つけていないみたいね。台の別の組み合わせを考えてみて。それと、答えを有効にするには、台に乗れるのは3人だけで、真ん中にしっかり立っていないといけないことを忘れないでね。頑張って！",
        "まだ3つの樽を見つけていないみたいね。樽の別の組み合わせを考えてみて。それと、答えを正解と認めてもらうには、樽に乗れるのは3人だけで、真ん中にしっかり立っていないといけないことを忘れないでね。頑張って！"
    };
    // 翻訳
    private final String[] area_helps = {
        "第2ステージについて説明するわ。私の横にロープがいくつか見えるでしょう。#bそのうち3本が次のステージへのポータルにつながっている#kの。#bパーティーメンバー3人が正しいロープを見つけてつかまる#kだけでいいわ。\r\nただし、下の方にぶら下がっていてはカウントされないの。正解と認められるよう、十分上まで登ってね。それと、ロープにつかまれるのはパーティーメンバー3人だけよ。そうしたら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。頑張って！",
        "第3ステージについて説明するわ。台の上に子猫が入った樽がたくさん見えるでしょう。#bそのうち3つの台が次のステージへのポータルにつながっている#kの。#bパーティーメンバー3人が正しい台を見つけて乗れば、ステージクリアよ。\r\nただし、正解と認められるには、端ではなく真ん中にしっかり立っていないといけないの。それと、台に乗れるのはパーティーメンバー3人だけよ。メンバーが台に乗ったら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。頑張って～！",
        "第4ステージについて説明するわ。近くに樽がたくさん見えるでしょう。#bそのうち3つの樽が次のステージへのポータルにつながっている#kの。#bパーティーメンバー3人が正しい樽を見つけて上に乗れば#k、ステージクリアよ。ただし、正解と認められるには、樽の端ではなく真ん中にしっかり立っていないといけないの。それと、樽に乗れるのはパーティーメンバー3人だけよ。メンバーが乗ったら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。頑張って！"
    };
    // 報酬 (lua の random( 0, 39 )), アイテムID, 個数
    private final int[][] rewards = {
        {2000004, 5}, {2000001, 100}, {2000002, 70}, {2000003, 100}, {2000006, 50}, {2022000, 15}, {2022003, 15},
        {2040002, 1}, {2040402, 1}, {2040502, 1}, {2040505, 1}, {2040602, 1}, {2040802, 1},
        {4003000, 30}, {4010000, 8}, {4010001, 8}, {4010002, 8}, {4010003, 8}, {4010004, 8}, {4010005, 8}, {4010006, 5},
        {4020000, 8}, {4020001, 8}, {4020002, 8}, {4020003, 8}, {4020004, 8}, {4020005, 8}, {4020006, 8}, {4020007, 3}, {4020008, 3},
        {1032002, 1}, {1032004, 1}, {1032005, 1}, {1032006, 1}, {1032007, 1}, {1032009, 1}, {1032010, 1},
        {1002026, 1}, {1002089, 1}, {1002090, 1}
    };
    private boolean reward = false;

    @Override
    public boolean action(int status, int answer) {
        if (hasPages()) {
            return nextPage();
        }
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        // 報酬
        if (this.reward) {
            if (status != 1) {
                return false;
            }
            if (!hasEmptySlot(MapleInventoryType.USE) || !hasEmptySlot(MapleInventoryType.ETC)) {
                // 翻訳
                say("報酬を受け取るには、消費インベントリとETCインベントリにそれぞれ少なくとも1つ空きスロットが必要よ。空きを作ってから話しかけてね。");
                return false;
            }
            int[] item = this.rewards[random(0, this.rewards.length - 1)];
            if (!exchange(0, item[0], item[1])) {
                // 翻訳
                say("うーん…消費インベントリとETCインベントリに本当に空きがある？インベントリがいっぱいだと、頑張ったご褒美をあげられないの。");
                return false;
            }
            warp(this.map_bonus);
            return false;
        }
        if (status != 0) {
            return false;
        }
        if (field_set.getVar("stage").equals("clear")) {
            this.reward = true;
            // 翻訳
            say("すごい！全ステージをクリアしてここまで来たのね。よく頑張ったご褒美に、ささやかなお礼をあげるわ。でも、受け取る前に、消費インベントリとETCインベントリに空きスロットがあるか確認してね。", false, true);
            return true;
        }
        int map_id = getMapId();
        // パーティーメンバー
        if (!isPartyBoss()) {
            if (map_id != this.map_stage1) {
                for (int i = 0; i < this.area_maps.length; i++) {
                    if (map_id == this.area_maps[i]) {
                        say(this.area_helps[i]);
                        break;
                    }
                }
                return false;
            }
            // 個人問題
            String key = getCharacterName() + "_";
            String prob = field_set.getVar(key);
            if (prob.equals("clear")) {
                // 翻訳
                say("すごい、私の問題に見事に答えたわね。これがパーティーの通行証よ。リーダーに渡してね。");
                return false;
            }
            if (prob.isEmpty()) {
                int q = random(0, this.questions.length - 1);
                field_set.setVar(key, String.valueOf(q));
                // 翻訳
                return sayPages("私の個人問題の答えと同じ枚数の#bクーポン#kを、リゲーターから集めてきてね。", this.questions[q]);
            }
            int q = integer(prob);
            int ans = this.answers[q];
            if (getItemCount(this.item_coupon) != ans) {
                // 翻訳
                return sayPages("不正解。問題の答えが示す枚数の#bクーポン#kを集めてこないと、通行証は渡せないの。もう一度問題を言うわね。", this.questions[q]);
            }
            if (!exchange(0, this.item_coupon, -ans, this.item_pass, 1)) {
                // 翻訳
                say("答えが示す枚数のクーポンを集めてきてね。  多すぎても少なすぎてもだめよ。クーポンをちゃんと持っているか確認してね。");
                return false;
            }
            field_set.setVar(key, "clear");
            // 翻訳
            say("正解！#b通行証#kを手に入れたわ。パーティーリーダーに渡してね。");
            return false;
        }
        String stage = field_set.getVar("stage");
        // ステージ1
        if (map_id == this.map_stage1) {
            if (stage.isEmpty()) {
                field_set.setVar("stage", "1");
                // 翻訳
                say("こんにちは。第1ステージへようこそ。  周りを見ると、リゲーターが歩き回っているでしょう。倒すと#bクーポン#kを吐き出すの。リーダー以外のパーティーメンバーはそれぞれ私に話しかけて、問題を聞いて、その答えと同じ枚数の#bクーポン#kを集めてね。\r\n正しい枚数の#bクーポン#kを集めた人には#b通行証#kをあげるわ。メンバー全員が#b通行証#kを手に入れてリーダーに渡し、リーダーがその#b通行証#kを私に渡せば、ステージクリアよ。早くステージをクリアするほど、挑戦できるステージも増えるわ。だから、手早く片付けることをおすすめするわ。それじゃ、頑張ってね。");
                return false;
            }
            if (!stage.equals("1")) {
                // 翻訳
                say("このステージはクリアよ。ポータルを使って次のステージへ進んでね。気をつけて…");
                return false;
            }
            // パーティーリーダーを除く
            int users = field_set.getUserCount() - 1;
            if (getItemCount(this.item_pass) < users) {
                // 翻訳
                say("ごめんなさい、通行証の枚数が足りないわ。正しい枚数の通行証を渡してね。パーティーメンバーの人数からリーダーを除いた数、#b" + users + "枚の通行証#kでステージクリアよ。パーティーメンバーに問題に答えて通行証を集め、あなたに渡すよう伝えてね。");
                return false;
            }
            if (!exchange(0, this.item_pass, -users)) {
                // 翻訳
                say("正しい枚数の通行証を渡してね。パーティーメンバーの人数からリーダーを除いた数よ。多すぎても少なすぎてもだめ。正しい枚数を持っているか確認してね。");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            enablePortal("next00", true);
            field_set.setVar("stage", "2");
            field_set.incExpAll(100);
            // 翻訳
            say("#b" + users + "枚の通行証#kを集めたわね！ステージクリアおめでとう！次のステージへ送るポータルを作るわ。そこに着くまで制限時間があるから、急いでね。みんな頑張って！");
            return false;
        }
        // ステージ2 - 4
        for (int i = 0; i < this.area_maps.length; i++) {
            if (map_id != this.area_maps[i]) {
                continue;
            }
            if (!stage.equals(this.area_stages[i])) {
                // 翻訳
                say("このステージのクエストは全員クリアしたわ。ポータルを使って次のステージへ進んでね…");
                return false;
            }
            if (field_set.getUserCount() != getUserCount()) {
                // 翻訳
                say("パーティーメンバーが全員そろっていないみたいね。クエストに参加するには、パーティー全員が前のステージから来ている必要があるの。メンバー全員をここにそろえてね…");
                return false;
            }
            String question = field_set.getVar("ans" + this.area_stages[i]);
            if (question.isEmpty()) {
                field_set.setVar("ans" + this.area_stages[i], shuffle(this.area_answers[i]));
                say(this.area_intros[i]);
                return false;
            }
            // lua の area_check, 各エリアの人数を連結した文字列 (合計が3人でない場合は空)
            int count = 0;
            String result = "";
            for (int j = 0; j < this.area_answers[i].length(); j++) {
                int cur = countUserInArea(j);
                count += cur;
                result += cur;
            }
            if (count != 3) {
                say(this.area_not_found[i]);
                return false;
            }
            if (!question.equals(result)) {
                effectScreen("quest/party/wrong_kor");
                effectSound("Grupo1/Falhou");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            enablePortal("next00", true);
            field_set.setVar("stage", this.area_next[i]);
            field_set.incExpAll(this.area_exps[i]);
            return false;
        }
        // ステージ5
        if (map_id == this.map_stage5) {
            if (!stage.equals("5")) {
                // 翻訳
                say("このステージのクエストは全員クリアしたわ。ポータルを使って次のステージへ進んでね…");
                return false;
            }
            if (field_set.getUserCount() != getUserCount()) {
                // 翻訳
                say("パーティーメンバーが全員そろっていないみたいね。クエストに参加するには、パーティー全員が前のステージから来ている必要があるの。メンバー全員をここにそろえてね…");
                return false;
            }
            if (getItemCount(this.item_pass) < this.stage5_pass_count) {
                // 翻訳
                say("こんにちは。最後の第5ステージへようこそ。マップを歩き回ると、ボスモンスターが何体か見えるわ。全部倒して、#b通行証#kを10枚集めてきてね。通行証を手に入れたら、パーティーリーダーが集めて、10枚そろったら私に渡してね。見覚えのあるモンスターかもしれないけど、思っているよりずっと強いから気をつけてね。頑張って！");
                return false;
            }
            if (!exchange(0, this.item_pass, -this.stage5_pass_count)) {
                // 翻訳
                say("ボスモンスターを全部倒して、#b通行証10枚#kを全部集めたわね。よくやったわ。");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            field_set.setVar("stage", "clear");
            field_set.incExpAll(1500);
            // 翻訳
            say("これが最後のボーナスステージへ続くポータルよ。普通のモンスターを少し楽に倒せるステージなの。制限時間内にできるだけたくさん倒してね。でも、NPCに話しかければいつでもステージから出られるわ。改めて、全ステージクリアおめでとう。気をつけてね…");
            return false;
        }
        return false;
    }
}
