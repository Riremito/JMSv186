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
// Quest.wz startscript : q22501s - q22504s (JMS186), お腹が空いたちびドラゴン, 乾草を一口, オオカミ肉を一口, 美味しい牛乳1, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_HungryBabyDragon extends QuestScriptMan {

    private final int quest_hungry = 22501; // お腹が空いたちびドラゴン
    private final int quest_hay = 22502; // 乾草を一口
    private final int quest_pork = 22503; // オオカミ肉を一口

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_hungry) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 2) {
                say("*gasp* How can you refuse to feed your Dragon? This is child abuse!", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    say("Yo, master. Now that I've shown you what I can do, it's your turn. Prove to me...that you can find food! I'm starving. You can use my power now, so you have to take care of me.", false, true);
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    say(2, "Eh, I still don't get what's going on, but I can't let a poor little critter like you starve, right? Food, you say? what do you want to eat?", true, true);
                    return true;
                }
                case 2 -> {
                    say("Hi, I was just born a few minutes ago. How would I know what I eat? All I know is that I'm a Dragon... I'm YOUR Dragon. And you're my master. You have to treat me well!", true, true);
                    return true;
                }
                case 3 -> {
                    askAccept("I guess we're supposed to learn together. But I'm hungry. Master, I want food. Remember, I'm a baby! I'll start crying soon!");
                    return true;
                }
                case 4 -> {
                    forceStartQuest();
                    // js の sendOkS は type が反映されない
                    say(2, "#b(Mir the baby Dragon appears to be extremely hungry. You must feed him. Maybe your Dad can give you advice on what dragons eat.)", false, false);
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_hay) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                say("Hm, you never know unless you try. That lizard is big enough to be on Maple's Believe It Or Not. It might eat hay", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    askAccept("Wouldn't a lizard enjoy a #bHandful of Hay#k, like a cow? There are a lot of #bHaystacks#k nearby, so try feeding it that.");
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    evanTutorial("UI/tutorial/evan/12/0");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        boolean pork = (getQuestId() == this.quest_pork);
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            if (pork) {
                say("How can you starve me like this. I'm just a baby. This is wrong!", false, true);
            } else {
                say("No use trying to find an answer to this on my own. I'd better look for #bsomeone older and wiser than master!#k", false, true);
            }
            return false;
        }
        switch (status) {
            case 0 -> {
                if (pork) {
                    say("No, no, no. This isn't what I need. I need something more nutritious, master!", false, true);
                } else {
                    say("Ugh. This isn't going to work. I need something else. No plants. No meat. What, you have no idea? But you're the master, and you're older than me, too. You must know what'd be good for me!", false, true);
                }
                return true;
            }
            case 1 -> {
                if (pork) {
                    say(2, "#bHm... So you're not a herbivore. You might be a carnivore. You're a Dragon, after all. How does some Pork sound?#k", true, true);
                } else {
                    say(2, "#But I don't. It's not like age has anything to do with this...", true, true);
                }
                return true;
            }
            case 2 -> {
                if (pork) {
                    askAccept("What's a...Pork? Never heard of it, but if it's yummy, I accept! Just feed me something tasty. Anything but plants!");
                } else {
                    askAccept("Since you're older, you must be more experienced in the world, too. Makes sense that you'd know more than me. Oh, fine. I'll ask someone who's even older than you, master!");
                }
                return true;
            }
            case 3 -> {
                forceStartQuest();
                // js の sendOkS は type が反映されない
                if (pork) {
                    say(2, "#b(Try giving Mir some Pork. You have to hunt a few Pigs at the farm. Ten should be plenty...)#k", false, false);
                } else {
                    say(2, "#b(You already asked Dad once, but you don't have any better ideas. Time to ask him again!)#k", false, false);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
