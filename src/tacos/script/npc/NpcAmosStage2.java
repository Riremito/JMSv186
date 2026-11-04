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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : PartyAmoria_play3 (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_play3) を利用
// lua のモンスター (2101049) は存在しないため GMS のモンスターに置き換え
// TODO : 結婚システムが未実装のため、カップル用の宝物庫 (670010750) への分岐は常に通らない
public class NpcAmosStage2 extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_STAGE4_CLEAR = 1;
    private static final int NODE_STAGE6_HELP = 2;
    private static final int NODE_STAGE6_ASK = 3;
    private static final int NODE_STAGE5_NEXT = 4;
    private static final int NODE_VAULT = 5;
    private final String field_set_name = "PartyAmoria";
    private final String field_set_bonus = "PartyAmoriaBo";
    private final int map_stage4 = 670010500; // Hidden Street : Stage 4 - Last Man Standing
    private final int map_stage5 = 670010600; // Hidden Street : Stage 5 - Fluttering Hearts
    private final int map_stage6 = 670010700; // Hidden Street : Stage 6 - Love Hurts
    private final int mob_balrog = 9400536; // Geist Balrog Phase 1 (TODO : lua の 2101049)
    private final int item_piece = 4031597; // Cupid Code Piece
    private final int item_fang = 4031594; // Geist Fang
    private final int piece_count = 50;
    // lua の takeawayitem (クエストアイテム)
    private final int[] items_quest = {
        4031592, // Entrance Ticket
        4031593, // Lip Lock Key
        4031594, // Geist Fang
        4031595, // Magik Mirror Shard
        4031596, // Wing Hammer
        4031597 // Cupid Code Piece
    };
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        switch (this.node) {
            case NODE_STAGE4_CLEAR -> {
                if (status == 1) {
                    // 翻訳
                    say("さて、次のステップは見ものだぞ…俺にとってはな。目的は単に向こう側にたどり着くことだ。行く手には扉から落とし穴、そのほかの嫌なサプライズまで、障害物が山ほどある。全速力で向こう側まで走るんだ。さもないと、巨大なモンスターに容赦なく襲われるぞ。待っているからな！", true, false);
                    field_set.transferFieldAll(this.map_stage5, "st00");
                    // 翻訳
                    field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "右へ走れ！逃げろ！走れ！！！走れ！！！走れ！！！");
                }
                return false;
            }
            case NODE_STAGE5_NEXT -> {
                if (status == 1) {
                    removeItemAll(this.item_piece);
                    warp(this.map_stage6, "st00");
                }
                return false;
            }
            case NODE_STAGE6_HELP -> {
                if (status == 1) {
                    // 翻訳
                    askYesNo("よし、それじゃあバルログに会いに下へ降りる準備はいいか？");
                    this.node = NODE_STAGE6_ASK;
                    return true;
                }
                return false;
            }
            case NODE_STAGE6_ASK -> {
                if (!ok) {
                    // 翻訳
                    say("はっはっは！スタミナはそのうちつくさ、友よ。まあ、修行する準備ができたら戻ってきな！");
                    return false;
                }
                // 翻訳
                say("君たちにはその資格がある！さあ、入れ！");
                field_set.transferFieldAll(this.map_stage6, "st01");
                field_set.setVar("stage6_clear", "s");
                summonMob(933, 432, this.mob_balrog);
                return false;
            }
            case NODE_VAULT -> {
                if (status != 1) {
                    return false;
                }
                int res = enterFieldSetParty(this.field_set_bonus, 0);
                switch (res) {
                    case TacosFieldSet.ENTER_SUCCESS -> {
                        for (int item : this.items_quest) {
                            removeItemAll(item);
                        }
                    }
                    case TacosFieldSet.ENTER_NO_PARTY -> {
                        // 翻訳
                        say("パーティーに入っていないな。このクエストはパーティーに入っているときだけ挑戦できる。");
                    }
                    case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                        // 翻訳
                        say("パーティーが6人ではない。パーティーメンバーが6人そろったら戻ってきてくれ。");
                    }
                    case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                        // 翻訳
                        say("パーティーの中にレベル40を超えていない者がいる。もう一度確認してくれ。");
                    }
                    case TacosFieldSet.ENTER_USING -> {
                        // 翻訳
                        say("すでにほかのパーティーがクエストのクリアを目指して入っている。しばらくしてからもう一度試してくれ。");
                    }
                    default -> {
                        // 翻訳
                        say("不明な理由により、君たちのパーティーを入れることはできない。しばらくしてからもう一度試してくれ。");
                    }
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
        // ステージ4 - ラストマンスタンディング
        if (map_id == this.map_stage4) {
            String stage4 = field_set.getVar("stage4_clear");
            // 翻訳 (lua の PartyAmoria_help)
            String help = "よし、次はまたアドレナリンが出まくること間違いなしだ…出口の扉にはかんぬきが3本ある。扉を開けるには、キューピッドコードのかけらが50個必要だ。このエリアには3種類のモンスターがいる。モンスターを全種類倒せば、キューピッドコードのかけらが見つかるはずだ。簡単だろう？ただ一つだけ覚えておいてくれ。時間は刻一刻と過ぎていくぞ。さあ、急げ！";
            if (stage4.isEmpty()) {
                if (!isPartyBoss()) {
                    say(help);
                    return false;
                }
                if (!field_set.getVar("stage3_clear").equals("1")) {
                    // 翻訳
                    say("まずミッションをクリアしてから話しかけてくれ。");
                    return false;
                }
                say(help);
                field_set.setVar("stage4_clear", "s");
                return false;
            }
            if (stage4.equals("1")) {
                // 翻訳
                say("さて、次のステップは見ものだぞ…俺にとってはな。目的は単に向こう側にたどり着くことだ。行く手には扉から落とし穴、そのほかの嫌なサプライズまで、障害物が山ほどある。全速力で向こう側まで走るんだ。さもないと、巨大なモンスターに容赦なく襲われるぞ。待っているからな！");
                field_set.transferFieldAll(this.map_stage5, "st00");
                return false;
            }
            if (!stage4.equals("s")) {
                return false;
            }
            if (!isPartyBoss()) {
                // 翻訳
                say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                return false;
            }
            if (getItemCount(this.item_piece) < this.piece_count) {
                // 翻訳
                say("扉を開けるには、キューピッドコードのかけらが50個必要だということを忘れるな。それより少ないとだめだぞ！");
                return false;
            }
            if (!exchange(0, this.item_piece, -this.piece_count)) {
                // 翻訳
                say("すまないが、インベントリにアイテムを入れるのに問題が起きているようだ。後でまた来てくれ。");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Party1/Clear");
            field_set.setVar("stage4_clear", "1");
            field_set.incExpAll(8000);
            // 翻訳
            say("内なる強さは、ストレスのかかる状況でこそ現れる。君たちはここで腕前を証明した。次のエリアで会おう！", false, true);
            this.node = NODE_STAGE4_CLEAR;
            return true;
        }
        // ステージ5 - フラッタリングハーツ
        if (map_id == this.map_stage5) {
            String stage5 = field_set.getVar("stage5_clear");
            if (countUserInArea(0) == 6) {
                field_set.setVar("stage_CountAll", "6");
            }
            if (stage5.isEmpty()) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("見事な速さだ！すまないが…パーティーのリーダーが必要なんだ、待っていてくれ！");
                    return false;
                }
                if (!field_set.getVar("stage4_clear").equals("1")) {
                    // 翻訳
                    say("まずミッションをクリアしてから話しかけてくれ。");
                    return false;
                }
                // 翻訳
                say("見事な速さだ！君たちが駆け抜けるのを見ているだけで疲れそうになったよ。さて、ゴールは近いぞ、友よ…最後のエリアへ進め！");
                field_set.setVar("stage5_clear", "s");
                return false;
            }
            if (stage5.equals("s")) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("見事な速さだ！すまないが…パーティーのリーダーが必要なんだ、待っていてくれ！");
                    return false;
                }
                if (!field_set.getVar("stage_CountAll").equals("6")) {
                    // 翻訳
                    say("すまないが、今はパーティーメンバーが全員そろっていないようだ。待っていてくれ。");
                    return false;
                }
                // 翻訳
                say("よくやった！パーティーメンバーがそろったな。");
                effectScreen("quest/party/clear");
                effectSound("Party1/Clear");
                field_set.setVar("stage5_clear", "1");
                field_set.incExpAll(9000);
                return false;
            }
            if (stage5.equals("1")) {
                // カップルの確認 (lua の stage_CountA) は結婚システムが未実装のため常に通らない
                field_set.setVar("stage_CoupleBo", "0");
                // 翻訳
                say("それでは、最後のステージへ続く道へ案内しよう。そのまま…", false, true);
                this.node = NODE_STAGE5_NEXT;
                return true;
            }
            return false;
        }
        // ステージ6 - ラブハーツ
        if (map_id == this.map_stage6) {
            String stage6 = field_set.getVar("stage6_clear");
            // 翻訳 (lua の PartyAmoria_help)
            String help = "君たちの強さには感服するよ、最後のステージにたどり着いたんだからな！さて、俺が戦ったバルログを覚えているか？実はその後、妙なことが起きたんだ。奴はずる賢くて凶暴な何かに変身した…手ごわい相手だ。ここにおびき寄せるのに本当に苦労したよ！倒すには努力と技術が必要だろう。アモリアの挑戦を完了するには、奴のガイストファングを持ってきてくれ。準備ができたら下へ向かってくれ。幸運を祈る！";
            if (stage6.isEmpty()) {
                if (countUserInArea(0) != 6) {
                    // 翻訳
                    say("すまないが、今はパーティーメンバーが全員そろっていないようだ。待っていてくれ。");
                    return false;
                }
                if (!isPartyBoss()) {
                    say(help);
                    return false;
                }
                if (!field_set.getVar("stage5_clear").equals("1")) {
                    // 翻訳
                    say("まずミッションをクリアしてから話しかけてくれ。");
                    return false;
                }
                say(help, false, true);
                this.node = NODE_STAGE6_HELP;
                return true;
            }
            if (stage6.equals("s")) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                    return false;
                }
                if (getItemCount(this.item_fang) < 1) {
                    // 翻訳
                    say("俺の宝物庫に入るには、戦いで腕前を証明してその資格を得なければならない！");
                    return false;
                }
                if (!exchange(0, this.item_fang, -1)) {
                    // 翻訳
                    say("すまないが、インベントリにアイテムを入れるのに問題が起きているようだ。後でまた来てくれ。");
                    return false;
                }
                // 翻訳
                say("その牙はあのモンスターからしか手に入らない…見事だ！君たちの優れた戦術、驚くべき速さ、そして抑えきれない力をこの目で見届けた。君たちと知り合えて誇りに思うよ！よければ、俺の宝物庫に入って賞品を選んでくれ！");
                effectScreen("quest/party/clear");
                effectSound("Party1/Clear");
                field_set.setVar("stage6_clear", "1");
                field_set.incExpAll(11000);
                return false;
            }
            if (stage6.equals("1")) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
                    return false;
                }
                // 翻訳
                say("俺の宝物庫へ連れて行こう。", false, true);
                this.node = NODE_VAULT;
                return true;
            }
            return false;
        }
        // TODO : ステージ7 - カップル用の宝物庫 (670010750) は結婚システムが未実装のため未実装
        return false;
    }
}
