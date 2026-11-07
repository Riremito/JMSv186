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
public class Quest_Aran_MissingWeapon extends QuestScriptMan {

    private final int item_sword = 1302000; // 剣
    private final int reward_exp = 35;

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 3) {
            // TODO : 文章 (js の英文を翻訳)
            say("*ぐすん* とりあえず、この剣ではだめですか？使っていただけたら光栄なのですが…", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceStartQuest();
                    return false;
                }
                // TODO : 文章 (js の英文を翻訳)
                say("待って、あなたはもしかして…まさか…#p1201000#がずっと話していた英雄様ですか？！#p1201000#！うなずいてばかりいないで…教えてください！この方があなたの待っていた英雄様なのですか？！", false, true);
                return true;
            }
            case 1 -> {
                say("   #i4001171#", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ごめんなさい。胸がいっぱいになってしまって…*ぐすん* なんてこと、涙が出てきちゃいました。本当によかったですね、#p1201000#。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ちょっと待ってください…武器を持っていませんね。英雄たちはそれぞれ特別な武器を持っていたと聞いています。きっと暗黒の魔法使いとの戦いでなくしてしまったのですね。", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("あなたの武器の代わりにはなりませんが、#bとりあえずこの剣を持っていってください#k。私からの贈り物です。英雄が手ぶらで歩き回るわけにはいきませんから。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_sword + "# 1 #t" + this.item_sword + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値");
                return true;
            }
            case 5 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    gainItem(this.item_sword, 1);
                    gainExp(this.reward_exp);
                }
                forceCompleteQuest();
                questEndEffect();
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（スキルは英雄にはほど遠い…それに剣？今まで剣を握ったことなんてあっただろうか？思い出せない…そもそもどうやって装備するんだろう？）#k", true, true);
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
