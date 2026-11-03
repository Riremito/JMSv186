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
public class NpcRobeira extends NpcScriptMan {

    private final int[] job_from = {
        210, // ウィザード(火・毒)
        220, // ウィザード(氷・雷)
        230 // クレリック
    };
    private final int[] job_to = {
        211, // メイジ(火・毒)
        221, // メイジ(氷・雷)
        231 // プリースト
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "今日からあなたは#bメイジ（火・毒）#kです。新しいスキルブックには、新しくなった火と毒の呪文や強化された呪文が載っています。#bエレメントアンプリフィケーション#k（属性呪文を強化）や#bスペルブースター#k（攻撃呪文全体の速度を強化）といったスキルで、モンスターを素早く正確に攻撃できるようになります。#bパーシャルレジスタンス#k（特定の属性攻撃への防御を強化）や#bシール#k（モンスターを封じる）といった防御呪文は、メイジの唯一の弱点であるHPの少なさを補ってくれるでしょう。",
        "今日からあなたは#bメイジ（氷・雷）#kです。新しいスキルブックには、新しくなった氷と雷の呪文や強化された呪文が載っています。#bエレメントアンプリフィケーション#k（属性呪文を強化）や#bスペルブースター#k（攻撃呪文全体の速度を強化）といったスキルで、モンスターを素早く正確に攻撃できるようになります。#bパーシャルレジスタンス#k（特定の属性攻撃への耐性を得られる）や#bシール#k（モンスターを封じる）といった防御呪文は、メイジの唯一の弱点であるHPの少なさを補ってくれるでしょう。",
        "今日からあなたは#bプリースト#kです。新しいスキルブックには、#bシャイニングレイ#kや#bサモンドラゴン#kといった新しくなった聖なる呪文や強化された呪文が載っています。#bミスティックドア#k（最寄りの町へ通じる扉を作る）や#bホーリーシンボル#k（獲得経験値を上昇）といったスキルは、パーティープレイに欠かせないものになるでしょう。#bドゥーム#k（モンスターをカタツムリに変える）のような型破りな呪文があるので、プリーストは最もユニークな職業なのです。"
    };
    // SPの確認を行わない
    private final boolean[] skip_sp_check = {
        false,
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
                say("あなたはもう試験に合格しているので、心配することはありません…まあ、決めたら話しかけてください。準備ができたら、3次転職させてあげましょう。準備ができていればの話ですが。");
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
                    nt.add("ジャクムダンジョンを探索しようとしてるのか？魔法使いじゃない者の能力を魔法使いが判断するわけにはいかない。君の職業の長老を訪ねてくれ。");
                    say(nt);
                    return false;
                }
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add(getPlayer().getLevel() < this.required_level ? "あなたはまだ3次転職する資格がありません。そのためには少なくともレベル70が必要です。もっと修行してから私に会いに来てください。" : "ふむ…#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
                    say(nt);
                    return false;
                }
                // 翻訳
                askYesNo("よろしい！これからあなたをはるかに強力な魔法使いにしてあげましょう。でもその前に、SPをきちんと使ったか確認してください。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要があります。ああ、それと、2次転職ですでに職業を選んでいるので、3次転職で選び直す必要はありません。今すぐ転職しますか？");
                return true;
            }
            case 1 -> {
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("ふむ…#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
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
