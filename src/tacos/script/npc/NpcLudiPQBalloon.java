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
// Npc.wz script : party2_play (JMS131-302), 会話文は lua_script_jp (party2_play) を利用
// アルト, レッドバルーン, オレンジバルーン, イエローバルーン, イエローグリーンバルーン, グリーンバルーン, アクアバルーン, スカイブルーバルーン, ブルーバルーン, パープルバルーン, ピンクバルーン
public class NpcLudiPQBalloon extends NpcScriptMan {

    private final String field_set_name = "Party3";
    private final String field_set_bonus_name = "Party4";
    private final int map_stage1 = 922010100; // ルディブリアム : 放置された塔<1段階>
    private final int map_stage2 = 922010200; // ルディブリアム : 放置された塔<2段階>
    private final int map_stage3 = 922010300; // ルディブリアム : 放置された塔<3段階>
    private final int map_stage4 = 922010400; // ルディブリアム : 放置された塔<4段階>
    private final int map_stage5 = 922010500; // ルディブリアム : 放置された塔<5段階>
    private final int map_stage6 = 922010600; // ルディブリアム : 放置された塔<6段階>
    private final int map_stage7 = 922010700; // ルディブリアム : 放置された塔<7段階>
    private final int map_stage8 = 922010800; // ルディブリアム : 放置された塔<8段階>
    private final int map_stage9 = 922010900; // ルディブリアム : 時空の亀裂
    private final int map_bonus = 922011000; // ルディブリアム : 放置された塔<ボーナス>
    private final int map_reward = 922011100; // ルディブリアム : 放置された塔<冒険の結実>
    private final int map_eos_tower_101 = 221024500; // ルディブリアム : エオス塔101階
    private final int item_pass = 4001022; // 次元の通行券
    private final int item_key = 4001023; // 次元のカギ
    private final int mob_weep_eye = 9300008; // 次元のウイープアイ
    private final int mob_rombard = 9300010; // 次元のロムバード
    private final int mob_mega_ban = 9300012; // モーメガバン
    private final int mob_king_block = 9300013; // 次元のキングブロック
    private final int quest_party = 7011; // パーティークエストのデータ
    private final int quest_clear = 7010; // パーティークエストのクリア
    // 次元の通行券を集めるステージ, ステージ名, 次のステージ名, 必要な次元の通行券の数, 経験値
    private final int[] map_stages = {922010100, 922010200, 922010300, 922010400, 922010500, 922010700};
    private final String[] stage_names = {"1", "2", "3", "4", "5", "7"};
    private final String[] stage_next = {"2", "3", "4", "5", "7", "8"};
    private final int[] pass_counts = {25, 15, 32, 6, 24, 3};
    private final int[] stage_exps = {3000, 3600, 4200, 4800, 5400, 6600};
    // 報酬 (lua の random( 0, 250 ) の 0 から 95 まで), アイテムID, 個数
    private final int[][] rewards = {
        {2000004, 10}, {2000002, 100}, {2000003, 100}, {2000006, 30}, {2022000, 30}, {2022003, 30},
        {2040002, 1}, {2040402, 1}, {2040502, 1}, {2040505, 1}, {2040602, 1}, {2040802, 1},
        {4003000, 50}, {4010000, 15}, {4010001, 15}, {4010002, 15}, {4010003, 15}, {4010004, 15}, {4010005, 15}, {4010006, 10},
        {4020000, 15}, {4020001, 15}, {4020002, 15}, {4020003, 15}, {4020004, 15}, {4020005, 15}, {4020006, 15}, {4020007, 6}, {4020008, 6},
        {1032002, 1}, {1032011, 1}, {1032008, 1}, {1102011, 1}, {1102012, 1}, {1102013, 1}, {1102014, 1},
        {2040803, 1}, {2070011, 1}, {2043001, 1}, {2043101, 1}, {2043201, 1}, {2043301, 1}, {2043701, 1}, {2043801, 1},
        {2044001, 1}, {2044101, 1}, {2044201, 1}, {2044301, 1}, {2044401, 1}, {2044501, 1}, {2044601, 1}, {2044701, 1},
        {2000004, 15}, {2000002, 80}, {2000003, 80}, {2000006, 25}, {2022000, 25}, {2022003, 25},
        {4003000, 55}, {4010000, 12}, {4010001, 12}, {4010002, 12}, {4010003, 12}, {4010004, 12}, {4010005, 12}, {4010006, 8},
        {4020000, 12}, {4020001, 12}, {4020002, 12}, {4020003, 12}, {4020004, 12}, {4020005, 12}, {4020006, 12}, {4020007, 4}, {4020008, 4},
        {2040001, 1}, {2040004, 1}, {2040301, 1}, {2040401, 1}, {2040501, 1}, {2040504, 1}, {2040601, 1}, {2040601, 1},
        {2040701, 1}, {2040704, 1}, {2040707, 1}, {2040801, 1}, {2040901, 1}, {2041001, 1}, {2041004, 1}, {2041007, 1},
        {2041010, 1}, {2041013, 1}, {2041016, 1}, {2041019, 1}, {2041022, 1}
    };

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        // 報酬
        if (map_id == this.map_reward) {
            if (status == 0) {
                // 翻訳
                say("すごい！全ステージをクリアして、今ここで勝利を味わっているのね。わあ！一人ひとりに、よく頑張ったと心からおめでとうを言うわ。ささやかなプレゼントよ。受け取る前に、消費インベントリと装備インベントリに空きスロットがあるか確認してね。", false, true);
                return true;
            }
            if (status != 1) {
                return false;
            }
            if (!hasEmptySlot(MapleInventoryType.USE) || !hasEmptySlot(MapleInventoryType.ETC)) {
                // 翻訳
                say("消費インベントリと装備インベントリにそれぞれ少なくとも1つ空きスロットが必要よ。空きを作ってから話しかけてね。");
                return false;
            }
            int rnum = random(0, 250);
            int item_id;
            int item_count;
            if (rnum < this.rewards.length) {
                item_id = this.rewards[rnum][0];
                item_count = this.rewards[rnum][1];
            } else if (rnum <= 150) {
                item_id = 2000004;
                item_count = 10;
            } else if (rnum <= 200) {
                item_id = 2000002;
                item_count = 100;
            } else {
                item_id = 2000003;
                item_count = 100;
            }
            if (!removeItemAll(this.item_pass)) {
                // 翻訳
                say("#t" + this.item_pass + "#の枚数は本当に正しい？もう一度確認してね。");
                return false;
            }
            if (!removeItemAll(this.item_key)) {
                // 翻訳
                say("#t" + this.item_key + "#の数は本当に正しい？もう一度確認してね。");
                return false;
            }
            if (!exchange(0, item_id, item_count)) {
                // 翻訳
                say("うーん…消費インベントリとETCインベントリに本当に空きスロットがある？インベントリがいっぱいだと、頑張ったご褒美をあげられないの…");
                return false;
            }
            setQuestData(this.quest_clear, "1");
            removeQuestData(this.quest_party);
            warp(this.map_eos_tower_101);
            return false;
        }
        // パーティーメンバーへの説明
        if (!isPartyBoss() || map_id == this.map_bonus) {
            // 翻訳
            if (map_id == this.map_stage1) {
                say("第1ステージについて説明しよう。マップのあちこちにモンスターがいる。そのモンスターたちは、別の次元への入口を開く#b#t" + this.item_pass + "##kというアイテムを持っている。それがあれば、エオスの塔の頂上に一歩近づける。そこで別の次元への扉が開き、ついにすべての元凶に出会えるだろう。\r\nモンスターを倒して#b#t" + this.item_pass + "#を25枚#k集め、パーティーリーダーに渡してくれ。リーダーがそれを私に渡せば、次のステージへ進める。幸運を祈る！");
            } else if (map_id == this.map_stage2) {
                say("第2ステージについて説明しよう。マップ中に箱がある。箱を壊すと、別のマップへ#G飛ばされる:飛ばされる#か、#t" + this.item_pass + "#を#Gもらえる:もらえる#。すべての箱を調べて#b#t" + this.item_pass + "#を15枚#k集めて、私のところに持ってきてくれ。集めた#t" + this.item_pass + "#はまとめてパーティーリーダーに渡し、リーダーが私に渡すんだ。\r\nちなみに、別の場所へ#G飛ばされ:飛ばされ#ても、そこで別の箱が見つかるかもしれない。だから、#Gテレポートされた:テレポートされた#見知らぬ場所からすぐに出てはいけない。  そこから出てしまうと戻れなくなり、クエストを最初からやり直すことになる。幸運を祈る！");
            } else if (map_id == this.map_stage3) {
                say("第3ステージについて説明しよう。ここにはモンスターと箱がたくさんある。モンスターを倒すと、別の次元のモンスターと同じように#b#t" + this.item_pass + "##kを落とす。箱を壊すとモンスターが現れて、そのモンスターも#b#t" + this.item_pass + "##kを落とす。\r\n集める#b#t" + this.item_pass + "##kの枚数は、私がパーティーリーダーに出す問題の答えで決まる。その問題の答えが、集める#b#t" + this.item_pass + "##kの枚数になるんだ。私がパーティーリーダーに問題を出したら、リーダーはメンバーと答えを相談していい。幸運を祈る！");
            } else if (map_id == this.map_stage4) {
                say("第4ステージについて説明しよう。ここは次元の裂け目によって作られた暗黒の空間だ。  その中には、闇に潜む#b#o" + this.mob_weep_eye + "##kというモンスターがいる。だから、目をしっかり開いていないとほとんど見えないだろう。モンスターを倒して#b#t" + this.item_pass + "#を6枚#k集めてくれ。\r\n言ったとおり、#b#o" + this.mob_weep_eye + "##kは目をしっかり開いていないと見えない。闇にこっそり溶け込む、変わった種類のモンスターなんだ。幸運を祈る！");
            } else if (map_id == this.map_stage5) {
                say("第5ステージについて説明しよう。ここにはたくさんの空間があって、その中にモンスターがいる。お前たちの任務は、パーティーで#b#t" + this.item_pass + "#を24枚#k集めることだ。説明すると、特定の職業でないと#b#t" + this.item_pass + "##kを集められない場合がある。だから気をつけてくれ。ヒントをやろう。#b#o" + this.mob_king_block + "##kという倒せないモンスターがいる。そのモンスターの向こう側へ行けるのは盗賊だけだ。魔法使いしか通れない道もある。それを見つけるのはお前たち次第だ。幸運を祈る！");
            } else if (map_id == this.map_stage6) {
                say("第6ステージについて説明しよう。ここには数字が書かれた箱があって、正しい箱の上で上矢印キーを押すと、次の箱へ移動する。このステージを通過するためのヒントを、パーティーリーダーに#b2回だけ#k教えよう。リーダーはそのヒントを覚えて、一歩ずつ正しく進まなければならない。\r\n頂上にたどり着くと、次のステージへのポータルがある。パーティー全員がポータルを通れば、ステージクリアだ。すべては正しい箱を覚えているかどうかにかかっている。幸運を祈る！！");
            } else if (map_id == this.map_stage7) {
                say("第7ステージについて説明しよう。ここには#b#o" + this.mob_rombard + "##kというとんでもなく強力なモンスターがいる。そのモンスターを倒して、次のステージに進むのに必要な#b#t" + this.item_pass + "##kを見つけてくれ。#b#t" + this.item_pass + "#を3枚#k集めてくれ。\r\nこのモンスターを倒すには、遠くから攻撃するんだ。遠距離から攻撃するしか方法はないんだが…ああ、それと気をつけてくれ、#o" + this.mob_rombard + "#はとても危険だ。油断すると間違いなく怪我をするぞ。幸運を祈る！");
            } else if (map_id == this.map_stage8) {
                say("第8ステージについて説明しよう。ここには乗れる台がたくさんある。そのうち#b5つ#kが#b次のステージへのポータル#kにつながっている。通過するには、#bパーティーメンバー5人を正しい台に乗せる#kんだ。\r\n注意しておくが、正解と認められるには、台の真ん中にしっかり立っていなければならない。それと、台に乗れるのは5人だけだということも覚えておいてくれ。そうしたら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kくれ。幸運を祈る！");
            } else if (map_id == this.map_stage9) {
                say("第9ステージについて説明しよう。いよいよ真の元凶をその手で捕らえるチャンスだ。右へ進むとモンスターがいる。それを倒すと、恐ろしい#b#o" + this.mob_mega_ban + "##kがどこからともなく現れる。お前たちのパーティーの存在にひどく興奮しているから、気をつけてくれ。 \r\nお前たちの任務は、そいつを倒して、持っている#b#t" + this.item_key + "##kを手に入れ、私のところに持ってくることだ。そのモンスターから鍵を奪えれば、次元の扉が再び開くことはない。お前たちを信じているぞ。幸運を祈る！");
            } else if (map_id == this.map_bonus) {
                say("ボーナスステージへ#Gようこそ:ようこそ#。本当に#b#o" + this.mob_mega_ban + "##kを倒したなんて信じられない！すごいぞ！だが時間があまりないから、さっそく本題に入ろう。ここには箱がたくさんある。お前たちの任務は、制限時間内に箱を壊して中のアイテムを手に入れることだ。運がよければ、あちこちで素晴らしいアイテムが手に入るかもしれない。これでワクワクしないなら、何でワクワクするんだ。  幸運を祈る！！");
            }
            return false;
        }
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        String stage = field_set.getVar("stage");
        // 第1ステージの開始
        if (map_id == this.map_stage1 && stage.isEmpty()) {
            field_set.setVar("stage", "1");
            // 翻訳
            say("こんにちは！第1ステージへ#Gようこそ:ようこそ#。マップを歩き回って、あちこちをうろつくいろいろな種類のモンスターを探してね。全部倒して、#b#t" + this.item_pass + "#を25枚#k集めて持ってきて。集めた#t" + this.item_pass + "#はパーティーリーダーにまとめて渡して、リーダーが私に渡してね。見覚えのあるモンスターかもしれないけど、思ったより強いかもしれないから気をつけてね！");
            return false;
        }
        // 第6ステージ (ヒントのみ)
        if (map_id == this.map_stage6) {
            if (field_set.getUserCount() != getUserCount()) {
                // 翻訳
                say("パーティーメンバーが全員そろっていないようだ。続けるには、前のステージからメンバー全員を連れてくる必要がある。足りないメンバーを探してくれ。");
                return false;
            }
            String val = getQuestData(this.quest_party);
            // 翻訳
            String text = "こんにちは！第6ステージへ#Gようこそ:ようこそ#。ここには数字が書かれた箱があって、正しい箱の上で上矢印キーを押すと、次の箱へ移動するわ。このステージを通過するためのヒントを、パーティーリーダーに#b2回だけ#k教えるわね。リーダーはそのヒントを覚えて、一歩ずつ正しく進まないといけないの。\r\n頂上にたどり着くと、次のステージへのポータルがあるわ。パーティー全員がポータルを通れば、ステージクリアよ。すべては正しい箱を覚えているかどうかにかかっているの。";
            if (val.isEmpty() || val.equals("1")) {
                setQuestData(this.quest_party, val.isEmpty() ? "1" : "e");
                say(text + "それじゃ、ヒントよ。聞き逃さないでね！\r\n\r\n#b1、3、3、2、真ん中、1、3、3、3、左、2、3、1、1、？#k");
            } else {
                say(text + "ヒントはもう#b2回#k教えたから、ここから先は手伝えないわ。頑張って！");
            }
            return false;
        }
        // ステージ名
        String stage_name = "";
        if (map_id == this.map_stage8) {
            stage_name = "8";
        } else if (map_id == this.map_stage9) {
            stage_name = "9";
        }
        for (int i = 0; i < this.map_stages.length; i++) {
            if (this.map_stages[i] == map_id) {
                stage_name = this.stage_names[i];
            }
        }
        if (stage_name.isEmpty()) {
            return false;
        }
        // lua の check_stage
        if (!stage.equals(stage_name)) {
            // 翻訳
            if (map_id == this.map_stage1) {
                say("うわあ！このステージのクエストクリアおめでとう。前方に見えるポータルを使って、次のステージへ進んでね。頑張って！");
            } else {
                say("このステージのクエストクリアおめでとう。前方に見えるポータルを使って、次のステージへ進んでくれ。");
            }
            return false;
        }
        if (map_id != this.map_stage1 && field_set.getUserCount() != getUserCount()) {
            // 翻訳
            say("パーティーメンバーが全員そろっていないようだ。続けるには、前のステージからメンバー全員を連れてくる必要がある。足りないメンバーを探してくれ。");
            return false;
        }
        // 第8ステージ
        if (map_id == this.map_stage8) {
            String question = field_set.getVar("ans");
            if (question.isEmpty()) {
                field_set.setVar("ans", shuffle("111110000"));
                // 翻訳
                say("第8ステージへ#Gようこそ:ようこそ#。ここには乗れる台がたくさんあるわ。そのうち#b5つ#kが#b次のステージへのポータル#kにつながっているの。通過するには、#bパーティーメンバー5人を正しい台に乗せる#kのよ。\r\n注意しておくけど、正解と認められるには、台の真ん中にしっかり立っていないといけないわ。それと、台に乗れるのは5人だけだということも覚えておいてね。そうしたら、パーティーリーダーが#b私をダブルクリックして、正解かどうか確かめて#kね。頑張って！");
                return false;
            }
            // lua の area_check( 9, 5 )
            int count = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                int cur = countUserInArea(i);
                count += cur;
                sb.append(cur);
            }
            if (count != 5) {
                // 翻訳
                say("まだ正しい5つの台を見つけていないみたいね。別の組み合わせを考えてみて。答えを有効にするには、パーティーメンバー5人が台の真ん中にしっかり立っていないといけないことを忘れないでね。頑張り続けて！");
                return false;
            }
            if (!question.equals(sb.toString())) {
                effectScreen("quest/party/wrong_kor");
                effectSound("Grupo1/Falhou");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            enablePortal("next00", true);
            field_set.setVar("stage", "9");
            // TODO : lua の incExpAll の第2引数 (7010) の用途は不明
            field_set.incExpAll(7200);
            return false;
        }
        // 第9ステージ
        if (map_id == this.map_stage9) {
            if (getItemCount(this.item_key) < 1) {
                // 翻訳
                say("よくここまでたどり着いたわね。いよいよ真の元凶をその手で捕らえるチャンスよ。右へ進むとモンスターがいるわ。それを倒すと、恐ろしい#b#o" + this.mob_mega_ban + "##kがどこからともなく現れるの。あなたたちのパーティーの存在にひどく興奮しているから、気をつけてね。 \r\nあなたたちの任務は、そいつを倒して、持っている#b#t" + this.item_key + "##kを手に入れ、私のところに持ってくることよ。そのモンスターから鍵を奪えれば、次元の扉が再び開くことはないわ。あなたたちを信じているわ。頑張って！");
                return false;
            }
            if (!exchange(0, this.item_key, -1)) {
                // 翻訳
                say("モンスターを全部倒して、#b#t" + this.item_pass + "##kを集めたのね。とても素晴らしいわ！");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            field_set.setVar("stage", "clear");
            // TODO : lua の incExpAll の第2引数 (7010) の用途は不明
            field_set.incExpAll(8500);
            int res = enterFieldSetParty(this.field_set_bonus_name, 0);
            // 翻訳
            if (res == TacosFieldSet.ENTER_ERROR || res == TacosFieldSet.ENTER_USING) {
                say("理由があって、入れてあげられないんだ。しばらくしてからもう一度試してね。");
            } else if (res == TacosFieldSet.ENTER_NO_PARTY) {
                say("パーティーに入っていないみたいね。このクエストに参加するにはパーティーに入っている必要があるわ。");
            }
            return false;
        }
        // 第1 - 5, 7 ステージ (次元の通行券を集める)
        for (int i = 0; i < this.map_stages.length; i++) {
            if (this.map_stages[i] != map_id) {
                continue;
            }
            int need = this.pass_counts[i];
            boolean enough = (map_id == this.map_stage3) ? getItemCount(this.item_pass) == need : need <= getItemCount(this.item_pass);
            if (!enough) {
                // 翻訳
                if (map_id == this.map_stage1) {
                    say("こんにちは！第1ステージへ#Gようこそ:ようこそ#。マップを歩き回って、あちこちをうろつくいろいろな種類のモンスターを探してね。全部倒して、#b#t" + this.item_pass + "#を25枚#k集めて持ってきて。集めた#t" + this.item_pass + "#はパーティーリーダーにまとめて渡して、リーダーが私に渡してね。見覚えのあるモンスターかもしれないけど、思ったより強いかもしれないから気をつけてね！");
                } else if (map_id == this.map_stage2) {
                    say("こんにちは！第2ステージへ#Gようこそ:ようこそ#。マップを歩き回って、あちこちに散らばっている箱を探してね。箱を壊すと、別のマップへ飛ばされるか、#t" + this.item_pass + "#がもらえるわ。すべての箱を調べて、#b#t" + this.item_pass + "#を15枚#k集めて持ってきて。集めた#t" + this.item_pass + "#はパーティーリーダーにまとめて渡して、リーダーが私に渡してね。頑張って！");
                } else if (map_id == this.map_stage3) {
                    say("こんにちは！第3ステージへ#Gようこそ:ようこそ#。ここにはモンスターと箱がたくさんあるわ。モンスターを倒すと、別の次元のモンスターと同じように#b#t" + this.item_pass + "##kを落とすの。箱を壊すとモンスターが現れて、そのモンスターも#b#t" + this.item_pass + "##kを落とすわ。\r\n集める#b#t" + this.item_pass + "##kの枚数は、私がパーティーリーダーに出す問題の答えで決まるの。その問題の答えが、集める#b#t" + this.item_pass + "##kの枚数になるのよ。私がパーティーリーダーに問題を出したら、リーダーはメンバーと答えを相談していいわ。それじゃ、問題よ。???を埋めてね \r\n\r\n#b レベル1のキャラクターのHP ( 魔法使いに転職するのに必要な最低レベル ( 盗賊に転職するのに必要な最低レベル = ???#k");
                } else if (map_id == this.map_stage4) {
                    say("こんにちは！第4ステージへ#Gようこそ:ようこそ#。ここは次元の裂け目によって作られた暗黒の空間よ。その中には、闇に潜む#b#o" + this.mob_weep_eye + "##kというモンスターがいるの。だから、目をしっかり開いていてもほとんど見えないわ。モンスターを倒して#b#t" + this.item_pass + "#を6枚#k集めてね。\r\nパーティーリーダーはみんなから#b#t" + this.item_pass + "##kを全部集めてね。言ったとおり、#b#o" + this.mob_weep_eye + "##kは目をしっかり開いていないと見えないの。闇にこっそり溶け込む、変わった種類のモンスターなのよ。頑張って！");
                } else if (map_id == this.map_stage5) {
                    say("こんにちは！第5ステージへ#Gようこそ:ようこそ#。ここにはたくさんの空間があって、その中にモンスターがいるわ。あなたたちの任務は、パーティーで#b#t" + this.item_pass + "#を24枚#k集めることよ。説明すると、特定の職業でないと#b#t" + this.item_pass + "##kを集められない場合があるの。だから気をつけてね。ヒントをあげるわ。#b#o" + this.mob_king_block + "##kという倒せないモンスターがいるの。そのモンスターの向こう側へ行けるのは盗賊だけよ。魔法使いしか通れない道もあるわ。それを見つけるのはあなたたち次第よ。頑張って！");
                } else {
                    say("こんにちは！第7ステージへ#Gようこそ:ようこそ#。  ここには#b#o" + this.mob_rombard + "##kというとんでもなく強力なモンスターがいるわ。そのモンスターを倒して、次のステージに進むのに必要な#b#t" + this.item_pass + "##kを見つけてね。#b#t" + this.item_pass + "#を3枚#k集めてね。\r\nこのモンスターを倒すには、遠くから攻撃するの。遠距離から攻撃するしか方法はないんだけど…ああ、それと気をつけて、#o" + this.mob_rombard + "#はとても危険よ。油断すると間違いなく怪我をするわ。頑張って！");
                }
                return false;
            }
            if (!exchange(0, this.item_pass, -need)) {
                // 翻訳
                say("モンスターを全部倒して、#b#t" + this.item_pass + "#を" + need + "枚#k集めたのね。とても素晴らしいわ！");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            effectObject("gate");
            enablePortal("next00", true);
            field_set.setVar("stage", this.stage_next[i]);
            // TODO : lua の incExpAll の第2引数 (7010) の用途は不明
            field_set.incExpAll(this.stage_exps[i]);
            // 翻訳
            say("次のステージへのポータルが開きました。");
            return false;
        }
        return false;
    }
}
