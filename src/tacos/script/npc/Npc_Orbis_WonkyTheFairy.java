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

import java.util.ArrayList;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party3_enter (JMS131-302), 会話文は lua_script_jp (party3_enter) を利用
// lua の party3_enter は処理全体がコメントアウトされている (jkによりブロック 20080730) ため、コメントアウトされた処理を利用
public class Npc_Orbis_WonkyTheFairy extends NpcScriptMan {

    private static final int MENU_ENTER = 0;
    private static final int MENU_TOWER = 1;
    private static final int MENU_FOOD = 2;
    private static final int MENU_FEATHER = 3;
    private static final int NODE_START = 0;
    private static final int NODE_MISSION = 1;
    private static final int NODE_MISSION_END = 2;
    private static final int NODE_LEAVE = 3;
    private static final int NODE_LEAVE_END = 4;
    private final String field_set_name = "Party5";
    private final int map_strange_tower = 200080101; // オルビス塔 : 見知らぬ塔
    private final int map_entrance = 920010000; // 女神の塔 : 入口
    private final int map_exit = 920011200; // 女神の塔 : 出口
    private final int party_member = 6;
    private final int level_min = 51;
    private final int level_max = 70;
    private final int item_takeaway_first = 4001044; // 回収するアイテム (4001044 - 4001063)
    private final int item_takeaway_last = 4001063;
    private final int item_takeaway_extra = 4001074; // 透明アイテム
    // ウィンキのお願い (lua の premission), 戦士, 魔法使い, 弓使い, 盗賊が各1名以上いる場合にランダムで1つ
    private final int[] item_buffs = {
        2022090, // 妖精ウィンキの祝福(攻撃)
        2022091, // 妖精ウィンキの祝福(防御)
        2022092, // 妖精ウィンキの祝福(避)
        2022093 // 妖精ウィンキの祝福(移動)
    };
    // 翻訳
    private final String[] buff_messages = {
        "ウォンキーのお願いをかなえたので、30分間、攻撃力と魔力が上がりました。",
        "ウォンキーのお願いをかなえたので、30分間、物理防御力と魔法防御力が上がりました。",
        "ウォンキーのお願いをかなえたので、30分間、命中率が上がりました。",
        "ウォンキーのお願いをかなえたので、30分間、移動速度とジャンプ力が上がりました。"
    };
    // ウィンキの好感度 (lua の feellike), 好感度 (3桁) + 最近食べた食べ物のコード (4桁)
    private final int quest_like = 7522;
    private final int quest_like_time = 7523; // 好感度を最後に更新した時刻
    private final String like_default = "1000000";
    private final int like_min = 100;
    private final int like_max = 500;
    private final int like_decay_time = 5040; // 好感度が下がるまでの時間 (分)
    private final String sound_eat = "Grupo3/Comer";
    // 食べ物 (lua の food_code, コードは index + 1)
    private final int[] food_ids = {
        2020001, // チキン
        2022001, // 小豆粥
        2020004, // ハンバーガー
        2022003, // ウナギ焼
        2001001, // チューチューアイス
        2020028, // ミルクチョコ
        2001002 // 小豆かき氷
    };
    // 翻訳
    private final String[] food_names = {
        "フライドチキン",
        "小豆粥",
        "ハンバーガー",
        "うなぎ",
        "アイスキャンディー",
        "チョコレート",
        "小豆かき氷"
    };
    // 翻訳
    private final String[] food_eat_good = {
        "フライドチキンを食べて",
        "小豆粥を1杯食べて",
        "ハンバーガーを食べて",
        "うなぎを食べて",
        "アイスキャンディーをなめて",
        "チョコレートを食べて",
        "冷たい小豆かき氷を食べて"
    };
    // 翻訳
    private final String[] food_eat_normal = {
        "フライドチキンを食べている",
        "小豆粥を食べている",
        "ハンバーガーを食べている",
        "うなぎを食べている",
        "アイスキャンディーをなめている",
        "チョコレートを食べている",
        "冷たい小豆かき氷を食べている"
    };
    // 機嫌が良くならなかった場合に上がる好感度
    private final int[] food_like = {1, 1, 1, 2, 2, 2, 3};
    // 機嫌が良くなる確率 (%) (lua の ate_food)
    private final int[] food_rate_all = {2, 2, 2, 2, 2, 2, 2}; // 最近食べた4つがすべて同じ
    private final int[] food_rate_last3 = {1, 3, 3, 7, 20, 26, 30}; // 最近食べた3つが同じ
    private final int[] food_rate_last2 = {2, 4, 4, 10, 27, 32, 40}; // 最近食べた2つが同じ
    private final int[] food_rate_last1 = {2, 5, 5, 13, 30, 40, 50}; // 最後に食べたものと同じ
    private final int[] food_rate_new = {4, 8, 8, 19, 39, 50, 69}; // 最近食べていない
    private final int[] food_rate_other = {3, 6, 6, 16, 36, 47, 65}; // それ以外
    // 翻訳
    private final String[] food_texts_other = {
        "やったー！～#bフライドチキン#k！！外はカリッと中はふんわり、フライドチキンにかなうものはないよ。",
        "やったー！～#b#t2022001##k！！！とっても甘くておいしい！これを食べると幸せいっぱいになるよ。",
        "やったー！～大好物の#bハンバーガー#k！このハンバーガーを見て！  最高においしそうでしょ？",
        "やったー！～#bうなぎ#k！！甘いタレのうなぎ…うーん…食べてみて～",
        "やったー！～この#bアイスキャンディー#kがあれば、夏の暑さも吹き飛ぶね！冷たーい！！",
        "やったー！～甘さが体中に広がる#bチョコレート#kだ。最高！君も食べてみて！",
        "やったー！おいしい#b小豆かき氷#kだ！かき氷に小豆とフルーツをのせて…うーん…冷たくて口が凍っちゃいそう！！"
    };
    // 好感度の報酬 (lua の feellike_reward)
    private final int item_reward_milk = 2020013; // トナカイの乳
    private final int item_reward_dawn = 2020014; // 暁の露
    private final int item_reward_dusk = 2020015; // 黄昏の露
    private final int item_reward_scroll_10 = 2040708; // 移動速度の書(靴)10%
    private final int item_reward_scroll_60 = 2040707; // 移動速度の書(靴)60%
    private final int item_reward_potion = 2000002; // 白い薬
    // クリアミッション (lua の clearmission2)
    // 翻訳
    private final String[] mission_asks = {
        "ちょっとお願いを聞いてくれない？#bパーティークエスト<女神の痕跡>#kを25分以内にクリアしてみて。僕、お腹ペコペコなんだ。できたら、追加の経験値をあげるよ。",
        "ちょっとお願いを聞いてくれない？#bパーティークエスト<女神の痕跡>#kで、#b不思議な種#kを8個集めてきてくれないかな？\r\n食べたことのない食べ物なんてもうないと思ってたんだけど、この#b不思議な種#kの味は本当に想像もつかないんだ～すごく食べてみたいな。それと、僕に渡すのはパーティーリーダーじゃないとだめだからね。",
        "ちょっとお願いを聞いてくれない？#bパーティークエスト<女神の痕跡>#kで、#bレコード#kを集めてきてくれないかな？6枚だけでいいんだ。それと、僕に渡すのはパーティーリーダーじゃないとだめだからね。",
        "ちょっとお願いを聞いてくれない？女神の塔の#bルイネル#kを倒してくれないかな？",
        "ちょっとお願いを聞いてくれない？女神の塔の#bジュニアセルビオン、ジュニアライオナー、ジュニアグルピン#kを倒してくれないかな？"
    };
    // 翻訳
    private final String[] mission_notices = {
        "<ミッション> パーティークエスト<女神の痕跡>を25分以内にクリアしよう！",
        "<ミッション> 不思議な種を8個以上集めよう！",
        "<ミッション> いろんな曲のレコードを6枚集めよう！",
        "<ミッション> ルイネルを倒そう！",
        "<ミッション> ジュニアセルビオン、ジュニアライオナー、ジュニアグルピンを倒そう！"
    };
    // 翻訳
    private final String[] mission_accepts = {
        "よし！話している間にも時間は過ぎていくよ。ミッションをクリアしてね！",
        "よし、#b不思議な種#k8個だね～パーティーリーダーが全部渡してね！",
        "よし、#bレコード#k6枚だね～種類は何でもいいけど、パーティーリーダーが全部渡してね！",
        "よし！「1匹残らず」倒してね！",
        "よし！「1匹残らず」倒してね！"
    };
    private int last_status = 0;
    private int menu = -1;
    private int node = NODE_START;
    private int mission = 0;

    @Override
    public boolean action(int status, int answer) {
        if (hasPages()) {
            return nextPage();
        }
        // lua では会話開始時に好感度を初期化している
        if (getQuestData(this.quest_like).isEmpty()) {
            setQuestData(this.quest_like, this.like_default);
            setQuestData(this.quest_like_time, currentTime());
        }
        // 入口
        if (getMapId() == this.map_entrance) {
            switch (this.node) {
                case NODE_START -> {
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("よく考えて、決めたら話しかけてね。");
                        return false;
                    }
                    // lua の clearmission (ウィンキのお願い)
                    TacosFieldSet field_set = getFieldSet(this.field_set_name);
                    if (field_set != null && !field_set.getVar("cm").equals("1")) {
                        field_set.setVar("cm", "1");
                        if (field_set.getVar("cMission").isEmpty()) {
                            String like_data = getQuestData(this.quest_like);
                            int like = like_data.length() < 3 ? 0 : integer(like_data.substring(0, 3));
                            int v0 = random(1, 15);
                            if ((100 <= like && like < 200 && v0 <= 3) || (200 <= like && like < 300 && v0 <= 5) || (300 <= like && like <= 400 && v0 <= 8)) {
                                this.mission = random(1, this.mission_asks.length);
                                this.last_status = status;
                                this.node = NODE_MISSION;
                                askYesNo(this.mission_asks[this.mission - 1]);
                                return true;
                            }
                        }
                    }
                    this.last_status = status;
                    this.node = NODE_LEAVE;
                    // 翻訳
                    askYesNo("パーティークエストを途中でやめて、ここから出たいの？出たら、また最初からやり直しだよ…");
                    return true;
                }
                case NODE_MISSION -> {
                    TacosFieldSet field_set = getFieldSet(this.field_set_name);
                    if (status < this.last_status) {
                        if (field_set != null) {
                            field_set.setVar("cMission", "0");
                        }
                        // 翻訳
                        say("ああ、わかったよ。残念だけど、わかった。また別の機会にお願いするね～", false, true);
                    } else {
                        if (field_set != null) {
                            field_set.setVar("cMission", String.valueOf(this.mission));
                            field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, this.mission_notices[this.mission - 1]);
                        }
                        say(this.mission_accepts[this.mission - 1], false, true);
                    }
                    this.node = NODE_MISSION_END;
                    return true;
                }
                case NODE_MISSION_END -> {
                    this.last_status = status;
                    this.node = NODE_LEAVE;
                    // 翻訳
                    askYesNo("パーティークエストを途中でやめて、ここから出たいの？出たら、また最初からやり直しだよ…");
                    return true;
                }
                case NODE_LEAVE -> {
                    if (status < this.last_status) {
                        // 翻訳
                        say("よく考えて、決めたら話しかけてね。");
                        return false;
                    }
                    this.node = NODE_LEAVE_END;
                    // 翻訳
                    say("もう一度試してね～", false, true);
                    return true;
                }
                case NODE_LEAVE_END -> {
                    warp(this.map_exit, "st00");
                    return false;
                }
                default -> {
                    return false;
                }
            }
        }
        if (getMapId() != this.map_strange_tower) {
            return false;
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("お～、会えて嬉しいよ！女神の塔を冒険したいの？グループに戦士、魔法使い、盗賊、弓使いが各1名以上いるとウィンキの祝福をかけてあげるわ。");
                nt.addMenu(MENU_ENTER, "入場を申し込む。");
                nt.addMenu(MENU_TOWER, "女神の塔について聞く。");
                nt.addMenu(MENU_FOOD, "ウィンキに食べ物を渡す。");
                nt.addMenu(MENU_FEATHER, "女神の羽について聞く。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.menu = answer;
                switch (answer) {
                    case MENU_ENTER -> {
                        if (!isPartyBoss()) {
                            // 翻訳
                            say("ねえ、パーティーリーダーに話しかけてほしいんだ。ほかの人じゃだめだよ。");
                            return false;
                        }
                        // lua の premission (戦士, 魔法使い, 弓使い, 盗賊が各1名以上いる場合はランダムでウィンキの祝福)
                        int warrior = 0, magician = 0, archer = 0, thief = 0;
                        for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                            int job = member.getJob();
                            if (100 <= job && job < 200) {
                                warrior++;
                            } else if (200 <= job && job < 300) {
                                magician++;
                            } else if (300 <= job && job < 400) {
                                archer++;
                            } else if (400 <= job && job < 500) {
                                thief++;
                            }
                        }
                        int buff = (0 < warrior && 0 < magician && 0 < archer && 0 < thief) ? random(1, this.item_buffs.length) : 0;
                        int res;
                        // lua では FieldSet の入場条件で判定しているが、FieldSet のデータに条件がないため NPC で判定する
                        if (TacosFieldSet.getPartyMembers(getPlayer()).size() != this.party_member) {
                            res = TacosFieldSet.ENTER_PARTY_MEMBER;
                        } else {
                            res = TacosFieldSet.ENTER_SUCCESS;
                            for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                                if (member.getLevel() < this.level_min || this.level_max < member.getLevel()) {
                                    res = TacosFieldSet.ENTER_PARTY_LEVEL;
                                    break;
                                }
                            }
                            if (res == TacosFieldSet.ENTER_SUCCESS) {
                                res = enterFieldSetParty(this.field_set_name, 0);
                            }
                        }
                        // 翻訳
                        switch (res) {
                            case TacosFieldSet.ENTER_SUCCESS -> {
                                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                                // lua の givePartyBuff
                                if (0 < buff) {
                                    givePartyBuff(this.item_buffs[buff - 1]);
                                    if (field_set != null) {
                                        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, this.buff_messages[buff - 1]);
                                    }
                                }
                                if (field_set != null) {
                                    // 宝箱のリアクター
                                    // TODO : lua の setReactorState の第4引数 (0) の用途は不明
                                    int rand_a1 = random(1, 10);
                                    int rand_a2 = random(1, 10);
                                    int rand_a3 = random(1, 10);
                                    if (rand_a1 == 1 || rand_a1 == 2) {
                                        field_set.setReactorState(16, "party3_box01", 1);
                                    }
                                    if (rand_a2 == 3 || rand_a2 == 4) {
                                        field_set.setReactorState(19, "party3_box02", 1);
                                    }
                                    if (rand_a3 == 5 || rand_a3 == 6) {
                                        field_set.setReactorState(22, "party3_box03", 1);
                                    }
                                }
                                // lua の party3_takeawayitem
                                for (int item_id = this.item_takeaway_first; item_id <= this.item_takeaway_last; item_id++) {
                                    removeItemAll(item_id);
                                }
                                removeItemAll(this.item_takeaway_extra);
                            }
                            case TacosFieldSet.ENTER_NO_PARTY -> {
                                say("パーティーに入っていないね。これをするにはパーティーに入っていないとだめだよ！");
                            }
                            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                                say("パーティーが6人じゃないよ！6人必要なんだ！！");
                            }
                            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                                say("パーティーの中にレベル51～70ではない人がいるよ。パーティーをレベル範囲内にしてね！");
                            }
                            case TacosFieldSet.ENTER_USING -> {
                                say("すでに別のパーティーがクエストのクリアを目指して入っているよ。しばらくしてからもう一度試してね。");
                            }
                            default -> {
                                say("理由があって入れてあげられないんだ。しばらくしてからもう一度試してね。");
                            }
                        }
                        return false;
                    }
                    case MENU_TOWER -> {
                        // 翻訳
                        return sayPages(
                                "数日前、大きな嵐の後で、オルビスの塔の上にある#b女神ミネルバの像#kの後ろに、雲に覆われた新しい道が現れたんだ。雲が晴れると、謎めいた新しい塔が姿を現したんだよ。",
                                "僕たちが探している塔は、オルビスを陰で支えている存在、#b女神ミネルバ#kの塔だという結論に達したんだ。#b女神ミネルバ#kは塔のどこかに閉じ込められているに違いないよ。どうかな？塔を探検して、女神の痕跡を見つけてくれない？"
                        );
                    }
                    case MENU_FOOD -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("ねえ、何か食べるものある？！");
                        for (int i = 0; i < this.food_ids.length; i++) {
                            nt.addMenu(i, "#t" + this.food_ids[i] + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    default -> {
                        // TODO : 女神の羽について聞く
                        return false;
                    }
                }
            }
            case 2 -> {
                if (this.menu != MENU_FOOD || answer < 0 || this.food_ids.length <= answer) {
                    return false;
                }
                int food_index = answer;
                int food_id = this.food_ids[food_index];
                boolean ate = false;
                if (0 < getItemCount(food_id)) {
                    if (!exchange(0, food_id, -1)) {
                        // 翻訳
                        message(this.food_names[food_index] + "がないから食べられないよ。");
                        return false;
                    }
                    effectSoundUser(this.sound_eat);
                    ate = true;
                }
                // lua の feellike
                String time_current = currentTime();
                String time_last = getQuestData(this.quest_like_time);
                if (time_last.isEmpty()) {
                    time_last = time_current;
                }
                String like_data = getQuestData(this.quest_like);
                // lua では未設定の場合のみ初期化しているが、不正な値の場合も初期化する
                if (!like_data.matches("\\d{7}")) {
                    like_data = this.like_default;
                    setQuestData(this.quest_like, like_data);
                }
                int like = Integer.parseInt(like_data.substring(0, 3));
                if (!time_last.equals(time_current) && this.like_decay_time < compareTime(time_current, time_last)) {
                    like = (130 <= like) ? like - 30 : this.like_min;
                }
                setQuestData(this.quest_like_time, time_current);
                ArrayList<String> pages = new ArrayList<>();
                String foods;
                if (ate) {
                    int code = food_index + 1;
                    // lua の ate_food
                    int food1 = like_data.charAt(3) - '0';
                    int food2 = like_data.charAt(4) - '0';
                    int food3 = like_data.charAt(5) - '0';
                    int food4 = like_data.charAt(6) - '0';
                    String food_tag = "#b#t" + food_id + "##k";
                    int rate;
                    // 翻訳
                    if (food1 == code && food2 == code && food3 == code && food4 == code) {
                        pages.add(food_tag + "、また？これを食べるのは大好きだけど、ちょっと飽きてきたなあ…");
                        rate = this.food_rate_all[food_index];
                    } else if (food2 == code && food3 == code && food4 == code) {
                        pages.add(food_tag + "はおいしいけど、今は別のものが食べたいな。");
                        rate = this.food_rate_last3[food_index];
                    } else if (food3 == code && food4 == code) {
                        pages.add("おお～…やっぱり、この" + food_tag + "は本当においしいね！");
                        rate = this.food_rate_last2[food_index];
                    } else if (food4 == code) {
                        pages.add("おお～…やっぱり、この" + food_tag + "は本当においしいね！");
                        rate = this.food_rate_last1[food_index];
                    } else if (food1 != code && food2 != code && food3 != code) {
                        pages.add("うわあ、これって" + food_tag + "じゃない？？？新しい食べ物はいつでも大歓迎！");
                        rate = this.food_rate_new[food_index];
                    } else {
                        pages.add(this.food_texts_other[food_index]);
                        rate = this.food_rate_other[food_index];
                    }
                    if (random(1, 100) <= rate) {
                        // 翻訳
                        message("ウォンキーは" + this.food_eat_good[food_index] + "、とてもいい気分になった。");
                        if (random(1, 20) == 1) {
                            // lua の feellike_reward
                            // 翻訳
                            pages.add("おいしい食べ物をこんなにたくさんくれて、本当に感謝しているよ。大したものじゃないけど、僕にはもう必要ないから持っていって。インベントリに少なくとも1つ空きスロットがあるか確認してね！");
                            boolean rewarded = true;
                            if (100 <= like && like < 200) {
                                int num = random(1, 100);
                                if (num <= 20) {
                                    rewarded = exchange(0, this.item_reward_milk, 1);
                                } else if (num <= 40) {
                                    rewarded = exchange(0, this.item_reward_dawn, 1);
                                } else if (num <= 42) {
                                    rewarded = exchange(0, this.item_reward_scroll_10, 1);
                                } else if (num <= 60) {
                                    rewarded = exchange(0, this.item_reward_dusk, 1);
                                } else if (num <= 80) {
                                    rewarded = exchange(0, this.item_reward_potion, 5);
                                } else {
                                    rewarded = exchange(0, this.item_reward_potion, 10);
                                }
                            } else if (200 <= like && like < 300) {
                                int num = random(1, 50);
                                if (num <= 10) {
                                    rewarded = exchange(0, this.item_reward_milk, 1);
                                } else if (num <= 20) {
                                    rewarded = exchange(0, this.item_reward_dawn, 1);
                                } else if (num == 21) {
                                    rewarded = exchange(0, this.item_reward_scroll_10, 1);
                                } else if (num == 22) {
                                    rewarded = exchange(0, this.item_reward_scroll_60, 1);
                                } else if (num <= 30) {
                                    rewarded = exchange(0, this.item_reward_dusk, 1);
                                } else {
                                    rewarded = exchange(0, this.item_reward_potion, 10);
                                }
                            } else if (300 <= like && like <= 400) {
                                // lua の条件のまま (21 - 24 は暁の露の条件に含まれるため、移動速度の書は出ない)
                                int num = random(1, 100);
                                if (1 <= num && num <= 20) {
                                    rewarded = exchange(0, this.item_reward_milk, 1);
                                } else if (20 <= num && num <= 40) {
                                    rewarded = exchange(0, this.item_reward_dawn, 1);
                                } else if (21 <= num && num <= 22) {
                                    rewarded = exchange(0, this.item_reward_scroll_60, 1);
                                } else if (22 <= num && num <= 24) {
                                    rewarded = exchange(0, this.item_reward_scroll_10, 1);
                                } else if (25 <= num && num <= 50) {
                                    rewarded = exchange(0, this.item_reward_dusk, 15);
                                } else {
                                    rewarded = exchange(0, this.item_reward_potion, 10);
                                }
                            }
                            if (!rewarded) {
                                // 翻訳
                                pages.add("残念だけど、消費インベントリがいっぱいで報酬を受け取れないよ。次は空きを作っておいてね。");
                            }
                        }
                        like += 5;
                    } else {
                        // 翻訳
                        message("ウォンキーはおいしそうに" + this.food_eat_normal[food_index] + "。");
                        like += this.food_like[food_index];
                    }
                    foods = like_data.substring(4, 7) + code;
                } else {
                    // 翻訳
                    message("ウォンキーの機嫌が悪くなった。");
                    like = (103 <= like) ? like - 3 : this.like_min;
                    foods = like_data.substring(3, 7);
                    // 翻訳 (lua ではアイスキャンディーの場合も #t2022001# になっているが、選択した食べ物にする)
                    pages.add("何だって？持ってきてないのに、どうやって#t" + food_id + "#を食べろっていうの？僕をからかってるの？");
                }
                like = Math.max(this.like_min, Math.min(this.like_max, like));
                setQuestData(this.quest_like, like + foods);
                return sayPages(pages.toArray(new String[0]));
            }
            default -> {
            }
        }
        return false;
    }
}
