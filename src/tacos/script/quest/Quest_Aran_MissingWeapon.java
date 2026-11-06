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
// Quest.wz endscript : q21011e (JMS186), 消えた武器, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_MissingWeapon extends QuestScriptMan {

    private final int item_sword = 1302000; // 剣
    private final int reward_exp = 35;

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 3) {
            say("*sniff sniff* Isn''t this sword good enough for you, just for now? I''d be so honored...", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceStartQuest();
                    return false;
                }
                say("Wait, are you... No way... Are you the hero that #p1201000# has been talking about all this time?! #p1201000#! Don''t just nod... Tell me! Is this the hero you''ve been waiting for?!", false, true);
                return true;
            }
            case 1 -> {
                say("   #i4001171#", true, true);
                return true;
            }
            case 2 -> {
                say("I''m sorry. I''m just so overcome with emotions... *Sniff sniff* My goodness, I''m starting to tear up. You must be so happy, #p1201000#.", true, true);
                return true;
            }
            case 3 -> {
                say("Wait a minute... You''re not carrying any weapons. From what I''ve heard, each of the heroes had a special weapon. Oh, you must have lost it during the battle against the Black Mage.", true, true);
                return true;
            }
            case 4 -> {
                askYesNo("This isn''t good enough to replace your weapon, but #bcarry this sword with you for now#k. It''s my gift to you. A hero can''t be walking around empty-handed. \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_sword + "# 1 #t" + this.item_sword + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp");
                return true;
            }
            case 5 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    gainItem(this.item_sword, 1);
                    gainExp(this.reward_exp);
                }
                forceCompleteQuest();
                questEndEffect();
                say(3, "#b(Your skills are nowhere close to being hero-like... But a sword? Have you ever even held a sword in your lifetime? You can''t remember... How do you even equip it?)#k", true, true);
                return true;
            }
            case 6 -> {
                tutorMsg(16);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
