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
// Quest.wz startscript : q3360s (JMS186), 暗証番号の認証, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Magatia_VerifyingThePassword extends QuestScriptMan {

    private final String password_chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int password_length = 10;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0) {
            say("Come on, hurry up. Get your pen and paper out if you're not that smart!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Oh! Finally you have come! I'm glad you are here in time. I have the master key for you to open the secert passage! Hahahaha! Isn't it amazing? Say it amazing!", false, true);
                return true;
            }
            case 1 -> {
                askAccept("All right, now, this key is very long and complex. I need you to memorize it very well. I won't say again, so you'd better write it down somewhere. Are you ready?");
                return true;
            }
            case 2 -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.password_length; i++) {
                    sb.append(this.password_chars.charAt(random(0, this.password_chars.length() - 1)));
                }
                String password = sb.toString();
                say("The key code is #b" + password + "#k. Got that? Put the key into the door of the secret passage, and you will be able to walk around the passage freely.");
                forceStartQuest(getQuestId(), password);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
