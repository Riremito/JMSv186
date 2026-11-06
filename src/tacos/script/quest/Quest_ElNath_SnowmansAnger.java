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
// Quest.wz startscript : q3108s (JMS186), スノウマンの怒り-手がかり発見, lua の処理を利用
public class Quest_ElNath_SnowmansAnger extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                say("（像は一目見ただけでまぶしいほど美しい。氷でできているように透き通っているが、氷ではないようだ。像の周りを回りながら、詳しく調べてみた。）", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("（像の片側が壊れている。周りには大きな足跡もいくつか見える。", true, true);
                return true;
            }
            case 2 -> {
                gainExp(200);
                // lua の qr.setState( 3108, 1 ), qr.setState( 3108, 2 )
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
