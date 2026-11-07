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
// TODO : 寄付のランキングは js の固定の文章のまま
public class Quest_Medal_KingOfDonation extends QuestScriptMan {

    private final int item_medal = 1142030; // リス港口寄付王の勲章

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("十分に準備ができたと思ったら、また来てください。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("#v" + this.item_medal + "# #e#b#t" + this.item_medal + "##k\n\r\n\r - 制限時間：1時間\n\r - この村に一番多く寄付すること....#nこの勲章にふさわしいかどうか、自分を試してみますか？");
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("現在の順位 \n\r\n\r1. #bMintLovePep#k : ???,???,??? メル\n\r2. #bKelviinXD#k : 68,000,000 メル\n\r3. #bxBabyRence#k : 49,999,999 メル\n\r4. #bXxTrIStaArxx#k : 29,999,999 メル\n\r5. #bxBabyRence#k : 14,000,000 メル\n\r\n\r現在の寄付王が寄付した正確な金額はお教えできませんので、ご了承ください。\n\rまた、記録は毎月1日にすべてリセットされることも覚えておいてください…", false, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("幸運を祈ります。決まった期限はありませんので、資格があると思ったらいつでも来てください。資格があるかどうか判断します。それと、この挑戦を放棄するか達成しない限り、ほかの称号には挑戦できないことを覚えておいてください…", true, true);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
