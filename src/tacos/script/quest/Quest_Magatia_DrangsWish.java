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
// Quest.wz startscript : q3353s (JMS186), ドランの願い, lua の処理を利用
public class Quest_Magatia_DrangsWish extends QuestScriptMan {

    private final int map_drang_lab = 926120200; // ドランの研究室
    private final String portal_drang_lab = "out00";

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // 翻訳
            say("えっ？嫌なのかい？君が嫌なら仕方ないけど…君が興味ありそうだったから、わざわざ苦労して調べておいたのに、パーウェンの好意をこんなに無視するなんて…しくしく。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("ほう～親切な冒険者の友よ！来たのかい？久しぶりだね？君に本当に本当に会いたかったよ！なぜかって？ふふふふ…前に君が聞いてきたことについてわかったんだ！ほら、あれさ。あの性格の暗い錬金術師の思念のことだよ。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                askAccept("あの人の別の思念の痕跡を突き止めたんだ。君が興味ありそうだったから、一生懸命探したんだよ。ふふふ…さあ、早く彼のところへ行ってごらん");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                warp(this.map_drang_lab, this.portal_drang_lab);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
