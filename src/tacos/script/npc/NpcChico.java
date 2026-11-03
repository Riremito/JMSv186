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
public class NpcChico extends NpcScriptMan {

    private static final int MENU_MAKE = 0;
    private static final int MENU_ABOUT = 1;
    private static final int MENU_OMOK = 0;
    private static final int MENU_MATCH_CARDS = 1;
    // 五目並べセット, 材料1 (99個), 材料2 (99個), 材料3 (1個)
    private final int[][] omok_sets = {
        {4080006, 4030013, 4030014, 4030009}, // 五目並べセット(ブロックパス、ピンクックマ)
        {4080007, 4030013, 4030016, 4030009}, // 五目並べセット(ブロックパス、トリックスター)
        {4080008, 4030014, 4030016, 4030009}, // 五目並べセット(ピンクックマ、トリックスター)
        {4080009, 4030015, 4030013, 4030009}, // 五目並べセット(クロワーパンダ、ブロックパス)
        {4080010, 4030015, 4030014, 4030009}, // 五目並べセット(クロワーパンダ、ピンクックマ)
        {4080011, 4030015, 4030016, 4030009} // 五目並べセット(クロワーパンダ、トリックスター)

    };
    private final int item_match_cards = 4080100; // 神経衰弱セット
    private final int item_monster_card = 4030012; // モンスターカード
    private final int material_count = 99;
    // ミニゲームの説明 (翻訳)
    private final String[][] rules = {
        {
            "これが五目並べのルールだよ。よく聞いてね。五目並べは、自分と相手が交互に盤に石を置いていって、横、斜め、縦のどれかに5つ並べた人が勝ちになるゲームなんだ。まず、ゲームルームを開けるのは#b五目並べセット#kを持っている人だけだよ。",
            "五目並べは1ゲームにつき#r100メル#kかかるよ。#b五目並べセット#kを持っていなくても、ルームに入って遊ぶことはできるんだ。でも、100メル持っていなければ、ルームには絶対に入れないよ。ルームを開く人も100メル持っていないとゲームはできないんだ。ゲーム中にメルがなくなると、自動的にルームから追い出されちゃうよ！",
            "ルームに入って、遊ぶ準備ができたら#b準備完了#kをクリックしてね。訪問者が#b準備完了#kをクリックしたら、ルームのオーナーは#b開始#kを押してゲームを始められるよ。来てほしくない訪問者がいたら、ルームのオーナーには追い出す権利があるんだ。その人の右側にxと書かれた四角があるから、それをクリックすればさようならさ、いい？",
            "ゲームが始まったら、#bルームのオーナーが先攻#kだよ。制限時間があって、時間内に打たないと順番を失っちゃうから気をつけてね。基本的に三三は禁止だけど、どうしてもそこに置かないと負けるという場合は置いてもいいんだ。三三は最後の防衛手段として認められているよ！ああ、それと#r6つや7つ並び#kはカウントされないよ。5つだけ！",
            "追い詰められたら、#b待った#kを頼めるよ。相手が受け入れてくれたら、自分と相手の最後の一手が取り消されるんだ。トイレに行きたくなったり、長く休みたくなったりしたら、#b引き分け#kを申し込めるよ。相手が受け入れれば、ゲームは引き分けで終わるんだ。友情を保つにはいい方法かもしれないね。",
            "ゲームが終わると新しいゲームが始まって、負けた人が先攻になるよ。ああ、それとゲームの途中で抜けることはできないんだ。抜けたいときは、#b投了か引き分け#kを申し込まなきゃいけないよ。もちろん、投了すればそのゲームは負けになるから気をつけてね。それに、対局中に「退出」をクリックして退出を申し込むと、ゲームが終わったらすぐに退出できるんだ。だから、こっちの方がずっと便利な抜け方だよ。"
        },
        {
            "これがカード合わせのルールだよ。よく聞いてね。カード合わせはその名のとおり、テーブルに並べられたカードの中からペアを見つけるゲームなんだ。すべてのペアが見つかったら、一番多くペアを取った人が勝ちだよ。五目並べと同じで、ゲームルームを開くには#b#t4080100##kが必要なんだ。",
            "カード合わせは1ゲームにつき#r100メル#kかかるよ。#b#t4080100##kを持っていなくても、ルームに入って遊ぶことはできるんだ。でも、100メル持っていなければ、ルームには絶対に入れないよ。ルームを開く人も100メル持っていないとゲームはできないんだ。ゲーム中にメルがなくなると、自動的にルームから追い出されちゃうよ！",
            "ルームに入って、遊ぶ準備ができたら#b準備完了#kをクリックしてね。訪問者が#b準備完了#kをクリックしたら、ルームのオーナーは#b開始#kを押してゲームを始められるよ。来てほしくない訪問者がいたら、ルームのオーナーには追い出す権利があるんだ。その人の右側にxと書かれた四角があるから、それをクリックすればさようならさ、いい？",
            "ああ、それと五目並べと違って、カード合わせではゲームルームを作るときに、ゲームで使うカードの枚数を決めなきゃいけないんだ。3x4、4x5、5x6の3つのモードがあって、それぞれ12枚、20枚、30枚のカードを使うよ。ルームを開いた後は変更できないから気をつけてね。どうしても変えたいなら、ルームを閉じて新しく開き直さなきゃいけないよ。",
            "ゲームが始まったら、#bルームのオーナーが先攻#kだよ。制限時間があって、時間内にめくらないと順番を失っちゃうから気をつけてね。自分の番でペアを見つけたら、ペアを見つけ続ける限りもう一度めくれるよ。記憶力を使って、怒涛の連続ターンを決めよう。",
            "自分と相手が同じ数のペアを取った場合は、連続で取った回数が多い方が勝ちになるよ。トイレに行きたくなったり、長く休みたくなったりしたら、#b引き分け#kを申し込めるよ。相手が受け入れれば、ゲームは引き分けで終わるんだ。友情を保つにはいい方法かもしれないね。",
            "ゲームが終わると新しいゲームが始まって、負けた人が先攻になるよ。ああ、それとゲームの途中で抜けることはできないんだ。抜けたいときは、#b投了か引き分け#kを申し込まなきゃいけないよ。もちろん、投了すればそのゲームは負けになるから気をつけてね。それに、対局中に「退出」をクリックして退出を申し込むと、ゲームが終わったらすぐに退出できるんだ。だから、こっちの方がずっと便利な抜け方だよ。"
        }

    };
    private int menu = -1;
    private int game = -1;
    private int set_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは！俺はこの周辺のミニゲームを統括している#b#p" + getNpcId() + "##kだよ。君、ミニゲームに関心があるようだな。 助けてあげようか。じゃ、やりたいことは？"
);
                nt.addMenu(MENU_MAKE, "ミニゲームアイテム作成");
                nt.addMenu(MENU_ABOUT, "ミニゲームに対する説明を聞く");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.menu = answer;
                NpcScriptTag nt = new NpcScriptTag();
                switch (this.menu) {
                    case MENU_MAKE -> {
                        // 翻訳
                        nt.add("ミニゲームのアイテムを作りたいの？ミニゲームはいきなり始められるものじゃないんだ。ミニゲームごとに特定のアイテムが必要なんだよ。どのミニゲームのアイテムを作りたい？");
                        nt.addMenu(MENU_OMOK, "五目並べセット");
                        nt.addMenu(MENU_MATCH_CARDS, "#t" + this.item_match_cards + "#");
                    }
                    case MENU_ABOUT -> {
                        // 翻訳
                        nt.add("ミニゲームについてもっと知りたいの？いいね！何でも聞いて。どのミニゲームについて知りたい？");
                        nt.addMenu(MENU_OMOK, "五目並べ");
                        nt.addMenu(MENU_MATCH_CARDS, "カード合わせ");
                    }
                    default -> {
                        return false;
                    }
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.rules.length <= answer) {
                    return false;
                }
                this.game = answer;
            }
            default -> {
            }
        }
        // ミニゲームの説明
        if (this.menu == MENU_ABOUT) {
            int index = status - 2;
            if (this.rules[this.game].length <= index) {
                return false;
            }
            boolean next = index + 1 < this.rules[this.game].length;
            say(this.rules[this.game][index], false, next);
            return next;
        }
        // 神経衰弱セットの作成 (翻訳)
        if (this.game == MENU_MATCH_CARDS) {
            switch (status) {
                case 2 -> {
                    boolean has_materials = this.material_count <= getPlayer().getItemQuantity(this.item_monster_card, false);
                    say("#b#t" + this.item_match_cards + "##kがほしいの？うーん…#t" + this.item_match_cards + "#を作るには、#b#t" + this.item_monster_card + "##kがいくつか必要なんだ。#t" + this.item_monster_card + "#は島のモンスターを倒すと手に入るよ。#t" + this.item_monster_card + "#を" + this.material_count + "個集めたら、#t" + this.item_match_cards + "#のセットを作れるよ。", false, has_materials);
                    return has_materials;
                }
                case 3 -> {
                    if (getPlayer().getItemQuantity(this.item_monster_card, false) < this.material_count || !MapleInventoryManipulator.checkSpace(getPlayer(), this.item_match_cards, 1, "")) {
                        say("本当に#r#t" + this.item_monster_card + "#を" + this.material_count + "個#k集めた？集めたなら、ETCインベントリがいっぱいでないか確認してね。");
                        return false;
                    }
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_monster_card, this.material_count, true, false);
                    MapleInventoryManipulator.addById(getPlayer(), this.item_match_cards, (short) 1);
                    say("はい、#b#t" + this.item_match_cards + "##kだよ！「カード合わせ」ルームはゲーム内のほとんどどこでも遊べるよ。ここでルームを開いて、たくさんのほかのユーザーと遊んでね。素晴らしい成績を残したら、何かいいことがあるかもしれないよ。応援してるから、遊んできて！", false, true);
                    return true;
                }
                case 4 -> {
                    say("ああ、カード合わせについて何か質問があったら、気軽に聞いてね。僕はしばらくここにいるから。さあ、ミニゲームで僕に勝てると思えるまで、ほかのユーザーと対戦して練習してね。まあ、そんな日は絶対に来ないけどね、はは…じゃあ、またね～");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 五目並べセットの作成 (翻訳)
        switch (status) {
            case 2 -> {
                say("#b五目並べ#kがしたいんだね？遊ぶには五目並べセットが必要なんだ。このアイテムを持っている人だけが、五目並べのゲームルームを開けるんだよ。市場の一部の場所を除けば、ほとんどどこでも遊べるよ。", false, true);
                return true;
            }
            case 3 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ゲームでどの駒を使いたいかによって、セットも違うんだ。どのセットを作りたい？");
                for (int i = 0; i < this.omok_sets.length; i++) {
                    nt.addMenu(i, "#t" + this.omok_sets[i][0] + "#");
                }
                askMenu(nt);
                return true;
            }
            case 4 -> {
                if (answer < 0 || this.omok_sets.length <= answer) {
                    return false;
                }
                this.set_index = answer;
                int[] set = this.omok_sets[this.set_index];
                boolean has_materials = this.material_count <= getPlayer().getItemQuantity(set[1], false) && this.material_count <= getPlayer().getItemQuantity(set[2], false) && checkItem(set[3]);
                say("#b#t" + set[0] + "##kを作りたいのかい？うーん…材料を集めてきてくれたら作ってあげるよ。よく聞いてね、必要な材料は：#r #t" + set[1] + "#, " + this.material_count + " #t" + set[2] + "#, 1 #t" + set[3] + "##kだよ。モンスターがときどき落とすと思うよ…", false, has_materials);
                return has_materials;
            }
            case 5 -> {
                int[] set = this.omok_sets[this.set_index];
                if (getPlayer().getItemQuantity(set[1], false) < this.material_count || getPlayer().getItemQuantity(set[2], false) < this.material_count || !checkItem(set[3]) || !MapleInventoryManipulator.checkSpace(getPlayer(), set[0], 1, "")) {
                    say("本当に#b#t" + set[1] + "#, #t" + set[2] + "#, #t" + set[3] + "##kを集めた？集めたなら、ETCインベントリがいっぱいでないか確認してね。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, set[1], this.material_count, true, false);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, set[2], this.material_count, true, false);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, set[3], 1, true, false);
                MapleInventoryManipulator.addById(getPlayer(), set[0], (short) 1);
                say("はい、#b#t" + set[0] + "##kだよ！ゲーム内のほとんどどこでも五目並べルームを開いて、ほかのメイプルユーザーと対戦して楽しめるよ。すごい成績を残したら、何かいいことがあるかもしれないよ。応援してるから、遊んできて！", false, true);
                return true;
            }
            case 6 -> {
                say("ああ、五目並べについて何か質問があったら、気軽に聞いてね。僕はしばらくここにいるから。さあ、ミニゲームで僕に勝てると思えるまで、ほかのユーザーと対戦して練習してね。まあ、そんな日は絶対に来ないけどね、ははは。じゃあ、またね～");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
