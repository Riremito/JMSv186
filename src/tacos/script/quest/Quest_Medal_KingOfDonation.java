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
// Quest.wz startscript : q29503s, endscript : q29503e (JMS186), 寄付王, 旧 js (scripts_JMS) を参考に実装, js の end は処理なし
// TODO : 文章 (js の英文のまま)
// TODO : 寄付のランキングは js の固定の文章のまま
public class Quest_Medal_KingOfDonation extends QuestScriptMan {

    private final int item_medal = 1142030; // リス港口寄付王の勲章

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("Come back when you feel like you are fully prepared for this.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("#v" + this.item_medal + "# #e#b#t" + this.item_medal + "##k\n\r\n\r - Time Limit: 1 hr\n\r - Donate the Most for this town....#nDo you want to test yourself and see if this Medal is for you?");
                return true;
            }
            case 1 -> {
                say("Current Rank \n\r\n\r1. #bMintLovePep#k : ???,???,??? mesos\n\r2. #bKelviinXD#k : 68,000,000 mesos\n\r3. #bxBabyRence#k : 49,999,999 mesos\n\r4. #bXxTrIStaArxx#k : 29,999,999 mesosn\n\r5. #bxBabyRence#k : 14,000,000 mesos\n\r\n\rUnderstand that we cannot divulge the exact number donated by the current King of Donation. \n\rAlso remember that all records will be reset every first of the month...", false, true);
                return true;
            }
            case 2 -> {
                say("Best of luck to you. There's no real set date for this, so if you feel like you qualify for this, then feel free to come see me so I can determine whether you qualify for it. And remember that you will not be able to challenge other Titles unless you either forfeit this challenge or complete it...", true, true);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
