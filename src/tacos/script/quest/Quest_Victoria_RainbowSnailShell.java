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
// Quest.wz endscript : q2156e (JMS186), 願いを聞き入れる無知改色デンデン皮!？, lua の処理を利用 (虹色のカタツムリ)
// TODO : 変身状態 (getMorphState) が未実装のため、殻を持っていない場合 (変身 7) の完了はできない
public class Quest_Victoria_RainbowSnailShell extends QuestScriptMan {

    private final int item_rainbow_shell = 2210006; // 虹色デンデンの殻
    private final int morph_snail = 7; // lua の target.getMorphState == 7
    private boolean has_shell = false;

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                this.has_shell = getItemCount(this.item_rainbow_shell) >= 1;
                if (this.has_shell) {
                    // 翻訳
                    say("よし。これがその虹色のカタツムリの殻ってわけね？ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 7500 経験値", false, true);
                    return true;
                }
                if (getMorphState() == this.morph_snail) {
                    // 翻訳
                    say("な、何なの、その姿は？なんでカタツムリになって来たの？！何？虹色のカタツムリの殻をかぶったらこうなったって？そういえば、カタツムリの伝説にも宝の危険について警告する言葉があったわね..ふう。よかった、食べなくて。欲張るからそんな姿になるのよ。", false, true);
                    return true;
                }
                // 翻訳
                say("うーん…#b#t" + this.item_rainbow_shell + "##kはまだ手に入れてないの？海辺の草むらに行って#b#o2220000##kを倒してみなさいってば。");
                return false;
            }
            case 1 -> {
                if (!this.has_shell) {
                    gainExp(10000);
                    gainFame(-1);
                    forceCompleteQuest();
                    questEndEffect();
                    return false;
                }
                if (!exchange(30000, this.item_rainbow_shell, -1)) {
                    // 翻訳
                    say("ETCインベントリと消費インベントリに空きがあるか確認してください。");
                    return false;
                }
                // TODO : NPC の特殊動作 (lua の self.setSpecialAction( "act2156" ))
                gainExp(7500);
                gainFame(3);
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("虹色のカタツムリの殻を分けるって約束しなかったかって？でも、これを半分に割ったら効き目がなくなっちゃうかもしれないじゃない？先に情報を教えたのはこっちなんだから、当然私がもらうわ！ふふっ！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
