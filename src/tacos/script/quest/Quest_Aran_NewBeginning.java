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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q21700s (JMS186), 新たな始まり, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_NewBeginning extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 3) {
            // TODO : 文章 (js の英文を翻訳)
            say("いいえ？一人で修行できるということですか？教官と一緒に修行した方がいい結果が出るということだけは言っておきますよ。この世界は一人では生きていけません。人と協力することを学ばなければなりません。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "少しずつ思い出してきたようですね。鉾があなたを認めたのでしょう。つまり、あなたは間違いなく#b鉾の使い手、アラン#kです。ほかに何か覚えていることはありませんか？鉾で使っていたスキルとか？何でもいいのです。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（いくつかのスキルを思い出したことを伝えた。）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "多くはありませんが、進歩ですね。それなら、凍りつく前の状態に戻ることを目指しましょう。記憶はなくしても、体が覚えている能力はすぐに取り戻せるはずです。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "どうすれば能力を取り戻せる？", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("方法はひとつだけです。修行！修行！修行！修行です！修行を続ければ、体が本能的に能力を思い出すはずです。その手助けとして、教官を紹介しましょう。");
                return true;
            }
            case 5 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("使い慣れた武器が一番だと思って、#b鉾#kを渡しました。修行に役立つはずです。", false, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("#b左#kに出ると修練場があります。そこで#b#p1202006##kに会ってください。少し物忘れがひどいのが心配ですが、あなたのために長い間スキルを研究してきた方です。きっと何か学べるはずです。", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
