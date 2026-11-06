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
// Quest.wz startscript : q20700s (JMS186), このまま出られる？, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_AreYouSureYouCanLeave extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            say("When will you realize how weak you are... When you get yourself in trouble in Victoria Island?", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                say("You have finally become a Knight-in-Training. I'd like to give you a mission right away, but you still look miles away from even being able to handle a task on your own. Are you sure you can even go to Victoria Island like this?", false, true);
                return true;
            }
            case 1 -> {
                askAccept("It's up to you to head over to Victoria Island, but a Knight-in-Training that can't take care of one''s self in battles is likely to cause harm to the Empress''s impeccable reputation. As the Head Tactician of this island, I can't let that happen, period. I want you to keep training until the right time comes.");
                return true;
            }
            case 2 -> {
                forceCompleteQuest();
                say("#p1102000#, the Training Instructor, will help you train into a serviceable knight. Once you reach Level 13, I'll assign you a mission or two. So until then, keep training.", false, true);
                return true;
            }
            case 3 -> {
                say("Oh, and are you aware that if you strike a conversation with #p1101001#, she'll give you a blessing? The blessing will definitely help you on your journey.", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
