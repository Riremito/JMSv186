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
// TODO : 文章 (js の英文のまま)
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
                    say("Are you sure you want to leave right now? The boss is not easy to battle against, so if you can gather some friends, then do it! Let me know when you're ready, so I can take you to the spot where it awaits. Okay?", false, true);
                } else {
                    say("I don't think you can escape from here now. If you do plan on battling here, you'll need more teammates to make it an even fight. What do you want to do? Do you still want to battle?", false, true);
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
