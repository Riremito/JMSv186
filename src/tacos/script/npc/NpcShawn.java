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
// Npc.wz script : guildquest1_comment (JMS131-302), 会話文は lua_script_jp (guildquest1_comment) を利用
public class NpcShawn extends NpcScriptMan {

    private final int item_ruby = 4001024; // 魔性のルビー
    private boolean menu = true;
    private boolean asked = false;
    private String[] says = null;
    private int says_index = 0;

    @Override
    public boolean action(int status, int answer) {
        if (this.menu) {
            this.menu = false;
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            if (!this.asked) {
                nt.add("私たちギルド連合は、長い間、古代の秘宝「エメラルドタブレット」の解読に取り組んできました。その結果、過去の謎の国シャレニアンがここに眠っていることがわかりました。さらに、伝説の神秘の宝石#t" + this.item_ruby + "#の手がかりが、シャレニアンの遺跡にあるかもしれないこともわかったのです。そこで、ギルド連合は最終的に#t" + this.item_ruby + "#を見つけるため、ギルドクエストを開始しました。\r\n#b#L0# シャレニアンって？#l \r\n#b#L1# #t" + this.item_ruby + "#？#l\r\n#b#L2# ギルドクエスト？#l\r\n#b#L3# もう大丈夫です。#l", false);
            } else {
                nt.add("ほかに質問はありますか？\r\n#b#L0# シャレニアンって？#l \r\n#b#L1# #t" + this.item_ruby + "#？それは何？#l\r\n#b#L2# ギルドクエスト？#l\r\n#b#L3# いいえ、もう大丈夫です。#l", false);
            }
            askMenu(nt);
            this.says = null;
            return true;
        }
        if (this.says == null) {
            // 翻訳
            switch (answer) {
                case 0 -> {
                    this.says = new String[]{
                        "シャレニアンは、かつてビクトリアアイランドのすべての地域を支配していた、文字を持つ古代文明です。ゴーレムの寺院や、ダンジョンの奥深くにある神殿など、誰が建てたのかわからない古い建造物は、実はシャレニアンの時代に造られたものなのです。",
                        "シャレニアン最後の王はシャレン3世という紳士で、とても賢く慈悲深い王だったようです。しかしある日、王国全体が崩壊してしまい、その理由はまったく明らかになっていません。"
                    };
                    this.asked = true;
                }
                case 1 -> {
                    this.says = new String[]{
                        "#t" + this.item_ruby + "#は、持ち主に永遠の若さをもたらすという伝説の宝石です。皮肉なことに、#t" + this.item_ruby + "#を持っていた者はみな不幸な末路をたどったようで、それがシャレニアン没落の理由なのかもしれません。"
                    };
                    this.asked = true;
                }
                case 2 -> {
                    // lua では nAsk が変更されない
                    this.says = new String[]{
                        "以前にも探検隊をシャレニアンに送り込みましたが、誰一人戻ってきませんでした。それがきっかけで、ギルドクエストを始めることになったのです。困難な挑戦に立ち向かえるほど強いギルド、そう、あなたたちのようなギルドを待っていました。",
                        "このギルドクエストの最終目標は、シャレニアンを探索して#t" + this.item_ruby + "#を見つけることです。力ですべてが解決する任務ではありません。ここではチームワークの方が大切なのです。"
                    };
                }
                default -> {
                    say("そうですか？ほかに聞きたいことがあれば、気軽に話しかけてください。");
                    return false;
                }
            }
            this.says_index = 0;
        } else {
            this.says_index++;
        }
        if (this.says.length <= this.says_index) {
            // 会話後にメニューへ戻る
            this.menu = true;
            return action(status, answer);
        }
        say(this.says[this.says_index], false, true);
        return true;
    }
}
