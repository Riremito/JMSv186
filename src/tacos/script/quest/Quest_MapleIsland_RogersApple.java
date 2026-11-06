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
// Quest.wz startscript : q1021s, endscript : q1021e (JMS186), ローザーとリンゴ, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_MapleIsland_RogersApple extends QuestScriptMan {

    private final int item_rogers_apple = 2010007; // ローザーのリンゴ
    private final int item_apple = 2010000; // リンゴ
    private final int item_green_apple = 2010009; // 緑リンゴ
    private final int required_hp = 50;
    private final int reward_exp = 10;

    @Override
    public boolean questStart(int status, int answer) {
        switch (status) {
            case 0 -> {
                say("Hey, Man~ What's up? Haha! I am Roger who can teach you adorable new Maplers lots of information.", false, true);
                return true;
            }
            case 1 -> {
                say("You are asking who made me do this? Ahahahaha!\r\nMyself! I wanted to do this and just be kind to you new travellers.", true, true);
                return true;
            }
            case 2 -> {
                askAccept("So..... Let me just do this for fun! Abaracadabra~!");
                return true;
            }
            case 3 -> {
                // TODO : js では HP を 25 にする処理がコメントアウトされている
                if (!haveItem(this.item_rogers_apple, 1)) {
                    gainItem(this.item_rogers_apple, 1);
                }
                say("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you #rRoger's Apple#k. Please take it. You will feel stronger. Open the Item window and double click to consume. Hey, it's very simple to open the Item window. Just press #bI#k on your keyboard.", false, true);
                return true;
            }
            case 4 -> {
                say("Please take all Roger's Apples that I gave you. You will be able to see the HP bar increasing. Please talk to me again when you recover your HP 100%.", true, false);
                return true;
            }
            case 5 -> {
                forceStartQuest();
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
                if (getPlayer().getStat().getHp() < this.required_hp) {
                    say("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?", false, true);
                    return false;
                }
                say("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.", false, true);
                return true;
            }
            case 1 -> {
                say("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!", true, true);
                return true;
            }
            case 2 -> {
                say("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care if yourself and Good luck my friend!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_apple + "# 3 #t" + this.item_apple + "#\r\n#v" + this.item_green_apple + "# 3 #t" + this.item_green_apple + "#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", true, true);
                return true;
            }
            case 3 -> {
                gainExp(this.reward_exp);
                gainItem(this.item_apple, 3);
                gainItem(this.item_green_apple, 3);
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
