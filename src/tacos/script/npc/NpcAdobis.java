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
import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
public class NpcAdobis extends NpcScriptMan {

    private static final int MENU_STAGE1 = 0;
    private static final int MENU_STAGE2 = 1;
    private static final int MENU_STAGE3 = 2;
    private static final int MENU_ABOUT = 3;
    private static final int MENU_LAVA = 4;
    private final int quest_stage1 = 7000; // ジャクムダンジョン 1段階
    private final int quest_stage2 = 7001; // ジャクムダンジョン 2段階
    private final int quest_stage3 = 7002; // ジャクムダンジョン 3段階
    private final int quest_lava = 6273; // 溶岩の中心部
    private final int item_gold_tooth = 4000082; // ゾンビのなくした金歯
    private final int item_fire_ore = 4031061; // 火の原石かけら
    private final int item_breath_of_lava = 4031062; // 火山の息吹
    private final int item_eye_of_fire = 4001017; // 火の目
    private final int item_glass_bottle = 4001109; // 強化ガラス瓶
    private final int map_zakum_stage2 = 280020000; // 閉鉱 : 火山の息<1段階>
    private final int gold_tooth_count = 30;
    private final int eye_of_fire_count = 5;
    // 翻訳
    private final String[] says_stage1 = {
        "この旅は、一人では解けないクエストが果てしなく続く迷路になるだろう。だが、その挑戦を受ける覚悟があるなら、エルナスの長老の館にいる自分の職業の長に話しかけて、クエストを受けてくるのだ。",
        "クエストを受けたら、パーティーに参加するか自分でパーティーを作り、パーティーのリーダーに私に話しかけさせてクエストを始めるのだ。準備ができたら、パーティーのリーダーをここに来させて、私に話しかけさせてくれ。"
    };
    // 翻訳
    private final String[] says_about = {
        "どこから始めればいいかわからないのか？このクエストを行うには、自分の職業の長から承認を受けなければならない。正しい手続きを踏まずに誰かを入れて、後で叱られたくはないのでな。私が入れられるのは、承認を受けたメンバーだけで構成されたパーティーだけだ。",
        "レベルの順にクエストを完了すれば、ジャクムダンジョンのボスに会えるだろう。私が頼むアイテムを集めてくれば、それを生贄のアイテムにしてやる。生贄のアイテムを祭壇に置けば、お前が見に来たものを見られるはずだ。そのためには、まず廃鉱を調べて#b#t4001018##kを持ち帰ってくるのだ。",
        "そこでは、#b#t4001018##kのほかに、紙の書類も見つかるだろう。それを#b#p2032002##kに渡せば、火の鉱石のかけらと一緒に何か役に立つものがもらえるかもしれない。次に、溶岩地帯を越えて#b#t4031062##kを見つけるのだ。危険な道のりになるだろうが…生贄のアイテムを作るには欠かせないアイテムなのだ。",
        "#b#t4031062##kを手に入れたら、レベル1と2で手に入れた#b火の鉱石のかけら#kと#b#t4031062##kを精錬する必要がある。だが心配はいらない。私が精錬してやろう。それをすべて終えたら、あとはジャクムダンジョンのボスに会うだけだ。まったく簡単ではないだろうが…全力を尽くしてくれ。"
    };
    private int menu = -1;

    @Override
    public boolean action(int status, int answer) {
        String val1 = getPlayer().getQuest(MapleQuest.getInstance(this.quest_stage1)).getCustomData();
        String val2 = getPlayer().getQuest(MapleQuest.getInstance(this.quest_stage2)).getCustomData();
        String val3 = getPlayer().getQuest(MapleQuest.getInstance(this.quest_stage3)).getCustomData();
        switch (status) {
            case 0 -> {
                // 確認でいいえを選択した
                if (this.menu == MENU_STAGE2) {
                    // 翻訳
                    say("そうか…だが、もし気が変わったら私に話しかけてくれ。");
                    return false;
                }
                if (this.menu == MENU_STAGE3) {
                    // 翻訳
                    say("そうか…だが、#b#t" + this.item_eye_of_fire + "##kがなければ、ジャクムダンジョンのボスに会えないことは覚えておいてくれ。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし、君たちは十分にできそうだな。どの段階に挑戦してみるんだ？");
                nt.addMenu(MENU_STAGE1, "閉鉱洞窟の調査に行く。(1段階)");
                nt.addMenu(MENU_STAGE2, "ジャクムダンジョンを探査する。(2段階)");
                nt.addMenu(MENU_STAGE3, "製錬を要請する。(3段階)");
                nt.addMenu(MENU_ABOUT, "クエストに対する説明を聞く。");
                if (getPlayer().getQuestStatus(this.quest_lava) == 2) {
                    nt.addMenu(MENU_LAVA, "溶岩の中心部に入る。");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.menu = answer;
                switch (this.menu) {
                    case MENU_STAGE1 -> {
                        // TODO : パーティーのリーダーの場合は FieldSet (Party2) で入場する, 1日3回まで (quest 7006)
                    }
                    case MENU_STAGE2 -> {
                        // 翻訳
                        if (!"end".equals(val1)) {
                            if ("fail".equals(val1)) {
                                say("お前は1段階目の途中のようだな。レベル2に進む前に、まずこれをクリアしなければならない。先に1段階目をクリアしてくれ。");
                                return false;
                            }
                            say("まだ前の段階をクリアしていないようだな。次のレベルに進む前に、前の段階をクリアしてくれ。");
                            return false;
                        }
                        if ("s".equals(val2)) {
                            askYesNo("うーむ…お前は以前このクエストに挑戦して、途中であきらめたようだな。どうする？このレベルにもう一度挑戦するか？");
                            return true;
                        }
                        if ("end".equals(val2)) {
                            askYesNo("うーむ…お前は以前このレベルをすでにクリアしているな。もう一度報酬を受け取るには、レベル1からクエストをやり直す必要がある。そうでなければ、クエストはできるが報酬はもらえない。それでもこのレベルにもう一度挑戦するか？");
                            return true;
                        }
                        askYesNo("無事に1段階目をクリアしたな。だが、ジャクムダンジョンのボスに会うまでの道のりはまだ長い。さて、どうだ？次の段階に進む準備はできているか？");
                        return true;
                    }
                    case MENU_STAGE3 -> {
                        // 翻訳
                        if (!"end".equals(val2)) {
                            say("うーむ…まだ前の段階をクリアしていないようだな。次のレベルに進む前に、前の段階をクリアしてくれ。");
                            return false;
                        }
                        if ("s".equals(val3) || "s2".equals(val3)) {
                            if (getPlayer().getItemQuantity(this.item_gold_tooth, false) < this.gold_tooth_count) {
                                say("まだ#bゾンビの失われた金歯を" + this.gold_tooth_count + "個#k持っていないようだな。全部集めてくれば、それを精錬して特別なアイテムを作ってやれるかもしれん…");
                                return false;
                            }
                            say("ははは、心配するな、あっという間に作ってやる！", false, true);
                            return true;
                        }
                        if ("end".equals(val3)) {
                            askYesNo("うーむ…お前は以前#b#t" + this.item_eye_of_fire + "##kを精錬した者ではないか？では、何の用だ？また#b#t" + this.item_fire_ore + "##kと#b#t" + this.item_breath_of_lava + "##kを混ぜて#b#t" + this.item_eye_of_fire + "##kを作りたいのか？");
                            return true;
                        }
                        MapleQuest.getInstance(this.quest_stage3).forceStart(getPlayer(), getNpcId(), "s");
                        say("うーむ、#b#t" + this.item_fire_ore + "##kと#b#t" + this.item_breath_of_lava + "##kを混ぜれば、ボスを召喚する生贄として使うアイテム、#b#t" + this.item_eye_of_fire + "##kを作ることができる。問題は…（ゴホゴホ）見てのとおり、最近はひどく体調が悪くてな、動き回ってアイテムを集めるのが難しいんだ。それで…私のために#bゾンビの失われた金歯を" + this.gold_tooth_count + "個#k集めてきてもらえないか？何に使うのかは聞かないでくれよ…");
                        return false;
                    }
                    case MENU_LAVA -> {
                        // 翻訳
                        if (getPlayer().getQuestStatus(this.quest_lava) != 2) {
                            say("お前がそこへ行く理由はなさそうだな。");
                            return false;
                        }
                        if (!checkItem(this.item_glass_bottle)) {
                            say("#b#t" + this.item_glass_bottle + "##kがなければ、溶岩の中心部に入ることはできない。");
                            return false;
                        }
                        // TODO : FieldSet (S4frame) で入場する, 他の人が挑戦中の場合は "残念ながら、今はほかの誰かが中でクエストに挑んでいる。後でもう一度試してくれ。" (翻訳)
                        return false;
                    }
                    default -> {
                    }
                }
            }
            default -> {
            }
        }
        // 説明
        if (this.menu == MENU_STAGE1 || this.menu == MENU_ABOUT) {
            String[] says = this.menu == MENU_STAGE1 ? this.says_stage1 : this.says_about;
            int index = status - 1;
            if (index < 0 || says.length <= index) {
                return false;
            }
            boolean next = index + 1 < says.length;
            say(says[index], false, next);
            return next;
        }
        switch (status) {
            case 2 -> {
                switch (this.menu) {
                    case MENU_STAGE2 -> {
                        // TODO : 1日3回まで (quest 7007)
                        if (!"end".equals(val2)) {
                            MapleQuest.getInstance(this.quest_stage2).forceStart(getPlayer(), getNpcId(), "s");
                        }
                        // 翻訳
                        say("よし！ここからは、障害物がたくさんあるマップへ移動してもらう。マップの一番奥に一人の女性が立っているから、彼女に話しかければ、ジャクムダンジョンのボスを召喚するアイテムの材料となるアイテムが見つかるだろう。そのアイテムを持ってきてくれ。幸運を祈る！", false, true);
                        return true;
                    }
                    case MENU_STAGE3 -> {
                        if ("end".equals(val3)) {
                            MapleQuest.getInstance(this.quest_stage3).forceStart(getPlayer(), getNpcId(), "s2");
                            // 翻訳
                            say("うーむ、#b#t" + this.item_fire_ore + "##kと#b#t" + this.item_breath_of_lava + "##kを混ぜれば、ボスを召喚する生贄として使うアイテム、#b#t" + this.item_eye_of_fire + "##kを作ることができる。問題は…（ゴホゴホ）見てのとおり、最近はひどく体調が悪くてな、動き回ってアイテムを集めるのが難しいんだ。それで…私のために#bゾンビの失われた金歯を" + this.gold_tooth_count + "個#k集めてきてもらえないか？何に使うのかは聞かないでくれよ…");
                            return false;
                        }
                        if (getPlayer().getItemQuantity(this.item_gold_tooth, false) < this.gold_tooth_count || !checkItem(this.item_fire_ore) || !checkItem(this.item_breath_of_lava) || !MapleInventoryManipulator.checkSpace(getPlayer(), this.item_eye_of_fire, this.eye_of_fire_count, "")) {
                            // 翻訳
                            say("うーむ…#r火の目#kを作るのに必要なアイテムを本当に全部持っているか？持っているなら、ETCインベントリがいっぱいでないか確認してくれ。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_gold_tooth, this.gold_tooth_count, true, false);
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_fire_ore, 1, true, false);
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_breath_of_lava, 1, true, false);
                        MapleInventoryManipulator.addById(getPlayer(), this.item_eye_of_fire, (short) this.eye_of_fire_count);
                        MapleQuest.getInstance(this.quest_stage3).forceStart(getPlayer(), getNpcId(), "end");
                        // 翻訳
                        say("これだ。これで左側の扉が開いているときに、ジャクムダンジョンの祭壇に入れるようになる。扉を通ってステージに入るには、#b#t" + this.item_eye_of_fire + "##kを持っている必要がある。さて、何人がここに入れるかな…？");
                        return false;
                    }
                    default -> {
                    }
                }
            }
            case 3 -> {
                if (this.menu == MENU_STAGE2) {
                    warp(this.map_zakum_stage2);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
