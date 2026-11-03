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
public class NpcRene extends NpcScriptMan {

    private final int[] job_from = {
        310, // ハンター
        320 // クロスボウマン
    };
    private final int[] job_to = {
        311, // レンジャー
        321 // スナイパー
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "あなたは今、#bレンジャー#kになりました。ぜひ極めてほしいスキルの一つが#bモータルブロー#kで、レンジャーが至近距離から矢を放てるスキルです。#bインフェルノ#kでは、レンジャーが一時的にモンスターに炎属性の攻撃を行えます。さらに、#bパペット#k（モンスターの注意を引くかかしを召喚）や#bシルバーホーク#k（モンスターを攻撃する銀の鷹を召喚）といったスキルが、遠距離攻撃のスペシャリストとしての弓使いの地位を確かなものにしてくれます。",
        "あなたは今、#bスナイパー#kになりました。ぜひ極めてほしいスキルの一つが#bモータルブロー#kで、スナイパーが至近距離から矢を放てるスキルです。#bブリザード#kでは、スナイパーが一時的にモンスターに氷属性の攻撃を行えます。さらに、#bパペット#k（モンスターの注意を引くかかしを召喚）や#bゴールデンイーグル#k（モンスターを攻撃する金の鷲を召喚）といったスキルが、遠距離攻撃のスペシャリストとしての弓使いの地位を確かなものにしてくれます。"
    };
    // SPの確認を行わない
    private final boolean[] skip_sp_check = {
        false,
        false
    };
    private final int required_level = 70;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("あなたはもう試験に合格しているので、心配することはありません…まあ、決めたら話しかけてください。準備ができたら、3次転職させてあげましょう。準備ができていればの話ですが…");
                return false;
            }
            case 0 -> {
                // TODO : 本来は力と知恵のテスト (クエスト 7500) が必要
                for (int i = 0; i < this.job_from.length; i++) {
                    if (getPlayer().getJob() == this.job_from[i]) {
                        this.job_index = i;
                        break;
                    }
                }
                if (this.job_index < 0) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("ジャクムダンジョンを探索しようとしてるんですか？…弓使いではないようですね？弓使いでない人を判断する能力までは持っていません。あなたを見極めることができる職業の長老を訪ねてください。");
                    say(nt);
                    return false;
                }
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add(getPlayer().getLevel() < this.required_level ? "あなたはまだ3次転職する資格がありません。そのためには少なくともレベル70が必要です。もっと修行してから私に会いに来てください。" : "#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
                    say(nt);
                    return false;
                }
                // 翻訳
                askYesNo("よし！これからあなたをはるかに強力な弓使いにしてあげましょう。でもその前に、SPをきちんと使ったか確認してください。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要があります。ああ、それと、2次転職ですでに職業を選んでいるので、3次転職で選び直す必要はありません。今すぐ転職しますか？");
                return true;
            }
            case 1 -> {
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
                    say(nt);
                    return false;
                }
                getPlayer().changeJob(this.job_to[this.job_index]);
                getPlayer().gainAp((short) 5);
                say(this.job_texts[this.job_index], false, true);
                return true;
            }
            case 2 -> {
                // 翻訳
                say("");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
