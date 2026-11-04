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
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class Npc_Ellinia_MarTheFairy extends NpcScriptMan {

    private final int quest_water_of_life = 2049; // 妖精‘マル’と生命の水
    private final int item_water_of_life = 5180000; // 生命の水
    private final int item_scroll_of_life = 4031034; // 生命の呪文書
    private final int item_pet_ap_reset = 4160011; // ペット能力値移転書
    private final int npc_kroi = 1012005; // クロイ
    private final int npc_bartos = 1012006; // バルトス
    private final int map_ellinia = 101000000; // エリニア : エリニア
    private final int map_henesys = 100000000; // ヘネシス : ヘネシス
    private final int MENU_REVIVE = 0;
    private final int MENU_TRANSFER = 1;
    private int quest_state = 0;
    private boolean asked_doll = false;
    private boolean asked_collect = false;
    private boolean asked_revive = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("私は妖精のマー。ペットを生き返らせたり、今あるEXPを別のペットに移したりできるわ。 \r\n");
                // 翻訳
                nt.addMenu(MENU_REVIVE, "人形をもう一度ペットに戻してほしいです。 ");
                // 翻訳
                nt.addMenu(MENU_TRANSFER, "ペットの今あるEXPを新しいペットに移したいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.asked_revive) {
                    // 復活の確認でいいえを選択した
                    // 翻訳
                    say("そう…まだ心の準備ができていないのね？そのペットを人形のままにしておくつもりじゃないわよね？気が変わったらまた来てね…");
                    return false;
                }
                if (answer == MENU_TRANSFER) {
                    if (getPetsInInventory().isEmpty() || !checkItem(this.item_pet_ap_reset)) {
                        // 翻訳
                        say("#t" + this.item_pet_ap_reset + "#も、親密度を移すペットも持っていないみたいね…  ヘネシスのクロイなら、#t" + this.item_pet_ap_reset + "#について絶対に知っているはずよ…");
                        return false;
                    }
                    // TODO : ペットの親密度の移し替え (askPetAllExcept, movePetStat)
                    return false;
                }
                if (answer != MENU_REVIVE) {
                    return false;
                }
                // TODO : 本来は命が尽きて人形になったペットのみが対象
                // 文章が空の場合はクライアントが先頭のペットを自動で選択する
                askPet("", getPetsInInventory());
                return true;
            }
            case 2 -> {
                if (this.asked_doll) {
                    // 人形の確認でいいえを選択した
                    // 翻訳
                    say("でも、確かに#p" + this.npc_kroi + "#が作ったものに似ているわね。ああ…まあ、いいわ。私は何年も#p" + this.npc_kroi + "#を見てきたけど、彼が人形に命を与える魔法に成功できるはずがないと思っているの。さて、それじゃあ…");
                    return false;
                }
                this.quest_state = getPlayer().getQuestStatus(this.quest_water_of_life);
                if (getAnswerPet() == null) {
                    if (this.quest_state == 2) {
                        // 翻訳
                        say("こんにちは…新しい命を得たペットの調子はどう？あなたがペットと幸せそうにしているのを見ると、とてもうれしいわ。それじゃあ…研究に戻らなくちゃ…");
                        return false;
                    }
                    // 翻訳
                    say("こんにちは、私は#p" + getNpcId() + "#、ここ#m" + this.map_ellinia + "#でいろいろな魔法を研究しているの。命の魔法を何百年も研究してきたけど、終わりがないのよね…それじゃあ、研究に戻らなくちゃ…");
                    return false;
                }
                if (this.quest_state == 1) {
                    if (!checkItem(this.item_water_of_life) || !checkItem(this.item_scroll_of_life)) {
                        // 翻訳
                        say("まだ#b#t" + this.item_water_of_life + "##kと#b#t" + this.item_scroll_of_life + "##kを手に入れていないのね？#m" + this.map_henesys + "#の#b#p" + this.npc_bartos + "##kに会いに行って。その人ならその書について知っているはずよ。早くアイテムを集めてきてね…");
                        return false;
                    }
                    this.asked_revive = true;
                    // 翻訳
                    askYesNo("#b#t" + this.item_water_of_life + "##kと#b#t" + this.item_scroll_of_life + "##kを手に入れたのね…これがあれば、私の魔力で人形を生き返らせることができるわ。どうする？アイテムを使って、ペットを目覚めさせる…？");
                    return true;
                }
                // 翻訳
                say("はじめまして！私は#p" + getNpcId() + "#、ここ#m" + this.map_ellinia + "#でいろいろな魔法を研究しているの。特に命の魔法に夢中なのよ。終わりのない神秘、命と呼ばれる神秘…私は命を作り出す方法を見つけようとしているの。", false, true);
                return true;
            }
            case 3 -> {
                if (this.quest_state == 1) {
                    // 翻訳
                    askPet("それで、どのペットを生き返らせたいの？一番生き返らせたいペットを選んで…", getPetsInInventory());
                    return true;
                }
                this.asked_doll = true;
                // 翻訳
                askYesNo("もう#p" + this.npc_kroi + "#に会ったみたいね。#p" + this.npc_kroi + "#は私と一緒に命の魔法を研究していた人よ。不完全な命の魔法を人形に使って、生きた動物を作ったと聞いたわ…あなたが持っている人形は、#p" + this.npc_kroi + "#が作った、#bペット#kと呼ばれるものと同じなの？");
                return true;
            }
            case 4 -> {
                if (this.quest_state == 1) {
                    TacosPet pet = getAnswerPet();
                    if (pet == null || !checkItem(this.item_water_of_life) || !checkItem(this.item_scroll_of_life)) {
                        // 翻訳
                        say("何かがおかしいわ…本当に#b#t" + this.item_water_of_life + "##kと#b#t" + this.item_scroll_of_life + "##kを持っている？その2つがないと、人形をペットに戻すことはできないのよ。");
                        return false;
                    }
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.CASH, this.item_water_of_life, 1, true, false);
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_scroll_of_life, 1, true, false);
                    // TODO : ペットの命の期限を延長する (setPetLife)
                    MapleQuest.getInstance(this.quest_water_of_life).forceComplete(getPlayer(), getNpcId());
                    // 翻訳
                    say("あなたの人形はペットに戻ったわ。でも、私の魔法は完璧じゃないの。だから、永遠の命は約束できない…#t" + this.item_water_of_life + "#が尽きる前に、そのペットを大切にしてあげてね。それじゃあ…さようなら…");
                    return false;
                }
                // 翻訳
                say("なるほど。人形が生きたペットになったのね…でも、#p" + this.npc_kroi + "#がペットに命を与えるのに使ったアイテム、#b#t" + this.item_water_of_life + "##kが尽きて、人形に戻ってしまった…今は人形だから、もちろん動かないわ…うーん…やっぱり命というものは、魔法で作り出せるものじゃないのかしら…？", false, true);
                return true;
            }
            case 5 -> {
                if (this.asked_collect) {
                    // アイテム集めの確認でいいえを選択した
                    // 翻訳
                    say("人形をそのままにしておきたいの？人形とはいえ…これで思い出を消すのも難しいでしょうね。後悔したら、また来てね？");
                    return false;
                }
                // 翻訳
                say("人形を命があったころのように戻したいの？ペットがあなただけに従って、そばにいてくれたころに戻りたいのよね？もちろん、十分可能よ。私は#p" + this.npc_kroi + "#と一緒に命の魔法を研究した妖精だもの…もしかしたら、また動かせるかもしれないわ…", false, true);
                return true;
            }
            case 6 -> {
                this.asked_collect = true;
                NpcScriptTag nt = new NpcScriptTag();
                // 原文ママ
                nt.add("#b生命の水#kと#b生命の呪文書#kさえあればその人形を何とか以前のように動く動物にすることができるかもしれません、どうでしょう…アイテムを集めてきますか？アイテムさえ持って来てくださったら喜んでその人形を元どおりにしてあげますよ…");
                askYesNo(nt);
                return true;
            }
            case 7 -> {
                MapleQuest.getInstance(this.quest_water_of_life).forceStart(getPlayer(), getNpcId(), "");
                // 翻訳
                say("わかったわ。もう一度言うけど、私に必要なのは#b#t" + this.item_water_of_life + "##kと#b#t" + this.item_scroll_of_life + "##kが1つずつよ。それを手に入れてくれたら、人形を生き返らせてあげられるわ。#b#t" + this.item_scroll_of_life + "##kが一番手に入りにくいの…#bヘネシス#kの#b#p" + this.npc_bartos + "##kを訪ねてみたらどう？その人がヒントをくれるかもしれないわ…");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
