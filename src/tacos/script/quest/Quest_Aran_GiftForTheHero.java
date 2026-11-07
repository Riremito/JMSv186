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
// Quest.wz startscript : q21013s, endscript : q21013e (JMS186), 英雄のためのプレゼント, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_GiftForTheHero extends QuestScriptMan {

    private final int item_chair = 3010062; // バンブーチェア
    private final int item_bamboo = 4032309; // 青竹
    private final int item_wood = 4032310; // 木材
    private final int reward_exp = 95;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // TODO : 文章 (js の英文を翻訳)
            say("きっと旅の役に立つはずです。どうか断らないでください。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askMenu("あ、英雄様ですね。ずっとお会いしたかったんです。\r\n#b#L0#（少し恥ずかしがっているようだ…）#l");
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("ずっと前から英雄様に贈りたいものがあったんです…村へ向かう途中でお忙しいのはわかっていますが、私の贈り物を受け取ってもらえますか？");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say(1, "贈り物の部品は近くの箱の中にしまってあります。お手数ですが、箱を壊して#b#t" + this.item_bamboo + "##kと#b#t" + this.item_wood + "##kを持ってきてもらえますか？すぐに組み立てますから。", false, true);
                return true;
            }
            case 3 -> {
                tutorMsg(18);
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
                // TODO : 文章 (js の英文を翻訳)
                say("あ、部品を全部持ってきてくれたんですね。少しだけ待ってください、組み立てますから…こうして…こうやって…それから… \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_chair + "# 1 #t" + this.item_chair + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値", false, true);
                return true;
            }
            case 1 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    gainItem(this.item_chair, 1);
                    gainExp(this.reward_exp);
                    forceCompleteQuest();
                    questEndEffect();
                }
                // TODO : 文章 (js の英文を翻訳)
                say(1, "はい、英雄様のために組み立てた椅子です！英雄様だってたまにはゆっくり休みたいはずだと思って、ずっと椅子を贈りたかったんです。えへへ。", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(1, "英雄だって無敵ではありません。英雄も人間です。困難にぶつかって、くじけそうになることもあるでしょう。でも、あなたはどんな困難も乗り越えられる力を持っているからこそ英雄なのです。", true, true);
                return true;
            }
            case 3 -> {
                tutorMsg(19);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
