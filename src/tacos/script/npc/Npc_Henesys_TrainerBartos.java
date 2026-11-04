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

/**
 *
 * @author Riremito
 */
// Npc.wz script : pet_lifeitem (JMS131-302), 会話文は lua_script_jp (pet_lifeitem) を利用
public class Npc_Henesys_TrainerBartos extends NpcScriptMan {

    private final int quest_water_of_life = 2049; // 妖精‘マル’と生命の水
    private final int item_bartos_letter = 4031035; // バルトスの手紙
    private final int item_scroll_of_life = 4031034; // 生命の呪文書
    private final int item_water_of_life = 5180000; // 生命の水
    private final int npc_frod = 1012007; // プロド
    private final int npc_mar = 1032102; // 妖精マル
    private final int map_ellinia = 101000000; // エリニア : エリニア
    private final int MENU_INFO = 0;
    private final int MENU_SCROLL = 1;
    private final int ANSWER_1 = 1;
    private final int ANSWER_2 = 2;
    private final int ANSWER_3 = 3;
    private final int ANSWER_4 = 1;
    private final int ANSWER_5 = 2;
    private boolean has_pet = false;
    private int menu = 0;
    private boolean asked_train = false;
    private boolean asked_test = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 本来は命が尽きて人形になったペットのみが対象
                // 文章が空の場合はクライアントが先頭のペットを自動で選択する
                askPet("", getPetsInInventory());
                return true;
            }
            case 1 -> {
                if (this.asked_train) {
                    // 訓練の確認でいいえを選択した
                    // 翻訳
                    say("うーん…今は忙しくてそれどころじゃない？とにかく、やりたくなったらまた声をかけてね。");
                    return false;
                }
                this.has_pet = getAnswerPet() != null;
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("僕に何か用？ ");
                // 翻訳
                nt.addMenu(MENU_INFO, "この場所について教えてください。 ");
                // 翻訳
                nt.addMenu(MENU_SCROLL, "命の書のことで来ました。 ");
                // 原文ママ (lua のメニュー番号の重複)
                nt.addMenu(MENU_SCROLL, "…");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                this.menu = answer;
                if (answer == MENU_INFO) {
                    if (checkItem(this.item_bartos_letter)) {
                        // 翻訳
                        say("その手紙を持って、ペットと一緒に障害物を飛び越えて、手紙を兄の#p" + this.npc_frod + "#に届けて。手紙を渡せば、きっとペットにいいことが起こるよ。");
                        return false;
                    }
                    this.asked_train = true;
                    // 翻訳
                    askYesNo("ここはペットと一緒に散歩できる道なんだ。ただ道を歩くこともできるし、ペットに障害物を越える訓練をさせることもできるよ。まだペットとあまり仲良くなっていないと、ペットがあまり言うことを聞いてくれないから困るかもしれないけど…どう？ペットを訓練してみる？");
                    return true;
                }
                if (answer != MENU_SCROLL) {
                    return false;
                }
                if (!this.has_pet || getPlayer().getQuestStatus(this.quest_water_of_life) != 1) {
                    // 翻訳
                    say("ねえ、本当に#b#p" + this.npc_mar + "##kに会ったの？会ったことがないなら嘘をつかないでよ、ばればれなんだから。下手な嘘だなあ！");
                    return false;
                }
                if (checkItem(this.item_scroll_of_life)) {
                    // 翻訳
                    say("うーん…もう#b#t" + this.item_scroll_of_life + "##kを持っているね。この書を#m" + this.map_ellinia + "#の#b#p" + this.npc_mar + "##kのところへ持っていって。");
                    return false;
                }
                // 翻訳
                say("#b動かない#bペット#kを連れて来たの？見ていて悲しいな…え？#b#p" + this.npc_mar + "##kから聞いて来たって？なるほど…#b#t" + this.item_scroll_of_life + "##kか…おいおい～僕が持っているわけ…あれ、ポケットの中のこれは何だ？", false, true);
                return true;
            }
            case 3 -> {
                if (this.menu == MENU_INFO) {
                    if (!exchange(0, this.item_bartos_letter, 1)) {
                        // 翻訳
                        say("ETCインベントリがいっぱいだよ！インベントリに空きがないと手紙を渡せないんだ。スロットを1つ空けてから話しかけてね。");
                        return false;
                    }
                    // 翻訳
                    say("よし、これが手紙だよ。まっすぐ行ったら、僕が送ったってわからないだろうからね。ペットと一緒に障害物を越えて、一番上まで行ったら、#p" + this.npc_frod + "#に話しかけて手紙を渡して。障害物を越えるときにペットに気を配っていれば、難しくないよ。頑張ってね！");
                    return false;
                }
                if (this.asked_test) {
                    // テストの確認でいいえを選択した
                    // 翻訳
                    say("何だって？！もうあきらめるの？ペットをちゃんと育てていれば、こんなの楽勝なのに！気が変わったらまた来てね。");
                    return false;
                }
                // 翻訳
                say("うわっ！！！こ…これは#b#t" + this.item_scroll_of_life + "##k？ああ、そうか…#p1012005#がたぶん僕の服を勝手に着て出かけたんだな…まったく！人の服を勝手に着るなって言ったのに…まあ、どうせ僕のものじゃないし…これが必要なの？うーん…", false, true);
                return true;
            }
            case 4 -> {
                this.asked_test = true;
                // 翻訳
                askYesNo("ただであげるわけにはいかないな！ペット全般についての知識を試させてもらうよ。飼い主が世話の仕方も知らないなんて、ペットにとってはひどいことだからね。全問正解しないと書はあげられないよ。どうする？テストを受ける？");
                return true;
            }
            case 5 -> {
                // 翻訳
                say("よし！5問あって、全部正解しなきゃいけないよ！準備はいい？いくよ！", false, true);
                return true;
            }
            case 6 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("第1問）#t2120000#を売っている#p1012004#がいる町はどこ？ ");
                nt.addMenu(0, " #m104000000#");
                nt.addMenu(1, " #m100000000#");
                nt.addMenu(2, " #m102000000#");
                nt.addMenu(3, " #m101000000#");
                nt.addMenu(4, " #m103000000#");
                nt.addMenu(5, " #m105040300#");
                askMenu(nt);
                return true;
            }
            case 7 -> {
                if (answer != this.ANSWER_1) {
                    // 翻訳
                    say("不正解！本当に何も知らないんだね…本当にペットを育てたことがあるの？ひどいなあ！");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("第2問）はは…今のは練習だよ！じゃあ…この中から、ペットとまったく関係のない人を選んで。 ");
                nt.addMenu(0, " #p1032102#");
                nt.addMenu(1, " #p1012005#");
                nt.addMenu(2, " #p1012101#");
                askMenu(nt);
                return true;
            }
            case 8 -> {
                if (answer != this.ANSWER_2) {
                    // 翻訳
                    say("不正解！本当に何も知らないんだね…本当にペットを育てたことがあるの？ひどいなあ！");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("第3問）簡単すぎた？よし、ペットについての説明のうち、正しくないものを選んで。 ");
                // 翻訳
                nt.addMenu(0, "ペットに名前をつけるには、ペットの命名アイテムが必要。 ");
                // 翻訳
                nt.addMenu(1, "ペットにコマンドを与えて従うと、親密度が上がることがある。 ");
                // 翻訳
                nt.addMenu(2, "ペットにちゃんと餌をやらないと、親密度が下がる。 ");
                // 翻訳
                nt.addMenu(3, "ペットは飼い主と一緒にモンスターを攻撃できる。#k");
                askMenu(nt);
                return true;
            }
            case 9 -> {
                if (answer != this.ANSWER_3) {
                    // 翻訳
                    say("不正解！本当に何も知らないんだね…本当にペットを育てたことがあるの？ひどいなあ！");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("第4問）あと2問！さて、ペットが人間の言葉を使い始めるのはどのレベルから？ ");
                nt.addMenu(0, "#e1. #n#bレベル5 #k");
                nt.addMenu(1, "#e2. #n#bレベル10 #k");
                nt.addMenu(2, "#e3. #n#bレベル15 #k");
                nt.addMenu(3, "#e4. #n#bレベル20#k");
                askMenu(nt);
                return true;
            }
            case 10 -> {
                if (answer != this.ANSWER_4) {
                    // 翻訳
                    say("不正解！本当に何も知らないんだね…本当にペットを育てたことがあるの？ひどいなあ！");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("第5問）最後の問題！#m100000000#の#p1012004#は#t2120000#を売っている。これで満腹度はどれだけ上がる？ ?");
                nt.addMenu(0, " 10");
                nt.addMenu(1, " 20");
                nt.addMenu(2, " 30");
                nt.addMenu(3, " 40");
                askMenu(nt);
                return true;
            }
            case 11 -> {
                if (answer != this.ANSWER_5) {
                    // 翻訳
                    say("ああ、だめ！！！もったいない！最後の問題だったのに！あきらめないで！");
                    return false;
                }
                // 翻訳
                say("正解！うーん…ペットについてよく知っているね。いいよ、そんなに詳しいなら喜んで書をあげるよ。僕のものじゃないけど…大事なものを入れたまま他人の服を着ていく人なんている？はい、どうぞ！", false, true);
                return true;
            }
            case 12 -> {
                if (!exchange(0, this.item_scroll_of_life, 1)) {
                    // 翻訳
                    say("あらら…ETCインベントリに空きスロットはある？いっぱいだと渡せないよ。");
                    return false;
                }
                // 翻訳
                say("よし…あとはこれを持って、#b#t" + this.item_water_of_life + "##kも一緒に持って#p" + this.npc_mar + "#に話しかけるだけだよ…ははは、幸運を祈るよ！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
