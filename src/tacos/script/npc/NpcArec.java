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
public class NpcArec extends NpcScriptMan {

    private final int[] job_from = {
        410, // アサシン
        420, // シーフ
        432 // ブレードスペシャリスト
    };
    private final int[] job_to = {
        411, // ハーミット
        421, // シーフマスター
        433 // ブレードロード
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "お前は正式に#bハーミット#kになった。この本には、影を分身や身代わりとして使うハーミットの新しい攻撃スキルが載っている。#bシャドーメル#k（MPの代わりにメルを使い、投げたメルの量に応じてモンスターにダメージを与える）や#bシャドーパートナー#k（ハーミットの動きをすべて真似る影を作り出し、まるでハーミットが2人いるかのようにモンスターを攻撃できる）といったスキルがある。これらのスキルを使って、倒しにくいモンスターを打ち倒せ。",
        "お前は正式に#bシーフマスター#kになった。スキルブックの新しいスキルの一つに#bバンドオブシーブス#kというスキルがある。これを使うと、ほかのシーフたちを呼び出して複数のモンスターを一度に攻撃できる。シーフマスターはメルをさまざまな方法で使うこともできる。モンスターを攻撃したり（#bメルエクスプロージョン#k、地面のメルを爆発させる）、身を守ったり（#bメルガード#k、物理ダメージを軽減する）できるんだ。",
        "You are now a #bBlade Lord#k. May #rOdin#k be with you!" // TODO : 文章が不明
    };
    // SPの確認を行わない
    private final boolean[] skip_sp_check = {
        false,
        false,
        true
    };
    private final int required_level = 70;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("君はもう試験に合格しているから、心配することはない…まあ、決めたら話しかけてくれ。準備ができたら、3次転職させてやろう。準備ができていればの話だがな…");
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
                    nt.add("ジャクムダンジョンを探索しようとしてるか？盗賊でもないくせに声をかけるな。君の職業の長老を訪ねてくれ。");
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
                askYesNo("よし！これからお前をはるかに強力な盗賊にしてやる。だがその前に、SPをきちんと使ったか確認しろ。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要がある。ああ、それと、2次転職ですでに職業を選んでいるから、3次転職で選び直す必要はない。今すぐ転職するか？");
                return true;
            }
            case 1 -> {
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("Come back when you are level 70 and used all your SP accordingly.");
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
                say("スタートの助けになるよう、SPとAPを少し渡しておいた。これでお前は本当に強力な盗賊になった。だが、現実の世界では、さらに乗り越えがたい障害が待ち受けていることを覚えておけ。これ以上修行しても上の段階へ進めないと感じたときに、そのときだけ、俺に会いに来い。ここで待っているぞ。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
