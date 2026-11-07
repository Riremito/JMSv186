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
// Npc.wz script : talkHelena (JMS176-302), js の処理を利用
// アランのプロローグ (ブラックロード : 避難準備中)
public class Npc_Aran_HelenaPrologue extends NpcScriptMan {

    private final int quest_trio = 21002; // アランのプロローグ (ヘレナとの会話)
    private final String effect_trio = "Effect/Direction1.img/aranTutorial/Trio";
    private final int menu_black_mage = 0;
    private final int menu_escape = 1;
    private final int menu_comrades = 2;
    // TODO : 文章 (js の英文を翻訳)
    private final String[] intro_texts = {
        "アラン、目が覚めたのね！怪我の具合はどう？…え？今どうなっているのか知りたいの？",
        "ここを離れる準備はすべて整ったわ。みんな方舟に乗り込んでいるし、飛んでいる間は神獣が方舟を守ってくれると言ってくれたから、心配はいらないわ。準備が終わったら、すぐにビクトリアアイランドへ脱出するつもりよ。",
        "アランの仲間たち…？彼らは…暗黒の魔法使いと戦いに行ったわ。私たちが脱出する間、暗黒の魔法使いを食い止めることにしたの。え？あなたも一緒に戦いたいですって？だめよ、絶対にだめ！あなたは怪我をしているのよ！今すぐ方舟に乗りなさい！"
    };

    @Override
    public boolean action(int status, int answer) {
        if (getQuestStatus(this.quest_trio) == 0) {
            if (status < 0) {
                return false;
            }
            if (status < this.intro_texts.length) {
                say(this.intro_texts[status], 0 < status, true);
                return true;
            }
            setQuestData(this.quest_trio, "1");
            showReservedEffect(this.effect_trio);
            return false;
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (js の英文を翻訳)
                nt.add("今は緊急事態よ。何が知りたいの？");
                // TODO : 文章 (js の英文を翻訳)
                nt.addMenu(this.menu_black_mage, "暗黒の魔法使いはどこに？");
                // TODO : 文章 (js の英文を翻訳)
                nt.addMenu(this.menu_escape, "脱出の準備は？");
                // TODO : 文章 (js の英文を翻訳)
                nt.addMenu(this.menu_comrades, "仲間たちは？");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer == this.menu_black_mage) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("暗黒の魔法使いは、今私たちがいる場所のすぐ近くにいると聞いたわ。暗黒の魔法使いが操るドラゴンたちのせいで、森を通って逃げることもできない。だから方舟で脱出することにしたの。ここを離れるには、ビクトリアアイランドへ飛んでいくしかないのよ。");
                } else if (answer == this.menu_escape) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("みんな方舟に乗り込んで、ここを脱出する準備はできているわ。あと何人か乗れば、ビクトリアアイランドへ出発よ。飛んでいる間は神獣が守ってくれるわ。今のエレヴには守るべき人がいないからと言ってね。");
                } else if (answer == this.menu_comrades) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("あなたの仲間たちは…私たちが脱出する時間を稼ぐために、自分たちだけで暗黒の魔法使いと戦いに行ったわ。あなたは怪我をしているから連れて行かなかったの。あの子を助けたら、あなたも方舟に乗って一緒に逃げるのよ、アラン！");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
