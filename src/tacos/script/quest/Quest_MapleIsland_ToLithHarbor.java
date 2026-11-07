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
// Quest.wz startscript : q1028s (JMS186), リス港口へ！, lua の処理を利用
public class Quest_MapleIsland_ToLithHarbor extends QuestScriptMan {

    private final int map_lith_harbor = 104000000; // リス : 港口
    private final String portal_lith_harbor = "maple00";

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // 翻訳
            say("おや、もう少し時間が必要かい？わかった…メイプルアイランドでやることを全部終えるまで、ここで待っているよ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("やあ、ビクトリアアイランドに行きたいなら、いつでも道を教えてあげるよ！もちろん、#b150メル#k払えればの話だけどね。何だって？#b#p12000##kからの伝言を持っているのかい？？？ああ、君があの人が言っていた人か。それなら話は別だ。メイプルアイランドのために頼みごとをしてくれているんだから、タダで連れて行ってあげよう。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                askAccept("それと、一度メイプルアイランドを出たら、二度とここには戻ってこられないことはわかっているよね。ビクトリアアイランドの#b#m" + this.map_lith_harbor + "##kに着くよ。サウスペリーより人が多いから、しっかり集中してね。心配いらないよ、きっとすぐに見つけられるさ。#b#p1002101##k");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                warp(this.map_lith_harbor, this.portal_lith_harbor);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
