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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party3_play (JMS131-302), 会話文は lua_script_jp (party3_play) を利用
public class Npc_Orbis_ChamberlainEak extends NpcScriptMan {

    private static final int NODE_NONE = 0;
    private static final int NODE_OUT_MENU = 1;
    private static final int NODE_OUT_CONFIRM = 2;
    private static final int NODE_TRANSFER_ALL = 3;
    private static final int NODE_WARP = 4;
    private static final int NODE_HELP = 5;
    private static final int NODE_OUT = 6;
    private final String field_set_name = "Party5";
    private final int map_entrance = 920010000; // 女神の塔 : 入口
    private final int map_center_tower = 920010100; // 女神の塔 : 中央塔
    private final int map_walkway = 920010200; // 女神の塔 : 散歩路
    private final int map_storage = 920010300; // 女神の塔 : 倉庫
    private final int map_lobby = 920010400; // 女神の塔 : 休憩室
    private final int map_sealed_room = 920010500; // 女神の塔 : 封印された部屋
    private final int map_lounge = 920010600; // 女神の塔 : ラウンジ
    private final int map_up_path = 920010700; // 女神の塔 : 登り道
    private final int map_garden = 920010800; // 女神の塔 : 庭園
    private final int map_sin_room = 920010900; // 女神の塔 : 罪人の部屋
    private final int map_dark_room = 920011000; // 女神の塔 : 暗黒の部屋
    private final int map_exit = 920011200; // 女神の塔 : 出口
    private final int map_strange_tower = 200080101; // オルビス塔 : 見知らぬ塔
    private final int item_first_small_piece = 4001050; // 一番目の小さいかけら
    private final int item_first_piece = 4001044; // 女神像の一番目のかけら
    private final int item_second_piece = 4001045; // 女神像の二番目のかけら
    private final int item_fifth_small_piece = 4001052; // 五番目の小さいかけら
    private final int item_fifth_piece = 4001048; // 女神像の五番目のかけら
    private final int item_grass_of_life = 4001055; // 生命の草
    private final int item_takeaway_first = 4001044; // 回収するアイテム (4001044 - 4001063)
    private final int item_takeaway_last = 4001063;
    private final int item_takeaway_extra = 4001074; // 透明アイテム
    private int node = NODE_NONE;
    private int last_status = 0;
    private int transfer_map = 0;
    private String transfer_portal = "";

    @Override
    public boolean action(int status, int answer) {
        if (hasPages()) {
            return nextPage();
        }
        switch (this.node) {
            case NODE_HELP -> {
                // lua の party3_help
                this.node = NODE_NONE;
                ArrayList<String> texts = new ArrayList<>();
                int map_id = getMapId();
                // 翻訳
                if (map_id == this.map_entrance) {
                    texts.add("何と言えばいいか。私の体を集めてくださってありがとうございます。私はイーク、女神ミネルバの信頼厚い侍従です。皆さんはミネルバ様を救いに来られたのですよね？私もお手伝いします。では、塔の入口までご案内しましょう。");
                } else if (map_id == this.map_center_tower) {
                    texts.add("はい、私の横にある壊れた像に、今#bミネルバ#k様が閉じ込められているのです。#bミネルバ#k様を救うには、壊れた部分を直すための#b像のかけら6つ#kを集め、像に神秘の力を注ぐための#b#t" + this.item_grass_of_life + "##kを持ってくる必要があります。");
                    texts.add("塔をよく探して、#b女神像のかけら#kと#b生命の草#kを見つけ、女神ミネルバ様を救ってください！");
                } else if (map_id == this.map_walkway) {
                    texts.add("ここは女神の塔の遊歩道です。ピクシーたちが#b#t" + this.item_first_piece + "#を30個のかけら#kに砕いて、それぞれ持ち去ってしまいました。ピクシーたちを倒して#b#t" + this.item_first_small_piece + "##kを取り戻してください。それを使って#b#t" + this.item_first_piece + "##kを作ります。ピクシーたちは女神像の力で強くなっていますから、気をつけてください～");
                } else if (map_id == this.map_storage) {
                    texts.add("ここはかつて女神の塔の倉庫でしたが、今ではセルビオンたちのすみかになっています。あるセルビオンが#b#t" + this.item_second_piece + "##kを持ち去って隠してしまいました。そいつを倒して、#b#t" + this.item_second_piece + "##kを取り戻してください。");
                } else if (map_id == this.map_lobby) {
                    texts.add("ここは女神の塔の休憩室です。女神ミネルバ様が音楽を聴くのが好きだった場所です。曜日によって、いろいろな種類の音楽を聴くのがお好きでした。");
                    texts.add("女神様は日によって違う種類の音楽を聴くのがお好きでした。\r\n #b月曜日はかわいい音楽、魅力的で愛らしいもの\r\n火曜日は怖い音楽、誰もがぞっとするようなもの\r\n水曜日は楽しい音楽、踊れるようなもの\r\n木曜日は悲しい音楽\r\n金曜日は背筋が寒くなるような音楽\r\n土曜日は短い編曲の速い音楽\r\nそして日曜日は気分に合わせてオペラ#k\r\n女神様は毎日好みが変わりました。本当に音楽を研究されていたのです。");
                    texts.add("その曜日の音楽を流せば、女神ミネルバ様の魂が反応して…何かが起こるかもしれません。");
                } else if (map_id == this.map_sealed_room) {
                    texts.add("ここは女神の塔の封印された部屋です。女神ミネルバ様が大切な持ち物を安心してしまっておける部屋でした。");
                    texts.add("横にある3つの台は呪いを解く錠前の役割をしていて、すべて同じ重さを支えなければなりません。どれどれ…ちょうどいい重さにするには、5人が上に乗る必要がありますね。それと、7回以内に問題を解かなければならず、答えを変えてもいけません。さもないと封印された部屋から追い出されてしまいます。");
                } else if (map_id == this.map_lounge) {
                    texts.add("ここは女神の塔のロビーで、客人が何晩か泊まる場所です。#b#t" + this.item_fifth_piece + "##kが40個のかけらに砕けて、あちこちに散らばっています。#b#t" + this.item_fifth_small_piece + "##kのかけらを探して集めてください。");
                } else if (map_id == this.map_up_path) {
                    texts.add("ここは女神の塔の上へ続く道です。上には頂上への扉を制御するレバーが5つあります。皆さんの任務は、正しく動かすべき2つのレバーを見つけることです。動かしたら教えてください。正しいレバーが動かされたかどうかお伝えします。");
                } else if (map_id == this.map_garden) {
                    texts.add("ここは女神の塔の庭です。#b生命の草#kはこの植木鉢でしか育ちません。草を育てれば種が手に入ります…ああ、そういえば、気をつけてください。女神様を像に閉じ込めた張本人、#bパパピクシー#kに出くわすかもしれません。");
                } else if (map_id == this.map_sin_room) {
                    texts.add("ここは女神の塔の罪の部屋です。女神ミネルバ様は、最も邪悪で最悪の罪を犯したモンスターたちをここに閉じ込めていました。幸い、この場所に像のかけらはありませんから、この部屋の右の坂を戻ってください。もう一度言いますが…何かが隠れているかもしれません…");
                } else if (map_id == this.map_dark_room) {
                    texts.add("うう、ここはとても暗いですね。ここは女神の塔の闇の部屋です。おや、どうやってここに来たのですか？ここには女神像のかけらはありません。ほかの部屋を探すことをおすすめします。");
                }
                if (!isPartyBoss()) {
                    texts.add("それでは、パーティーリーダーの案内で進んでください。");
                }
                return sayPages(texts.toArray(new String[0]));
            }
            case NODE_OUT -> {
                // lua の party3_out
                this.node = NODE_NONE;
                if (getMapId() != this.map_exit) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("どうしますか？ \r\n#b#L0#イークに従う。#l\r\n#b#L1# パーティークエストをやめて立ち去る。#l");
                    askMenu(nt);
                    this.node = NODE_OUT_MENU;
                    return true;
                }
                // アイテムの回収
                for (int item_id = this.item_takeaway_first; item_id <= this.item_takeaway_last; item_id++) {
                    removeItemAll(item_id);
                }
                removeItemAll(this.item_takeaway_extra);
                // TODO : lua の cancelPartyBuff (妖精ウィンキの祝福 2022090 - 2022093) は未実装
                warp(this.map_strange_tower, "st00");
                return false;
            }
            case NODE_OUT_MENU -> {
                if (answer == 0) {
                    this.node = NODE_HELP;
                    return action(status, answer);
                }
                if (answer != 1) {
                    return false;
                }
                this.node = NODE_OUT_CONFIRM;
                this.last_status = status;
                // 翻訳
                askYesNo("本当に出ますか？");
                return true;
            }
            case NODE_OUT_CONFIRM -> {
                if (status < this.last_status) {
                    // 翻訳
                    say("よく考えてから話しかけてください。");
                    return false;
                }
                warp(this.map_exit, "st00");
                return false;
            }
            case NODE_TRANSFER_ALL -> {
                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                if (field_set != null) {
                    field_set.transferFieldAll(this.transfer_map, this.transfer_portal);
                }
                return false;
            }
            case NODE_WARP -> {
                warp(this.transfer_map, this.transfer_portal);
                return false;
            }
            default -> {
            }
        }
        if (status != 0) {
            return false;
        }
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        int map_id = getMapId();
        if (map_id == this.map_exit) {
            this.node = NODE_OUT;
            return action(status, answer);
        }
        if (field_set == null) {
            return false;
        }
        boolean boss = isPartyBoss();
        // 入口
        if (map_id == this.map_entrance) {
            if (field_set.getVar("prestage_clear").equals("1")) {
                // 翻訳
                say("では、塔の入口までご案内しましょう。", false, true);
                this.node = NODE_WARP;
                this.transfer_map = this.map_entrance;
                this.transfer_portal = "st00";
                return true;
            }
            if (!boss) {
                // 翻訳
                say("私の体を起こしてくださってありがとうございます。皆さんは女神ミネルバ様を救いに来られたのですよね？できる限りお手伝いします。それでは、パーティーリーダーの案内で進んでください。");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            field_set.setVar("prestage_clear", "1");
            // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
            field_set.incExpAll(6000);
            // 翻訳
            say("私の体を起こしてくださってありがとうございます。皆さんは女神ミネルバ様を救いに来られたのですよね？できる限りお手伝いします。では、塔の入口までご案内しましょう。", false, true);
            this.node = NODE_TRANSFER_ALL;
            this.transfer_map = this.map_entrance;
            this.transfer_portal = "st00";
            return true;
        }
        // 中央塔
        if (map_id == this.map_center_tower) {
            String stage0 = field_set.getVar("stage0_clear");
            int minerva = field_set.getReactorState(1, "minerva");
            if (boss && minerva == 6) {
                field_set.setVar("stage0_clear", "1");
                // 翻訳
                say("ああ！像が修復されました！本当にありがとうございます！あとは#b女神ミネルバ#k様を取り戻すための生命の草だけです。生命の草は女神の塔の庭でしか育ちません。今すぐそこへお連れしましょう。", false, true);
                this.node = NODE_TRANSFER_ALL;
                this.transfer_map = this.map_garden;
                this.transfer_portal = "in00";
                return true;
            }
            if (boss && minerva == 7) {
                // 翻訳
                say("女神ミネルバ様を救ってくださって、何とお礼を言えばいいかわかりません！");
                return false;
            }
            if (!boss) {
                if (stage0.isEmpty() || stage0.equals("s")) {
                    this.node = NODE_OUT;
                    return action(status, answer);
                }
                return false;
            }
            if (stage0.isEmpty()) {
                field_set.setVar("stage0_clear", "s");
                this.node = NODE_HELP;
                return action(status, answer);
            }
            if (stage0.equals("s")) {
                this.node = NODE_OUT;
                return action(status, answer);
            }
            if (stage0.equals("1")) {
                // 翻訳
                say("生命の草は手に入りましたか？手に入ったら、女神像の中央に置いてください。");
            }
            return false;
        }
        // 散歩路, ラウンジ (小さいかけらを集めて女神像のかけらと交換する)
        if (map_id == this.map_walkway || map_id == this.map_lounge) {
            boolean walkway = map_id == this.map_walkway;
            String key = walkway ? "stage1_clear" : "stage5_clear";
            int item_small = walkway ? this.item_first_small_piece : this.item_fifth_small_piece;
            int item_piece = walkway ? this.item_first_piece : this.item_fifth_piece;
            int required = walkway ? 30 : 40;
            String stage = field_set.getVar(key);
            if (!boss) {
                if (!stage.equals("1")) {
                    this.node = NODE_HELP;
                    return action(status, answer);
                }
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋に入ってください。");
                return false;
            }
            if (stage.isEmpty()) {
                field_set.setVar(key, "s");
                this.node = NODE_HELP;
                return action(status, answer);
            }
            if (!stage.equals("s")) {
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋に入ってください。");
                return false;
            }
            int count = getItemCount(item_small);
            if (count < required) {
                // 翻訳
                say("#b#t" + item_small + "##kは全部集めましたか？");
                return false;
            }
            if (!exchange(0, item_small, -count, item_piece, 1)) {
                // 翻訳
                say("インベントリに空きがあるか確認してください。");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            field_set.setVar(key, "1");
            // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
            field_set.incExpAll(7500);
            // 翻訳
            say("おお！#b#t" + item_small + "##kを" + required + "個集めてくださったのですね！これで#b#t" + item_piece + "##kが作れます。さあ、今すぐ作りましょう！");
            return false;
        }
        // 倉庫
        if (map_id == this.map_storage) {
            String stage2 = field_set.getVar("stage2_clear");
            if (stage2.isEmpty()) {
                if (boss) {
                    field_set.setVar("stage2_clear", "s");
                }
                this.node = NODE_HELP;
                return action(status, answer);
            }
            if (!stage2.equals("s")) {
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋に入ってください。");
                return false;
            }
            if (!boss) {
                return false;
            }
            if (getItemCount(this.item_second_piece) < 1) {
                // 翻訳
                say("#b#t" + this.item_second_piece + "##kは集めましたか？");
                return false;
            }
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            field_set.setVar("stage2_clear", "1");
            // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
            field_set.incExpAll(7500);
            return false;
        }
        // 休憩室 (曜日の音楽)
        if (map_id == this.map_lobby) {
            int music = field_set.getReactorState(4, "music");
            if (!boss || music == 0) {
                this.node = NODE_HELP;
                return action(status, answer);
            }
            if (field_set.getVar("stage3_clear").equals("1")) {
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋に入ってください。");
                return false;
            }
            // lua の dayofweek (日曜日 = 0)
            int day = LocalDate.now().getDayOfWeek().getValue() % 7;
            if (music != day + 1) {
                // 翻訳
                say("これではありません。");
                return false;
            }
            // TODO : lua の setReactorState の第4引数 (0) の用途は不明
            field_set.setReactorState(4, "stone3", 1);
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            field_set.setVar("stage3_clear", "1");
            // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
            field_set.incExpAll(7500);
            // 翻訳
            say("そう！この音楽です！女神様はときどきこの音楽を聴くのが大好きでした。");
            return false;
        }
        // 封印された部屋 (3つの台の人数)
        if (map_id == this.map_sealed_room) {
            String stage4 = field_set.getVar("stage4_clear");
            if (stage4.equals("1")) {
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋で女神像のかけらを探してください。");
                return false;
            }
            if (!boss) {
                this.node = NODE_HELP;
                return action(status, answer);
            }
            // lua の party3_nQuizAns, 3つの台の人数 (合計5人) を設定する
            if (stage4.isEmpty() || field_set.getVar("ans1").isEmpty()) {
                int ans1 = random(0, 5);
                int ans2 = random(0, 5 - ans1);
                ArrayList<Integer> answers = new ArrayList<>();
                answers.add(ans1);
                answers.add(ans2);
                answers.add(5 - ans1 - ans2);
                Collections.shuffle(answers);
                field_set.setVar("ans1", String.valueOf(answers.get(0)));
                field_set.setVar("ans2", String.valueOf(answers.get(1)));
                field_set.setVar("ans3", String.valueOf(answers.get(2)));
            }
            if (stage4.isEmpty()) {
                field_set.setVar("try", "0");
                field_set.setVar("stage4_clear", "s");
                this.node = NODE_HELP;
                return action(status, answer);
            }
            // lua の area "1", "2", "3" は Map.wz の area の 0, 1, 2 番目
            int area1 = countUserInArea(0);
            int area2 = countUserInArea(1);
            int area3 = countUserInArea(2);
            if (area1 + area2 + area3 != 5) {
                // 翻訳
                say("台の上に5人乗る必要があります。");
                return false;
            }
            int co = 0;
            if (area1 == integer(field_set.getVar("ans1"))) {
                co++;
            }
            if (area2 == integer(field_set.getVar("ans2"))) {
                co++;
            }
            if (area3 == integer(field_set.getVar("ans3"))) {
                co++;
            }
            if (co == 3) {
                // TODO : lua の setReactorState の第4引数 (0) の用途は不明
                field_set.setReactorState(5, "stone4", 1);
                effectScreen("quest/party/clear");
                effectSound("Grupo1/Completado");
                field_set.setVar("stage4_clear", "1");
                // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
                field_set.incExpAll(7500);
                // 翻訳
                say("正解です。祭壇の上に何かが現れました。");
                return false;
            }
            int tried = integer(field_set.getVar("try")) + 1;
            field_set.setVar("try", String.valueOf(tried));
            if (7 <= tried) {
                // 翻訳
                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "失敗したので、部屋から追い出されます。");
                field_set.setVar("try", "0");
                field_set.setVar("stage4_clear", "");
                transferFieldAll(this.map_center_tower, "in03");
                return false;
            }
            // 翻訳
            String text = "これは" + tried + "回目の挑戦です。\r\n";
            text += co == 0 ? "すべての台の重さが違います。" : "重さが正しい台は" + co + "つです。";
            if (tried == 6) {
                text += co == 0 ? "\r\nあと1回しか挑戦できません。気をつけてください！" : "\r\n残りの挑戦は1回です。気をつけてください。";
            }
            say(text);
            return false;
        }
        // 登り道 (5つのレバー)
        if (map_id == this.map_up_path) {
            String stage6 = field_set.getVar("stage6_clear");
            if (stage6.equals("1")) {
                // 翻訳
                say("この部屋ではもうすることはないようです。ほかの部屋に入ってください。");
                return false;
            }
            if (!boss) {
                this.node = NODE_HELP;
                return action(status, answer);
            }
            if (stage6.isEmpty()) {
                field_set.setVar("stage6_clear", "s");
                this.node = NODE_HELP;
                return action(status, answer);
            }
            // TODO : lua では正解のレバー (stage6_ans1) を2回確認していて stage6_ans2 を確認していない
            boolean correct = field_set.getReactorState(11, field_set.getVar("stage6_ans1")) == 1
                    && field_set.getReactorState(11, field_set.getVar("stage6_ans2")) == 1
                    && field_set.getReactorState(11, field_set.getVar("stage6_wans1")) == 0
                    && field_set.getReactorState(11, field_set.getVar("stage6_wans2")) == 0
                    && field_set.getReactorState(11, field_set.getVar("stage6_wans3")) == 0;
            if (!correct) {
                // 翻訳
                say("何も起こりませんでした。正しいレバーを動かしていないようです。");
                return false;
            }
            // TODO : lua の setReactorState の第4引数 (0) の用途は不明
            field_set.setReactorState(11, "stone6", 1);
            effectScreen("quest/party/clear");
            effectSound("Grupo1/Completado");
            field_set.setVar("stage6_clear", "1");
            // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
            field_set.incExpAll(7500);
            // 翻訳
            say("レバーを正しく動かしました。祭壇の上に何かが現れました。");
            return false;
        }
        // 庭園
        if (map_id == this.map_garden) {
            if (boss && 0 < getItemCount(this.item_grass_of_life)) {
                // 翻訳
                say("おお、生命の草を見つけたのですね！これで女神様を救えます！塔の中央に戻って、女神様を救ってください！");
                return false;
            }
            this.node = NODE_OUT;
            return action(status, answer);
        }
        // 罪人の部屋, 暗黒の部屋
        if (map_id == this.map_sin_room || map_id == this.map_dark_room) {
            this.node = NODE_HELP;
            return action(status, answer);
        }
        // 宝物倉庫など
        this.node = NODE_OUT;
        return action(status, answer);
    }
}
