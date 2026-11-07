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
// Quest.wz startscript : q22000s, endscript : q22000e (JMS186), 不思議な夢, 旧 js (scripts_JMS) を参考に実装
public class Quest_Evan_StrangeDream extends QuestScriptMan {

    private final int reward_exp = 20;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 6) {
            // TODO : 文章 (js の英文を翻訳)
            say("ん？#p1013101#に話したくないの？お兄ちゃんには優しくしなきゃだめよ。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("よく眠れた、エヴァン？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bうん、お母さんは？#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("私もよく眠れたわ。でも、あなたはずいぶん疲れているみたいね。本当によく眠れたの？昨夜の雷で目が覚めちゃった？", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bううん、そうじゃないんだ、お母さん。昨夜、変な夢を見たんだ。#k", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("変な夢？どんな夢？", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bええと…#k", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（夢の中でドラゴンに会ったことを話した。）", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("あはは、ドラゴン？すごいわね。丸のみにされなくてよかったわ！その夢の話、#p1013101#にもしてあげたら？きっと面白がるわよ。");
                return true;
            }
            case 8 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("#b#p1013101##kは#b玄関前#kでブルドッグに餌をやっているわ。外に出ればすぐに会えるはずよ。", false, true);
                return true;
            }
            case 9 -> {
                evanTutorial("UI/tutorial/evan/1/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("よう、エヴァン。起きたのか？目の下にくまができてるぞ。よく眠れなかったのか？ん？変な夢？どんな夢だ？おお？ドラゴンの夢？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ぶわははははは、ドラゴン？本気かよ？夢占いなんてわからないけど、いい夢っぽいじゃないか！夢に犬も出てきたか？ははは！\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#" + this.reward_exp + " 経験値", true, true);
                return true;
            }
            case 2 -> {
                gainExp(this.reward_exp);
                evanTutorial("UI/tutorial/evan/2/0");
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
