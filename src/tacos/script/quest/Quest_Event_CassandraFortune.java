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
// Quest.wz endscript : q9809e (JMS186), カサンドラの占い (4周年)
public class Quest_Event_CassandraFortune extends QuestScriptMan {

    private final int item_maple_leaf = 4001126; // モミジ
    private final int item_ticket_change = 4055000; // カサンドラの占いチケット (変身)
    private final int item_ticket_beauty = 4055001; // カサンドラの占いチケット (美容室)
    private final int item_buff_meso = 2022124; // カサンドラの魔法 (金運)
    private final int item_buff_health_1 = 2022151; // カサンドラの魔法 (健康)
    private final int item_buff_health_2 = 2022152; // カサンドラの魔法 (健康)
    private int fortune = 0;

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                this.fortune = random(0, 9999);
                // 翻訳
                say("さあ～ついに今日の占いの結果が出ましたよ！\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#fUI/UIWindow.img/QuestIcon/5/0#", false, true);
                return true;
            }
            case 1 -> {
                if (this.fortune <= 2) {
                    int item_ticket = (this.fortune <= 1) ? this.item_ticket_change : this.item_ticket_beauty;
                    // TODO : lua の exchangeEx の DateExpire:2007052100 (有効期限) は未実装
                    if (!exchange(0, this.item_maple_leaf, -4, item_ticket, 1)) {
                        // 翻訳
                        say("ETCインベントリに空きがあるか確認してください。");
                        return false;
                    }
                    if (this.fortune <= 1) {
                        // 翻訳
                        say("今日は変身するのにいい日だそうです。一度変身に挑戦してみるといいですよ。");
                    } else {
                        // 翻訳
                        say("今日はあなたの後ろに美の女神の気配を感じます。美容室に行ってみるといいでしょう。");
                    }
                    forceCompleteQuest();
                    questEndEffect();
                    return false;
                }
                // lua ではモミジの消費に失敗した場合も効果を与える
                exchange(0, this.item_maple_leaf, -4);
                if (this.fortune <= 3001) {
                    giveBuff(this.item_buff_meso);
                } else if (this.fortune <= 8000) {
                    giveBuff(this.item_buff_health_1);
                } else {
                    giveBuff(this.item_buff_health_2);
                }
                forceCompleteQuest();
                questEndEffect();
                if (this.fortune <= 3001) {
                    // 翻訳
                    say("今日は金運がとてもいいですね。メルを手に入れる機会がたくさんありそうです。");
                } else {
                    // 翻訳
                    say("今日は体が健康になる運勢ですね。健康になった体で狩りを楽しんでみるといいですよ。");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
