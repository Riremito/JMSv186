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
// Npc.wz script : helperCygnus (JMS165-302), js の処理を利用
// ノーブレスのチュートリアルのガイド (ReqCUser.OnTalkToTutor から呼ばれる)
public class Npc_Ereve_SignetTutorial extends NpcScriptMan {

    private final int menu_about = 0;
    private final int menu_knights = 12;
    // チュートリアルの項目 (UserTutorMsg の番号)
    private final int[] tutor_indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    // TODO : 文章 (js の英文を翻訳)
    private final String[] tutor_menus = {
        "ミニマップ",
        "クエストウィンドウ",
        "インベントリ",
        "通常攻撃",
        "アイテムの拾い方",
        "アイテムの装備",
        "スキルウィンドウ",
        "クイックスロットの使い方",
        "箱の壊し方",
        "椅子への座り方",
        "能力値の上げ方"
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (js の英文を翻訳)
                nt.add("待ってください！ここで説明することはレベル10までプレイすれば自然と身につくものなので、今すぐ覚える必要はありません。前もって知っておきたい方だけ聞いてくださいね。\r\nさて、どれについて知りたいですか？");
                // TODO : 文章 (js の英文を翻訳)
                nt.addMenu(this.menu_about, "あなたについて教えてください。");
                for (int i = 0; i < this.tutor_indexes.length; i++) {
                    nt.addMenu(this.tutor_indexes[i], this.tutor_menus[i]);
                }
                // TODO : 文章 (js の英文を翻訳)
                nt.addMenu(this.menu_knights, "シグナス騎士団とは？");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer == this.menu_about) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("私は女王シグナスとエレヴを守る神獣にお仕えしています。神獣は私に女王の願いを叶えるよう命じました。それは、シグナス騎士団の一員になることを目指してメイプルワールドにやって来た魂たちを導くことです。ですから私は、あなたが騎士になるか、レベル11になるまで一緒にいます。わからないことがあれば聞いてくださいね！", false, true);
                    return true;
                }
                if (answer == this.menu_knights) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("メイプルワールドは長い間平和を保ってきましたが、暗黒の魔法使いの気配が少しずつ戻ってきているようです。邪悪な暗黒の魔法使いを止めるため、女王はシグナス騎士団を結成することにしました。レベル10になれば、あなたも正式にシグナス騎士団への入団を申し込むことができます。");
                    return false;
                }
                for (int index : this.tutor_indexes) {
                    if (index == answer) {
                        tutorMsg(index);
                        break;
                    }
                }
                return false;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("でも、短い時間ですべてを理解しようとしなくても大丈夫です。ここで教えられることはすべて、ゲームをプレイしていれば自然と身につく基本的な知識です。レベル10になってもどうすればいいかわからないときや、すでに知っていることを確認したいときにだけ聞いてくれれば十分です。一度に全部覚える必要はありませんから、気楽にいきましょう。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
