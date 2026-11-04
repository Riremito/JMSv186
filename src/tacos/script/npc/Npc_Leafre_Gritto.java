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
public class Npc_Leafre_Gritto extends NpcScriptMan {

    private final int quest_job4 = 6914; // 英雄としての資質
    private final int item_skill_book = 2280003; // [スキルブック]メイプルヒーロー
    private final int required_level = 120;
    private final int[] job_from = {
        211, // メイジ(火、毒)
        221, // メイジ(氷、雷)
        231 // プリースト
    };
    private final int[] job_to = {
        212, // アークメイジ
        222, // アークメイジ
        232 // ビショップ
    };
    // 翻訳
    private final String[] job_names = {
        "アークメイジ",
        "アークメイジ",
        "ビショップ"
    };
    // 翻訳
    private final String[][] job_texts = {
        {
            "あなたは最高の魔法使い、#bアークメイジ#kになりました。アークメイジは、\r\n#bインフィニティ#kや#bビッグバン#kのように、自らの力だけでなく自然のマナも使うことができます",
            "アークメイジの力はこれだけではありません。アークメイジは火と毒の属性に優れています。修行を積めば、究極の属性だけでなく、自分や敵の属性さえも変えられるかもしれません。"
        },
        {
            "あなたは最高の魔法使い、#bアークメイジ#kになりました。アークメイジは、\r\n#bインフィニティ#kや#bビッグバン#kのように、自らの力だけでなく自然のマナも使うことができます",
            "アークメイジの力はこれだけではありません。アークメイジは火と毒の属性に優れています。修行を積めば、究極の属性だけでなく、自分や敵の属性さえも変えられるかもしれません。"
        },
        {
            "あなたは最高の魔法使い、#bビショップ#kになりました。  ビショップは、\r\n#bインフィニティ#kや#bビッグバン#kのように、自らの力だけでなく自然のマナも使うことができます",
            "ビショップの力はこれだけではありません。ビショップは神の力を借りることができます。強力な聖属性の魔法を使い、死者をよみがえらせることさえできるかもしれません。"
        }
    };
    private final int MENU_CHANGE = 0;
    private final int MENU_THINK = 1;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.job_to.length; i++) {
                    if (getJob() == this.job_to[i]) {
                        // 翻訳
                        say("あなたは最高の魔法使い、#b" + this.job_names[i] + "#kの地位に就きました。強い力にはより大きな責任が伴います。これから待ち受けるすべての試練を乗り越えられるよう願っています。");
                        return false;
                    }
                }
                for (int i = 0; i < this.job_from.length; i++) {
                    if (getJob() == this.job_from[i]) {
                        this.job_index = i;
                    }
                }
                if (this.job_index < 0) {
                    // 翻訳
                    say("なぜ私に会いたいのだ？私に聞きたいことなど何もないだろう。");
                    return false;
                }
                if (getLevel() < this.required_level) {
                    // 翻訳
                    say("魔法使いの極みの道を歩むには、まだ弱すぎます。もっと強くなったら戻ってきてください。");
                    return false;
                }
                if (getQuestStatus(this.quest_job4) != 2) {
                    // 翻訳
                    say("まだ4次転職する準備ができていないようだ。準備ができたら話しかけてくれ。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("あなたは真の魔法使いになる資格があります。 \r\n転職しますか？");
                nt.addMenu(MENU_CHANGE, this.job_names[this.job_index] + "に転職したい。");
                nt.addMenu(MENU_THINK, "少し考えさせて。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_CHANGE) {
                    // 翻訳
                    say("最高の魔法使いになることをためらう必要はありません..決めたらいつでも話しかけてください。準備ができていれば、4次転職させてあげましょう。");
                    return false;
                }
                if (getJob() != this.job_from[this.job_index]) {
                    return false;
                }
                if (getRemainingSp() > (getLevel() - this.required_level) * 3) {
                    // 翻訳
                    say("ふむ…#bSP#kが多すぎる。SPがそんなに残っていては4次転職できない。");
                    return false;
                }
                if (!canHold(this.item_skill_book, 1)) {
                    // 翻訳
                    say("インベントリに空きスロットがないので進めない。インベントリを整理してからもう一度試してくれ。");
                    return false;
                }
                gainItem(this.item_skill_book, 1);
                changeJob(this.job_to[this.job_index]);
                gainAp(5);
                say(this.job_texts[this.job_index][0], false, true);
                return true;
            }
            case 2 -> {
                say(this.job_texts[this.job_index][1], false, true);
                return true;
            }
            case 3 -> {
                // 翻訳
                say("すべては君がどれだけ修行するかにかかっていることを忘れるな。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
