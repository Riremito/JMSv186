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

import java.time.LocalTime;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : PartyAmoria_play (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_play) を利用
// lua のモンスター (2101043-2101048) は存在しないため GMS のモンスターに置き換え
// GM (lua の nJob >= 500) への答えの表示は say ではなく message で表示する
public class Npc_Amoria_AmosStage extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_STAGE1_CLEAR = 1;
    private static final int NODE_STAGE1_GUIDE = 2;
    private final String field_set_name = "PartyAmoria";
    private final int map_stage1 = 670010200; // Hidden Street : Stage 1 - Magik Mirror
    private final int map_stage2_day = 670010300; // Hidden Street : Stage 2 - Heart Strings
    private final int map_stage2_dusk = 670010301; // Hidden Street : Stage 2 - Heart Strings
    private final int map_stage2_night = 670010302; // Hidden Street : Stage 2 - Heart Strings
    private final int map_stage3 = 670010400; // Hidden Street : Stage 3 - Twisted Switcher
    private final int mob_punish = 9400523; // Evil Eye GL (TODO : lua の 2101043)
    private final int mob_punish2 = 9400524; // Curse Eye GL (TODO : lua の 2101044)
    private final int mob_slime = 9400519; // G Slime (9400519-9400522, TODO : lua の 2101045-2101048)
    private final int[][] punish_positions = {{1120, 192}, {1301, 215}, {1290, 126}, {1413, 239}, {1467, 100}, {1558, 256}, {1726, 225}};
    private final int[][] slime_positions = {{1715, -45}, {1571, -50}, {1889, -37}, {1523, -34}, {1664, -20}, {1834, 7}, {1523, -34}, {1664, -20}, {1834, 7}, {1993, 3}};
    private int node = NODE_START;
    private int map_next = 0;

    @Override
    public boolean action(int status, int answer) {
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        switch (this.node) {
            case NODE_STAGE1_CLEAR -> {
                if (status == 1) {
                    warp(this.map_next, "st00");
                }
                return false;
            }
            case NODE_STAGE1_GUIDE -> {
                if (status == 1) {
                    // 翻訳
                    say("下へ降りるには、ここにある3つのポータルから1つを選ばなければならない。大事な注意点だが、本当によく考えて、使うポータルを慎重に選んでくれ。幸運を祈る。", true, false);
                    field_set.setVar("stage1down", "ing");
                }
                return false;
            }
            default -> {
            }
        }
        if (status != 0) {
            return false;
        }
        int map_id = getMapId();
        // ステージ1 - マジックミラー
        if (map_id == this.map_stage1) {
            if (field_set.getVar("stage1_clear").equals("1")) {
                // 時間帯によって移動先が変わる
                int minutes = LocalTime.now().getHour() * 60 + LocalTime.now().getMinute();
                if (minutes <= 480) {
                    this.map_next = this.map_stage2_night;
                } else if (minutes <= 960) {
                    this.map_next = this.map_stage2_day;
                } else {
                    this.map_next = this.map_stage2_dusk;
                }
                // 翻訳
                say("第1ステージのクリア、お見事。それでは第2ステージへ連れて行こう。", false, true);
                this.node = NODE_STAGE1_CLEAR;
                return true;
            }
            if (field_set.getVar("stage1down").equals("ing")) {
                effectObject("gate0");
                effectObject("gate1");
                effectObject("gate2");
                int rn = random(0, 2);
                enablePortal("go00", rn == 0);
                enablePortal("go01", rn == 1);
                enablePortal("go02", rn == 2);
                // 翻訳
                say("まあまあ、もう一度考え直した方がいいかもしれないぞ。どのポータルが開くか見てみろ…");
                return false;
            }
            if (!isPartyBoss()) {
                // 翻訳
                say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                return false;
            }
            // 翻訳
            say("さて、最初は、下にいる俺の友達のグリマーマンに会ってほしい。先へ進む方法を詳しく教えてくれるはずだ！", false, true);
            this.node = NODE_STAGE1_GUIDE;
            return true;
        }
        // ステージ2 - ハートストリングス (昼, 夕暮れ, 夜)
        if (map_id == this.map_stage2_day || map_id == this.map_stage2_dusk || map_id == this.map_stage2_night) {
            String stage2 = field_set.getVar("stage2_clear");
            // lua の PartyAmoria_help
            String help;
            if (map_id == this.map_stage2_day) {
                // 翻訳
                help = "ああ、鏡のことは心配いらないよ、彼はたくさん持っているから。今度は少し難しいよ。第2ステージなんだ。同じ記号が下にあるロープが見えるかな？よし。正しいロープを2本選べば、次のエリアへ進めるよ。間違ったロープを選んだら…まあ、武器を構えておいてね。忘れないで、みんなそれぞれロープを1本選ばないといけないよ！2本選ぶのはおすすめしないな。上に登るには、それぞれの花の根元にポータルがあるよ。うまく使えば、簡単に上まで行けるはずさ。頑張って！";
            } else if (map_id == this.map_stage2_dusk) {
                // 翻訳
                help = "ああ、鏡のことは心配いらないよ、彼はたくさん持っているから。ここは本当に時間が経つのが早いね、話している間にもう日が沈んでいくよ。今度は少し難しいよ。第2ステージなんだ。同じ記号が下にあるロープが見えるかな？よし。正しいロープを2本選べば、次のエリアへ進めるよ。間違ったロープを選んだら…まあ、武器を構えておいてね。忘れないで、みんなそれぞれロープを1本選ばないといけないよ！2本選ぶのはおすすめしないな。上に登るには、それぞれの花の根元にポータルがあるよ。うまく使えば、簡単に上まで行けるはずさ。頑張って！";
            } else {
                // 翻訳
                help = "ああ、鏡のことは心配いらないよ、彼はたくさん持っているから。うーん、見て、もう外は真っ暗だ。よし、今度は少し難しいよ。第2ステージなんだ。同じ記号が下にあるロープが見えるかな？よし。正しいロープを2本選べば、次のエリアへ進めるよ。間違ったロープを選んだら…まあ、武器を構えておいてね。忘れないで、みんなそれぞれロープを1本選ばないといけないよ！2本選ぶのはおすすめしないな。上に登るには、それぞれの花の根元にポータルがあるよ。うまく使えば、簡単に上まで行けるはずさ。頑張って！";
            }
            if (stage2.isEmpty()) {
                if (!isPartyBoss()) {
                    say(help);
                    return false;
                }
                if (!field_set.getVar("stage1_clear").equals("1")) {
                    // 翻訳
                    say("まずミッションをクリアしてから話しかけてくれ。");
                    return false;
                }
                say(help);
                field_set.setVar("stage2_clear", "s");
                return false;
            }
            if (stage2.equals("1")) {
                // 翻訳
                say("ここではもうすることはないようだ。次のステージへ進んでくれ。");
                return false;
            }
            if (!stage2.equals("s")) {
                return false;
            }
            // lua の PartyAmoria_SMSQuizAns (合計5人になるように3本のロープの人数を決める)
            if (field_set.getVar("ans1").isEmpty()) {
                int ans2_1 = random(0, 5);
                int ans2_2 = random(0, 5 - ans2_1);
                int ans2_3 = 5 - ans2_1 - ans2_2;
                String order = shuffle("123");
                int[] ans = {ans2_1, ans2_2, ans2_3};
                for (int i = 0; i < 3; i++) {
                    field_set.setVar("ans" + order.charAt(i), String.valueOf(ans[i]));
                }
            }
            int area1 = countUserInArea(1);
            int area2 = countUserInArea(2);
            int area3 = countUserInArea(3);
            if (area1 + area2 + area3 != 5) {
                // 翻訳
                say("ロープに5人ぶら下がる必要がある。");
                return false;
            }
            if (getPlayer().isAdmin()) {
                // 翻訳
                message("答えは :" + field_set.getVar("ans1") + field_set.getVar("ans2") + field_set.getVar("ans3"));
            }
            int co = 0;
            if (area1 == Integer.parseInt(field_set.getVar("ans1"))) {
                co++;
            }
            if (area2 == Integer.parseInt(field_set.getVar("ans2"))) {
                co++;
            }
            if (area3 == Integer.parseInt(field_set.getVar("ans3"))) {
                co++;
            }
            if (co == 3) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                    return false;
                }
                // 翻訳
                say("正解だ。次のステージへのポータルだ。幸運を祈る！");
                effectScreen("quest/party/clear");
                effectSound("Party1/Clear");
                field_set.setVar("stage2_clear", "1");
                effectObject("gate");
                enablePortal("next00", true);
                field_set.incExpAll(4000);
                return false;
            }
            int tried = (field_set.getVar("try").isEmpty() ? 0 : Integer.parseInt(field_set.getVar("try"))) + 1;
            field_set.setVar("try", String.valueOf(tried));
            effectSound("Party1/Failed");
            effectScreen("quest/party/wrong_kor");
            if (7 <= tried) {
                field_set.setVar("try", "0");
                field_set.setVar("stage2_clear", "");
                // lua の mobSummon_3
                for (int i = 0; i < 20; i++) {
                    summonMob(-709, -1042, this.mob_punish2);
                }
                // 翻訳
                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "解くのに失敗した。その結果、恐ろしいモンスターが何体も召喚された。");
                return false;
            }
            String text;
            if (co == 0) {
                // 翻訳
                text = "これは" + tried + "回目の挑戦だ。\r\nすべての台の重さが違う。";
            } else {
                // 翻訳
                text = "これは" + tried + ".\r\nAll " + co + "つの台の重さが同じだ。";
            }
            if (tried == 6) {
                // 翻訳
                text += "\r\n残りの挑戦は1回だから、気をつけてくれ。";
            }
            say(text);
            return false;
        }
        // ステージ3 - ツイステッドスイッチャー
        if (map_id == this.map_stage3) {
            String stage3 = field_set.getVar("stage3_clear");
            // 翻訳 (lua の PartyAmoria_help)
            String help = "これでもう時間を忘れることはないだろう！はっはっは！よし、冗談はここまでだ。第3ステージへようこそ。見てのとおり、違う文字や数字が書かれたスイッチがいくつかある。正しい5つの組み合わせのときだけポータルが開くんだ。君たち天才なら、何回か試せばいけるだろう。頑張れ！";
            if (stage3.isEmpty()) {
                if (!isPartyBoss()) {
                    say(help);
                    return false;
                }
                if (!field_set.getVar("stage2_clear").equals("1")) {
                    // 翻訳
                    say("まずミッションをクリアしてから話しかけてくれ。");
                    return false;
                }
                say(help);
                field_set.setVar("stage3_clear", "s");
                return false;
            }
            if (stage3.equals("1")) {
                // 翻訳
                say("ここではもうすることはないようだ。次のステージへ進んでくれ。");
                return false;
            }
            if (!stage3.equals("s")) {
                return false;
            }
            String question = field_set.getVar("answer3");
            if (question.isEmpty()) {
                field_set.setVar("answer3", shuffle("111110000"));
                field_set.setVar("question", field_set.getVar("answer3"));
            }
            // lua の area_check (9つのスイッチに合計5人)
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (int i = 0; i < 9; i++) {
                int cur = countUserInArea(i);
                if (2 <= cur) {
                    // 翻訳
                    say("2人より多く乗ってはいけない！！");
                    return false;
                }
                count += cur;
                sb.append(cur);
            }
            String answer3 = count == 5 ? sb.toString() : "";
            int strike = 0;
            if (!answer3.isEmpty() && question.length() == 9) {
                for (int i = 0; i < 9; i++) {
                    if (question.charAt(i) == '1' && answer3.charAt(i) == '1') {
                        strike++;
                    }
                }
            }
            if (getPlayer().isAdmin()) {
                // 翻訳
                message(strike + " ストライク！！！ 答えは :" + question + "なのに、君がいるのは" + answer3);
            }
            if (!isPartyBoss()) {
                // 翻訳
                say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                return false;
            }
            if (answer3.isEmpty()) {
                // 翻訳
                say("まだ5つのスイッチを見つけていないようだな。スイッチの別の組み合わせを考えてみてくれ。それと、正解と認められるには、スイッチの上に乗れるのは5人だけで、真ん中にしっかり立っていなければならないことを忘れるな。頑張れ！");
                return false;
            }
            if (question.isEmpty()) {
                field_set.setVar("question", shuffle("111110000"));
                return false;
            }
            if (question.equals(answer3)) {
                // 翻訳
                say("正解だ。次のステージへのポータルだ。幸運を祈る！");
                effectScreen("quest/party/clear");
                effectSound("Party1/Clear");
                field_set.setVar("stage3_clear", "1");
                effectObject("gate");
                enablePortal("next00", true);
                field_set.incExpAll(6000);
                return false;
            }
            // lua の mobSummon_2 (正解のスイッチの数だけスライムを召喚する)
            int mob_id = this.mob_slime + random(0, 3);
            int begin = new int[]{0, 0, 1, 3, 6}[Math.min(strike, 4)];
            for (int i = 0; i < Math.min(strike, 4); i++) {
                summonMob(this.slime_positions[begin + i][0], this.slime_positions[begin + i][1], mob_id);
            }
            if (strike == 0) {
                field_set.setVar("question", field_set.getVar("answer3"));
                return false;
            }
            effectSound("Party1/Failed");
            effectScreen("quest/party/wrong_kor");
            // lua の mobSummon
            for (int[] pos : this.punish_positions) {
                summonMob(pos[0], pos[1], this.mob_punish);
            }
            // 翻訳
            field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "解くのに失敗した。その結果、恐ろしいモンスターが何体も召喚された。");
            return false;
        }
        return false;
    }
}
