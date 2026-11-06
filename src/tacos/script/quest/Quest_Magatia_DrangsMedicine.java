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
// Quest.wz startscript : q3354s (JMS186), ドランの薬, lua の処理を利用
public class Quest_Magatia_DrangsMedicine extends QuestScriptMan {

    private final int map_restricted_area = 261020401; // アルカドノ研究所 : 関係者以外出入禁止区域

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // 翻訳
            say("まだ実験が完全に終わったわけではありません。危険な実験道具が多いので、席を外してください。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("ふう…これ以上研究が進みません。事実上、実験は失敗したも同然です。いくら研究しても、元の記憶をすべて保ったまま人間の肉体を機械に変えるのは不可能だとわかったのです…でも…その代わりに、もっといいものを作りました。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("それはほかでもない…娘のキニのための薬です。キニは生まれつき体が弱いのです。ただそういうものだと思っていましたが…実はそれは妖精と人間の混血だからどうしようもないことだそうです。だから、あの子のために薬を開発しました。", true, true);
                return true;
            }
            case 2 -> {
                // 翻訳
                askAccept("ふふ…本当に誇らしいです。人間を機械にして寿命を延ばす研究は失敗してしまいましたが…妖精のように永遠に生きることはできなくても、それ以上の幸せを見つけられると思います…ああ、そろそろ研究をまとめなければ。爆発物が多くて危険なので、あなたをこの研究室から追い出します");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                warp(this.map_restricted_area);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
