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
// Quest.wz startscript : q4686s, q4689s (JMS186), 決戦！移動要塞ベルガモット, 強襲！機械化小隊マーヴェリック, 旧 js (scripts_JMS) を参考に実装
public class Quest_Zipangu_FutureBoss extends QuestScriptMan {

    private final int quest_vergamot = 4686; // 決戦！移動要塞ベルガモット
    private final int map_vergamot = 802000209; // ジパング : お台場 2100年
    private final int map_maverick = 802000309; // ジパング : 公園 2095年

    @Override
    public boolean questStart(int status, int answer) {
        boolean vergamot = (getQuestId() == this.quest_vergamot);
        switch (status) {
            case 0 -> {
                if (vergamot) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("本当に今すぐ出発するのか？ボスは簡単に倒せる相手じゃないから、仲間を集められるなら集めたほうがいい！準備ができたら教えてくれ。奴が待ち構えている場所まで連れていこう。いいな？", false, true);
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    say("もうここからは逃げられないと思う。ここで戦うつもりなら、互角に戦うためにもっと仲間が必要だ。どうする？それでも戦うか？", false, true);
                }
                return true;
            }
            case 1 -> {
                warp(vergamot ? this.map_vergamot : this.map_maverick);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
